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

package br.com.zup.beagle.android.exception

import br.com.zup.beagle.android.networking.RequestData
import br.com.zup.beagle.android.networking.ResponseData

/**
 * Exception throw when the request fails.
 *
 * @param responseData is used to return data made by the request.
 * @param requestData is used to make HTTP requests.
 * @param message get a message from the exception thrown.
 */
data class BeagleApiException(
    val responseData: ResponseData,
    val requestData: RequestData,
    override val message: String = responseData.toString()
) : BeagleException(message)
