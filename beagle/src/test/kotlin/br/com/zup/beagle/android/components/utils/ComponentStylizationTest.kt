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

import android.view.View
import br.com.zup.beagle.R
import br.com.zup.beagle.android.BaseTest
import br.com.zup.beagle.android.annotation.RegisterWidget
import br.com.zup.beagle.android.components.Text
import br.com.zup.beagle.android.setup.DesignSystem
import br.com.zup.beagle.android.utils.StyleManager
import br.com.zup.beagle.android.utils.applyStyle
import br.com.zup.beagle.android.utils.styleManagerFactory
import br.com.zup.beagle.android.utils.toAndroidId
import br.com.zup.beagle.android.widget.RootView
import br.com.zup.beagle.android.widget.WidgetView
import br.com.zup.beagle.android.widget.core.BeagleJson
import io.mockk.Runs
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.verifyOrder
import org.junit.Assert
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("Given a ComponentStylization")
class ComponentStylizationTest : BaseTest() {

    private val accessibilitySetup: AccessibilitySetup = mockk(relaxed = true)

    private val view: View = mockk(relaxed = true, relaxUnitFun = true)

    private val widget: Text = mockk(relaxed = true)

    private val designSystem: DesignSystem = mockk(relaxed = true)

    private val styleManager: StyleManager = mockk(relaxed = true)

    private val componentStylization = ComponentStylization<WidgetView>(accessibilitySetup)

    @BeforeAll
    override fun setUp() {
        super.setUp()
        styleManagerFactory = styleManager
        mockkStatic("br.com.zup.beagle.android.utils.ViewExtensionsKt")

    }

    @BeforeEach
    fun clear() {
        clearMocks(rootView.getBeagleConfigurator())
        every { rootView.getBeagleConfigurator().designSystem } returns designSystem
    }

    @DisplayName("When apply")
    @Nested
    inner class Apply {

        @DisplayName("Then should set beagle_component_id tag")
        @Test
        fun testApplyShouldSetBeagleComponentTag() {
            // Given
            val widgetId = "123"
            val slotId = slot<String>()
            every { view.setTag(R.id.beagle_component_id, capture(slotId)) } just Runs
            every { view.id = any() } just Runs
            every { widget.id } returns widgetId
            every { view.setTag(R.id.beagle_component_type, any()) } just Runs
            every { view.applyStyle(widget, designSystem) } just Runs

            // When
            componentStylization.apply(rootView, view, widget)

            // Then
            assertEquals(widgetId, slotId.captured)
        }

        @DisplayName("Then should set beagle_component_type tag")
        @Test
        fun testComponentRegisteredWhenApplyShouldSetBeagleComponentType() {
            // Given
            val widgetId = "123"
            val slotId = slot<String>()
            @Suppress("UNCHECKED_CAST")
            every { rootView.getBeagleConfigurator().registeredWidgets } returns listOf(Text::class.java) as List<Class<WidgetView>>

            every { view.setTag(R.id.beagle_component_type, capture(slotId)) } just Runs
            every { view.id = any() } just Runs
            every { widget.id } returns widgetId
            every { view.setTag(R.id.beagle_component_id, any()) } just Runs
            every { view.applyStyle(widget, designSystem) } just Runs

            // When
            componentStylization.apply(rootView, view, widget)

            // Then
            assertEquals("custom:text", slotId.captured)
        }

        @DisplayName("Then should set beagle_component_type tag")
        @Test
        fun testComponentNotRegisteredWhenApplyShouldSetBeagleComponentType() {
            // Given
            val widgetId = "123"
            val slotId = slot<String>()

            every { view.setTag(R.id.beagle_component_type, capture(slotId)) } just Runs
            every { view.id = any() } just Runs
            every { widget.id } returns widgetId
            every { view.setTag(R.id.beagle_component_id, any()) } just Runs
            every { view.applyStyle(widget, designSystem) } just Runs

            // When
            componentStylization.apply(rootView, view, widget)

            // Then
            assertEquals("beagle:text", slotId.captured)
        }

        @DisplayName("Then should apply style and apply accessibility")
        @Test
        fun testAfterBuildViewWhenApplyShouldApplyStyleAndAccessibility() {
            // GIVEN
            val widgetId = "123"
            val slotId = slot<Int>()

            every { view.setTag(R.id.beagle_component_type, any()) } just Runs
            every { view.setTag(R.id.beagle_component_id, any()) } just Runs
            every { widget.id } returns widgetId
            every { view.id = capture(slotId) } just Runs
            every { view.applyStyle(widget, designSystem) } just Runs

            // WHEN
            componentStylization.apply(rootView, view, widget)

            // THEN
            assertEquals(widgetId.toAndroidId(), slotId.captured)
            verifyOrder {
                view.applyStyle(widget, designSystem)
                accessibilitySetup.applyAccessibility(view, widget)
            }
        }
    }

    @DisplayName("when apply")
    @Nested
    inner class ProGuardTest {

        @DisplayName("Then should get name from annotation")
        @Test
        fun testApplyOfBeagleJsonWidgetShouldGetNameFromAnnotation() {
            //given
            val widget = BeagleJsonWidgetWithName()
            val slotId = slot<String>()

            every { view.setTag(R.id.beagle_component_type, capture(slotId)) } just Runs
            every { view.id = any() } just Runs
            every { view.setTag(R.id.beagle_component_id, any()) } just Runs
            every { view.applyStyle(widget, designSystem) } just Runs

            //when
            componentStylization.apply(rootView, view, widget)
            //then
            Assert.assertEquals("beagle:widgetname", slotId.captured)
        }

        @DisplayName("Then should get name from class")
        @Test
        fun testApplyOfBeagleJsonWidgetShouldGetNameFromClass() {
            //given
            val widget = BeagleJsonWidgetWithoutName()
            val slotId = slot<String>()

            every { view.setTag(R.id.beagle_component_type, capture(slotId)) } just Runs
            every { view.id = any() } just Runs
            every { view.setTag(R.id.beagle_component_id, any()) } just Runs
            every { view.applyStyle(widget, designSystem) } just Runs

            //when
            componentStylization.apply(rootView, view, widget)

            //then
            Assert.assertEquals("beagle:beaglejsonwidgetwithoutname", slotId.captured)
        }

        @DisplayName("Then should get name from annotation")
        @Test
        fun testApplyOfRegisterActionWidgetShouldGetNameFromAnnotation() {
            val widget = RegisterWidgetWithName()
            //when
            every { rootView.getBeagleConfigurator().registeredWidgets } returns widgetList(widget)
            val slotId = slot<String>()

            every { view.setTag(R.id.beagle_component_type, capture(slotId)) } just Runs
            every { view.id = any() } just Runs
            every { view.setTag(R.id.beagle_component_id, any()) } just Runs
            every { view.applyStyle(widget, designSystem) } just Runs

            //when
            componentStylization.apply(rootView, view, widget)

            //then
            Assert.assertEquals("custom:widgetname", slotId.captured)
        }

        @DisplayName("Then should get name from class")
        @Test
        fun testApplyOfRegisterActionWidgetShouldGetNameFromClass() {
            //given
            val widget = RegisterWidgetWithoutName()
            every { rootView.getBeagleConfigurator().registeredWidgets } returns widgetList(widget)
            val slotId = slot<String>()

            every { view.setTag(R.id.beagle_component_type, capture(slotId)) } just Runs
            every { view.id = any() } just Runs
            every { view.setTag(R.id.beagle_component_id, any()) } just Runs
            every { view.applyStyle(widget, designSystem) } just Runs

            //when
            componentStylization.apply(rootView, view, widget)

            //then
            Assert.assertEquals("custom:registerwidgetwithoutname", slotId.captured)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun widgetList(widget: WidgetView) = listOf(widget::class.java as Class<WidgetView>)
}

@BeagleJson(name = "widgetName")
internal class BeagleJsonWidgetWithName() : WidgetView() {
    override fun buildView(rootView: RootView): View {
        return View(rootView.getContext())
    }
}

@BeagleJson
internal class BeagleJsonWidgetWithoutName() : WidgetView() {
    override fun buildView(rootView: RootView): View {
        return View(rootView.getContext())
    }
}

@RegisterWidget
internal class RegisterWidgetWithoutName() : WidgetView() {
    override fun buildView(rootView: RootView): View {
        return View(rootView.getContext())
    }
}

@RegisterWidget(name = "widgetName")
internal class RegisterWidgetWithName() : WidgetView() {
    override fun buildView(rootView: RootView): View {
        return View(rootView.getContext())
    }
}
