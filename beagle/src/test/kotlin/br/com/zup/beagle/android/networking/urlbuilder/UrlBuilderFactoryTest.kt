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

package br.com.zup.beagle.android.networking.urlbuilder

import br.com.zup.beagle.android.BaseConfigurationTest
import io.mockk.every
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class UrlBuilderFactoryTest : BaseConfigurationTest() {

    private val urlBuilderFactory = UrlBuilderFactory()

    @Test
    fun make_should_return_custom_dispatcher() {
        // Given
        every { beagleConfigurator.urlBuilder } returns urlBuilder

        // When
        val actual = urlBuilderFactory.make(beagleConfigurator)

        // Then
        assertEquals(urlBuilder, actual)
    }
}