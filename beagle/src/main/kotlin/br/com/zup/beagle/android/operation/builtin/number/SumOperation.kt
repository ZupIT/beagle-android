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

@RegisterOperation("sum")
internal class SumOperation : Operation {

    override fun execute(vararg params: OperationType?): OperationType {

        val result = params.sumOf {

            var number: OperationType = OperationType.Null
            if (it?.value is String) {
                val mid = TypeConverter.convertStringToDouble(it.value as String)
                number = when (mid) {
                    is OperationType.Null -> OperationType.TypeNumber(0)
                    else -> mid
                }
            } else if (it?.value is Int) {
                number = OperationType.TypeNumber(it.value as Int)
            } else if (it?.value is Double) {
                number = OperationType.TypeNumber(it.value as Double)
            }

            (number.value as Number).toDouble()
        }

        return OperationType.TypeNumber(result)

    }
}