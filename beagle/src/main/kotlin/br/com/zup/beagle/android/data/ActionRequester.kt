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

import br.com.zup.beagle.android.exception.BeagleApiException
import br.com.zup.beagle.android.networking.HttpClient
import br.com.zup.beagle.android.networking.RequestData
import br.com.zup.beagle.android.networking.ResponseData
import br.com.zup.beagle.android.setup.BeagleEnvironment
import br.com.zup.beagle.android.utils.doRequest
import kotlinx.coroutines.suspendCancellableCoroutine
import java.lang.Exception
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

internal class ActionRequester(
    private val httpClient: HttpClient? = BeagleEnvironment.beagleSdk.httpClientFactory?.create(),
) {

    @Throws(BeagleApiException::class)
    suspend fun fetchAction(requestData: RequestData): ResponseData = suspendCancellableCoroutine { cont ->
        try {
            val call = requestData.doRequest(httpClient, onSuccess = { response ->
                cont.resume(response)
            }, onError = { response ->
                cont.resumeWithException(BeagleApiException(response, requestData))
            })
            cont.invokeOnCancellation {
                call.cancel()
            }
        } catch (e: Exception) {
            cont.resumeWithException(e)
        }
    }
}
