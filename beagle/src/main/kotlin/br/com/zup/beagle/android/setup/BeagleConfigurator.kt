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

package br.com.zup.beagle.android.setup

import android.app.Application
import br.com.zup.beagle.android.action.Action
import br.com.zup.beagle.android.analytics.AnalyticsProvider
import br.com.zup.beagle.android.data.serializer.BeagleJsonSerializer
import br.com.zup.beagle.android.data.serializer.BeagleJsonSerializerFactory
import br.com.zup.beagle.android.data.serializer.BeagleMoshi
import br.com.zup.beagle.android.data.serializer.adapter.generic.TypeAdapterResolver
import br.com.zup.beagle.android.imagedownloader.BeagleImageDownloader
import br.com.zup.beagle.android.imagedownloader.DefaultImageDownloader
import br.com.zup.beagle.android.logger.BeagleLogger
import br.com.zup.beagle.android.navigation.BeagleControllerReference
import br.com.zup.beagle.android.networking.HttpClient
import br.com.zup.beagle.android.networking.ViewClient
import br.com.zup.beagle.android.networking.ViewClientDefault
import br.com.zup.beagle.android.networking.urlbuilder.UrlBuilder
import br.com.zup.beagle.android.networking.urlbuilder.UrlBuilderDefault
import br.com.zup.beagle.android.operation.Operation
import br.com.zup.beagle.android.widget.WidgetView
import com.squareup.moshi.Moshi

class BeagleConfigurator(
    private val beagleSdk: BeagleSdk,
    internal val moshi: Moshi,
    internal val application: Application,
    internal val serializer: BeagleJsonSerializer = BeagleJsonSerializerFactory.create(moshi),
) {
    companion object {
        val configurator: BeagleConfigurator by lazy {
            BeagleConfigurator(
                moshi = BeagleMoshi.moshi,
                beagleSdk = BeagleEnvironment.beagleSdk,
                application = BeagleEnvironment.application)
        }

        fun factory(
            beagleSdk: BeagleSdk?,
            moshi: Moshi = beagleSdk?.let { BeagleMoshi.moshiFactory(beagleSdk) } ?: BeagleMoshi.moshi,
            application: Application = BeagleEnvironment.application,
        ) =
            beagleSdk?.let {
                BeagleConfigurator(beagleSdk = it,
                    moshi = moshi, application = application)
            } ?: configurator
    }

    internal val httpClient: HttpClient by lazy {
        (this.beagleSdk.httpClientFactory?.create()
            ?: BeagleEnvironment.beagleSdk.httpClientFactory?.create()) as HttpClient
    }

    internal val viewClient: ViewClient by lazy {
        this.beagleSdk.viewClient ?: BeagleEnvironment.beagleSdk.viewClient ?: ViewClientDefault(httpClient)
    }

    internal val baseUrl: String by lazy {
        this.beagleSdk.config.baseUrl
    }

    internal val environment: Environment by lazy {
        this.beagleSdk.config.environment
    }

    internal val designSystem: DesignSystem? by lazy {
        this.beagleSdk.designSystem ?: BeagleEnvironment.beagleSdk.designSystem
    }

    internal val imageDownloader: BeagleImageDownloader by lazy {
        this.beagleSdk.imageDownloader ?: BeagleEnvironment.beagleSdk.imageDownloader ?: DefaultImageDownloader()
    }

    internal val controllerReference: BeagleControllerReference? by lazy {
        this.beagleSdk.controllerReference ?: BeagleEnvironment.beagleSdk.controllerReference
    }

    //TODO Check references
    internal val typeAdapterResolver: TypeAdapterResolver? by lazy {
        this.beagleSdk.typeAdapterResolver ?: BeagleEnvironment.beagleSdk.typeAdapterResolver
    }

    internal val analyticsProvider: AnalyticsProvider? by lazy {
        this.beagleSdk.analyticsProvider ?: BeagleEnvironment.beagleSdk.analyticsProvider
    }

    internal val urlBuilder: UrlBuilder by lazy {
        this.beagleSdk.urlBuilder ?: BeagleEnvironment.beagleSdk.urlBuilder ?: UrlBuilderDefault()
    }

    internal val logger: BeagleLogger? by lazy {
        this.beagleSdk.logger ?: BeagleEnvironment.beagleSdk.logger
    }

    internal val registeredWidgets: List<Class<WidgetView>> by lazy {
        this.beagleSdk.registeredWidgets()
    }

    internal val registeredActions: List<Class<Action>> by lazy {
        this.beagleSdk.registeredActions()
    }

    internal val registeredOperations: Map<String, Operation> by lazy {
        this.beagleSdk.registeredOperations()
    }
}
