/*
 * Copyright 2020, 2022 ZUP IT SERVICOS EM TECNOLOGIA E INOVACAO SA
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

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import br.com.zup.beagle.R
import br.com.zup.beagle.android.action.NavigationContext
import br.com.zup.beagle.android.components.layout.Screen
import br.com.zup.beagle.android.data.serializer.BeagleJsonSerializerFactory
import br.com.zup.beagle.android.data.serializer.BeagleJsonSerializer
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

abstract class BeagleActivity : AppCompatActivity() {

    private val screenViewModel by lazy { ViewModelProvider(this).get(BeagleScreenViewModel::class.java) }
    private val screenRequest by lazy {
        intent.extras?.getParcelable<RequestData>(
            FIRST_SCREEN_REQUEST_KEY
        )
    }

    private val screen by lazy { intent.extras?.getString(FIRST_SCREEN_KEY) }

    private var navigationContext: NavigationContext? = null

    private val fragmentManager: FragmentManager = supportFragmentManager

    /**
     * [br.com.zup.beagle.android.view.custom.BeagleNavigator.pushStack] use this variable to call function
     * launch after this others functions in navigator can control the result
     * like: [br.com.zup.beagle.android.view.custom.BeagleNavigator.popView]
     * and [br.com.zup.beagle.android.view.custom.BeagleNavigator.popStack]
     */
    internal val nextActivity = registerForActivityResult(BeagleActivityContract()) { data ->
        val fragment = supportFragmentManager.fragments.lastOrNull() as? BeagleFragment
        if (data != null && fragment != null) {
            fragment.updateNavigationContext(data)
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
        navigationContext = intent.extras?.getParcelable(NAVIGATION_CONTEXT_KEY)
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
            {
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

    internal fun navigateTo(
        requestData: RequestData,
        screen: Screen?,
        navigationContext: NavigationContext?,
    ) {
        this.navigationContext = navigationContext
        fetch(requestData, screen)
    }

    private fun fetch(requestData: RequestData, screen: ServerDrivenComponent? = null) {
        val liveData = screenViewModel.fetchComponent(requestData, screen)
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
                BeagleFragment.newInstance(
                    component,
                    screenName,
                    navigationContext,
                )
            )
            .addToBackStack(screenName)
            .commit()
    }

    companion object {
        private val beagleSerializer: BeagleJsonSerializer = BeagleJsonSerializerFactory.serializer
        internal const val FIRST_SCREEN_REQUEST_KEY = "FIRST_SCREEN_REQUEST_KEY"
        internal const val FIRST_SCREEN_KEY = "FIRST_SCREEN_KEY"
        internal const val NAVIGATION_CONTEXT_KEY = "NAVIGATION_CONTEXT_KEY"

        fun bundleOf(
            requestData: RequestData,
            navigationContext: NavigationContext? = null,
        ): Bundle {
            return Bundle(2).apply {
                putParcelable(FIRST_SCREEN_REQUEST_KEY, requestData)
                putParcelable(NAVIGATION_CONTEXT_KEY, navigationContext)
            }
        }

        internal fun bundleOf(
            requestData: RequestData,
            fallbackScreen: Screen,
            navigationContext: NavigationContext? = null,
        ): Bundle {
            return Bundle(3).apply {
                putParcelable(FIRST_SCREEN_REQUEST_KEY, requestData)
                putParcelable(NAVIGATION_CONTEXT_KEY, navigationContext)
                putString(FIRST_SCREEN_KEY, beagleSerializer.serializeComponent(fallbackScreen))
            }
        }

        internal fun bundleOf(
            screen: Screen,
            navigationContext: NavigationContext? = null,
        ): Bundle {
            return Bundle(2).apply {
                putString(FIRST_SCREEN_KEY, beagleSerializer.serializeComponent(screen))
                putParcelable(NAVIGATION_CONTEXT_KEY, navigationContext)
            }
        }

        fun bundleOf(
            screenJson: String,
            navigationContext: NavigationContext? = null,
        ): Bundle {
            return Bundle(2).apply {
                putString(FIRST_SCREEN_KEY, screenJson)
                putParcelable(NAVIGATION_CONTEXT_KEY, navigationContext)
            }
        }

        internal fun bundleOf(navigationContext: NavigationContext): Bundle {
            return Bundle(1).apply {
                putParcelable(NAVIGATION_CONTEXT_KEY, navigationContext)
            }
        }
    }
}

internal class BeagleActivityContract : ActivityResultContract<Intent, NavigationContext?>() {

    override fun createIntent(context: Context, intent: Intent?): Intent {
        return intent ?: Intent(context, BeagleActivity::class.java)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): NavigationContext? {
        val data = intent?.getParcelableExtra<NavigationContext>(BeagleActivity.NAVIGATION_CONTEXT_KEY)
        return if (resultCode == Activity.RESULT_OK && data != null) data
        else null
    }
}