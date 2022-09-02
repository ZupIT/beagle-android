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
    override val config: BeagleConfig = this@asBeagleSdk.configWrapped!!
    override val deepLinkHandler: DeepLinkHandler? =
        this@asBeagleSdk.deepLinkHandlerWrapped
    override val httpClientFactory: HttpClientFactory? =
        this@asBeagleSdk.httpClientFactoryWrapped
    override val designSystem: DesignSystem? =
        this@asBeagleSdk.designSystem?.create(this@asBeagleSdk)
    override val imageDownloader: BeagleImageDownloader? =
        this@asBeagleSdk.imageDownloaderWrapped
    override val viewClient: ViewClient? =
        this@asBeagleSdk.viewClientWrapped
    override val controllerReference: BeagleControllerReference? =
        this@asBeagleSdk.controllerReferenceWrapped
    override val typeAdapterResolver: TypeAdapterResolver? =
        this@asBeagleSdk.typeAdapterResolverWrapped
    override val analyticsProvider: AnalyticsProvider? =
        this@asBeagleSdk.analyticsProviderWrapped
    override val urlBuilder: UrlBuilder? =
        this@asBeagleSdk.urlBuilderWrapped
    override val logger: BeagleLogger? =
        this@asBeagleSdk.loggerWrapped

    override fun registeredWidgets(): List<Class<WidgetView>> =
        this@asBeagleSdk.registeredWidgetsWrapped!!

    override fun registeredActions(): List<Class<Action>> =
        this@asBeagleSdk.registeredActionsWrapped!!

    override fun registeredOperations(): Map<String, Operation> =
        this@asBeagleSdk.registeredOperationsWrapped!!

}

val BeagleSdkWrapper.configWrapped: BeagleConfig?
    get() = BeagleConfigProvider(this, this.config)
        .get()

val BeagleSdkWrapper.deepLinkHandlerWrapped: DeepLinkHandler?
    get() = BeagleConfigProvider(this, this.deepLinkHandler)
        .get()

val BeagleSdkWrapper.httpClientFactoryWrapped: HttpClientFactory?
    get() = BeagleConfigProvider(this, this.httpClientFactory)
        .get()

val BeagleSdkWrapper.designSystemWrapped: DesignSystem?
    get() = BeagleConfigProvider(this, this.designSystem)
        .get()

val BeagleSdkWrapper.imageDownloaderWrapped: BeagleImageDownloader?
    get() = BeagleConfigProvider(this, this.imageDownloader)
        .get()

val BeagleSdkWrapper.viewClientWrapped: ViewClient?
    get() = BeagleConfigProvider(this, this.viewClient)
        .get()

val BeagleSdkWrapper.controllerReferenceWrapped: BeagleControllerReference?
    get() = BeagleConfigProvider(this, this.controllerReference)
        .get()

val BeagleSdkWrapper.typeAdapterResolverWrapped: TypeAdapterResolver?
    get() = BeagleConfigProvider(this, this.typeAdapterResolver)
        .get()

val BeagleSdkWrapper.analyticsProviderWrapped: AnalyticsProvider?
    get() = BeagleConfigProvider(this, this.analyticsProvider)
        .get()

val BeagleSdkWrapper.urlBuilderWrapped: UrlBuilder?
    get() = BeagleConfigProvider(this, this.urlBuilder)
        .get()

val BeagleSdkWrapper.loggerWrapped: BeagleLogger?
    get() = BeagleConfigProvider(this, this.logger)
        .get()

val BeagleSdkWrapper.registeredWidgetsWrapped: List<Class<WidgetView>>?
    get() = BeagleConfigProvider(this, this.registeredWidgets())
        .get()

val BeagleSdkWrapper.registeredActionsWrapped: List<Class<Action>>?
    get() = BeagleConfigProvider(this, this.registeredActions())
        .get()

val BeagleSdkWrapper.registeredOperationsWrapped: Map<String, Operation>?
    get() = BeagleConfigProvider(this, this.registeredOperations())
        .get()

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