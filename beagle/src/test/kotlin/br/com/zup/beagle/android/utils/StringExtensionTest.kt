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

package br.com.zup.beagle.android.utils

import org.junit.Assert.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested

import org.junit.jupiter.api.Test

@DisplayName("Given a String")
class StringExtensionTest {

    @DisplayName("When call to android id")
    @Nested
    inner class AndroidIdTest {

        @DisplayName("Then should return a correct hash")
        @Test
        fun testAndroidId() {
            // Given
            val myId = "text"

            // When
            val result = myId.toAndroidId()

            // Then
            assertEquals(3556653, result.toLong())
        }

    }

}
