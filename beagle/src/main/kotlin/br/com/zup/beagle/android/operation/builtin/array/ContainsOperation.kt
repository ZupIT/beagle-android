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

package br.com.zup.beagle.android.operation.builtin.array

import br.com.zup.beagle.android.operation.Operation
import br.com.zup.beagle.android.operation.OperationType
import br.com.zup.beagle.android.annotation.RegisterOperation

@RegisterOperation("contains")
internal class ContainsOperation : Operation {

    override fun execute(vararg params: OperationType?): OperationType {
        val parameterOne = params[0]
        val elementCompare = params[1]

        if (parameterOne is OperationType.TypeJsonArray) {
            val array = parameterOne.value
            for (index in 0 until array.length()) {
                if (array[index] == elementCompare?.value) {
                    return OperationType.TypeBoolean(true)
                }
            }
        }

        return OperationType.TypeBoolean(false)
    }
}