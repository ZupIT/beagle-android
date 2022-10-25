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

package br.com.zup.beagle.android.operation.builtin.comparison

import br.com.zup.beagle.android.operation.Operation
import br.com.zup.beagle.android.operation.OperationType
import java.lang.NumberFormatException

internal interface ComparisonOperationHandler {

    fun handleOperation(first: OperationType?,
                        second: OperationType?,
                        operation: Operation): OperationType {
        val result = when {
            first?.value is Int && second?.value is String -> handleStringAndIntCase(first, second, operation)
            second?.value is String && first?.value is Int -> handleStringAndIntCase(second, first, operation)
            first?.value is Double && second?.value is String -> handleStringAndDoubleCase(first, second, operation)
            second?.value is String && first?.value is Int -> handleStringAndDoubleCase(second, first, operation)
            first?.value is Int && second?.value is Double -> handleIntAndDoubleCase(first, second, operation)
            second?.value is Int && first?.value is Double -> handleIntAndDoubleCase(second, first, operation)
            else -> false
        }
        return OperationType.TypeBoolean(result)
    }

    private fun handleStringAndIntCase(first: OperationType?,
                                       second: OperationType?,
                                       operation: Operation): Boolean {
        try {
            val secondAsInt: Int = (second?.value as String).toInt()
            return when (operation) {
                is EqOperation -> (first?.value as Int) == secondAsInt
                is GtOperation -> (first?.value as Int) > secondAsInt
                is LtOperation -> (first?.value as Int) < secondAsInt
                is GteOperation -> (first?.value as Int) >= secondAsInt
                is LteOperation -> (first?.value as Int) <= secondAsInt
                else -> false
            }
        } catch (exception: NumberFormatException) { }
        return false
    }

    private fun handleStringAndDoubleCase(first: OperationType?,
                                          second: OperationType?,
                                          operation: Operation): Boolean {
        try {
            val secondAsDouble: Double = (second?.value as String).toDouble()
            return when (operation) {
                is EqOperation -> (first?.value as Double) == secondAsDouble
                is GtOperation -> (first?.value as Double) > secondAsDouble
                is LtOperation -> (first?.value as Double) < secondAsDouble
                is GteOperation -> (first?.value as Double) >= secondAsDouble
                is LteOperation -> (first?.value as Double) <= secondAsDouble
                else -> false
            }
        } catch (exception: NumberFormatException) { }
        return false
    }

    private fun handleIntAndDoubleCase(first: OperationType?,
                                       second: OperationType?,
                                       operation: Operation): Boolean {
        val secondAsDouble: Double = second?.value as Double
        val integerPartOfDouble: Int = secondAsDouble.toInt()

        val decimalValue: Double = secondAsDouble - integerPartOfDouble

        if (decimalValue != 0.0) {
            return false
        }

        return when (operation) {
            is EqOperation -> (first?.value as Int) == integerPartOfDouble
            is GtOperation -> (first?.value as Int) > integerPartOfDouble
            is LtOperation -> (first?.value as Int) < integerPartOfDouble
            is GteOperation -> (first?.value as Int) >= integerPartOfDouble
            is LteOperation -> (first?.value as Int) <= integerPartOfDouble
            else -> false
        }
    }
}