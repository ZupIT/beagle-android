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

import br.com.zup.beagle.android.operation.OperationType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("Given Sum Operation")
internal class SumOperationTest {

    val sumOperation = SumOperation()

    @DisplayName("When passing parameters")
    @Nested
    inner class Sum {

        @Test
        @DisplayName("Then should return correct value")
        fun checkSubtractDoubleCorrect() {
            // GIVEN
            val inputOne = OperationType.TypeNumber(2.0)
            val inputTwo = OperationType.TypeNumber(2.0)

            // WHEN
            val result = sumOperation.execute(inputOne, inputTwo)

            // THEN
            val expected = OperationType.TypeNumber(4.0)

            assertEquals(expected, result)
        }

        @Test
        @DisplayName("Then should return correct value")
        fun checkMultipleIntegerCorrect() {
            // GIVEN
            val inputOne = OperationType.TypeNumber(2)
            val inputTwo = OperationType.TypeNumber(2)

            // WHEN
            val result = sumOperation.execute(inputOne, inputTwo)

            // THEN
            val expected = OperationType.TypeNumber(4)

            assertEquals(expected, result)
        }

        @Test
        @DisplayName("Then should return correct value")
        fun checkCoercion() {
            // GIVEN
            val operationResolver = SumOperation()

            val operations = listOf<Pair<Any, Any>>(
                6 to 4, 4.5 to 6, 4.5 to 4.5, 6 to 4.5,
                1 to 1.5, 2.0 to 1, "1" to 1.0, 2.5 to "1.0", "1" to "1", "2" to 1,
                1 to true, "1" to false, "" to ""
            )

            //When
            val result = operations.map {
                OperationType.TypeString(it.first.toString()) to OperationType.TypeString(it.second.toString()) }
                .map { operationResolver.execute(it.first, it.second) }

            // THEN
            val expected = listOf<Number?>(10, 10.5, 9.0, 10.5, 2.5, 3.0, 2.0, 3.5, 2, 3, null, null, null)

            assertEquals(expected.map { it?.let { OperationType.TypeNumber(it) } ?: OperationType.Null }, result)
        }
    }

    @DisplayName("When execute operation with empty parameters")
    @Nested
    inner class ExceptionOperation {

        @Test
        @DisplayName("Then should throw exception")
        fun checkException() {
            // WHEN THEN
            assertThrows<ArrayIndexOutOfBoundsException> {
                sumOperation.execute()
            }
        }

    }
}