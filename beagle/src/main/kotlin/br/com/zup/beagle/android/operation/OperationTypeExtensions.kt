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

package br.com.zup.beagle.android.operation

import br.com.zup.beagle.android.operation.builtin.CombineOperationUtil

internal fun OperationType.isInt() = this.value is Int || kotlin.runCatching {
    if(this.isString()) {
        this.value.toString().toInt()
        true
    } else {
        false
    }

}.getOrElse { false }

internal fun OperationType.isDouble() = this.value is Double || kotlin.runCatching {
    if(this.isString()) {
        this.value.toString().toDouble()
        true
    } else {
        false
    }
}.getOrElse { false }

internal fun OperationType.convertToDouble(): OperationType {
    return if (this.isDouble() || this.isInt())
        OperationType.TypeNumber(this.value.toString().toDouble())
    else OperationType.Null
}

internal fun OperationType.convertToInt(): OperationType {
    return if (this.isDouble() || this.isInt())
        OperationType.TypeNumber(this.value.toString().toDouble().toInt())
    else OperationType.Null
}

internal fun OperationType.convertToString(): OperationType {
    return if (this.value != null && this.value != OperationType.Null)
        OperationType.TypeString(this.value.toString())
    else
        OperationType.Null
}

internal fun OperationType.isString() = this.value is String

internal operator fun OperationType.plus(other: OperationType): OperationType {
    return CombineOperationUtil.combineOperations(this, other, Int::plus, Double::plus)
}

internal operator fun OperationType.minus(other: OperationType): OperationType {
    return CombineOperationUtil.combineOperations(this, other, Int::minus, Double::minus)
}

internal operator fun OperationType.div(other: OperationType): OperationType {
    return CombineOperationUtil.combineOperations(this, other, Int::div, Double::div)
}

internal operator fun OperationType.times(other: OperationType): OperationType {
    return CombineOperationUtil.combineOperations(this, other, Int::times, Double::times)
}
