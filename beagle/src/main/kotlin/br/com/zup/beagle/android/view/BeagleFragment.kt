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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import br.com.zup.beagle.android.action.NavigationContext
import br.com.zup.beagle.android.action.SetContextInternal
import br.com.zup.beagle.android.context.ContextData
import br.com.zup.beagle.android.context.normalize
import br.com.zup.beagle.android.data.serializer.BeagleJsonSerializer
import br.com.zup.beagle.android.setup.BeagleConfigurator
import br.com.zup.beagle.android.utils.ObjectWrapperForBinder
import br.com.zup.beagle.android.utils.applyBackgroundFromWindowBackgroundTheme
import br.com.zup.beagle.android.utils.getRootId
import br.com.zup.beagle.android.utils.toView
import br.com.zup.beagle.android.view.viewmodel.AnalyticsViewModel
import br.com.zup.beagle.android.view.viewmodel.BeagleScreenViewModel
import br.com.zup.beagle.android.view.viewmodel.ScreenContextViewModel
import br.com.zup.beagle.android.widget.FragmentRootView
import br.com.zup.beagle.android.widget.UndefinedWidget
import br.com.zup.beagle.android.widget.core.ServerDrivenComponent

internal class BeagleFragment : Fragment() {

    private val beagleConfigurator: BeagleConfigurator by lazy {
        requireNotNull((arguments?.getBinder(BEAGLE_CONFIGURATOR)
            as? ObjectWrapperForBinder)?.data as? BeagleConfigurator)
    }

    private val beagleSerializer: BeagleJsonSerializer by lazy {
        beagleConfigurator.serializer
    }

    private val screen: ServerDrivenComponent by lazy {
        val json = arguments?.getString(JSON_SCREEN_KEY) ?: beagleSerializer.serializeComponent(
            UndefinedWidget()
        )
        beagleSerializer.deserializeComponent(json)
    }

    private val screenIdentifier by lazy {
        arguments?.getString(SCREEN_IDENTIFIER_KEY)
    }

    private val screenViewModel by lazy {
        ViewModelProvider(requireActivity(), BeagleScreenViewModel.provideFactory(beagleConfigurator)).get(
            BeagleScreenViewModel::class.java
        )
    }

    private val contextViewModel by lazy {
        ViewModelProvider(this, ScreenContextViewModel.provideFactory(
            beagleConfigurator
        )).get(
            ScreenContextViewModel::class.java
        )
    }

    private val analyticsViewModel by lazy {
        ViewModelProvider(requireActivity()).get(
            AnalyticsViewModel::class.java
        )
    }

    /**
     * This can be awkward, but to save some data in the fragment you need this strategy
     * https://stackoverflow.com/questions/15313598/how-to-correctly-save-instance-state-of-fragments-in-back-stack
     */
    internal val savedState: Bundle = Bundle()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        screenViewModel.onScreenLoadFinished()
        screenIdentifier?.let { screenIdentifier ->
                analyticsViewModel.createScreenReport(
                    FragmentRootView(this, this.id, screenIdentifier,
                        config = beagleConfigurator),
                    getRootId(screen))
        }

        val navigationContext = arguments?.getParcelable<NavigationContext>(NAVIGATION_CONTEXT_KEY)
        if (navigationContext != null) {
            updateNavigationContext(navigationContext)
            arguments?.remove(NAVIGATION_CONTEXT_KEY)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val navigationContextData = getNavigationContextData(savedInstanceState)
        return context?.let {
            FrameLayout(it).apply {
                id = View.generateViewId()
                contextViewModel.addContext(this, navigationContextData)
                applyBackgroundFromWindowBackgroundTheme(it)
                addView(
                    screen.toView(
                        this@BeagleFragment,
                        screenIdentifier = screenIdentifier,
                        beagleConfigurator = beagleConfigurator
                    ))
            }
        }
    }


    /**
     * When rotating application the strategy to save data local don't keep the data
     * because this you need to save the data in this bundle
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        saveContextData()
        outState.putAll(savedState)
    }

    /** In case of rotation the data need to be saved and restored */
    private fun getNavigationContextData(savedInOnCreateView: Bundle?): ContextData {

        var contextData = ContextData(
            id = NAVIGATION_CONTEXT_DATA_ID,
            value = "",
        )

        savedState.getParcelable<ContextData>(NAVIGATION_CONTEXT_DATA_KEY)?.let {
            contextData = it
        }

        contextData = savedInOnCreateView?.getParcelable(NAVIGATION_CONTEXT_DATA_KEY)
            ?: arguments?.getParcelable(NAVIGATION_CONTEXT_DATA_KEY) ?: contextData

        return contextData
    }

    override fun onDestroyView() {
        super.onDestroyView()
        saveContextData()
    }

    private fun saveContextData() {
        val contextData = contextViewModel.getContextData(NAVIGATION_CONTEXT_DATA_ID)

        savedState.putParcelable(NAVIGATION_CONTEXT_DATA_KEY, contextData?.normalize(beagleConfigurator.moshi))
    }

    fun updateNavigationContext(navigationContext: NavigationContext) {
        contextViewModel.updateContext(requireView(), SetContextInternal(
            contextId = NAVIGATION_CONTEXT_DATA_ID,
            value = navigationContext.value,
            path = navigationContext.path,
        ))
        contextViewModel.tryLinkContextInBindWithoutContext(requireView())
    }

    companion object {

        @JvmStatic
        fun newInstance(
            component: ServerDrivenComponent,
            screenIdentifier: String? = null,
            navigationContext: NavigationContext? = null,
            beagleConfigurator: BeagleConfigurator,
        ) = newInstance(
            beagleConfigurator.serializer.serializeComponent(component),
            screenIdentifier,
            navigationContext,
            beagleConfigurator
        )

        @JvmStatic
        fun newInstance(
            json: String,
            screenIdentifier: String? = null,
            navigationContext: NavigationContext? = null,
            beagleConfigurator: BeagleConfigurator,
        ) = BeagleFragment().apply {
            arguments = newBundle(json, screenIdentifier, navigationContext, beagleConfigurator)
        }

        fun newBundle(
            json: String,
            screenIdentifier: String? = null,
            navigationContext: NavigationContext? = null,
            beagleConfigurator: BeagleConfigurator,
        ): Bundle {
            val bundle = Bundle(4)
            bundle.putString(JSON_SCREEN_KEY, json)
            bundle.putString(SCREEN_IDENTIFIER_KEY, screenIdentifier)
            bundle.putBinder(BEAGLE_CONFIGURATOR, ObjectWrapperForBinder(beagleConfigurator))

            navigationContext?.let {
                bundle.putParcelable(NAVIGATION_CONTEXT_KEY, it)
            }
            return bundle
        }

        private const val JSON_SCREEN_KEY = "JSON_SCREEN_KEY"
        private const val BEAGLE_CONFIGURATOR = "BEAGLE_CONFIGURATOR"
        private const val SCREEN_IDENTIFIER_KEY = "SCREEN_IDENTIFIER_KEY"
        internal const val NAVIGATION_CONTEXT_KEY = "NAVIGATION_CONTEXT_KEY"
        internal const val NAVIGATION_CONTEXT_DATA_KEY = "NAVIGATION_CONTEXT_DATA_KEY"
        internal const val NAVIGATION_CONTEXT_DATA_ID = "navigationContext"

    }
}

