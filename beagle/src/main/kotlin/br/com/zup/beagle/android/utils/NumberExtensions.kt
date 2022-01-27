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

package br.com.zup.beagle.android.utils

import br.com.zup.beagle.android.setup.BeagleEnvironment

fun Int.px(): Int = pxToDp(this.toDouble()).toInt()

fun Int.dp(): Int = dpToPx(this.toDouble()).toInt()

fun Float.px(): Float = pxToDp(this.toDouble()).toFloat()

fun Float.dp(): Float = dpToPx(this.toDouble()).toFloat()

fun Double.px(): Double = pxToDp(this)

fun Double.dp(): Double = dpToPx(this)

private fun pxToDp(value: Double): Double {
    return value / BeagleEnvironment.application.resources.displayMetrics.density
}

private fun dpToPx(value: Double): Double {
    return value * BeagleEnvironment.application.resources.displayMetrics.density
}
