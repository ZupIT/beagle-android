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

import android.content.Intent
import br.com.zup.beagle.android.logger.BeagleLogger
import br.com.zup.beagle.android.widget.RootView
import br.com.zup.beagle.sample.DeepLinkHandlerObject

class AppDeepLinkHandler(private val logger: BeagleLogger?) : br.com.zup.beagle.android.navigation.DeepLinkHandler {
    override fun getDeepLinkIntent(rootView: RootView, path: String, data: Map<String, String>?,
                                   shouldResetApplication: Boolean): Intent {
        logger?.info("DeepLinkHandlerSecond:path: $path")
        return DeepLinkHandlerObject.handleDeepLink(path, data)
    }
}