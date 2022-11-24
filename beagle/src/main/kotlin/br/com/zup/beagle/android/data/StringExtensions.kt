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

import br.com.zup.beagle.android.networking.HttpAdditionalData
import br.com.zup.beagle.android.networking.HttpMethod
import br.com.zup.beagle.android.networking.RequestData
import br.com.zup.beagle.android.networking.urlbuilder.UrlBuilder
import br.com.zup.beagle.android.networking.urlbuilder.UrlBuilderFactory
import br.com.zup.beagle.android.setup.BeagleConfigurator

internal fun String.toRequestData(beagleConfigurator: BeagleConfigurator,
                                  urlBuilder: UrlBuilder = UrlBuilderFactory().make(beagleConfigurator),
                                  method: HttpMethod = HttpMethod.GET): RequestData {
    val newUrl = this.formatUrl(beagleConfigurator, urlBuilder)
    return RequestData(
        url = newUrl,
        httpAdditionalData = HttpAdditionalData(method = method)
    )
}

internal fun String.formatUrl(
    beagleConfigurator: BeagleConfigurator,
    urlBuilder: UrlBuilder = UrlBuilderFactory().make(beagleConfigurator),
) = urlBuilder.format(beagleConfigurator.baseUrl, this) ?: ""
