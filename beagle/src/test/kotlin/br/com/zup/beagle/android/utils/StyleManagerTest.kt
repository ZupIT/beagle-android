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

import android.content.Context
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.View
import br.com.zup.beagle.R
import br.com.zup.beagle.android.BaseConfigurationTest
import br.com.zup.beagle.android.components.Button
import br.com.zup.beagle.android.components.Text
import br.com.zup.beagle.android.components.layout.Container
import br.com.zup.beagle.android.context.constant
import br.com.zup.beagle.android.setup.DesignSystem
import br.com.zup.beagle.android.widget.core.StyleComponent
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class StyleManagerTest : BaseConfigurationTest() {

    private lateinit var styleManager: StyleManager

    private val view: View = mockk(relaxed = true)

    private val typedArray: TypedArray = mockk(relaxed = true)

    private val context: Context = mockk(relaxed = true)

    private val serverDrivenComponent: StyleComponent = mockk(relaxed = true)

    private val designSystem: DesignSystem = mockk(relaxed = true)

    private val colorDrawable: ColorDrawable = mockk(relaxed = true)

    private val drawable: Drawable = mockk(relaxed = true)

    private val typedValue: TypedValue = mockk(relaxed = true)

    private var textAppearanceInt: Int = 0

    @BeforeAll
    override fun setUp() {
        super.setUp()
        every { beagleSdk.designSystem } returns designSystem//mockk(relaxed = true)
        every { designSystem.textStyle(any()) } returns textAppearanceInt
        every { context.obtainStyledAttributes(any<Int>(), any()) } returns mockk()
    }

    @BeforeEach
    fun clear() {
        mockkStatic("br.com.zup.beagle.android.utils.ViewExtensionsKt")
        clearMocks(
            designSystem,
            context,
            typedValue,
            view,
            answers = false
        )
        every { view.background } returns mockk()
        styleManager = StyleManager(typedValue = typedValue).apply { init(designSystem) }
    }

    @Test
    fun test_getBackgroundColor_when_view_has_a_null_background() {
        //Given
        val expected = Color.TRANSPARENT
        every { view.background } returns null

        //When
        styleManager.applyStyleComponent(serverDrivenComponent, view)

        //Then
        verify(exactly = 1) {
            view.applyViewBackgroundAndCorner(
                expected,
                serverDrivenComponent
            )
        }
    }

    @Test
    fun test_getBackgroundColor_when_text_has_a_color_drawable_background() {
        //Given
        val serverDrivenComponent = Text(constant(""))
        every { context.obtainStyledAttributes(any<Int>(), any()) } returns typedArray
        every { view.background } returns colorDrawable
        every { colorDrawable.color } returns Color.WHITE

        //When
        styleManager.applyStyleComponent(serverDrivenComponent, view)

        //Then
        verify(exactly = 1) {
            view.applyViewBackgroundAndCorner(
                Color.WHITE,
                serverDrivenComponent
            )
        }
    }

    @Test
    fun test_getBackgroundColor_when_text_not_is_color_drawable() {
        //Given
        val serverDrivenComponent = Text(constant(""))
        every { context.obtainStyledAttributes(any<Int>(), any()) } returns typedArray

        //When
        styleManager.applyStyleComponent(serverDrivenComponent, view)

        //Then
        verify(exactly = 1) { view.applyViewBackgroundAndCorner(null, serverDrivenComponent) }
    }

    @Test
    fun test_getBackgroundColor_when_button_has_a_color_drawable_background() {
        //Given
        val serverDrivenComponent = Button(constant(""))
        every { context.obtainStyledAttributes(any<Int>(), any()) } returns typedArray
        every { view.background } returns colorDrawable
        every { colorDrawable.color } returns Color.WHITE

        //When
        styleManager.applyStyleComponent(serverDrivenComponent, view)

        //Then
        verify(exactly = 1) {
            view.applyViewBackgroundAndCorner(
                Color.WHITE,
                serverDrivenComponent
            )
        }
    }

    @Test
    fun test_getBackgroundColor_when_Button_not_is_color_drawable() {
        //Given
        val serverDrivenComponent = Button(constant(""))
        every { context.obtainStyledAttributes(any<Int>(), any()) } returns typedArray

        //When
        styleManager.applyStyleComponent(serverDrivenComponent, view)

        //Then
        verify(exactly = 1) { view.applyViewBackgroundAndCorner(null, serverDrivenComponent) }
    }

    @Test
    fun test_getBackgroundColor_when_view_has_a_color_drawable_background() {
        //Given
        val serverDrivenComponent = Container(mockk())
        every { colorDrawable.color } returns Color.BLACK
        every { view.background } returns colorDrawable

        //When
        styleManager.applyStyleComponent(serverDrivenComponent, view)

        //Then
        verify(exactly = 1) {
            view.applyViewBackgroundAndCorner(
                Color.BLACK,
                serverDrivenComponent
            )
        }
    }

    @Test
    fun test_getBackgroundColor_when_view_has_not_a_color_drawable_background() {
        //Given
        val serverDrivenComponent = Container(mockk())
        every { view.background } returns drawable

        //When
        styleManager.applyStyleComponent(serverDrivenComponent, view)

        //Then
        verify(exactly = 1) { view.applyViewBackgroundAndCorner(null, serverDrivenComponent) }
    }

    @Test
    fun getTypedValueByResId_should_return_TypedValue() {
        // GIVEN
        val resId = 0
        val theme = mockk<Resources.Theme>(relaxed = true)
        every { context.theme } returns theme

        // WHEN
        val result = styleManager.getTypedValueByResId(resId, context)

        // THEN
        verify(exactly = 1) { theme.resolveAttribute(resId, typedValue, true) }
        assertEquals(typedValue, result)
    }

    @Test
    fun getButtonStyle_should_return_button_style() {
        // GIVEN
        val buttonStyle = "stub"
        val buttonStyleResource = 0
        every { designSystem.buttonStyle(buttonStyle) } returns buttonStyleResource

        // WHEN
        val result = styleManager.getButtonStyle(buttonStyle)

        // THEN
        assertEquals(buttonStyleResource, result)
    }

    @Test
    fun getButtonStyle_should_call_empty_when_not_pass_style() {
        // GIVEN
        val buttonStyle = null
        val buttonStyleResource = 0
        every { designSystem.buttonStyle("") } returns buttonStyleResource

        // WHEN
        val result = styleManager.getButtonStyle(buttonStyle)

        // THEN
        verify(exactly = 1) { designSystem.buttonStyle("") }
        assertEquals(buttonStyleResource, result)
    }

    @Test
    fun getTabBarTypedArray_should_call_BeagleTabBarStyle() {
        // GIVEN
        val tabStyle = null
        val tabStyleResource = 0
        every { designSystem.tabViewStyle("") } returns tabStyleResource

        // WHEN
        styleManager.getTabBarTypedArray(context, tabStyle)

        // THEN
        verify(exactly = 1) {
            context.obtainStyledAttributes(tabStyleResource, R.styleable.BeagleTabBarStyle)
        }
    }

    @Test
    fun getInputTextStyle_should_return_default_when_inputTextStyle_is_null() {
        // GIVEN
        val textInputStyle = null
        val textInputStyleResource = R.style.Widget_AppCompat_EditText
        every { designSystem.inputTextStyle("") } returns textInputStyle

        // WHEN
        val result = styleManager.getInputTextStyle(textInputStyle)

        // THEN
        verify(exactly = 1) { designSystem.inputTextStyle("") }
        assertEquals(textInputStyleResource, result)
    }

    @Test
    fun getInputTextStyle_should_return_a_inputTextStyle_when_is_not_null() {
        // GIVEN
        val textInputStyle = 0
        every { designSystem.inputTextStyle("") } returns textInputStyle

        // WHEN
        val result = styleManager.getInputTextStyle("")

        // THEN
        verify(exactly = 1) { designSystem.inputTextStyle("") }
        assertEquals(textInputStyle, result)
    }
}
