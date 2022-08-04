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

import android.app.Activity
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import br.com.zup.beagle.android.BaseTest
import br.com.zup.beagle.android.context.Bind
import br.com.zup.beagle.android.context.ContextBinding
import br.com.zup.beagle.android.context.ContextData
import br.com.zup.beagle.android.context.constant
import br.com.zup.beagle.android.data.serializer.BeagleSerializer
import br.com.zup.beagle.android.networking.RequestData
import br.com.zup.beagle.android.setup.DesignSystem
import br.com.zup.beagle.android.testutil.RandomData
import br.com.zup.beagle.android.view.ViewFactory
import br.com.zup.beagle.android.view.custom.BeagleView
import br.com.zup.beagle.android.view.custom.OnLoadCompleted
import br.com.zup.beagle.android.view.custom.OnServerStateChanged
import br.com.zup.beagle.android.view.viewmodel.AnalyticsViewModel
import br.com.zup.beagle.android.view.viewmodel.GenerateIdViewModel
import br.com.zup.beagle.android.view.viewmodel.ListViewIdViewModel
import br.com.zup.beagle.android.view.viewmodel.OnInitViewModel
import br.com.zup.beagle.android.view.viewmodel.ScreenContextViewModel
import br.com.zup.beagle.android.widget.ActivityRootView
import br.com.zup.beagle.android.widget.FragmentRootView
import br.com.zup.beagle.android.widget.RootView
import br.com.zup.beagle.android.widget.Widget
import br.com.zup.beagle.android.widget.core.Style
import br.com.zup.beagle.android.widget.core.StyleComponent
import io.mockk.CapturingSlot
import io.mockk.Runs
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.verify
import io.mockk.verifyOrder
import io.mockk.verifySequence
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private val URL = RandomData.httpUrl()
private val requestData = RequestData(URL)
private const val SCREEN_ID = "screenId"

@DisplayName("Given a View")
class ViewExtensionsKtTest : BaseTest() {

    private val view: View = mockk()

    private val contextData = ContextData("contextId", "stub")

    private val viewGroup: ViewGroup = mockk(relaxed = true)
    private val beagleRootView: RootView = mockk(relaxed = true)

    private val contextViewModel: ScreenContextViewModel = mockk(relaxed = true)

    private val generateIdViewModel: GenerateIdViewModel = mockk(relaxed = true)

    private val listViewIdViewModel: ListViewIdViewModel = mockk(relaxed = true)

    private val onInitViewModel: OnInitViewModel = mockk(relaxed = true)

    private val fragment: Fragment = mockk(relaxed = true)

    private val activity: AppCompatActivity = mockk(relaxed = true)

    private val beagleView: BeagleView = mockk(relaxed = true)

    private val onServerStateChanged: OnServerStateChanged = mockk()

    private val inputMethodManager: InputMethodManager = mockk(relaxed = true)

    private val designSystem: DesignSystem = mockk()

    private val imageView: ImageView = mockk()

    private val viewSlot = slot<View>()

    private val analyticsViewModel = mockk<AnalyticsViewModel>()

    @BeforeAll
    override fun setUp() {
        super.setUp()

        mockkStatic(TextViewCompat::class)
        mockkStatic("br.com.zup.beagle.android.utils.StringExtensionsKt")
        mockkStatic("br.com.zup.beagle.android.utils.NumberExtensionsKt")

        mockkObject(ViewFactory)

        prepareViewModelMock(
            contextViewModel,
            generateIdViewModel,
            listViewIdViewModel,
            onInitViewModel,
            analyticsViewModel
        )

        every { ViewFactory.makeBeagleView(any()) } returns beagleView
        every { ViewFactory.makeView(any()) } returns beagleView
        every { viewGroup.addView(capture(viewSlot)) } just Runs
        every { viewGroup.context } returns activity
        every { beagleView.loadView(any()) } just Runs
        every { activity.getSystemService(Activity.INPUT_METHOD_SERVICE) } returns inputMethodManager
        every { beagleSdk.designSystem } returns designSystem
        every { TextViewCompat.setTextAppearance(any(), any()) } just Runs
        every { imageView.scaleType = any() } just Runs
        every { imageView.setImageResource(any()) } just Runs
        every { analyticsViewModel.createScreenReport(any(), any()) } just Runs
    }

    @BeforeEach
    fun clear() {
        mockkStatic("br.com.zup.beagle.android.utils.ViewGroupExtensionsKt")
        mockkStatic("br.com.zup.beagle.android.utils.ViewExtensionsKt")
        clearMocks(
            ViewFactory,
            inputMethodManager,
            beagleView,
            viewGroup,
            generateIdViewModel,
            view,
            answers = false
        )
    }

    @DisplayName("When setContextData is called")
    @Nested
    inner class SetContextData {

        @DisplayName("Then should set bindings")
        @Test
        fun checkIfContextBindingWasSet() {
            // Given
            val contextBinding = setOf<ContextBinding>()
            val bindingSlot = slot<Set<ContextBinding>>()
            every { view.getContextBinding() } returns contextBinding
            every { view.setContextBinding(capture(bindingSlot)) } just Runs

            // When
            view.setContextData(contextData)

            // Then
            assertEquals(contextData, bindingSlot.captured.first().context)
            assertEquals(emptySet<Bind<*>>(), bindingSlot.captured.first().bindings)
        }

        @DisplayName("Then should not set bindings")
        @Test
        fun checkIfContextBindingWasNotSet() {
            // Given
            val expectedBindings = mutableSetOf<Bind<*>>()
            val bindingSlot = slot<Set<ContextBinding>>()
            every { view.getContextBinding() } returns null
            every { view.setContextBinding(capture(bindingSlot)) } just Runs

            // When
            view.setContextData(contextData)

            // Then
            assertEquals(bindingSlot.captured.first().context, contextData)
            assertEquals(bindingSlot.captured.first().bindings, expectedBindings)
        }
    }

    @DisplayName("When hideKeyboard")
    @Nested
    inner class HideKeyboard {

        @DisplayName("Then should call hideSoftInputFromWindow with currentFocus")
        @Test
        fun testHideKeyboardShouldCallHideSoftInputFromWindowWithCurrentFocus() {
            // Given
            every { activity.currentFocus } returns beagleView

            // When
            viewGroup.hideKeyboard()

            // Then
            verify(exactly = 1) { inputMethodManager.hideSoftInputFromWindow(any(), 0) }
        }

        @DisplayName("Then should call hideSoftInputFromWindow with created view")
        @Test
        fun testHideKeyboardShouldCallHideSoftInputFromWindowWithCreatedView() {
            // Given
            every { activity.currentFocus } returns null
            every { ViewFactory.makeView(activity) } returns beagleView

            // When
            viewGroup.hideKeyboard()

            // Then
            verify(exactly = 1) { inputMethodManager.hideSoftInputFromWindow(any(), 0) }
        }
    }

    @DisplayName("When applyStroke")
    @Nested
    inner class ApplyStroke {

        @DisplayName("Then must call setStroke")
        @Test
        fun testGivenColorValuesAndBorderSizeWhenApplyStrokeIsCalledThenMustCallSetStroke() {
            // Given
            val defaultColor = "#000000"
            val resultWidth = 5
            val resultColor = 0
            val styleWidget = mockk<StyleComponent>()
            val style = Style(borderWidth = constant(resultWidth.toDouble()), borderColor = constant(defaultColor))
            val gradientDrawable = mockk<GradientDrawable>(relaxUnitFun = true, relaxed = true)

            every { viewGroup.background } returns gradientDrawable
            every { viewGroup.beagleRootView } returns beagleRootView
            every { styleWidget.style } returns style
            every { resultWidth.dp() } returns resultWidth
            every { defaultColor.toAndroidColor() } returns resultColor

            // When
            viewGroup.applyStroke(styleWidget)

            // Then
            verify {
                gradientDrawable.setStroke(resultWidth, resultColor)
            }
        }

        @DisplayName("Then should not call setStroke")
        @Test
        fun testGivenBorderWidthWithNullValueWhenApplyStrokeIsCalledThenShouldNotCallSetStroke() {
            // Given
            val defaultColor = "#000000"
            val resultWidth = 5
            val resultColor = 0
            val styleWidget = mockk<StyleComponent>()
            val style = Style(borderWidth = null, borderColor = constant(defaultColor))
            val gradientDrawable = mockk<GradientDrawable>(relaxUnitFun = true, relaxed = true)

            every { viewGroup.background } returns gradientDrawable
            every { styleWidget.style } returns style
            every { defaultColor.toAndroidColor() } returns resultColor

            // When
            viewGroup.applyStroke(styleWidget)

            // Then
            verify(exactly = 0) {
                gradientDrawable.setStroke(resultWidth, resultColor)
            }
        }

        @DisplayName("Then should not call setStroke")
        @Test
        fun testGivenBorderColorWithNullValueWhenApplyStrokeIsCalledThenShouldNotCallSetStroke() {
            // Given
            val resultWidth = 5
            val resultColor = 0
            val styleWidget = mockk<StyleComponent>()
            val style = Style(borderWidth = constant(resultWidth.toDouble()), borderColor = null)
            val gradientDrawable = mockk<GradientDrawable>(relaxUnitFun = true, relaxed = true)

            every { viewGroup.background } returns gradientDrawable
            every { styleWidget.style } returns style
            every { resultWidth.dp() } returns resultWidth

            // When
            viewGroup.applyStroke(styleWidget)

            // Then
            verify(exactly = 0) {
                gradientDrawable.setStroke(resultWidth, resultColor)
            }
        }

        @DisplayName("Then create a new instance")
        @Test
        fun testGivenBackgroundWithNullValueWhenValueIsNullThenCreateANewInstance() {
            // Given
            val defaultColor = "#gf5487"
            val resultWidth = 5
            val resultColor = 0
            val styleWidget = mockk<StyleComponent>()
            val style = Style(borderWidth = constant(resultWidth.toDouble()), borderColor = constant(defaultColor))
            mockkConstructor(GradientDrawable::class)

            every { viewGroup.background } returns null
            every { viewGroup.beagleRootView } returns beagleRootView
            every { styleWidget.style } returns style
            every { resultWidth.dp() } returns resultWidth
            every { defaultColor.toAndroidColor() } returns resultColor
            every {
                anyConstructed<GradientDrawable>().setStroke(
                    resultWidth,
                    resultColor
                )
            } just Runs

            // When
            viewGroup.applyStroke(styleWidget)

            // Then
            verify(exactly = 1) {
                anyConstructed<GradientDrawable>().setStroke(resultWidth, resultColor)
            }
        }
    }

    @DisplayName("When load view")
    @Nested
    inner class LoadView {

        @DisplayName("Then should create BeagleView and call loadView with fragment")
        @Test
        fun testLoadViewShouldCreateBeagleViewAndCallLoadViewWithFragment() {
            // Given When
            viewGroup.loadView(fragment, requestData, onServerStateChanged)

            // Then
            verifySequence {
                generateIdViewModel.createIfNotExisting(0)
                ViewFactory.makeBeagleView(any<FragmentRootView>())
                beagleView.serverStateChangedListener = any()
                beagleView.loadView(requestData)
                beagleView.loadCompletedListener = any()
                beagleView.listenerOnViewDetachedFromWindow = any()
            }
        }

        @DisplayName("Then should create BeagleView and call loadView with activity")
        @Test
        fun testLoadViewShouldCreateBeagleViewAndCallLoadViewWithActivity() {
            // When
            viewGroup.loadView(activity, requestData, onServerStateChanged)

            // Then
            verify {
                ViewFactory.makeBeagleView(any<ActivityRootView>())
                beagleView.loadView(requestData)
            }
        }

        @DisplayName("Then should call removeAllViews and call addView when load completes")
        @Test
        fun testLoadViewShouldCalRemoveAllViewsAndCallAddViewWhenLoadComplete() {
            // Given
            val slot = slot<OnLoadCompleted>()
            every { beagleView.loadCompletedListener = capture(slot) } just Runs

            // When
            viewGroup.loadView(fragment, requestData, onServerStateChanged)
            slot.captured.invoke()

            // Then
            assertEquals(beagleView, viewSlot.captured)
            verifyOrder {
                viewGroup.removeAllViews()
                viewGroup.addView(beagleView)
            }
        }

        @DisplayName("Then should call addView when load complete")
        @Test
        fun testLoadViewWwithoutStateShouldAddViewWhenLoadComplete() {
            // Given
            val slot = slot<OnLoadCompleted>()
            every { beagleView.loadCompletedListener = capture(slot) } just Runs

            // When
            viewGroup.loadView(fragment, requestData)
            slot.captured.invoke()

            // Then
            assertEquals(beagleView, viewSlot.captured)
            verify(exactly = 1) { viewGroup.addView(beagleView) }
        }

        @DisplayName("Then should set stateChangedListener to beagleView")
        @Test
        fun testLoadViewShouldSetStateChangedListenerToBeagleView() {
            // Given
            val slot = slot<OnServerStateChanged>()
            every { beagleView.serverStateChangedListener = capture(slot) } just Runs

            // When
            viewGroup.loadView(fragment, requestData, onServerStateChanged)

            // Then
            assertEquals(slot.captured, onServerStateChanged)
        }

        @DisplayName("Then should create the rootView with right parameters")
        @Test
        fun testDeprecatedLoadViewWithActivity() {
            //given
            val slot = commonGiven()

            //when
            viewGroup.loadView(activity, requestData, onServerStateChanged)

            //then
            assertEquals(requestData.url, slot.captured.getScreenId())
        }

        @DisplayName("Then should create the rootView with right parameters")
        @Test
        fun testLoadViewWithActivity() {
            //given
            val slot = commonGiven()

            //when
            viewGroup.loadView(activity, requestData)

            //then
            assertEquals(requestData.url, slot.captured.getScreenId())
        }

        @DisplayName("Then should create the rootView with right parameters")
        @Test
        fun testLoadViewWithActivityAndOnServerStateChanged() {
            //given
            val slot = commonGiven()

            //when
            viewGroup.loadView(activity, requestData, onServerStateChanged)

            //then
            assertEquals(requestData.url, slot.captured.getScreenId())
        }

        @DisplayName("Then should create the rootView with right parameters")
        @Test
        fun testDeprecatedLoadViewWithFragment() {
            //given
            val slot = commonGiven()

            //when
            viewGroup.loadView(fragment, requestData, onServerStateChanged)

            //then
            assertEquals(requestData.url, slot.captured.getScreenId())
        }

        @DisplayName("Then should create the rootView with right parameters")
        @Test
        fun testLoadViewWithFragment() {
            //given
            val slot = commonGiven()

            //when
            viewGroup.loadView(fragment, requestData)

            //then
            assertEquals(requestData.url, slot.captured.getScreenId())
        }

        @DisplayName("Then should create the rootView with right parameters")
        @Test
        fun testLoadViewWithFragmentAndOnServerStateChanged() {
            //given
            val slot = commonGiven()

            //when
            viewGroup.loadView(fragment, requestData, onServerStateChanged)

            //then
            assertEquals(requestData.url, slot.captured.getScreenId())
        }

        private fun commonGiven(): CapturingSlot<RootView> {
            val slot = slot<RootView>()
            every { ViewFactory.makeBeagleView(capture(slot)) } returns beagleView
            return slot
        }
    }

    @DisplayName("When render screen")
    @Nested
    inner class RenderScreen {
        private val json = """{
                "_beagleComponent_" : "beagle:screenComponent",
                "child" : {
                "_beagleComponent_" : "beagle:text",
                "text" : "hello"
            }
            }"""

        private val component: Widget = mockk(relaxed = true)
        private val serializerFactory: BeagleSerializer = mockk(relaxed = true)

        @DisplayName("Then should create the rootView with right parameters")
        @Test
        fun testRenderScreenWithActivity() {
            // Given
            beagleSerializerFactory = serializerFactory
            every { component.id } returns SCREEN_ID
            every { serializerFactory.deserializeComponent(any()) } returns component

            // When
            viewGroup.loadView(activity, json, SCREEN_ID)

            // Then
            verifySequence {
                viewGroup.id
                serializerFactory.deserializeComponent(json)
                ViewFactory.makeBeagleView(any<ActivityRootView>())
                beagleView.addServerDrivenComponent(component)
                beagleView.listenerOnViewDetachedFromWindow = any()
                viewGroup.removeAllViews()
                viewGroup.addView(beagleView)
            }
        }

        @DisplayName("Then should create the rootView with right parameters")
        @Test
        fun testRenderScreenWithFragment() {
            // Given
            beagleSerializerFactory = serializerFactory
            every { component.id } returns SCREEN_ID
            every { serializerFactory.deserializeComponent(any()) } returns component

            // When
            viewGroup.loadView(fragment, json, SCREEN_ID)

            // Then
            verifySequence {
                viewGroup.id
                serializerFactory.deserializeComponent(json)
                ViewFactory.makeBeagleView(any<FragmentRootView>())
                beagleView.addServerDrivenComponent(component)
                beagleView.listenerOnViewDetachedFromWindow = any()
                viewGroup.removeAllViews()
                viewGroup.addView(beagleView)
            }
        }
    }
}
