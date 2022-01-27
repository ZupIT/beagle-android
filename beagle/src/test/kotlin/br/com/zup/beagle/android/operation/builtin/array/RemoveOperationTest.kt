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

import br.com.zup.beagle.android.operation.OperationType
import org.json.JSONArray
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("Given Remove Operation")
internal class RemoveOperationTest {

    val removeOperation = RemoveOperation()

    @DisplayName("When remove one item")
    @Nested
    inner class RemoveItem {

        @Test
        @DisplayName("Then should has correct array")
        fun removeItemInArray() {
            // GIVEN
            val list = OperationType.TypeJsonArray(JSONArray(listOf(1, 2, 3, 2)))
            val item = OperationType.TypeNumber(2)

            // WHEN
            val result = removeOperation.execute(list, item)

            // THEN
            val expected = OperationType.TypeJsonArray(JSONArray(listOf(1, 3)))
            assertEquals(expected.toString(), result.toString())
        }
    }
}