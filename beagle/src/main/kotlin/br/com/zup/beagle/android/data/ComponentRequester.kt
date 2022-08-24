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

package br.com.zup.beagle.android.data

import br.com.zup.beagle.android.data.serializer.BeagleJsonSerializer
import br.com.zup.beagle.android.exception.BeagleException
import br.com.zup.beagle.android.networking.OnError
import br.com.zup.beagle.android.networking.OnSuccess
import br.com.zup.beagle.android.networking.RequestCall
import br.com.zup.beagle.android.networking.RequestData
import br.com.zup.beagle.android.networking.ResponseData
import br.com.zup.beagle.android.networking.ViewClient
import br.com.zup.beagle.android.setup.BeagleConfigurator
import br.com.zup.beagle.android.widget.core.ServerDrivenComponent
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

internal class ComponentRequester(
    private val beagleConfigurator: BeagleConfigurator,
    private val viewClient: ViewClient,
    private val serializer: BeagleJsonSerializer,
) {

    @Throws(BeagleException::class)
    suspend fun fetchComponent(requestData: RequestData) = fetchAndDeserialize(requestData, ::fetch)

    @Throws(BeagleException::class)
    suspend fun prefetchComponent(requestData: RequestData) = fetchAndDeserialize(requestData, ::prefetch)

    private suspend fun fetchAndDeserialize(
        requestData: RequestData,
        call: suspend (requestData: RequestData) -> ResponseData,
    ): ServerDrivenComponent {
        val responseData = call(requestData.copy(url = requestData.url.formatUrl(beagleConfigurator)))
        return serializer.deserializeComponent(String(responseData.data))
    }

    private suspend fun fetch(requestData: RequestData) =
        suspendedViewClientCall(requestData, viewClient::fetch)

    private suspend fun prefetch(requestData: RequestData) =
        suspendedViewClientCall(requestData, viewClient::prefetch)

    private suspend fun suspendedViewClientCall(
        requestData: RequestData,
        viewClientCall: (requestData: RequestData, onSuccess: OnSuccess, onError: OnError) -> RequestCall?,
    ): ResponseData = suspendCancellableCoroutine { cont ->
        try {
            val call = viewClientCall(requestData, { response ->
                cont.resume(response)
            }, { response ->
                cont.resume(response)
            })
            cont.invokeOnCancellation {
                call?.cancel()
            }
        } catch (e: Exception) {
            cont.resumeWithException(e)
        }
    }
}
