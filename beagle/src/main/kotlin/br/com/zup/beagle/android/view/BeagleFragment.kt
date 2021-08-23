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

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModelProvider
import br.com.zup.beagle.android.context.ContextData
import br.com.zup.beagle.android.data.serializer.BeagleSerializer
import br.com.zup.beagle.android.utils.applyBackgroundFromWindowBackgroundTheme
import br.com.zup.beagle.android.utils.toView
import br.com.zup.beagle.android.view.viewmodel.AnalyticsViewModel
import br.com.zup.beagle.android.view.viewmodel.BeagleScreenViewModel
import br.com.zup.beagle.android.view.viewmodel.ScreenContextViewModel
import br.com.zup.beagle.android.widget.UndefinedWidget
import br.com.zup.beagle.android.widget.core.ServerDrivenComponent
import android.os.Looper
import android.util.Log
import java.util.logging.Logger


internal class BeagleFragment : Fragment() {

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
        ViewModelProvider(requireActivity()).get(
            BeagleScreenViewModel::class.java
        )
    }

    private val contextViewModel by lazy {
        ViewModelProvider(this).get(
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
    private val savedState: Bundle = Bundle()

    private val contextDataList by lazy {
        savedState.getParcelableArrayList(CONTEXT_DATA_LIST_KEY) ?: arrayListOf<ContextData>()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        screenViewModel.onScreenLoadFinished()
        screenIdentifier?.let { screenIdentifier ->
            analyticsViewModel.createScreenReport(screenIdentifier)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        addContextDataInArgumentsToList(savedInstanceState)
        return context?.let {
            FrameLayout(it).apply {
                contextViewModel.addContext(this, contextDataList)
                applyBackgroundFromWindowBackgroundTheme(it)
                addView(
                    screen.toView(
                        this@BeagleFragment,
                        screenIdentifier = screenIdentifier,
                    ))
            }
        }
    }

    /**
     * When rotating application the strategy to save data local don't keep the data
     * because this you need to save the data in this bundle
     */
    override fun onSaveInstanceState(outState: Bundle) {
        saveContextData()
        outState.putAll(savedState)

        super.onSaveInstanceState(outState)
    }

    /** In case of rotation the data need to be saved and restored */
    private fun addContextDataInArgumentsToList(savedInOnCreateView: Bundle?) {
        val contextDataLocalList = savedInOnCreateView?.getParcelableArrayList<ContextData>(CONTEXT_DATA_LIST_KEY)
            ?: arguments?.getParcelableArrayList(CONTEXT_DATA_LIST_KEY)
        contextDataLocalList?.let {
            contextDataList.addAll(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        saveContextData()
    }

    private fun saveContextData() {
        savedState.putParcelableArrayList(CONTEXT_DATA_LIST_KEY, contextDataList)
    }

    fun updateContext(contextData: ContextData) {
        contextDataList.add(contextData)
        contextViewModel.addContext(requireView(), contextData, true)
        contextViewModel.linkBindingToContextAndEvaluateThem(requireView())
    }

    companion object {

        @JvmStatic
        fun newInstance(
            component: ServerDrivenComponent,
            screenIdentifier: String? = null,
            contextData: ContextData? = null,
        ) = newInstance(
            beagleSerializer.serializeComponent(component),
            screenIdentifier,
            contextData
        )

        @JvmStatic
        fun newInstance(
            json: String,
            screenIdentifier: String? = null,
            contextData: ContextData? = null,
        ) = BeagleFragment().apply {
            arguments = newBundle(json, screenIdentifier, contextData)
        }

        fun newBundle(
            json: String,
            screenIdentifier: String? = null,
            contextData: ContextData? = null,
        ): Bundle {
            val bundle = Bundle()
            bundle.putString(JSON_SCREEN_KEY, json)
            bundle.putString(SCREEN_IDENTIFIER_KEY, screenIdentifier)
            contextData?.let {
                bundle.putParcelableArrayList(CONTEXT_DATA_LIST_KEY, arrayListOf(it))
            }
            return bundle
        }

        private val beagleSerializer: BeagleSerializer = BeagleSerializer()
        private const val JSON_SCREEN_KEY = "JSON_SCREEN_KEY"
        private const val SCREEN_IDENTIFIER_KEY = "SCREEN_IDENTIFIER_KEY"
        private const val CONTEXT_DATA_LIST_KEY = "CONTEXT_DATA_LIST_KEY"

    }
}
