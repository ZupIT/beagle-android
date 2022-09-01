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

package br.com.zup.beagle.sample.config

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
import br.com.zup.beagle.android.setup.BeagleConfig
import br.com.zup.beagle.android.setup.BeagleConfigFactory
import br.com.zup.beagle.android.setup.BeagleSdkWrapper
import br.com.zup.beagle.android.setup.DesignSystem
import br.com.zup.beagle.android.setup.Environment
import br.com.zup.beagle.android.setup.beagleConfigFactory
import br.com.zup.beagle.android.widget.WidgetView
import br.com.zup.beagle.sample.AppDeepLinkHandler
import br.com.zup.beagle.sample.actions.CustomAndroidAction
import br.com.zup.beagle.sample.constants.BASE_URL
import br.com.zup.beagle.sample.operations.IsValidCPFOperation
import br.com.zup.beagle.sample.widgets.ActionExecutor
import br.com.zup.beagle.sample.widgets.Input
import br.com.zup.beagle.sample.widgets.MutableText
import br.com.zup.beagle.sample.widgets.Text3

class BeagleSetupThirdConfig: BeagleSdkWrapper {
    override val logger: BeagleConfigFactory<BeagleLogger> = beagleConfigFactory {
        BeagleLoggerDefault()
    }

    override val config = beagleConfigFactory<BeagleConfig> { beagleSdkWrapper ->
        object : BeagleConfig {
            override val environment: Environment get() {
                return Environment.DEBUG.also {
                    beagleSdkWrapper.logger?.create(beagleSdkWrapper)?.info("BeagleSetupThird:environment: $it")
                }
            }
            override val baseUrl: String get() {
                return BASE_URL.also {
                    beagleSdkWrapper.logger?.create(beagleSdkWrapper)?.info("BeagleSetupThird:baseUrl: $it")
                }
            }
        }
    }

    override val deepLinkHandler = beagleConfigFactory<DeepLinkHandler> { AppDeepLinkHandler() }
    override val httpClientFactory: BeagleConfigFactory<HttpClientFactory>? = null
    override val designSystem: BeagleConfigFactory<DesignSystem>? = null
    override val imageDownloader: BeagleConfigFactory<BeagleImageDownloader>? = null
    override val viewClient: BeagleConfigFactory<ViewClient>? = null
    override val controllerReference: BeagleConfigFactory<BeagleControllerReference>? = null
    override val typeAdapterResolver: BeagleConfigFactory<TypeAdapterResolver>? = null
    override val analyticsProvider: BeagleConfigFactory<AnalyticsProvider>? = null
    override val urlBuilder: BeagleConfigFactory<UrlBuilder>? = null
    override fun registeredWidgets() =
        beagleConfigFactory {
            listOf(
                ActionExecutor::class.java as Class<WidgetView>,
                Text3::class.java as Class<WidgetView>,
                MutableText::class.java as Class<WidgetView>,
                Input::class.java as Class<WidgetView>)
        }

    override fun registeredActions() =
        beagleConfigFactory {
            listOf(
                CustomAndroidAction::class.java as Class<Action>,
            )
        }

    override fun registeredOperations() =
        beagleConfigFactory {
            mapOf<String, Operation>(
                "isValidCpf" to IsValidCPFOperation(),

                )
        }
}