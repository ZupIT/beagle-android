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


package br.com.zup.beagle.android.engine.renderer.ui

import android.content.Context
import android.view.View
import br.com.zup.beagle.android.BaseTest
import br.com.zup.beagle.android.engine.renderer.ViewConvertableRenderer
import br.com.zup.beagle.android.widget.WidgetView
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class ViewConvertableRendererTest : BaseTest() {

    private val widget: WidgetView = mockk(relaxed = true)

    private val context: Context = mockk()

    private val view: View = mockk(relaxed = true)

    private lateinit var viewConvertableRenderer: ViewConvertableRenderer

    @BeforeAll
    override fun setUp() {
        super.setUp()

        every { rootView.getContext() } returns context

        viewConvertableRenderer = ViewConvertableRenderer(widget)
    }

    @Test
    fun build_should_make_a_native_view() {
        // Given
        every { widget.buildView(rootView) } returns view

        // When
        val actual = viewConvertableRenderer.buildView(rootView)

        // Then
        assertEquals(view, actual)
    }
}

