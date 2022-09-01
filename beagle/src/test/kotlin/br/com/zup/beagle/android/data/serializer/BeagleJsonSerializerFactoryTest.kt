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

package br.com.zup.beagle.android.data.serializer

import br.com.zup.beagle.android.BaseTest
import com.squareup.moshi.Moshi
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("Given a BeagleJsonSerializerFactory")
class BeagleJsonSerializerFactoryTest : BaseTest() {

    @DisplayName("When try to create jsonSerializer")
    @Nested
    inner class SerializeTest {

        @DisplayName("Then should return the correct implementation of moshi")
        @Test
        fun testCreateObjectWithMoshi() {
            //Given
            val beagleMoshi: Moshi = mockk()

            // When
            val actual = BeagleJsonSerializerFactory.create(beagleMoshi)

            // Then
            assertTrue(actual is BeagleSerializer)
            assertEquals((actual as BeagleSerializer).moshi, beagleMoshi)
        }
    }
}
