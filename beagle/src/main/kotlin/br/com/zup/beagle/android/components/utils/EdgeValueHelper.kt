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

import br.com.zup.beagle.android.widget.core.UnitType

internal data class EdgeValueHelper(
    var left: UnitValueConstant? = null,
    var top: UnitValueConstant? = null,
    var right: UnitValueConstant? = null,
    var bottom: UnitValueConstant? = null,
    var horizontal: UnitValueConstant? = null,
    var vertical: UnitValueConstant? = null,
    var all: UnitValueConstant? = null,
)

internal data class UnitValueConstant(
    var value: Double?,
    var type: UnitType
)