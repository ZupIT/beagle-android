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

package br.com.zup.beagle.android.operation.builtin

import br.com.zup.beagle.android.operation.OperationType
import br.com.zup.beagle.android.operation.isDouble
import br.com.zup.beagle.android.operation.isInt

internal object CombineOperationUtil {

    @Suppress("ComplexCondition")
    fun combineOperations(
        lhs: OperationType,
        rhs: OperationType,
        intOperator: (Int, Int) -> Int,
        doubleOperator: (Double, Double) -> Double
    ) : OperationType {
        return if (lhs.isInt() && rhs.isInt()) {
            OperationType.TypeNumber(intOperator(lhs.value.toString().toInt(),
                rhs.value.toString().toInt()))
        }
        else if(lhs.isInt() && rhs.isDouble() || lhs.isDouble() && rhs.isInt()){
            OperationType.TypeNumber(doubleOperator(lhs.value.toString().toDouble(),
                rhs.value.toString().toDouble()))
        }
        else if(lhs.isDouble() && rhs.isDouble()){
            OperationType.TypeNumber(doubleOperator(lhs.value.toString().toDouble(),
                rhs.value.toString().toDouble()))
        } else OperationType.Null
    }
}