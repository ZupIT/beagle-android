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

package br.com.zup.beagle.android.utils

import br.com.zup.beagle.android.exception.BeagleApiException
import br.com.zup.beagle.android.logger.BeagleMessageLogs
import br.com.zup.beagle.android.networking.HttpClient
import br.com.zup.beagle.android.networking.RequestCall
import br.com.zup.beagle.android.networking.RequestData
import br.com.zup.beagle.android.networking.ResponseData

internal fun RequestData.doRequest(
    httpClient: HttpClient?,
    onSuccess: (responseData: ResponseData) -> Unit,
    onError: (responseData: ResponseData) -> Unit,
): RequestCall {
    if (httpClient == null) {
        throw BeagleApiException(
            ResponseData(
                statusCode = -1,
                data = "An instance of HttpClient was not found.".toByteArray(),
            ), this)
    }

    BeagleMessageLogs.logHttpRequestData(this)

    return httpClient.execute(
        request = this,
        onSuccess = { response ->
            BeagleMessageLogs.logHttpResponseData(response)
            onSuccess(response)
        }, onError = { response ->
        onError(response)
        val exception = BeagleApiException(
            response,
            this,
            "FetchData error for url ${this.url}",
        )
        BeagleMessageLogs.logUnknownHttpError(exception)
    })
}
