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

package br.com.zup.beagle.android.view.viewmodel

import android.view.View
import br.com.zup.beagle.android.BaseConfigurationTest
import br.com.zup.beagle.android.action.SetContextInternal
import br.com.zup.beagle.android.context.Bind
import br.com.zup.beagle.android.context.ContextData
import br.com.zup.beagle.android.context.ContextDataEvaluation
import br.com.zup.beagle.android.context.ContextDataManager
import br.com.zup.beagle.android.context.ImplicitContextManager
import br.com.zup.beagle.android.context.InternalContextObserver
import br.com.zup.beagle.android.utils.Observer
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("Given a ScreenContextViewModel")
class ScreenContextViewModelTest : BaseConfigurationTest() {

    private val contextDataManager = mockk<ContextDataManager>(relaxed = true)
    private val contextDataEvaluation = mockk<ContextDataEvaluation>(relaxed = true)
    private val implicitContextManager = mockk<ImplicitContextManager>(relaxed = true)
    val view = mockk<View>()

    private lateinit var screenContextViewModel: ScreenContextViewModel

    @BeforeEach
    fun clear() {
        screenContextViewModel = ScreenContextViewModel(beagleConfigurator,
            contextDataManager,
            contextDataEvaluation,
            implicitContextManager)
    }

    @Test
    fun `GIVEN ScreenContextViewModel WHEN setIdToViewWithContext THEN should call setIdToViewWithContext`() {
        // When
        screenContextViewModel.setIdToViewWithContext(view)

        // Then
        verify(exactly = 1) { contextDataManager.setIdToViewWithContext(view) }
    }

    @Test
    fun `GIVEN ScreenContextViewModel WHEN addContext with default shouldOverrideExistingContext THEN should call addContext`() {
        // Given
        val contextData = mockk<ContextData>()

        // When
        screenContextViewModel.addContext(view, contextData)

        // Then
        verify(exactly = 1) { contextDataManager.addContext(view, contextData, false) }
    }

    @Test
    fun `GIVEN ScreenContextViewModel WHEN addContext with shouldOverrideExistingContext THEN should call addContext`() {
        // Given
        val contextData = mockk<ContextData>()
        val shouldOverrideExistingContext = true

        // When
        screenContextViewModel.addContext(view, contextData, shouldOverrideExistingContext)

        // Then
        verify(exactly = 1) { contextDataManager.addContext(view, contextData, shouldOverrideExistingContext) }
    }

    @Test
    fun `GIVEN ScreenContextViewModel WHEN restoreContext THEN should call restoreContext`() {
        // When
        screenContextViewModel.restoreContext(view)

        // Then
        verify(exactly = 1) { contextDataManager.restoreContext(view) }
    }

    @Test
    fun `GIVEN ScreenContextViewModel WHEN getContextData THEN should call getContextData`() {
        // When
        screenContextViewModel.getListContextData(view)

        // Then
        verify(exactly = 1) { contextDataManager.getListContextData(view) }
    }

    @Test
    fun `GIVEN ScreenContextViewModel WHEN updateContext THEN should call updateContext`() {
        // Given
        val setContextInternal = mockk<SetContextInternal>()

        // When
        screenContextViewModel.updateContext(view, setContextInternal)

        // Then
        verify(exactly = 1) { contextDataManager.updateContext(view, setContextInternal) }
    }

    @Test
    fun `GIVEN ScreenContextViewModel WHEN onViewIdChanged THEN should call onViewIdChanged`() {
        // Given
        val oldId = 0
        val newId = 1

        // When
        screenContextViewModel.onViewIdChanged(oldId, newId, view)

        // Then
        verify(exactly = 1) { contextDataManager.onViewIdChanged(oldId, newId, view) }
    }

    @Test
    fun `GIVEN ScreenContextViewModel WHEN addBindingToContext THEN should call addBinding`() {
        // Given
        val bind = mockk<Bind.Expression<String>>()
        val observer = mockk<Observer<String?>>()

        // When
        screenContextViewModel.addBindingToContext(view, bind, observer)

        // Then
        verify(exactly = 1) { contextDataManager.addBinding(view, bind, observer) }
    }

    @Test
    fun `GIVEN ScreenContextViewModel WHEN linkBindingToContextAndEvaluateThem THEN should call linkBindingToContextAndEvaluateThem`() {
        // When
        screenContextViewModel.linkBindingToContextAndEvaluateThem(view)

        // Then
        verify(exactly = 1) { contextDataManager.linkBindingToContextAndEvaluateThem(view) }
    }

    @Test
    fun `GIVEN ScreenContextViewModel WHEN addImplicitContext THEN should call addImplicitContext`() {
        // Given
        val context = mockk<ContextData>()
        val sender = mockk<View>()
        val originView = mockk<View>()

        // When
        screenContextViewModel.addImplicitContext(context, sender, originView)

        // Then
        verify(exactly = 1) { implicitContextManager.addImplicitContext(context, sender, originView) }
    }

    @Test
    fun `GIVEN ScreenContextViewModel WHEN evaluateExpressionForImplicitContext THEN should use both context and evaluate`() {
        // Given
        val originView = mockk<View>()
        val bind = mockk<Bind.Expression<String>>()
        val implicitContexts = listOf<ContextData>()
        val contexts = listOf<ContextData>()
        every { implicitContextManager.getImplicitContextForView(originView) } returns implicitContexts
        every { contextDataManager.getContextsFromBind(originView, bind) } returns contexts

        // When
        screenContextViewModel.evaluateExpressionForImplicitContext(originView, bind)

        // Then
        verify(exactly = 1) { contextDataEvaluation.evaluateBindExpression(contexts, bind) }
    }

    @Test
    fun `GIVEN ScreenContextViewModel WHEN evaluateExpressionForGivenContext THEN should use both context and evaluate`() {
        // Given
        val originView = mockk<View>()
        val bind = mockk<Bind.Expression<String>>()
        val givenContext = ContextData(id = "context", value = "")
        val contexts = listOf(ContextData(id = "contextInViewTree", value = ""))
        val expectedContexts = contexts + givenContext
        every { contextDataManager.getContextsFromBind(originView, bind) } returns contexts

        // When
        screenContextViewModel.evaluateExpressionForGivenContext(originView, givenContext, bind)

        // Then
        verify(exactly = 1) { contextDataEvaluation.evaluateBindExpression(expectedContexts, bind) }
    }

    @Test
    fun `GIVEN ScreenContextViewModel WHEN clearContexts THEN should call clearContexts`() {
        // When
        screenContextViewModel.clearContexts()

        // Then
        verify(exactly = 1) { contextDataManager.clearContexts() }
    }

    @Test
    fun `GIVEN ScreenContextViewModel WHEN addContextObserver THEN should call addContextObserver`() {
        //Given
        val contextId = "contextId"
        val contextObserver: InternalContextObserver = {

        }

        // When
        screenContextViewModel.addContextObserver("contextId", contextObserver)

        // Then
        verify(exactly = 1) { contextDataManager.addContextObserver(contextId, contextObserver) }
    }

    @Test
    fun `GIVEN ScreenContextViewModel WHEN removeContextObserver THEN should call removeContextObserver`() {
        //Given
        val contextId = "contextId"

        // When
        screenContextViewModel.removeContextObserver(contextId)

        // Then
        verify(exactly = 1) { contextDataManager.removeContextObserver(contextId) }
    }

    @DisplayName("When tryLinkContextInBindWithoutContext is called")
    @Nested
    inner class ContextInBindWithoutContextTest {

        @DisplayName("Then should call manager")
        @Test
        fun testTryLinkContextInBindWithoutContext() {

            // When
            screenContextViewModel.tryLinkContextInBindWithoutContext(view)

            // Then
            verify(exactly = 1) { contextDataManager.tryLinkContextInBindWithoutContext(view) }
        }
    }

    @DisplayName("When getContextData is called")
    @Nested
    inner class GetContextDataTest {

        @DisplayName("Then should call manager")
        @Test
        fun testGetContextData() {

            // When
            screenContextViewModel.getContextData("context")

            // Then
            verify(exactly = 1) { contextDataManager.getContextData("context") }
        }
    }

    @DisplayName("When addContext with default shouldOverrideExistingContext and context list")
    @Nested
    inner class AddContextListTest {

        @DisplayName("Then should call manager")
        @Test
        fun testAddContextList() {
            // Given
            val contextData = mockk<ContextData>()

            // When
            screenContextViewModel.addContext(view, listOf(contextData))

            // Then
            verify(exactly = 1) { contextDataManager.addContext(view, listOf(contextData), false) }
        }
    }

    @DisplayName("When addContext with shouldOverrideExistingContext and context list")
    @Nested
    inner class AddContextListWithShouldOverrideExistingContextTest {

        @DisplayName("Then should call manager")
        @Test
        fun testAddContextListWithShouldOverrideExistingContext() {
            // Given
            val contextData = mockk<ContextData>()

            // When
            screenContextViewModel.addContext(view, listOf(contextData), true)

            // Then
            verify(exactly = 1) { contextDataManager.addContext(view, listOf(contextData), true) }
        }
    }

}
