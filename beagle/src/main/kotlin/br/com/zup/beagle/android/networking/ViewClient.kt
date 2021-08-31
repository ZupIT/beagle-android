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

package br.com.zup.beagle.android.networking

/**
 * ViewClient is the contract responsible for making requests
 * that load the Beagle's components and actions.
 */
interface ViewClient {

    /**
     * Method that executes the request.
     * This is the recommended place to do processing
     * before and after the request happens, such as caching.
     * @param requestData carries all the information of the request
     * @param onSuccess
     * @param onError
     * @return
     */
    fun fetch(
        requestData: RequestData,
        onSuccess: OnSuccess,
        onError: OnError,
    ): RequestCall
}
