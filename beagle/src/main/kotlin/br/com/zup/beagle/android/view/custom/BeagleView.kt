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

package br.com.zup.beagle.android.view.custom

import android.annotation.SuppressLint
import android.view.View
import br.com.zup.beagle.android.data.formatUrl
import br.com.zup.beagle.android.networking.RequestData
import br.com.zup.beagle.android.utils.BeagleRetry
import br.com.zup.beagle.android.utils.generateViewModelInstance
import br.com.zup.beagle.android.utils.getRootId
import br.com.zup.beagle.android.view.ServerDrivenState
import br.com.zup.beagle.android.view.viewmodel.AnalyticsViewModel
import br.com.zup.beagle.android.view.viewmodel.BeagleViewModel
import br.com.zup.beagle.android.view.viewmodel.ViewState
import br.com.zup.beagle.android.widget.RootView
import br.com.zup.beagle.android.widget.core.ServerDrivenComponent

typealias OnServerStateChanged = (serverState: ServerDrivenState) -> Unit

typealias OnLoadCompleted = () -> Unit

@SuppressLint("ViewConstructor")
internal class BeagleView(
    private val rootView: RootView,
    private val viewModel: BeagleViewModel = rootView.generateViewModelInstance(
        BeagleViewModel.provideFactory(rootView.getBeagleConfigurator())),
) : InternalBeagleFlexView(rootView) {

    var serverStateChangedListener: OnServerStateChanged? = null

    var loadCompletedListener: OnLoadCompleted? = null

    fun loadView(requestData: RequestData) {
        loadView(requestData, null)
    }

    fun updateView(url: String, view: View) {
        val urlFormatted = url.formatUrl(beagleConfigurator = rootView.getBeagleConfigurator())
        loadView(RequestData(url = urlFormatted), view)
    }

    private fun loadView(requestData: RequestData, view: View?) {
        viewModel.fetchComponent(requestData).observe(rootView.getLifecycleOwner(), { state ->
            handleResponse(state, view)
        })
    }

    private fun handleResponse(
        state: ViewState?, view: View?,
    ) {
        when (state) {
            is ViewState.Loading -> handleLoading(state.value)
            is ViewState.Error -> handleError(state.throwable, state.retry)
            is ViewState.DoRender -> renderComponent(state.component, view, state.screenId)
        }
    }

    private fun handleLoading(isLoading: Boolean) {

        val serverState = if (isLoading) {
            ServerDrivenState.Started
        } else {
            ServerDrivenState.Finished
        }
        serverStateChangedListener?.invoke(serverState)
    }

    private fun handleError(throwable: Throwable, retry: BeagleRetry) {
        serverStateChangedListener?.invoke(ServerDrivenState.Error(throwable, retry))
    }

    private fun renderComponent(
        component: ServerDrivenComponent,
        view: View? = null,
        screenIdentifier: String?,
    ) {
        serverStateChangedListener?.invoke(ServerDrivenState.Success)
        if (view != null) {
            removeView(view)
            addServerDrivenComponent(component)
        } else {
            removeAllViewsInLayout()
            addServerDrivenComponent(component)
            loadCompletedListener?.invoke()
        }
        screenIdentifier?.let {
            rootView.generateViewModelInstance<AnalyticsViewModel>().createScreenReport(
                rootView, getRootId(component)
            )
        }
    }
}
