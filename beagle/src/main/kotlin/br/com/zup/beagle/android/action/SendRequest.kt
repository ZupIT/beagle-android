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

package br.com.zup.beagle.android.action

import android.view.View
import br.com.zup.beagle.android.annotation.ContextDataValue
import br.com.zup.beagle.android.context.Bind
import br.com.zup.beagle.android.context.ContextData
import br.com.zup.beagle.android.context.normalizeContextValue
import br.com.zup.beagle.android.utils.evaluateExpression
import br.com.zup.beagle.android.utils.generateViewModelInstance
import br.com.zup.beagle.android.utils.handleEvent
import br.com.zup.beagle.android.view.viewmodel.ActionRequestViewModel
import br.com.zup.beagle.android.view.viewmodel.FetchViewState
import br.com.zup.beagle.android.widget.RootView
import br.com.zup.beagle.android.widget.core.BeagleJson
import br.com.zup.beagle.android.analytics.ActionAnalyticsConfig

/**
 * Enum with HTTP methods.
 */
@SuppressWarnings("UNUSED_PARAMETER")
@BeagleJson
enum class RequestActionMethod {
    /**
     * Request we representation of an resource.
     */
    GET,

    /**
     * The POST method is used when we want to create a resource.
     */
    POST,

    /**
     * Require that a resource be "saved" in the given URI.
     */
    PUT,

    /**
     * Deletes the specified resource.
     */
    DELETE,

    /**
     * Returns only the headers of a response.
     */
    HEAD,

    /**
     * Used to update parts of a resource
     */
    PATCH
}

/**
 * SendRequest is used to make HTTP requests.
 *
 * @param url Required. Server URL.
 * @param method  HTTP method.
 * @param headers Header items for the request.
 * @param data Content that will be delivered with the request.
 * @param onSuccess Success action.
 * @param onError  Error action.
 * @param onFinish Finish action.
 */
@BeagleJson(name = "sendRequest")
data class SendRequest(
    val url: Bind<String>,
    val method: Bind<RequestActionMethod> = Bind.Value(RequestActionMethod.GET),
    val headers: Bind<Map<String, String>>? = null,
    @ContextDataValue
    val data: Any? = null,
    val onSuccess: List<Action>? = null,
    val onError: List<Action>? = null,
    val onFinish: List<Action>? = null,
    override var analytics: ActionAnalyticsConfig? = null,
) : AnalyticsAction, AsyncAction by AsyncActionImpl() {

    override fun execute(rootView: RootView, origin: View) {
        val viewModel = rootView.generateViewModelInstance<ActionRequestViewModel>(
            ActionRequestViewModel.provideFactory(
                rootView.getBeagleConfigurator()
            ))
        val setContext = toSendRequestInternal(rootView, origin)
        viewModel.fetch(setContext).observe(rootView.getLifecycleOwner(), { state ->
            onActionFinished()
            executeActions(rootView, state, origin)
        })
    }

    private fun executeActions(
        rootView: RootView,
        state: FetchViewState,
        origin: View,
    ) {
        onFinish?.let {
            handleEvent(
                rootView,
                origin,
                it,
                analyticsValue = "onFinish"
            )
        }

        when (state) {
            is FetchViewState.Error -> onError?.let {
                handleEvent(
                    rootView,
                    origin,
                    it,
                    ContextData("onError", state.response),
                    analyticsValue = "onError"
                )
            }
            is FetchViewState.Success -> onSuccess?.let {
                handleEvent(
                    rootView,
                    origin,
                    it,
                    ContextData("onSuccess", state.response),
                    analyticsValue = "onSuccess"
                )
            }
        }
    }

    private fun toSendRequestInternal(rootView: RootView, origin: View) = SendRequestInternal(
        rootView = rootView,
        url = evaluateExpression(rootView, origin, this.url) ?: "",
        method = evaluateExpression(rootView, origin, this.method) ?: RequestActionMethod.GET,
        headers = this.headers?.let { evaluateExpression(rootView, origin, it) },
        data = this.data?.normalizeContextValue(
            rootView.getBeagleConfigurator().moshi)?.let { evaluateExpression(rootView, origin, it) },
        onSuccess = this.onSuccess,
        onError = this.onError,
        onFinish = this.onFinish
    )
}

internal data class SendRequestInternal(
    val rootView: RootView,
    val url: String,
    val method: RequestActionMethod = RequestActionMethod.GET,
    val headers: Map<String, String>?,
    val data: Any? = null,
    val onSuccess: List<Action>? = null,
    val onError: List<Action>? = null,
    val onFinish: List<Action>? = null,
)
