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

package br.com.zup.beagle.android.engine.renderer

import android.view.View
import br.com.zup.beagle.android.BaseTest
import br.com.zup.beagle.android.components.utils.ComponentStylization
import br.com.zup.beagle.android.context.ContextComponentHandler
import br.com.zup.beagle.android.testutil.RandomData
import br.com.zup.beagle.android.utils.generateViewModelInstance
import br.com.zup.beagle.android.view.viewmodel.GenerateIdViewModel
import br.com.zup.beagle.android.view.viewmodel.ScreenContextViewModel
import br.com.zup.beagle.android.widget.RootView
import br.com.zup.beagle.android.widget.Widget
import io.mockk.Runs
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

private open class AbstractViewRenderer(
    override val component: Widget,
    componentStylization: ComponentStylization<Widget>,
    contextComponentHandler: ContextComponentHandler,
) : ViewRenderer<Widget>(componentStylization, contextComponentHandler) {
    override fun buildView(rootView: RootView): View {
        return mockk()
    }
}

class AbstractViewRendererTest : BaseTest() {

    private val contextViewModel = mockk<ScreenContextViewModel>(relaxed = true)
    private val generateIdViewModel = mockk<GenerateIdViewModel>()
    private val component = mockk<Widget>(relaxed = true)
    private val componentStylization = mockk<ComponentStylization<Widget>>(relaxed = true)
    private val contextViewRenderer = mockk<ContextComponentHandler>(relaxed = true)

    private lateinit var viewRenderer: AbstractViewRenderer

    @BeforeAll
    override fun setUp() {
        super.setUp()

        prepareViewModelMock(generateIdViewModel, contextViewModel)

        viewRenderer = spyk(
            AbstractViewRenderer(
                component,
                componentStylization,
                contextViewRenderer
            )
        )
    }

    @BeforeEach
    fun clear() {
        clearMocks(
            rootView,
            componentStylization,
            contextViewRenderer,
            answers = false
        )
    }

    @Test
    fun build_should_call_contextViewRenderer_and_componentStylization() {
        // Given
        val viewId = RandomData.int()
        val view = mockk<View>()
        every { viewRenderer.buildView(any()) } returns view
        every { generateIdViewModel.getViewId(rootView.getParentId()) } returns viewId
        every { view.id } returns View.NO_ID
        every { view.id = any() } just Runs

        // When
        viewRenderer.build(rootView)

        // Then
        verify {
            rootView.generateViewModelInstance<ScreenContextViewModel>(
                ScreenContextViewModel.provideFactory(rootView.getBeagleConfigurator())
            )
            componentStylization.apply(rootView, view, component)
            contextViewRenderer.handleComponent(view, contextViewModel, component)
        }
    }

    @Test
    fun build_should_not_generate_id_for_view() {
        // Given
        val view = mockk<View>()
        every { viewRenderer.buildView(any()) } returns view
        every { view.id } returns RandomData.int()
        every { view.addOnAttachStateChangeListener(any()) } just Runs

        // When
        viewRenderer.build(rootView)

        // Then
        verify(exactly = 0) { view.id = any() }
    }
}
