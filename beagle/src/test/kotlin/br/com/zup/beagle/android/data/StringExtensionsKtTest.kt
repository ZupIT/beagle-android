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

package br.com.zup.beagle.android.data

import br.com.zup.beagle.android.BaseConfigurationTest
import br.com.zup.beagle.android.networking.RequestData
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.Assert.assertEquals
import org.junit.jupiter.api.Test

class StringExtensionsKtTest : BaseConfigurationTest() {

    @Test
    fun `should return request data when using extension function to mapper`() {
        // Given
        mockkStatic("br.com.zup.beagle.android.data.StringExtensionsKt")
        every { any<String>().formatUrl(any(), any()) } returns ""

        // When
        val requestData = "".toRequestData(beagleConfigurator, urlBuilder)

        // Then
        val expectedResult = RequestData(url = "")
        assertEquals(expectedResult, requestData)
    }

    @Test
    fun `should return new url when using extension function to format`() {
        // Given
        every { urlBuilder.format(any(), "") } returns ""

        // When
        val requestData = "".formatUrl(beagleConfigurator, urlBuilder)

        // Then
        assertEquals("", requestData)
    }


}