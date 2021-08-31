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

package br.com.zup.beagle.android.data

import br.com.zup.beagle.android.data.serializer.BeagleSerializer
import br.com.zup.beagle.android.exception.BeagleException
import br.com.zup.beagle.android.networking.RequestData
import br.com.zup.beagle.android.networking.ResponseData
import br.com.zup.beagle.android.networking.ViewClient
import br.com.zup.beagle.android.networking.ViewClientDefault
import br.com.zup.beagle.android.setup.BeagleEnvironment
import br.com.zup.beagle.android.widget.core.ServerDrivenComponent
import kotlinx.coroutines.suspendCancellableCoroutine
import java.lang.Exception
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

internal class ComponentRequester(
    private val viewClient: ViewClient = BeagleEnvironment.beagleSdk.viewClient ?: ViewClientDefault(),
    private val serializer: BeagleSerializer = BeagleSerializer(),
) {

    @Throws(BeagleException::class)
    suspend fun fetchComponent(requestData: RequestData): ServerDrivenComponent {
        val responseData = fetch(requestData)
        return serializer.deserializeComponent(String(responseData.data))
    }

    private suspend fun fetch(requestData: RequestData): ResponseData = suspendCancellableCoroutine { cont ->
        try {
            val call = viewClient.fetch(requestData, onSuccess = { response ->
                cont.resume(response)
            }, onError = { response ->
                cont.resume(response)
            })
            cont.invokeOnCancellation {
                call.cancel()
            }
        } catch (e: Exception) {
            cont.resumeWithException(e)
        }
    }
}
