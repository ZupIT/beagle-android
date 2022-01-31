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

package br.com.zup.beagle.android.compiler

import br.com.zup.beagle.compiler.shared.BeagleClass

val DEEP_LINK_HANDLER = BeagleClass(
    packageName = "br.com.zup.beagle.android.navigation",
    className = "DeepLinkHandler"
)
val HTTP_CLIENT_FACTORY_HANDLER = BeagleClass(
    packageName = "br.com.zup.beagle.android.networking",
    className = "HttpClientFactory"
)
val VIEW_CLIENT = BeagleClass(
    packageName = "br.com.zup.beagle.android.networking",
    className = "ViewClient"
)
val DESIGN_SYSTEM = BeagleClass(
    packageName = "br.com.zup.beagle.android.setup",
    className = "DesignSystem"
)
val BEAGLE_CONFIG = BeagleClass(
    packageName = "br.com.zup.beagle.android.setup",
    className = "BeagleConfig"
)
val BEAGLE_SDK = BeagleClass(
    packageName = "br.com.zup.beagle.android.setup",
    className = "BeagleSdk"
)
val BEAGLE_ACTIVITY = BeagleClass(
    packageName = "br.com.zup.beagle.android.view",
    className = "BeagleActivity"
)
val DEFAULT_BEAGLE_ACTIVITY = BeagleClass(
    packageName = "br.com.zup.beagle.android.view",
    className = "ServerDrivenActivity"
)
val URL_BUILDER_HANDLER = BeagleClass(
    packageName = "br.com.zup.beagle.android.networking.urlbuilder",
    className = "UrlBuilder"
)
val ANALYTICS_PROVIDER = BeagleClass(
    packageName = "br.com.zup.beagle.android.analytics",
    className = "AnalyticsProvider"
)
val CONTROLLER_REFERENCE = BeagleClass(
    packageName = "br.com.zup.beagle.android.navigation",
    className = "BeagleControllerReference"
)
val BEAGLE_CUSTOM_ADAPTER = BeagleClass(
    packageName = "br.com.zup.beagle.android.data.serializer.adapter.generic",
    className = "TypeAdapterResolver"
)
val BEAGLE_PARAMETERIZED_TYPE_FACTORY = BeagleClass(
    packageName = "br.com.zup.beagle.android.data.serializer.adapter.generic",
    className = "ParameterizedTypeFactory"
)
val BEAGLE_LOGGER = BeagleClass(
    packageName = "br.com.zup.beagle.android.logger",
    className = "BeagleLogger"
)
val BEAGLE_IMAGE_DOWNLOADER = BeagleClass(
    packageName = "br.com.zup.beagle.android.imagedownloader",
    className = "BeagleImageDownloader"
)
val ANDROID_ACTION = BeagleClass(
    packageName = "br.com.zup.beagle.android.action",
    className = "Action"
)
