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

package br.com.zup.beagle.android.components.layout

import android.graphics.Color
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import br.com.zup.beagle.android.components.BaseComponentTest
import br.com.zup.beagle.android.utils.ToolbarManager
import br.com.zup.beagle.android.view.BeagleActivity
import br.com.zup.beagle.android.view.ViewFactory
import br.com.zup.beagle.android.view.custom.BeagleFlexView
import br.com.zup.beagle.android.widget.core.ServerDrivenComponent
import br.com.zup.beagle.android.widget.core.Style
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.verify
import io.mockk.verifyOrder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

private const val DEFAULT_COLOR = 0xFFFFFF

class ScreenTest : BaseComponentTest() {

    private val context: BeagleActivity = mockk(relaxed = true)

    private val component: ServerDrivenComponent = mockk()

    private lateinit var screen: Screen

    @BeforeAll
    override fun setUp() {
        super.setUp()

        mockkStatic(Color::class)

        every { Color.parseColor(any()) } returns DEFAULT_COLOR
        every { rootView.getContext() } returns context
    }

    @BeforeEach
    fun clear() {
        screen = Screen(navigationBar = null, child = component)
    }

    @Test
    fun build_should_create_a_screenWidget_with_grow_1_and_justifyContent_SPACE_BETWEEN() {
        // Given
        val style = slot<Style>()
        every { ViewFactory.makeBeagleFlexView(any(), capture(style)) } returns beagleFlexView
        every { context.supportActionBar } returns null

        // When
        screen.buildView(rootView)

        // Then
        assertEquals(1.0, style.captured.flex?.grow)
    }

    @Test
    fun build_should_call_content_builder() {
        // Given
        val style = slot<Style>()
        every { beagleFlexView.addView(view, capture(style)) } just Runs
        every { context.supportActionBar } returns null

        // When
        screen.buildView(rootView)

        // Then
        verify(atLeast = 1) { beagleFlexView.addView(component) }
    }

    @Test
    fun build_should_hideNavigationBar_when_navigationBar_is_null() {
        // GIVEN
        val toolbar: Toolbar = mockk(relaxed = true)
        val actionBar: ActionBar = mockk(relaxed = true)

        every { context.supportActionBar } returns actionBar
        every { context.getToolbar() } returns toolbar
        val expected = View.GONE
        every { toolbar.visibility = any() } just Runs
        every { toolbar.visibility } returns expected

        // WHEN
        screen.buildView(rootView)

        // THEN
        assertEquals(expected, toolbar.visibility)
        verify(atLeast = 1) { actionBar.hide() }
    }

    @Test
    fun should_keep_window_attach_callbacks_null_when_screen_event_not_presented() {
        // When
        val view = screen.buildView(rootView)

        // Then
        assertTrue(view is BeagleFlexView)
        verify(exactly = 0) { view.addOnAttachStateChangeListener(any()) }
    }

    @Test
    fun buildView_should_call_configureToolbar_before_configureNavigationBarForScreen() {
        //GIVEN
        val navigationBar = NavigationBar("Stub")

        mockkConstructor(ToolbarManager::class)

        every {
            anyConstructed<ToolbarManager>().configureToolbar(
                any(),
                any(),
                any(),
                any()
            )
        } just Runs

        every {
            anyConstructed<ToolbarManager>().configureNavigationBarForScreen(
                any(),
                any(),
            )
        } just Runs

        screen = Screen(child = screen, navigationBar = navigationBar)

        //WHEN
        screen.buildView(rootView)

        //THEN
        verifyOrder {
            anyConstructed<ToolbarManager>().configureNavigationBarForScreen(context, navigationBar)
            anyConstructed<ToolbarManager>().configureToolbar(
                rootView,
                navigationBar,
                any(),
                screen
            )
        }
    }
}
