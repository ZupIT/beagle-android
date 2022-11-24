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

    override val config: BeagleConfig by lazy {
        BeagleConfigProvider(this@asBeagleSdk, this@asBeagleSdk.config)
        .get()!!
    }
    override val deepLinkHandler: DeepLinkHandler? by lazy {
        BeagleConfigProvider(this@asBeagleSdk, this@asBeagleSdk.deepLinkHandler)
            .get()
    }

    override val httpClientFactory: HttpClientFactory? by lazy {
        BeagleConfigProvider(this@asBeagleSdk, this@asBeagleSdk.httpClientFactory)
            .get()
    }

    override val designSystem: DesignSystem? by lazy {
        BeagleConfigProvider(this@asBeagleSdk, this@asBeagleSdk.designSystem)
            .get()
    }

    override val imageDownloader: BeagleImageDownloader? by lazy {
        BeagleConfigProvider(this@asBeagleSdk, this@asBeagleSdk.imageDownloader)
            .get()
    }

    override val viewClient: ViewClient? by lazy { BeagleConfigProvider(this@asBeagleSdk,
        this@asBeagleSdk.viewClient)
        .get()
    }
    override val controllerReference: BeagleControllerReference? by lazy {
        BeagleConfigProvider(this@asBeagleSdk, this@asBeagleSdk.controllerReference)
            .get()
    }
    override val typeAdapterResolver: TypeAdapterResolver? by lazy {
        BeagleConfigProvider(this@asBeagleSdk, this@asBeagleSdk.typeAdapterResolver)
            .get()
    }

    override val analyticsProvider: AnalyticsProvider? by lazy {
        BeagleConfigProvider(this@asBeagleSdk, this@asBeagleSdk.analyticsProvider)
            .get()
    }

    override val urlBuilder: UrlBuilder? by lazy {
        BeagleConfigProvider(this@asBeagleSdk, this@asBeagleSdk.urlBuilder)
            .get()
    }
    override val logger: BeagleLogger? by lazy {
        BeagleConfigProvider(this@asBeagleSdk, this@asBeagleSdk.logger)
            .get()
    }

    override fun registeredWidgets(): List<Class<WidgetView>> =
        BeagleConfigProvider(this@asBeagleSdk, this@asBeagleSdk.registeredWidgets)
            .get()!!

    override fun registeredActions(): List<Class<Action>> =
        BeagleConfigProvider(this@asBeagleSdk, this@asBeagleSdk.registeredActions)
            .get()!!

    override fun registeredOperations(): Map<String, Operation> =
        BeagleConfigProvider(this@asBeagleSdk, this@asBeagleSdk.registeredOperations)
            .get()!!

}

interface BeagleConfigFactory<T> {
    fun create(beagleConfigurator: BeagleSdkWrapper): T
}

fun <T> beagleConfigFactory(execution: (beagleConfigurator: BeagleSdkWrapper) -> T) =
    object : BeagleConfigFactory<T> {
        override fun create(beagleConfigurator: BeagleSdkWrapper) = execution(beagleConfigurator)
    }

internal class BeagleConfigProvider<T>(private val owner: BeagleSdkWrapper,
                                       private val factory: BeagleConfigFactory<T>?) {
    fun get() = factory?.create(owner)
}