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
import org.json.JSONArray

internal interface ComparisonValidationParameterOperation {

    fun parametersIsNull(params: Array<out OperationType?>): Boolean =
        params.isNullOrEmpty() || checkItemsInParameterIsNull(params)

    /**
     * Compares 2 params.
     * Returns zero if the first is equal to the second, a negative number if it's less than,
     * or a positive number if it's greater than.
     * Returns Null if it's an invalid operation
     */
    @Suppress("UNCHECKED_CAST")
    fun comparison(vararg params: OperationType?): Int? {
        val paramsAsNumber: List<Comparable<Any>?> = params.map {
            when (it?.value) {
                is Int -> ((it.value as Int).toDouble()).toBigDecimal() as Comparable<Any>
                is Double -> (it.value as Double).toBigDecimal() as Comparable<Any>
                is JSONArray -> {
                    ((it.value as JSONArray).join(",")) as Comparable<Any>
                }
                is String -> {
                    try {
                        ((it.value as String).toBigDecimal()) as Comparable<Any>
                    } catch (e: Throwable) {
                        (it.value as String) as Comparable<Any>
                    }

                }
                else -> null
            }

        }

        if(paramsAsNumber.size != 2) return null

        val value1 = paramsAsNumber[0]
        val value2 = paramsAsNumber[1]

        return kotlin.runCatching { value2?.let { (value1)?.compareTo(it) } }.getOrNull()
    }

    private fun checkItemsInParameterIsNull(params: Array<out OperationType?>): Boolean =
        params[0] is OperationType.Null || params[1] is OperationType.Null
}