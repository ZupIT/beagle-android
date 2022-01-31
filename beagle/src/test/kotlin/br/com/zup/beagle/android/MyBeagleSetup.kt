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

package br.com.zup.beagle.android

import br.com.zup.beagle.android.analytics.AnalyticsProvider
import br.com.zup.beagle.android.action.Action
import br.com.zup.beagle.android.data.serializer.adapter.generic.TypeAdapterResolver
import br.com.zup.beagle.android.imagedownloader.BeagleImageDownloader
import br.com.zup.beagle.android.logger.BeagleLogger
import br.com.zup.beagle.android.navigation.BeagleControllerReference
import br.com.zup.beagle.android.navigation.DeepLinkHandler
import br.com.zup.beagle.android.networking.HttpClientFactory
import br.com.zup.beagle.android.networking.ViewClient
import br.com.zup.beagle.android.networking.urlbuilder.UrlBuilder
import br.com.zup.beagle.android.operation.Operation
import br.com.zup.beagle.android.setup.BeagleConfig
import br.com.zup.beagle.android.setup.BeagleSdk
import br.com.zup.beagle.android.setup.DesignSystem
import br.com.zup.beagle.android.setup.Environment
import br.com.zup.beagle.android.widget.WidgetView

class MyBeagleSetup(override val analyticsProvider: AnalyticsProvider? = null) : BeagleSdk {

    override val config: BeagleConfig = object : BeagleConfig {
        override val environment: Environment = Environment.DEBUG
        override val baseUrl: String = ""
    }

    override val deepLinkHandler: DeepLinkHandler? = null

    override val httpClientFactory: HttpClientFactory? = null

    override val viewClient: ViewClient? = null

    override val designSystem: DesignSystem? = null

    override val imageDownloader: BeagleImageDownloader? = null

    override val controllerReference: BeagleControllerReference? = null

    override val typeAdapterResolver: TypeAdapterResolver? = null

    override val urlBuilder: UrlBuilder? = null

    override val logger: BeagleLogger? = null

    override fun registeredWidgets(): List<Class<WidgetView>> = emptyList()

    override fun registeredActions(): List<Class<Action>> = emptyList()

    override fun registeredOperations(): Map<String, Operation> = emptyMap()
}
