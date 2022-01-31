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

package br.com.zup.beagle.android.components.utils

import android.graphics.drawable.GradientDrawable
import android.view.View
import br.com.zup.beagle.android.utils.dp
import br.com.zup.beagle.android.utils.toAndroidColor

internal data class StrokeHelper(var borderColor: String? = null,
                                 var borderWidth: Double? = null)

internal fun StrokeHelper.applyStroke(view: View) {
    if (borderColor != null && borderColor != "" && borderWidth != null) {
        val androidColor = borderColor!!.toAndroidColor()
        val width = borderWidth!!.toInt().dp()
        androidColor?.let { color ->
            val gradient = view.background as? GradientDrawable ?: GradientDrawable()
            gradient.setStroke(width, color)
            view.background = gradient
            view.requestLayout()
        }
    }
}