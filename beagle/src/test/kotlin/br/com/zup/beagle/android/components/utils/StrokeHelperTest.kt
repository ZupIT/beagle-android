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

package br.com.zup.beagle.android.components.utils

import android.util.DisplayMetrics
import android.view.View
import br.com.zup.beagle.android.setup.BeagleEnvironment
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.mockk.verify
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Given a StrokeHelper")
class StrokeHelperTest {

    private val view: View = mockk(relaxUnitFun = true, relaxed = true)

    private val borderColor = "#FFF000"
    private val borderWidth = 2.0

    @BeforeAll
    fun setUp() {
        mockkObject(BeagleEnvironment)
        every { BeagleEnvironment.application } returns mockk() {
            every { resources } returns mockk {
                every { displayMetrics } returns DisplayMetrics().apply {
                    density = 1f
                }
            }
        }
    }

    @AfterAll
    fun tearDown() {
        unmockkObject(BeagleEnvironment)
    }

    @Nested
    @DisplayName("When applyStroke is called with null values")
    inner class ApplyStrokeNullValues {

        @Test
        @DisplayName("Then should not requestLayout or and set background on view")
        fun testNullValues() {
            // Given
            val subject = StrokeHelper()

            // When
            subject.applyStroke(view)

            // Then
            verify(exactly = 0) { view.requestLayout() }
            verify(exactly = 0) { view.background }
        }

        @Test
        @DisplayName("With borderColor null then should not requestLayout or and set background on view")
        fun testBorderColorNullValues() {
            // Given
            val subject = StrokeHelper(borderWidth = borderWidth)

            // When
            subject.applyStroke(view)

            // Then
            verify(exactly = 0) { view.requestLayout() }
            verify(exactly = 0) { view.background }
        }


        @Test
        @DisplayName("With borderWidth null then should not requestLayout or and set background on view")
        fun testBorderWidthNullValues() {
            // Given
            val subject = StrokeHelper(borderColor = borderColor)

            // When
            subject.applyStroke(view)

            // Then
            verify(exactly = 0) { view.requestLayout() }
            verify(exactly = 0) { view.background }
        }

    }

    @Nested
    @DisplayName("When applyStroke is called with borderColor and borderWidth")
    inner class ApplyStrokeWithValues {

        @Test
        @DisplayName("Then should requestLayout and set background on view")
        fun testWithValues() {
            // Given
            val subject = StrokeHelper(borderColor = borderColor, borderWidth = borderWidth)

            // When
            subject.applyStroke(view)

            // Then
            verify(exactly = 1) { view.background }
            verify(exactly = 1) { view.requestLayout() }
        }
    }
}
