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

@file:JvmName("BeagleUtils")

package br.com.zup.beagle.android.utils

import br.com.zup.beagle.android.view.BeagleActivity
import br.com.zup.beagle.android.view.custom.BeagleNavigator

fun String.toAndroidId(): Int {
    // Validation required to avoid conflict of View.generateViewId() with a component's numeral id
    return if (toIntOrNull() != null) {
        toInt()
    } else {
        hashCode()
    }
}

internal fun BeagleActivity.configureSupportActionBar() {
    val toolbar = this.getToolbar()
    if (this.supportActionBar == null) {
        this.setSupportActionBar(toolbar)
        this.supportActionBar?.hide()
    }
    toolbar.setNavigationOnClickListener {
        BeagleNavigator.popView(this)
    }
}