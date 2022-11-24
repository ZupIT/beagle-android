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
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.Assert
import org.junit.Before
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

private const val CONFIG_NAME = "BeagleSdkMockk"
@ExtendWith(MockKExtension::class)
class BeagleSdkWrapperTest {
    @MockK(relaxed = true, relaxUnitFun = true) lateinit var beagleLogger: BeagleLogger
    @MockK(relaxed = true, relaxUnitFun = true) lateinit var beagleConfig: BeagleConfig
    @MockK(relaxed = true, relaxUnitFun = true) lateinit var deepLinkHandler: DeepLinkHandler
    @MockK(relaxed = true, relaxUnitFun = true) lateinit var httpClientFactory: HttpClientFactory
    @MockK(relaxed = true, relaxUnitFun = true) lateinit var designSystem: DesignSystem
    @MockK(relaxed = true, relaxUnitFun = true) lateinit var imageDownloader: BeagleImageDownloader
    @MockK(relaxed = true, relaxUnitFun = true) lateinit var viewClient: ViewClient
    @MockK(relaxed = true, relaxUnitFun = true) lateinit var controllerReference: BeagleControllerReference
    @MockK(relaxed = true, relaxUnitFun = true) lateinit var typeAdapterResolver: TypeAdapterResolver
    @MockK(relaxed = true, relaxUnitFun = true) lateinit var analyticsProvider: AnalyticsProvider
    @MockK(relaxed = true, relaxUnitFun = true) lateinit var urlBuilder: UrlBuilder
    @MockK(relaxed = true, relaxUnitFun = true) lateinit var registeredWidgets: List<Class<WidgetView>>
    @MockK(relaxed = true, relaxUnitFun = true) lateinit var registeredActions: List<Class<Action>>
    @MockK(relaxed = true, relaxUnitFun = true) lateinit var registeredOperations: Map<String, Operation>

    @InjectMockKs
    lateinit var subject: BeagleSdkMockk
    @Before
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `GIVEN BeagleSdkWrapper WHEN call asBeagleSdk THEN should have logger`() {
        val instance = subject.asBeagleSdk().logger
        Assert.assertEquals(instance, beagleLogger)
    }

    @Test
    fun `GIVEN BeagleSdkWrapper WHEN call asBeagleSdk THEN should have config`() {
        val instance = subject.asBeagleSdk().config
        Assert.assertEquals(instance, beagleConfig)
    }

    @Test
    fun `GIVEN BeagleSdkWrapper WHEN call asBeagleSdk THEN should have deepLinkHandler`() {
        val instance = subject.asBeagleSdk().deepLinkHandler
        Assert.assertEquals(instance, deepLinkHandler)
    }

    @Test
    fun `GIVEN BeagleSdkWrapper WHEN call asBeagleSdk THEN should have httpClientFactory`() {
        val instance = subject.asBeagleSdk().httpClientFactory
        Assert.assertEquals(instance, httpClientFactory)
    }

    @Test
    fun `GIVEN BeagleSdkWrapper WHEN call asBeagleSdk THEN should have designSystem`() {
        val instance = subject.asBeagleSdk().designSystem
        Assert.assertEquals(instance, designSystem)
    }

    @Test
    fun `GIVEN BeagleSdkWrapper WHEN call asBeagleSdk THEN should have imageDownloader`() {
        val instance = subject.asBeagleSdk().imageDownloader
        Assert.assertEquals(instance, imageDownloader)
    }

    @Test
    fun `GIVEN BeagleSdkWrapper WHEN call asBeagleSdk THEN should have viewClient`() {
        val instance = subject.asBeagleSdk().viewClient
        Assert.assertEquals(instance, viewClient)
    }

    @Test
    fun `GIVEN BeagleSdkWrapper WHEN call asBeagleSdk THEN should have controllerReference`() {
        val instance = subject.asBeagleSdk().controllerReference
        Assert.assertEquals(instance, controllerReference)
    }

    @Test
    fun `GIVEN BeagleSdkWrapper WHEN call asBeagleSdk THEN should have typeAdapterResolver`() {
        val instance = subject.asBeagleSdk().typeAdapterResolver
        Assert.assertEquals(instance, typeAdapterResolver)
    }

    @Test
    fun `GIVEN BeagleSdkWrapper WHEN call asBeagleSdk THEN should have urlBuilder`() {
        val instance = subject.asBeagleSdk().urlBuilder
        Assert.assertEquals(instance, urlBuilder)
    }

    @Test
    fun `GIVEN BeagleSdkWrapper WHEN call asBeagleSdk THEN should have registeredWidgets`() {
        val instance = subject.asBeagleSdk().registeredWidgets()
        Assert.assertEquals(instance, registeredWidgets)
    }

    @Test
    fun `GIVEN BeagleSdkWrapper WHEN call asBeagleSdk THEN should have registeredActions`() {
        val instance = subject.asBeagleSdk().registeredActions()
        Assert.assertEquals(instance, registeredActions)
    }

    @Test
    fun `GIVEN BeagleSdkWrapper WHEN call asBeagleSdk THEN should have registeredOperations`() {
        val instance = subject.asBeagleSdk().registeredOperations()
        Assert.assertEquals(instance, registeredOperations)
    }
}

@Suppress("LongParameterList")
class BeagleSdkMockk(
    beagleLogger: BeagleLogger,
    beagleConfig: BeagleConfig,
    deepLinkHandler: DeepLinkHandler,
    httpClientFactory: HttpClientFactory,
    designSystem: DesignSystem,
    imageDownloader: BeagleImageDownloader,
    viewClient: ViewClient,
    controllerReference: BeagleControllerReference,
    typeAdapterResolver: TypeAdapterResolver,
    analyticsProvider: AnalyticsProvider,
    urlBuilder: UrlBuilder,
    registeredWidgets: List<Class<WidgetView>>,
    registeredActions: List<Class<Action>>,
    registeredOperations: Map<String, Operation>,
): BeagleSdkWrapper {

    override val logger = beagleConfigFactory {
        beagleLogger
    }

    override val config = beagleConfigFactory<BeagleConfig> {
        it.asBeagleSdk().logger?.info("$CONFIG_NAME:config")
        beagleConfig
    }

    override val deepLinkHandler = beagleConfigFactory<DeepLinkHandler> {
        it.asBeagleSdk().logger?.info("$CONFIG_NAME:deepLinkHandler")
        deepLinkHandler
    }

    override val httpClientFactory = beagleConfigFactory<HttpClientFactory>  {
        it.asBeagleSdk().logger?.info("$CONFIG_NAME:httpClientFactory")
        httpClientFactory
    }

    override val designSystem = beagleConfigFactory<DesignSystem> {
        it.asBeagleSdk().logger?.info("$CONFIG_NAME:designSystem")
        designSystem
    }

    override val imageDownloader = beagleConfigFactory {
        it.asBeagleSdk().logger?.info("BeagleSetupSecond:imageDownloader")
        imageDownloader
    }
    override val viewClient = beagleConfigFactory<ViewClient> {
        it.asBeagleSdk().logger?.info("BeagleSetupSecond:viewClient")
        viewClient
    }

    override val controllerReference = beagleConfigFactory {
        it.asBeagleSdk().logger?.info("$CONFIG_NAME:controllerReference")
        controllerReference
    }

    override val typeAdapterResolver = beagleConfigFactory {
        it.asBeagleSdk().logger?.info("$CONFIG_NAME:typeAdapterResolver")
        typeAdapterResolver
    }

    override val analyticsProvider = beagleConfigFactory<AnalyticsProvider> {
        it.asBeagleSdk().logger?.info("$CONFIG_NAME:analyticsProvider")
        analyticsProvider
    }

    override val urlBuilder = beagleConfigFactory<UrlBuilder> {
        it.asBeagleSdk().logger?.info("$CONFIG_NAME:urlBuilder")
        urlBuilder
    }

    override val registeredWidgets =
        beagleConfigFactory {
            it.asBeagleSdk().logger?.info("$CONFIG_NAME:registeredWidgets")
            registeredWidgets
        }

    override val registeredActions =
        beagleConfigFactory {
            it.asBeagleSdk().logger?.info("$CONFIG_NAME:registeredActions")
            registeredActions
        }

    override val registeredOperations =
        beagleConfigFactory {
            it.asBeagleSdk().logger?.info("$CONFIG_NAME:registeredOperations")
            registeredOperations
        }
}
