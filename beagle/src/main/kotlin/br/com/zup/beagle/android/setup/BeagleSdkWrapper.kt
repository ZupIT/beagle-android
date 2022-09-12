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

    val registeredWidgets: BeagleConfigFactory<List<Class<WidgetView>>>
    val registeredActions: BeagleConfigFactory<List<Class<Action>>>
    val registeredOperations: BeagleConfigFactory<Map<String, Operation>>
}

fun BeagleSdkWrapper.asBeagleSdk(): BeagleSdk = object : BeagleSdk {
    override val config: BeagleConfig = this@asBeagleSdk.configInstance
    override val deepLinkHandler: DeepLinkHandler? =
        this@asBeagleSdk.deepLinkHandlerInstance
    override val httpClientFactory: HttpClientFactory? =
        this@asBeagleSdk.httpClientFactoryInstance
    override val designSystem: DesignSystem? =
        this@asBeagleSdk.designSystem?.create(this@asBeagleSdk)
    override val imageDownloader: BeagleImageDownloader? =
        this@asBeagleSdk.imageDownloaderInstance
    override val viewClient: ViewClient? =
        this@asBeagleSdk.viewClientInstance
    override val controllerReference: BeagleControllerReference? =
        this@asBeagleSdk.controllerReferenceInstance
    override val typeAdapterResolver: TypeAdapterResolver? =
        this@asBeagleSdk.typeAdapterResolverInstance
    override val analyticsProvider: AnalyticsProvider? =
        this@asBeagleSdk.analyticsProviderInstance
    override val urlBuilder: UrlBuilder? =
        this@asBeagleSdk.urlBuilderInstance
    override val logger: BeagleLogger? =
        this@asBeagleSdk.loggerInstance

    override fun registeredWidgets(): List<Class<WidgetView>> =
        this@asBeagleSdk.registeredWidgetsInstance

    override fun registeredActions(): List<Class<Action>> =
        this@asBeagleSdk.registeredActionsInstance

    override fun registeredOperations(): Map<String, Operation> =
        this@asBeagleSdk.registeredOperationsInstance

}

val BeagleSdkWrapper.configInstance: BeagleConfig
    get() = BeagleConfigProvider(this, this.config)
        .get()!!

val BeagleSdkWrapper.deepLinkHandlerInstance: DeepLinkHandler?
    get() = BeagleConfigProvider(this, this.deepLinkHandler)
        .get()

val BeagleSdkWrapper.httpClientFactoryInstance: HttpClientFactory?
    get() = BeagleConfigProvider(this, this.httpClientFactory)
        .get()

val BeagleSdkWrapper.designSystemInstance: DesignSystem?
    get() = BeagleConfigProvider(this, this.designSystem)
        .get()

val BeagleSdkWrapper.imageDownloaderInstance: BeagleImageDownloader?
    get() = BeagleConfigProvider(this, this.imageDownloader)
        .get()

val BeagleSdkWrapper.viewClientInstance: ViewClient?
    get() = BeagleConfigProvider(this, this.viewClient)
        .get()

val BeagleSdkWrapper.controllerReferenceInstance: BeagleControllerReference?
    get() = BeagleConfigProvider(this, this.controllerReference)
        .get()

val BeagleSdkWrapper.typeAdapterResolverInstance: TypeAdapterResolver?
    get() = BeagleConfigProvider(this, this.typeAdapterResolver)
        .get()

val BeagleSdkWrapper.analyticsProviderInstance: AnalyticsProvider?
    get() = BeagleConfigProvider(this, this.analyticsProvider)
        .get()

val BeagleSdkWrapper.urlBuilderInstance: UrlBuilder?
    get() = BeagleConfigProvider(this, this.urlBuilder)
        .get()

val BeagleSdkWrapper.loggerInstance: BeagleLogger?
    get() = BeagleConfigProvider(this, this.logger)
        .get()

val BeagleSdkWrapper.registeredWidgetsInstance: List<Class<WidgetView>>
    get() = BeagleConfigProvider(this, this.registeredWidgets)
        .get()!!

val BeagleSdkWrapper.registeredActionsInstance: List<Class<Action>>
    get() = BeagleConfigProvider(this, this.registeredActions)
        .get()!!

val BeagleSdkWrapper.registeredOperationsInstance: Map<String, Operation>
    get() = BeagleConfigProvider(this, this.registeredOperations)
        .get()!!

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