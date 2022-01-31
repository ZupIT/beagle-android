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

package br.com.zup.beagle.android.action

import android.view.View
import br.com.zup.beagle.android.BaseTest
import br.com.zup.beagle.android.logger.BeagleLoggerProxy
import br.com.zup.beagle.android.testutil.RandomData
import br.com.zup.beagle.android.utils.evaluateExpression
import br.com.zup.beagle.android.view.viewmodel.ScreenContextViewModel
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class SetContextTest : BaseTest() {

    private val viewModel = mockk<ScreenContextViewModel>()
    private val view = mockk<View>()

    @BeforeAll
    override fun setUp() {
        super.setUp()

        mockkObject(BeagleLoggerProxy)
        mockkStatic("br.com.zup.beagle.android.utils.ActionExtensionsKt")

        prepareViewModelMock(viewModel)
    }

    @Test
    fun execute_should_call_viewModel_updateContext() {
        // Given
        val evaluated = RandomData.string()
        val setContext = SetContext(
            contextId = RandomData.string(),
            value = ""
        )
        val updateContext = slot<SetContextInternal>()
        every { setContext.evaluateExpression(any(), view, any<Any>()) } returns evaluated
        every { viewModel.updateContext(view, capture(updateContext)) } just Runs

        // When
        setContext.execute(rootView, view)

        // Then
        assertEquals(setContext.contextId, updateContext.captured.contextId)
        assertEquals(evaluated, updateContext.captured.value)
    }

    @Test
    fun execute_should_not_call_viewModel_updateContext_when_evaluate_returns_null() {
        // Given
        val setContext = SetContext(
            contextId = RandomData.string(),
            value = ""
        )
        every { setContext.evaluateExpression(any(), view, any<Any>()) } returns null
        every { BeagleLoggerProxy.warning(any()) } just Runs


        // When
        setContext.execute(rootView, view)

        // Then
        verify(exactly = 0) { viewModel.updateContext(view, any()) }
        verify { BeagleLoggerProxy.warning("SetContext with id=${setContext.contextId} evaluated to null") }
    }
}