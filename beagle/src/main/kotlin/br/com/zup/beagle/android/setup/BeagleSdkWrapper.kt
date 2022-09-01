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
import br.com.zup.beagle.android.analytics.AnalyticsProvider
import br.com.zup.beagle.android.data.serializer.adapter.generic.TypeAdapterResolver
import br.com.zup.beagle.android.imagedownloader.BeagleImageDownloader
import br.com.zup.beagle.android.logger.BeagleLogger
import br.com.zup.beagle.android.navigation.BeagleControllerReference
import br.com.zup.beagle.android.navigation.DeepLinkHandler
import br.com.zup.beagle.android.networking.HttpClientFactory
import br.com.zup.beagle.android.networking.ViewClient
import br.com.zup.beagle.android.networking.urlbuilder.UrlBuilder
import br.com.zup.beagle.android.operation.Operation
import br.com.zup.beagle.android.widget.WidgetView

interface BeagleSdkWrapper {
    val config: BeagleConfigFactory<BeagleConfig>
    val deepLinkHandler: BeagleConfigFactory<DeepLinkHandler>?
    val httpClientFactory: BeagleConfigFactory<HttpClientFactory>?
    val designSystem: BeagleConfigFactory<DesignSystem>?
    val imageDownloader: BeagleConfigFactory<BeagleImageDownloader>?
    val viewClient: BeagleConfigFactory<ViewClient>?
    val controllerReference: BeagleConfigFactory<BeagleControllerReference>?
    val typeAdapterResolver: BeagleConfigFactory<TypeAdapterResolver>?
    val analyticsProvider: BeagleConfigFactory<AnalyticsProvider>?
    val urlBuilder: BeagleConfigFactory<UrlBuilder>?
    val logger: BeagleConfigFactory<BeagleLogger>?

    fun registeredWidgets(): BeagleConfigFactory<List<Class<WidgetView>>>
    fun registeredActions(): BeagleConfigFactory<List<Class<Action>>>
    fun registeredOperations(): BeagleConfigFactory<Map<String, Operation>>
}

fun BeagleSdkWrapper.asBeagleSdk(): BeagleSdk = object : BeagleSdk {
    override val config: BeagleConfig = this@asBeagleSdk.config.create(this@asBeagleSdk)
    override val deepLinkHandler: DeepLinkHandler? =
        this@asBeagleSdk.deepLinkHandler?.create(this@asBeagleSdk)
    override val httpClientFactory: HttpClientFactory? =
        this@asBeagleSdk.httpClientFactory?.create(this@asBeagleSdk)
    override val designSystem: DesignSystem? =
        this@asBeagleSdk.designSystem?.create(this@asBeagleSdk)
    override val imageDownloader: BeagleImageDownloader? =
        this@asBeagleSdk.imageDownloader?.create(this@asBeagleSdk)
    override val viewClient: ViewClient? =
        this@asBeagleSdk.viewClient?.create(this@asBeagleSdk)
    override val controllerReference: BeagleControllerReference? =
        this@asBeagleSdk.controllerReference?.create(this@asBeagleSdk)
    override val typeAdapterResolver: TypeAdapterResolver? =
        this@asBeagleSdk.typeAdapterResolver?.create(this@asBeagleSdk)
    override val analyticsProvider: AnalyticsProvider? =
        this@asBeagleSdk.analyticsProvider?.create(this@asBeagleSdk)
    override val urlBuilder: UrlBuilder? =
        this@asBeagleSdk.urlBuilder?.create(this@asBeagleSdk)
    override val logger: BeagleLogger? =
        this@asBeagleSdk.logger?.create(this@asBeagleSdk)

    override fun registeredWidgets(): List<Class<WidgetView>> =
        this@asBeagleSdk.registeredWidgets().create(this@asBeagleSdk)

    override fun registeredActions(): List<Class<Action>> =
        this@asBeagleSdk.registeredActions().create(this@asBeagleSdk)


    override fun registeredOperations(): Map<String, Operation> =
        this@asBeagleSdk.registeredOperations().create(this@asBeagleSdk)

}

interface BeagleConfigFactory<T> {
    fun create(beagleConfigurator: BeagleSdkWrapper): T
}

fun <T> beagleConfigFactory(execution: (beagleConfigurator: BeagleSdkWrapper) -> T) =
    object : BeagleConfigFactory<T> {
        override fun create(beagleConfigurator: BeagleSdkWrapper) = execution(beagleConfigurator)
    }

class BeagleConfigProvider<T>(private val owner: BeagleSdkWrapper, private val factory: BeagleConfigFactory<T>?) {
    fun get() = factory?.create(owner)
}