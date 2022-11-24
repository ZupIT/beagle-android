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

import br.com.zup.beagle.android.navigation.BeagleControllerReference
import br.com.zup.beagle.android.view.BeagleActivity

val beagleControllerReferenceObject = object : BeagleControllerReference {
    override fun classFor(id: String?): Class<BeagleActivity> =
        br.com.zup.beagle.android.view.ServerDrivenActivity::class.java as Class<BeagleActivity>
}