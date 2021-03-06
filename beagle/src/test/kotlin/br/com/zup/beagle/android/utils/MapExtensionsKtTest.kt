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

import br.com.zup.beagle.android.testutil.RandomData
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class MapExtensionsKtTest {

    @Test
    fun toLowerKeys_should_return_map_keys_as_lowerCase() {
        // Given
        val headers = mapOf(
            "aaa" to RandomData.string(),
            "AsA" to RandomData.string(),
            "BBB" to RandomData.string()
        )

        // When
        val keysLower = headers.toLowerKeys()

        // Then
        val keys = keysLower.keys.toList()
        assertEquals("aaa", keys[0])
        assertEquals("asa", keys[1])
        assertEquals("bbb", keys[2])
    }
}