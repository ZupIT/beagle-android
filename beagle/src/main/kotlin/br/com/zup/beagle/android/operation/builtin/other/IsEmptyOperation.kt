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

package br.com.zup.beagle.android.operation.builtin.other

import br.com.zup.beagle.android.operation.Operation
import br.com.zup.beagle.android.operation.OperationType
import br.com.zup.beagle.android.operation.builtin.SafeGetHelper
import br.com.zup.beagle.android.annotation.RegisterOperation

@RegisterOperation("isEmpty")
internal class IsEmptyOperation : Operation, SafeGetHelper {

    override fun execute(vararg params: OperationType?): OperationType {
        val result = when (val operationType = safeGet(params, 0)) {
            is OperationType.TypeString -> operationType.value.isEmpty()
            is OperationType.TypeJsonArray -> operationType.value.length() == 0
            is OperationType.TypeJsonObject -> operationType.value.length() == 0
            else -> true
        }
        return OperationType.TypeBoolean(result)
    }

}