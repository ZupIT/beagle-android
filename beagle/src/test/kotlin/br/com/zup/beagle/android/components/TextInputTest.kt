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

package br.com.zup.beagle.android.components

import android.content.Context
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.TextViewCompat
import br.com.zup.beagle.android.action.SetContext
import br.com.zup.beagle.android.context.Bind
import br.com.zup.beagle.android.context.ContextData
import br.com.zup.beagle.android.context.constant
import br.com.zup.beagle.android.setup.BeagleEnvironment
import br.com.zup.beagle.android.testutil.setPrivateField
import br.com.zup.beagle.android.utils.StyleManager
import br.com.zup.beagle.android.utils.handleEvent
import br.com.zup.beagle.android.utils.styleManagerFactory
import br.com.zup.beagle.android.view.ViewFactory
import br.com.zup.beagle.android.widget.core.TextInputType
import io.mockk.Runs
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

const val VALUE_KEY = "value"
val PLACE_HOLDER = constant("Text Hint")
val READ_ONLY = constant(true)
val ENABLED = constant(true)
const val STYLE_ID = "Style"
val ERROR = constant("Error")
val TYPE = constant(TextInputType.NUMBER)

@DisplayName("Given a TextInput")
internal class TextInputTest : BaseComponentTest() {

    private val focusCapture = slot<View.OnFocusChangeListener>()
    private val textWatcherCapture = slot<TextWatcher>()
    private val editText: AppCompatEditText = mockk(relaxed = true, relaxUnitFun = true)
    private val styleManager: StyleManager = mockk(relaxed = true)
    private val context: Context = mockk()
    private val textWatcher: TextWatcher = mockk()

    private lateinit var textInput: TextInput

    @BeforeAll
    override fun setUp() {
        super.setUp()

        mockkStatic(TextViewCompat::class)

        styleManagerFactory = styleManager

        every { ViewFactory.makeInputText(any(), any()) } returns editText
        every { ViewFactory.makeInputText(any()) } returns editText
        every { TextViewCompat.setTextAppearance(any(), any()) } just Runs

        every { BeagleEnvironment.application } returns mockk(relaxed = true)

        every { editText.context } returns context

        textInput = callTextInput(TYPE)

        every { editText.addTextChangedListener(capture(textWatcherCapture)) } just Runs

        every { editText.onFocusChangeListener = capture(focusCapture) } just Runs

        mockkStatic("br.com.zup.beagle.android.utils.WidgetExtensionsKt")
    }

    @BeforeEach
    fun clear() {
        clearMocks(
            editText,
            ViewFactory,
            answers = false
        )

        textInput.setPrivateField("textWatcher", textWatcher)
    }

    private fun callTextInput(
        type: Bind<TextInputType> = TYPE,
        styleId: String? = STYLE_ID,
        showError: Bind<Boolean> = constant(false),
    ) = TextInput(
        value = constant(VALUE_KEY),
        placeholder = PLACE_HOLDER,
        readOnly = READ_ONLY,
        enabled = ENABLED,
        styleId = styleId,
        type = type,
        showError = showError,
        error = ERROR,
        onChange = listOf(SetContext(contextId = "textInputValue", value = "a")),
        onFocus = listOf(SetContext(contextId = "textInputValue", value = "b")),
        onBlur = listOf(SetContext(contextId = "textInputValue", value = "c"))
    )

    @DisplayName("When enable to show error")
    @Nested
    inner class TextInputEnableErrorForm {

        @Test
        @DisplayName("Then should show field error")
        fun testErrorEnabled() {
            // Given
            textInput = callTextInput(TYPE, styleId = null, showError = constant(true))

            // When
            val view = textInput.buildView(rootView)

            // Then
            assertTrue(view is EditText)
            verify(exactly = 1) {
                editText.error = ERROR.value
            }
        }

        @Test
        @DisplayName("Then should show correct it")
        fun testErrorDisabled() {
            // Given
            textInput = callTextInput(TYPE, styleId = null, showError = constant(false))

            // When
            val view = textInput.buildView(rootView)

            // Then
            assertTrue(view is EditText)
            verify(exactly = 0) {
                editText.error = ERROR.value
            }
        }
    }


    @DisplayName("When set field enabled")
    @Nested
    inner class TextInputSetFieldEnabled {

        @Test
        @DisplayName("Then should show edit text enabled")
        fun testFieldEnabledTrue() {
            // Given
            textInput =
                callTextInput().copy(enabled = constant(true), readOnly = null)

            // When
            val view = textInput.buildView(rootView)

            // Then
            assertTrue(view is EditText)
            verify(exactly = 1) {
                editText.isEnabled = true
            }
        }

        @Test
        @DisplayName("Then should show edit text disabled")
        fun testFieldEnabledFalse() {
            // Given
            textInput =
                callTextInput().copy(enabled = constant(false), readOnly = null)

            // When
            val view = textInput.buildView(rootView)

            // Then
            assertTrue(view is EditText)
            verify(exactly = 1) {
                editText.isEnabled = false
            }
        }

        @Test
        @DisplayName("Then should not call edit text enabled")
        fun testFieldEnabledNotCalled() {
            // Given
            textInput = callTextInput().copy(enabled = null, readOnly = null)

            // When
            val view = textInput.buildView(rootView)

            // Then
            assertTrue(view is EditText)
            verify(exactly = 0) {
                editText.isEnabled = any()
            }
        }
    }

    @DisplayName("When disable to show error")
    @Nested
    inner class TextInputDisableErrorForm {

        @Test
        @DisplayName("Then should not call field error")
        fun testErrorDisabled() {
            // Given
            textInput = callTextInput(TYPE, styleId = null, showError = constant(false))

            // When
            val view = textInput.buildView(rootView)

            // Then
            assertTrue(view is EditText)
            verify(exactly = 0) {
                editText.error = ERROR.value
            }
        }
    }

    @DisplayName("When set the configurations for TextInput")
    @Nested
    inner class TextInputConfigurations {

        @Test
        @DisplayName("Then should build a textView")
        fun testBuildButtonWithoutStyle() {
            // Given
            textInput = callTextInput(TYPE, styleId = null)

            // When
            val view = textInput.buildView(rootView)

            // Then
            assertTrue(view is EditText)
            verify(exactly = 1) {
                ViewFactory.makeInputText(any())
                editText.setText(VALUE_KEY)
                editText.hint = PLACE_HOLDER.value
                editText.isEnabled = READ_ONLY.value.not()
                editText.isEnabled = ENABLED.value
                editText.isFocusable = true
                editText.isFocusableInTouchMode = true
            }
        }

        @Test
        @DisplayName("Then should build a textView")
        fun buildEditTextInstance() {
            // When
            val view = textInput.buildView(rootView)

            // Then
            assertTrue(view is EditText)
            verify(exactly = 1) {
                ViewFactory.makeInputText(any())
                editText.setText(VALUE_KEY)
                editText.hint = PLACE_HOLDER.value
                editText.isEnabled = READ_ONLY.value.not()
                editText.isEnabled = ENABLED.value
                editText.isFocusable = true
                editText.isFocusableInTouchMode = true
            }
        }

        @Test
        @DisplayName("Then check if setUpOnTextChange is set calling onChange")
        fun checkCallOnChange() {
            val newValue = "newValue"

            val valueWithContext = ContextData(
                id = "onChange",
                value = mapOf(VALUE_KEY to newValue)
            )

            // When
            val view = textInput.buildView(rootView)

            textWatcherCapture.captured.onTextChanged(newValue, 0, 0, 0)

            // Then
            assertTrue(view is EditText)
            verify {
                textInput.handleEvent(
                    rootView,
                    view,
                    textInput.onChange!!,
                    valueWithContext,
                    "onChange"
                )
            }
        }

        @Test
        @DisplayName("Then check if setUpOnFocusChange is set calling onFocus")
        fun checkCallOnFocus() {
            val valueWithContext = ContextData(
                id = "onFocus",
                value = mapOf(VALUE_KEY to editText.text.toString())
            )

            // When
            val view = textInput.buildView(rootView)

            focusCapture.captured.onFocusChange(view, true)

            // Then
            assertTrue(view is EditText)
            verify {
                textInput.handleEvent(
                    rootView,
                    view,
                    textInput.onFocus!!,
                    valueWithContext,
                    "onFocus"
                )
            }
        }

        @Test
        @DisplayName("Then check if setUpOnFocusChange is set calling onBlur")
        fun checkCallOnBlur() {
            val valueWithContext = ContextData(
                id = "onBlur",
                value = mapOf(VALUE_KEY to editText.text.toString())
            )

            // When
            val view = textInput.buildView(rootView)

            focusCapture.captured.onFocusChange(view, false)

            // Then
            assertTrue(view is EditText)
            verify {
                textInput.handleEvent(
                    rootView,
                    view,
                    textInput.onBlur!!,
                    valueWithContext,
                    "onBlur"
                )
            }
        }

        @Test
        @DisplayName("Then verify set enabled config of text input")
        fun verifyEnabledConfig() {
            // When
            textInput.buildView(rootView)

            // Then
            verify(exactly = 1) {
                editText.isEnabled = true
            }
        }

        @Test
        @DisplayName("Then check if the text was removed")
        fun verifyTextRemoval() {
            // Given
            textInput.buildView(rootView)

            // Then
            verify(exactly = 1) { editText.removeTextChangedListener(textWatcher) }
        }
    }

    @DisplayName("When passing input type")
    @Nested
    inner class InputTypeTest {

        @Test
        @DisplayName("Then should call setRawInputType with TYPE_CLASS_DATETIME")
        fun testInputTypeDate() {
            // Given
            val type = constant(TextInputType.DATE)

            // When
            val textInput = callTextInput(type)
            textInput.buildView(rootView)

            // Then
            verify(exactly = 1) { editText.setRawInputType(InputType.TYPE_CLASS_DATETIME) }
        }
    }

    @Test
    @DisplayName("Then should call setRawInputType with TYPE_TEXT_VARIATION_EMAIL_ADDRESS")
    fun setInputTypeEmail() {
        // Given
        val type = constant(TextInputType.EMAIL)

        // When
        val textInput = callTextInput(type)
        textInput.buildView(rootView)

        // Then
        verify(exactly = 1) { editText.setRawInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS) }
    }

    @Test
    @DisplayName("Then should call TYPE_CLASS_TEXT or TYPE_TEXT_VARIATION_PASSWORD")
    fun setInputTypePassword() {
        // Given
        val type = constant(TextInputType.PASSWORD)

        // When
        val textInput = callTextInput(type)
        textInput.buildView(rootView)

        // Then
        verify(exactly = 1) {
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
    }

    @Test
    @DisplayName("Then should call setRawInputType with TYPE_CLASS_NUMBER")
    fun setInputTypeNumber() {
        // Given
        val type = constant(TextInputType.NUMBER)

        // When
        val textInput = callTextInput(type)
        textInput.buildView(rootView)

        // Then
        verify(exactly = 1) { editText.setRawInputType(InputType.TYPE_CLASS_NUMBER) }
    }

    @Test
    @DisplayName(" Then should call setRawInputType with TYPE_CLASS_TEXT or TYPE_TEXT_FLAG_CAP_SENTENCES")
    fun setInputTypeText() {
        // Given
        val type = constant(TextInputType.TEXT)

        // When
        val textInput = callTextInput(type)
        textInput.buildView(rootView)

        // Then
        verify(exactly = 1) { editText.setRawInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES) }
    }

}
