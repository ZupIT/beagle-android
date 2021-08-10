/*
 * Copyright 2020 ZUP IT SERVICOS EM TECNOLOGIA E INOVACAO SA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.zup.beagle.android.view

import android.os.Build
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import br.com.zup.beagle.R
import br.com.zup.beagle.android.components.layout.Screen
import br.com.zup.beagle.android.data.serializer.BeagleSerializer
import br.com.zup.beagle.android.networking.RequestData
import br.com.zup.beagle.android.utils.BeagleRetry
import br.com.zup.beagle.android.view.viewmodel.BeagleScreenViewModel
import br.com.zup.beagle.android.view.viewmodel.ViewState
import br.com.zup.beagle.android.widget.core.ServerDrivenComponent

sealed class ServerDrivenState {

    class WebViewError(throwable: Throwable, retry: BeagleRetry) : Error(throwable, retry)

    /**
     * indicates that a server-driven component fetch has begun
     */
    object Started : ServerDrivenState()

    /**
     * indicates that a server-driven component fetch has finished
     */
    object Finished : ServerDrivenState()

    /**
     * indicates a success state while fetching a server-driven component
     */
    object Success : ServerDrivenState()

    /**
     * indicates that a server-driven component fetch has cancel
     */
    object Canceled : ServerDrivenState()

    /**
     * indicates an error state while fetching a server-driven component
     *
     * @param throwable error occurred. See {@link br.com.zup.beagle.android.exception.BeagleApiException},
     * See {@link br.com.zup.beagle.android.exception.BeagleException}
     * @param retry action to be performed when an error occurs
     */
    open class Error(val throwable: Throwable, val retry: BeagleRetry) : ServerDrivenState()
}

private val beagleSerializer: BeagleSerializer = BeagleSerializer()
private const val FIRST_SCREEN_REQUEST_KEY = "FIRST_SCREEN_REQUEST_KEY"
private const val FIRST_SCREEN_KEY = "FIRST_SCREEN_KEY"

abstract class BeagleActivity : AppCompatActivity() {

    private val screenViewModel by lazy { ViewModelProvider(this).get(BeagleScreenViewModel::class.java) }
    private val screenRequest by lazy {
        intent.extras?.getParcelable<RequestData>(
            FIRST_SCREEN_REQUEST_KEY
        )
    }

    private val screen by lazy { intent.extras?.getString(FIRST_SCREEN_KEY) }

    private val fragmentManager: FragmentManager = supportFragmentManager

    companion object {
        fun bundleOf(requestData: RequestData): Bundle {
            return Bundle(1).apply {
                putParcelable(FIRST_SCREEN_REQUEST_KEY, requestData)
            }
        }

        internal fun bundleOf(requestData: RequestData, fallbackScreen: Screen): Bundle {
            return Bundle(2).apply {
                putParcelable(FIRST_SCREEN_REQUEST_KEY, requestData)
                putAll(bundleOf(fallbackScreen))
            }
        }

        internal fun bundleOf(screen: Screen): Bundle {
            return Bundle(1).apply {
                putString(FIRST_SCREEN_KEY, beagleSerializer.serializeComponent(screen))
            }
        }

        fun bundleOf(screenJson: String): Bundle {
            return Bundle(1).apply {
                putString(FIRST_SCREEN_KEY, screenJson)
            }
        }
    }

    abstract fun getToolbar(): Toolbar

    @IdRes
    abstract fun getServerDrivenContainerId(): Int

    abstract fun onServerDrivenContainerStateChanged(state: ServerDrivenState)

    open fun getFragmentTransitionAnimation() = FragmentTransitionAnimation(
        R.anim.slide_from_right,
        R.anim.none_animation,
        R.anim.none_animation,
        R.anim.slide_to_right
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)


        adjustInputMode()


        observeScreenLoadFinish()
    }

    @SuppressWarnings("deprecation")
    private fun adjustInputMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
        } else {
            @Suppress("DEPRECATION")
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }
    }

    private fun observeScreenLoadFinish() {
        screenViewModel.screenLoadFinished.observe(
            this,
            Observer {
                onServerDrivenContainerStateChanged(ServerDrivenState.Success)
                onServerDrivenContainerStateChanged(ServerDrivenState.Finished)
            }
        )
    }

    override fun onResume() {
        super.onResume()

        if (fragmentManager.fragments.size == 0) {
            screen?.let { screen ->
                fetch(
                    RequestData(url = ""),
                    beagleSerializer.deserializeComponent(screen)
                )
            } ?: run {
                screenRequest?.let { request -> fetch(request) }
            }
        }
    }

    override fun onBackPressed() {
        if (screenViewModel.isFetchComponent()) {
            if (fragmentManager.backStackEntryCount == 0) {
                finish()
            }
        } else if (fragmentManager.backStackEntryCount == 1) {
            finish()
        } else {
            super.onBackPressed()
        }
    }

    fun hasServerDrivenScreen(): Boolean = fragmentManager.backStackEntryCount > 0

    internal fun navigateTo(requestData: RequestData, screen: Screen?) {
        fetch(requestData, screen)
    }

    private fun fetch(requestData: RequestData, Screen: ServerDrivenComponent? = null) {
        val liveData = screenViewModel.fetchComponent(requestData, Screen)
        handleLiveData(liveData)
    }

    private fun handleLiveData(state: LiveData<ViewState>) {
        state.observe(this, {
            when (it) {
                is ViewState.Error -> {
                    onServerDrivenContainerStateChanged(
                        ServerDrivenState.Error(
                            it.throwable,
                            it.retry
                        )
                    )
                    onServerDrivenContainerStateChanged(ServerDrivenState.Finished)
                }

                is ViewState.Loading -> {
                    if (it.value) {
                        onServerDrivenContainerStateChanged(ServerDrivenState.Started)
                    }
                }

                is ViewState.DoCancel -> {
                    onServerDrivenContainerStateChanged(ServerDrivenState.Canceled)
                }

                is ViewState.DoRender -> {
                    showScreen(it.screenId, it.component)
                }
            }
        })
    }

    private fun showScreen(screenName: String?, component: ServerDrivenComponent) {
        val transition = getFragmentTransitionAnimation()
        fragmentManager
            .beginTransaction()
            .setCustomAnimations(
                transition.enter,
                transition.exit,
                transition.popEnter,
                transition.popExit
            )
            .replace(
                getServerDrivenContainerId(),
                BeagleFragment.newInstance(component, screenName)
            )
            .addToBackStack(screenName)
            .commit()
    }
}