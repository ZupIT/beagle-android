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
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("Given Lte Operation")
internal class LteOperationTest {

    val lteOperation = LteOperation()

    @DisplayName("When execute operation with integer numbers")
    @Nested
    inner class IntegerNumbers {

        @Test
        @DisplayName("Then should return true")
        fun checkLessThanInteger() {
            // GIVEN
            val inputOne = OperationType.TypeNumber(1)
            val inputTwo = OperationType.TypeNumber(2)

            // WHEN
            val result = lteOperation.execute(inputOne, inputTwo)

            // THEN
            val expected = OperationType.TypeBoolean(true)
            assertEquals(expected, result)
        }

        @Test
        @DisplayName("Then should return true")
        fun checkLessThanEqualInteger() {
            // GIVEN
            val inputOne = OperationType.TypeNumber(1)
            val inputTwo = OperationType.TypeNumber(1)

            // WHEN
            val result = lteOperation.execute(inputOne, inputTwo)

            // THEN
            val expected = OperationType.TypeBoolean(true)
            assertEquals(expected, result)
        }

        @Test
        @DisplayName("Then should return false")
        fun checkNotLessThanInteger() {
            // GIVEN
            val inputOne = OperationType.TypeNumber(2)
            val inputTwo = OperationType.TypeNumber(1)

            // WHEN
            val result = lteOperation.execute(inputOne, inputTwo)

            // THEN
            val expected = OperationType.TypeBoolean(false)
            assertEquals(expected, result)
        }
    }

    @DisplayName("When execute operation with double numbers")
    @Nested
    inner class DoubleNumbers {

        @Test
        @DisplayName("Then should return true")
        fun checkLessThanDouble() {
            // GIVEN
            val inputOne = OperationType.TypeNumber(1.0)
            val inputTwo = OperationType.TypeNumber(2.0)

            // WHEN
            val result = lteOperation.execute(inputOne, inputTwo)

            // THEN
            val expected = OperationType.TypeBoolean(true)
            assertEquals(expected, result)
        }

        @Test
        @DisplayName("Then should return true")
        fun checkLessThanEqualDouble() {
            // GIVEN
            val inputOne = OperationType.TypeNumber(1.0)
            val inputTwo = OperationType.TypeNumber(1.0)

            // WHEN
            val result = lteOperation.execute(inputOne, inputTwo)

            // THEN
            val expected = OperationType.TypeBoolean(true)
            assertEquals(expected, result)
        }

        @Test
        @DisplayName("Then should return false")
        fun checkNotLessThanDouble() {
            // GIVEN
            val inputOne = OperationType.TypeNumber(2.0)
            val inputTwo = OperationType.TypeNumber(1.0)

            // WHEN
            val result = lteOperation.execute(inputOne, inputTwo)

            // THEN
            val expected = OperationType.TypeBoolean(false)
            assertEquals(expected, result)
        }
    }


    @DisplayName("When execute operation with empty parameters")
    @Nested
    inner class NullOperation {

        @Test
        @DisplayName("Then should return null")
        fun checkNull() {
            // WHEN THEN
            // WHEN
            val result = lteOperation.execute()

            // THEN
            val expected = OperationType.Null
            assertEquals(expected, result)
        }

    }

    @DisplayName("When execute operation with type coercion parameters")
    @Nested
    inner class TypeCoercionLteOperation {

        @Test
        @DisplayName("Then should return as expected")
        fun checkLte() {
            //Given
            val operationResolver = LteOperation()
            val operations = listOf<Pair<Any, Any>>(
                2 to 1,
                1 to 1,
                1 to 2,
                2.0 to 1.0,
                2.0 to 1,
                1.0 to 1,
                1.0 to 2,
                "2" to 1.0,
                "2" to 1,
                "2" to "1",
                "1" to "1",
                "1" to "1.0",
                "1.0" to 2.0,
                "1.0" to "2.0",
                "true" to 2,
            )

            //When
            val result = operations.map {
                OperationType.TypeString(it.first.toString()) to OperationType.TypeString(it.second.toString()) }
                .map { operationResolver.execute(it.first, it.second) }

            //Then
            val expected = listOf(
                false,
                true,
                true,
                false,
                false,
                true,
                true,
                false,
                false,
                false,
                true,
                true,
                true,
                true,
                false
            )

            assertEquals(expected, result.map { it.value })
        }
    }
}