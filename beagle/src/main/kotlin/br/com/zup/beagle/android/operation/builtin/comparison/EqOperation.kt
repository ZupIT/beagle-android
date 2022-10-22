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

package br.com.zup.beagle.android.operation.builtin.comparison

import br.com.zup.beagle.android.operation.Operation
import br.com.zup.beagle.android.operation.OperationType
import br.com.zup.beagle.android.annotation.RegisterOperation
import java.lang.NumberFormatException

@RegisterOperation("eq")
internal class EqOperation : Operation {

    override fun execute(vararg params: OperationType?): OperationType {
        return when (params.size) {
            2 -> handleOperation(params[0], params[1])
            else -> OperationType.TypeBoolean(false)
        }
    }

    private fun handleOperation(first: OperationType?, second: OperationType?): OperationType {
        val result = when {
            first?.value is Int && second?.value is String -> handleStringAndIntCase(first, second)
            second?.value is String && first?.value is Int -> handleStringAndIntCase(second, first)
            first?.value is Double && second?.value is String -> handleStringAndDoubleCase(first, second)
            second?.value is String && first?.value is Int -> handleStringAndDoubleCase(second, first)
            first?.value is Int && second?.value is Double -> handleIntAndDoubleCase(first, second)
            second?.value is Int && first?.value is Double -> handleIntAndDoubleCase(second, first)
            else -> false
        }
        return OperationType.TypeBoolean(result)
    }

    private fun handleStringAndIntCase(first: OperationType?, second: OperationType?): Boolean {
        try {
            val secondAsInt: Int = (second?.value as String).toInt()
            return (first?.value as Int) == secondAsInt
        } catch (exception: NumberFormatException) {

        }
        return false
    }

    private fun handleStringAndDoubleCase(first: OperationType?, second: OperationType?): Boolean {
        try {
            val secondAsDouble: Double = (second?.value as String).toDouble()
            return (first?.value as Double) == secondAsDouble
        } catch (exception: NumberFormatException) {

        }
        return false
    }

    private fun handleIntAndDoubleCase(first: OperationType?, second: OperationType?): Boolean {
        val secondAsDouble: Double = second?.value as Double
        val integerPartOfDouble: Int = secondAsDouble.toInt()
        return when (secondAsDouble - integerPartOfDouble) {
            0.0 -> (first?.value as Int) == integerPartOfDouble
            else -> false
        }
    }

}