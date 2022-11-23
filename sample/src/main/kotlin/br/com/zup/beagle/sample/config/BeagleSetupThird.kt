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

package br.com.zup.beagle.sample.config

import br.com.zup.beagle.android.action.Action
import br.com.zup.beagle.android.analytics.AnalyticsProvider
import br.com.zup.beagle.android.logger.BeagleLogger
import br.com.zup.beagle.android.navigation.DeepLinkHandler
import br.com.zup.beagle.android.networking.HttpClientFactory
import br.com.zup.beagle.android.networking.ViewClient
import br.com.zup.beagle.android.networking.urlbuilder.UrlBuilder
import br.com.zup.beagle.android.operation.Operation
import br.com.zup.beagle.android.setup.BeagleConfig
import br.com.zup.beagle.android.setup.BeagleSdkWrapper
import br.com.zup.beagle.android.setup.DesignSystem
import br.com.zup.beagle.android.setup.asBeagleSdk
import br.com.zup.beagle.android.setup.beagleConfigFactory
import br.com.zup.beagle.android.widget.WidgetView
import br.com.zup.beagle.sample.AppAnalytics
import br.com.zup.beagle.sample.AppBeagleConfig
import br.com.zup.beagle.sample.AppDeepLinkHandler
import br.com.zup.beagle.sample.AppDesignSystem
import br.com.zup.beagle.sample.widgets.ActionExecutor
import br.com.zup.beagle.sample.widgets.Input
import br.com.zup.beagle.sample.widgets.MutableText
import br.com.zup.beagle.sample.widgets.Text3

private const val CONFIG_NAME = "BeagleSetupThird"

class BeagleSetupThird: BeagleSdkWrapper {
    override val logger = beagleConfigFactory<BeagleLogger> {
        BeagleLoggerDefault()
    }

    override val config = beagleConfigFactory<BeagleConfig> {
        it.logInfo("$CONFIG_NAME:config")
        AppBeagleConfig()
    }

    override val deepLinkHandler = beagleConfigFactory<DeepLinkHandler> {
        it.logInfo("$CONFIG_NAME:deepLinkHandler")
        AppDeepLinkHandler()
    }

    override val httpClientFactory = beagleConfigFactory<HttpClientFactory>  {
        it.logInfo("$CONFIG_NAME:httpClientFactory")
        AppHttpClientFactory()
    }

    override val designSystem = beagleConfigFactory<DesignSystem> {
        it.logInfo("$CONFIG_NAME:designSystem")
        AppDesignSystem()
    }

    override val imageDownloader = beagleConfigFactory {
        it.logInfo("BeagleSetupSecond:imageDownloader")
        imageDownloaderObject
    }
    override val viewClient = beagleConfigFactory<ViewClient> {
        it.logInfo("BeagleSetupSecond:viewClient")
        AppViewClient(it.asBeagleSdk().httpClientFactory?.create())
    }

    override val controllerReference = beagleConfigFactory {
        it.logInfo("$CONFIG_NAME:controllerReference")
        beagleControllerReferenceObject
    }

    override val typeAdapterResolver = beagleConfigFactory {
        it.logInfo("$CONFIG_NAME:typeAdapterResolver")
        typeAdapterObject
    }

    override val analyticsProvider = beagleConfigFactory<AnalyticsProvider> {
        it.logInfo("$CONFIG_NAME:analyticsProvider")
        AppAnalytics()
    }

    override val urlBuilder = beagleConfigFactory<UrlBuilder> {
        it.logInfo("$CONFIG_NAME:urlBuilder")
        AppUrlBuilder()
    }

    override val registeredWidgets =
        beagleConfigFactory {
            it.logInfo("$CONFIG_NAME:registeredWidgets")
            listOf(
                ActionExecutor::class.java as Class<WidgetView>,
                Text3::class.java as Class<WidgetView>,
                MutableText::class.java as Class<WidgetView>,
                Input::class.java as Class<WidgetView>)
        }

    override val registeredActions =
        beagleConfigFactory {
            it.logInfo("$CONFIG_NAME:registeredActions")
            listOf(
                br.com.zup.beagle.sample.actions.CustomAndroidAction::class.java as Class<Action>,
            )
        }

    override val registeredOperations =
        beagleConfigFactory {
            it.logInfo("$CONFIG_NAME:registeredOperations")
            mapOf<String, Operation>(
                "isValidCpf" to br.com.zup.beagle.sample.operations.IsValidCPFOperation(),

                )
        }
}