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

package br.com.zup.beagle.android.setup

import br.com.zup.beagle.android.action.Action
import br.com.zup.beagle.android.data.serializer.adapter.generic.TypeAdapterResolver
import br.com.zup.beagle.android.imagedownloader.BeagleImageDownloader
import br.com.zup.beagle.android.logger.BeagleLogger
import br.com.zup.beagle.android.navigation.BeagleControllerReference
import br.com.zup.beagle.android.navigation.DeepLinkHandler
import br.com.zup.beagle.android.networking.HttpClientFactory
import br.com.zup.beagle.android.networking.urlbuilder.UrlBuilder
import br.com.zup.beagle.android.operation.Operation
import br.com.zup.beagle.android.widget.WidgetView
import br.com.zup.beagle.android.analytics.AnalyticsProvider
import br.com.zup.beagle.android.networking.ViewClient

interface BeagleSdk {

    val config: BeagleConfig
    val deepLinkHandler: DeepLinkHandler?
    val httpClientFactory: HttpClientFactory?
    val designSystem: DesignSystem?
    val imageDownloader: BeagleImageDownloader?
    val viewClient: ViewClient?
    val controllerReference: BeagleControllerReference?
    val typeAdapterResolver: TypeAdapterResolver?
    val urlBuilder: UrlBuilder?
    val analyticsProvider: AnalyticsProvider?
    val logger: BeagleLogger?

    fun registeredWidgets(): List<Class<WidgetView>>
    fun registeredActions(): List<Class<Action>>
    fun registeredOperations(): Map<String, Operation>
}
