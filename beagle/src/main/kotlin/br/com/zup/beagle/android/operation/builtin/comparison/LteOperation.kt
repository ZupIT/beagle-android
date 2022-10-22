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

@RegisterOperation("lte")
internal class LteOperation : Operation, ComparisonValidationParameterOperation, ComparisonOperationHandler {

    override fun execute(vararg params: OperationType?): OperationType {
        if (parametersIsNull(params)) return OperationType.Null

        return when (params.size) {
            2 -> handleOperation(params[0], params[1])
            else -> OperationType.TypeBoolean(false)
        }
    }

    private fun handleOperation(first: OperationType?, second: OperationType?): OperationType {
        val result = when {
            first?.value is Int && second?.value is String -> handleStringAndIntCase(first, second, this)
            second?.value is String && first?.value is Int -> handleStringAndIntCase(second, first, this)
            first?.value is Double && second?.value is String -> handleStringAndDoubleCase(first, second, this)
            second?.value is String && first?.value is Int -> handleStringAndDoubleCase(second, first, this)
            first?.value is Int && second?.value is Double -> handleIntAndDoubleCase(first, second, this)
            second?.value is Int && first?.value is Double -> handleIntAndDoubleCase(second, first, this)
            else -> false
        }
        return OperationType.TypeBoolean(result)
    }

}