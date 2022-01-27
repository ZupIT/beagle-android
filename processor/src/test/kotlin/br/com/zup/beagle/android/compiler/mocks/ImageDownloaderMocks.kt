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

package br.com.zup.beagle.android.compiler.mocks

const val IMAGE_DOWNLOAD_IMPORT = "import br.com.zup.beagle.android.imagedownloader.BeagleImageDownloader"

const val VALID_IMAGE_DOWNLOAD =
    """
        import br.com.zup.beagle.android.imagedownloader.BeagleImageDownloader

        @BeagleComponent
        class ImageDownloaderTest: BeagleImageDownloader { }
    """

const val VALID_SECOND_IMAGE_DOWNLOAD =
    """
        @BeagleComponent
        class ImageDownloaderTestTwo: BeagleImageDownloader { }
    """

const val VALID_THIRD_IMAGE_DOWNLOAD =
    """
        class ImageDownloaderTestThree: BeagleImageDownloader { }
    """

const val LIST_OF_IMAGE_DOWNLOAD = VALID_IMAGE_DOWNLOAD + VALID_SECOND_IMAGE_DOWNLOAD

const val VALID_IMAGE_DOWNLOADER_BEAGLE_SDK =
    """
        @file:Suppress("OverridingDeprecatedMember", "DEPRECATION", "UNCHECKED_CAST")
        
        package br.com.test.beagle

        import br.com.zup.beagle.android.`data`.serializer.adapter.generic.TypeAdapterResolver
        import br.com.zup.beagle.android.action.Action
        import br.com.zup.beagle.android.analytics.AnalyticsProvider
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
        import br.com.zup.beagle.android.widget.WidgetView
        import java.lang.Class
        import kotlin.String
        import kotlin.Suppress
        import kotlin.collections.List
        import kotlin.collections.Map
        
        public final class BeagleSetup : BeagleSdk { 
            
            public override val deepLinkHandler : DeepLinkHandler? = null

            public override val httpClientFactory : HttpClientFactory? = null
            
            public override val designSystem : DesignSystem? = null
            
            public override val viewClient : ViewClient? = null

            public override val urlBuilder : UrlBuilder? = null

            public override val analyticsProvider : AnalyticsProvider? = null

            public override val logger : BeagleLogger? = null

            public override val imageDownloader : BeagleImageDownloader = br.com.test.beagle.ImageDownloaderTest()
            
            public override val config : BeagleConfig = br.com.test.beagle.BeagleConfigImpl()
            
            public override val controllerReference : BeagleControllerReference = RegisteredControllers()
            
            public override val typeAdapterResolver : TypeAdapterResolver = RegisteredCustomTypeAdapter

            public override fun registeredWidgets() : List<Class<WidgetView>> = RegisteredWidgets.registeredWidgets()

            public override fun registeredOperations() : Map<String, Operation> = 
                RegisteredOperations.registeredOperations()
            
            public override fun registeredActions() : List<Class<Action>> = RegisteredActions.registeredActions()
        }
    """

const val VALID_IMAGE_DOWNLOADER_BEAGLE_SDK_FROM_REGISTRAR =
    """
        @file:Suppress("OverridingDeprecatedMember", "DEPRECATION", "UNCHECKED_CAST")

        package br.com.test.beagle
        
        import br.com.zup.beagle.android.`data`.serializer.adapter.generic.TypeAdapterResolver
        import br.com.zup.beagle.android.action.Action
        import br.com.zup.beagle.android.analytics.AnalyticsProvider
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
        import br.com.zup.beagle.android.widget.WidgetView
        import java.lang.Class
        import kotlin.String
        import kotlin.Suppress
        import kotlin.collections.List
        import kotlin.collections.Map
        
        public final class BeagleSetup : BeagleSdk {
          public override val deepLinkHandler: DeepLinkHandler? = null
        
          public override val httpClientFactory: HttpClientFactory? = null
          
          public override val designSystem: DesignSystem? = null
          
          public override val viewClient: ViewClient? = null
        
          public override val urlBuilder: UrlBuilder? = null

          public override val analyticsProvider: AnalyticsProvider? = null
        
          public override val logger: BeagleLogger? = null
        
          public override val imageDownloader: BeagleImageDownloader =
              br.com.test.beagle.ImageDownloaderTestThree()
        
          public override val config: BeagleConfig = br.com.test.beagle.BeagleConfigImpl()
        
          public override val controllerReference: BeagleControllerReference = RegisteredControllers()
        
          public override val typeAdapterResolver: TypeAdapterResolver = RegisteredCustomTypeAdapter

          public override fun registeredWidgets(): List<Class<WidgetView>> =
              RegisteredWidgets.registeredWidgets()
        
          public override fun registeredOperations(): Map<String, Operation> =
              RegisteredOperations.registeredOperations()
        
          public override fun registeredActions(): List<Class<Action>> =
              RegisteredActions.registeredActions()
        }

    """
