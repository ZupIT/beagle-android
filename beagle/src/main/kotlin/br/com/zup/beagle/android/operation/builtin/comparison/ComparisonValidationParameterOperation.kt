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

import br.com.zup.beagle.android.operation.OperationType

internal interface ComparisonValidationParameterOperation {

    fun parametersIsNull(params: Array<out OperationType?>): Boolean =
        params.isNullOrEmpty() || checkItemsInParameterIsNull(params)

    @Suppress("UNCHECKED_CAST")
    fun comparison(params: Array<out OperationType?>): Int? {
        val paramsAsNumber = params.map {
            when (it?.value) {
                is Int -> OperationType.TypeNumber((it.value as Int).toDouble())
                is Double -> OperationType.TypeNumber(it.value as Double)
                is String -> {
                    try {
                        OperationType.TypeNumber((it.value as String).toDouble())
                    } catch (e: Throwable) {
                        null
                    }

                }
                else -> null
            }

        }
        val value1 = paramsAsNumber[0]?.value
        val value2 = paramsAsNumber[1]?.value

        return (value1 as? Comparable<Any>)?.compareTo(value2 as Double)
    }

    private fun checkItemsInParameterIsNull(params: Array<out OperationType?>): Boolean =
        params[0] is OperationType.Null || params[1] is OperationType.Null
}