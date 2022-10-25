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

package br.com.zup.beagle.android.operation.builtin.number

import br.com.zup.beagle.android.operation.Operation
import br.com.zup.beagle.android.operation.OperationType
import br.com.zup.beagle.android.annotation.RegisterOperation
import br.com.zup.beagle.android.operation.builtin.TypeConverter
import br.com.zup.beagle.android.operation.builtin.comparison.ComparisonValidationParameterOperation

@RegisterOperation("divide")
internal class DivideOperation : Operation, ComparisonValidationParameterOperation {

    override fun execute(vararg params: OperationType?): OperationType {

        return params.reduce { parameterOne, parameterTwo ->
            val two: Array<OperationType?> = arrayOf(parameterOne, parameterTwo)
            when (parametersIsNull(two)) {
                true -> OperationType.Null
                false -> handleDivide(parameterOne, parameterTwo)
            }
        } ?: OperationType.Null
    }

    private fun handleDivide(one: OperationType?, two: OperationType?) : OperationType {
        return when {
            one?.value is String && two?.value is String -> handleTwoString(one as OperationType.TypeString, two as OperationType.TypeString)
            one?.value is String && two?.value is Number -> handleOneString(one as OperationType.TypeString, two as OperationType.TypeNumber)
            two?.value is String && one?.value is Number -> handleOneString(two as OperationType.TypeString, one as OperationType.TypeNumber)
            else -> OperationType.TypeNumber((one?.value as Double) / (two?.value as Double))
        }
    }

    private fun handleTwoString(one: OperationType.TypeString, two: OperationType.TypeString): OperationType {
        val stringOne: OperationType = TypeConverter.convertStringToDouble(one.value)
        val stringTwo: OperationType = TypeConverter.convertStringToDouble(two.value)

        return when {
            stringOne is OperationType.Null -> OperationType.Null
            stringTwo is OperationType.Null -> OperationType.Null
            else -> OperationType.TypeNumber(stringOne.value as Double / stringTwo.value as Double)
        }

    }

    private fun handleOneString(one: OperationType.TypeString, two: OperationType.TypeNumber): OperationType {
        return when (val stringOne: OperationType = TypeConverter.convertStringToDouble(one.value)) {
            is OperationType.Null -> OperationType.Null
            else -> OperationType.TypeNumber(stringOne.value as Double / two.value as Double)
        }

    }

}