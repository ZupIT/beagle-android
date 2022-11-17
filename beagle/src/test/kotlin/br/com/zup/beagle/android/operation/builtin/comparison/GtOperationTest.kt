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

@DisplayName("Given Gt Operation")
internal class GtOperationTest {

    val gteOperation = GtOperation()

    @DisplayName("When execute operation with integer numbers")
    @Nested
    inner class IntegerNumbers {

        @Test
        @DisplayName("Then should return true")
        fun checkGreaterThanInteger() {
            // GIVEN
            val inputOne = OperationType.TypeNumber(2)
            val inputTwo = OperationType.TypeNumber(1)

            // WHEN
            val result = gteOperation.execute(inputOne, inputTwo)

            // THEN
            val expected = OperationType.TypeBoolean(true)
            assertEquals(expected, result)
        }

        @Test
        @DisplayName("Then should return false")
        fun checkEqualIntegerReturnFalse() {
            // GIVEN
            val inputOne = OperationType.TypeNumber(1)
            val inputTwo = OperationType.TypeNumber(1)

            // WHEN
            val result = gteOperation.execute(inputOne, inputTwo)

            // THEN
            val expected = OperationType.TypeBoolean(false)
            assertEquals(expected, result)
        }

        @Test
        @DisplayName("Then should return false")
        fun checkNotEqualToInteger() {
            // GIVEN
            val inputOne = OperationType.TypeNumber(1)
            val inputTwo = OperationType.TypeNumber(2)

            // WHEN
            val result = gteOperation.execute(inputOne, inputTwo)

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
        fun checkGreaterThanDouble() {
            // GIVEN
            val inputOne = OperationType.TypeNumber(2.0)
            val inputTwo = OperationType.TypeNumber(1.0)

            // WHEN
            val result = gteOperation.execute(inputOne, inputTwo)

            // THEN
            val expected = OperationType.TypeBoolean(true)
            assertEquals(expected, result)
        }

        @Test
        @DisplayName("Then should return false")
        fun checkEqualDoubleReturnFalse() {
            // GIVEN
            val inputOne = OperationType.TypeNumber(1.0)
            val inputTwo = OperationType.TypeNumber(1.0)

            // WHEN
            val result = gteOperation.execute(inputOne, inputTwo)

            // THEN
            val expected = OperationType.TypeBoolean(false)
            assertEquals(expected, result)
        }

        @Test
        @DisplayName("Then should return false")
        fun checkNotEqualToDouble() {
            // GIVEN
            val inputOne = OperationType.TypeNumber(1.0)
            val inputTwo = OperationType.TypeNumber(2.0)

            // WHEN
            val result = gteOperation.execute(inputOne, inputTwo)

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
            val result = gteOperation.execute()

            // THEN
            val expected = OperationType.Null
            assertEquals(expected, result)
        }

    }

    @DisplayName("When execute operation with type coertion parameters")
    @Nested
    inner class TypeCoertionOperation {

        @Test
        @DisplayName("Then should return true")
        fun checkGt() {
            //Given
            val operationResolver = GtOperation()
            val operations = listOf<Pair<OperationType, OperationType>>(
                OperationType.TypeAny( 2) to OperationType.TypeAny( 1),
                OperationType.TypeAny( 1) to OperationType.TypeAny( 1),
                OperationType.TypeAny( 1) to OperationType.TypeAny( 2),
                OperationType.TypeAny( 2.0) to OperationType.TypeAny( 1.0),
                OperationType.TypeAny( 2.0) to OperationType.TypeAny( 1),
                OperationType.TypeAny( 1.0) to OperationType.TypeAny( 1),
                OperationType.TypeAny( "2") to OperationType.TypeAny( 1.0),
                OperationType.TypeAny( "2") to OperationType.TypeAny( 1),
                OperationType.TypeAny( "2") to OperationType.TypeAny( "1"),
                OperationType.TypeAny( "1") to OperationType.TypeAny( "1"),
                OperationType.TypeAny( "1") to OperationType.TypeAny( "1.0"),
                OperationType.TypeAny( "1.0") to OperationType.TypeAny( 2.0),
                OperationType.TypeAny( "1.0") to OperationType.TypeAny( "2.0"),
                OperationType.TypeAny( "true") to OperationType.TypeAny( 2),
            )

            //When
            val result = operations.map { operationResolver.execute(it.first, it.second) }

            //Then
            val expected = listOf(
                OperationType.TypeBoolean(true),
                OperationType.TypeBoolean(false),
                OperationType.TypeBoolean(false),
                OperationType.TypeBoolean(true),
                OperationType.TypeBoolean(true),
                OperationType.TypeBoolean(false),
                OperationType.TypeBoolean(true),
                OperationType.TypeBoolean(true),
                OperationType.TypeBoolean(true),
                OperationType.TypeBoolean(false),
                OperationType.TypeBoolean(false),
                OperationType.TypeBoolean(false),
                OperationType.TypeBoolean(false),
                OperationType.TypeBoolean(false)
            )

            assertEquals(expected, result)
        }
    }

}