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

package br.com.zup.beagle.android.operation.builtin.convert

import br.com.zup.beagle.android.operation.OperationType
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("Given Int Operation")
internal class IntOperationTest {
    val operationResolver = IntOperation()

    @DisplayName("When passing parameters")
    @Nested
    inner class IntConversion {

        @Test
        @DisplayName("Then should return correct value")
        fun checkConversion() {
            // GIVEN

            val operations = listOf<Any>(
                1,
                1.1,
                "1",
                "1.1",
                "string"
            )

            //When
            val result = operations.map {
                OperationType.TypeString(it.toString())}
                .map { operationResolver.execute(it) }

            // THEN
            val expected = listOf<Number?>(
                1,
                1,
                1,
                1,
                null
            )

            assertEquals(expected.map {
                it?.let { OperationType.TypeNumber(it) } ?: OperationType.Null
            }, result)
        }
    }

    @DisplayName("When execute operation with empty parameters")
    @Nested
    inner class ExceptionOperation {

        @Test
        @DisplayName("Then should throw exception")
        fun checkException() {
            // When
            val result = operationResolver.execute()

            // Then
            val expected = OperationType.Null


            assertEquals(expected, result)
        }

    }
}