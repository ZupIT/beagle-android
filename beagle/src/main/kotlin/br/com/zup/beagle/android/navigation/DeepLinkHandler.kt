/*
 * Copyright 2021 ZUP IT SERVICOS EM TECNOLOGIA E INOVACAO SA
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

package br.com.zup.beagle.android.navigation

import android.content.Intent
import br.com.zup.beagle.android.widget.RootView

/**
 * The DeepLinkHandler is an interface that sets how to configure the navigation between Server-Driven
 * activities and native activities.
 */
interface DeepLinkHandler {
    /**
     * @param rootView holder the reference of activity or fragment.
     * @param path route-defined value.
     * @param data Content that will be deliver with the navigation.
     * @param shouldResetApplication Opens a screen with the route informed from a new flow and clears the view
     * stack for the entire application.
     */
    fun getDeepLinkIntent(rootView: RootView,
                          path: String,
                          data: Map<String, String>?,
                          shouldResetApplication: Boolean): Intent
}
