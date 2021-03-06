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

import android.view.View
import br.com.zup.beagle.android.BaseTest
import br.com.zup.beagle.android.components.Text
import br.com.zup.beagle.android.components.layout.Container
import br.com.zup.beagle.android.context.constant
import br.com.zup.beagle.android.view.custom.InternalBeagleFlexView
import br.com.zup.beagle.android.view.viewmodel.GenerateIdViewModel
import br.com.zup.beagle.android.view.viewmodel.ListViewIdViewModel
import br.com.zup.beagle.android.widget.core.ServerDrivenComponent
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("Given a GenerateIdManager")
class GenerateIdManagerTest : BaseTest() {

    private val generateIdViewModel = mockk<GenerateIdViewModel>(relaxed = true)
    private val listViewIdViewModel = mockk<ListViewIdViewModel>(relaxed = true)
    private val view = mockk<InternalBeagleFlexView>()
    private val generatedId = 1
    private lateinit var generateIdManager: GenerateIdManager

    @BeforeEach
    fun clear() {
        clearMocks(generateIdViewModel,rootView)
        mockkStatic(View::class)
        every { View.generateViewId() } returns generatedId

        generateIdManager =
            GenerateIdManager(rootView, generateIdViewModel, listViewIdViewModel)
    }

    @DisplayName("When createSingleManagerByRootViewId is called")
    @Nested
    inner class CreateManager {

        @DisplayName("Then should call createIfNotExisting to a rootView with parentId")
        @Test
        fun createSingleManagerByRootViewId() {
            // Given
            val parentId = 10
            every { rootView.getParentId() } returns parentId

            // When
            generateIdManager.createSingleManagerByRootViewId()

            // Then
            verify(exactly = 1) { generateIdViewModel.createIfNotExisting(parentId) }
        }
    }

    @DisplayName("When onViewDetachedFromWindow is called")
    @Nested
    inner class SetViewCreated {

        @DisplayName("Then should call setViewCreated and prepareToReuseIds to a rootView with parentId")
        @Test
        fun setViewCreatedAndPrepareToReuseIds() {
            // Given
            val parentId = 10
            every { rootView.getParentId() } returns parentId

            // When
            generateIdManager.onViewDetachedFromWindow(view)

            // Then
            verify(exactly = 1) { generateIdViewModel.setViewCreated(parentId) }
            verify(exactly = 1) { listViewIdViewModel.prepareToReuseIds(view) }
        }
    }

    @DisplayName("When manageId is called")
    @Nested
    inner class ManageId {

        @DisplayName("Then should not generateViewId to a component it is not an IdentifierComponent")
        @Test
        fun notGenerateViewId() {
            // Given
            val component = mockk<ServerDrivenComponent>()

            // When
            generateIdManager.manageId(component, view)

            // Then
            verify(exactly = 0) { View.generateViewId() }
        }

        @DisplayName("Then should not generateViewId to a a IdentifierComponent with id")
        @Test
        fun notGenerateViewIdWithId() {
            // Given
            val component = Container(listOf()).apply {
                id = "stub"
            }
            every { view.isAutoGenerateIdEnabled() } returns true

            // When
            generateIdManager.manageId(component, view)

            // Then
            verify(exactly = 0) { View.generateViewId() }
        }

        @DisplayName("Then should getViewId to a IdentifierComponent without id and isAutoGenerateIdEnabled")
        @Test
        fun getViewId() {
            // Given
            val component = Container(listOf())
            every { view.isAutoGenerateIdEnabled() } returns true
            val recoveryId = 1
            every { generateIdViewModel.getViewId(any()) } returns recoveryId

            // When
            generateIdManager.manageId(component, view)

            // Then
            verify(exactly = 1) { generateIdViewModel.getViewId(rootView.getParentId()) }
            assertEquals(recoveryId.toString(), component.id)
        }

        @DisplayName("Then should try getViewId to a IdentifierComponent without id and isAutoGenerateIdEnabled")
        @Test
        fun tryGetViewId() {
            // Given
            val component = Container(listOf())
            every { view.isAutoGenerateIdEnabled() } returns true
            every { generateIdViewModel.getViewId(any()) } throws Exception()

            // When
            generateIdManager.manageId(component, view)

            // Then
            verify(exactly = 1) { View.generateViewId() }
            assertEquals(generatedId.toString(), component.id)
        }

        @DisplayName("Then should markEachNestedComponentAsNoIdIfNeeded to a IdentifierComponent without id and auto generate id disabled")
        @Test
        fun markEachNestedComponentAsNoIdIfNeeded() {
            // Given
            val componentWithoutId = Text(constant("stub"))
            val children = listOf<ServerDrivenComponent>(componentWithoutId)
            val component = Container(children)
            every { view.isAutoGenerateIdEnabled() } returns false

            // When
            generateIdManager.manageId(component, view)

            // Then
            assertEquals(COMPONENT_NO_ID, component.id)
            assertEquals(COMPONENT_NO_ID, componentWithoutId.id)
        }

        @DisplayName("Then should should keep previous ids to a IdentifierComponent without id and auto generate id disabled")
        @Test
        fun keepPreviousIds() {
            // Given
            val previousId = "id"
            val componentWithId = Text(constant("stub")).apply {
                id = previousId
            }
            val children = listOf<ServerDrivenComponent>(componentWithId)
            val component = Container(children)
            every { view.isAutoGenerateIdEnabled() } returns false

            // When
            generateIdManager.manageId(component, view)

            // Then
            assertEquals(COMPONENT_NO_ID, component.id)
            assertEquals(previousId, componentWithId.id)
        }
    }
}
