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

import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.core.widget.doOnTextChanged
import br.com.zup.beagle.android.action.Action
import br.com.zup.beagle.android.annotation.RegisterWidget
import br.com.zup.beagle.android.context.Bind
import br.com.zup.beagle.android.context.ContextData
import br.com.zup.beagle.android.utils.beagleComponent
import br.com.zup.beagle.android.utils.handleEvent
import br.com.zup.beagle.android.utils.observeBindChanges
import br.com.zup.beagle.android.utils.styleManagerFactory
import br.com.zup.beagle.android.view.ViewFactory
import br.com.zup.beagle.android.widget.RootView
import br.com.zup.beagle.android.widget.WidgetView
import br.com.zup.beagle.android.widget.core.TextInputType
import br.com.zup.beagle.android.widget.core.TextInputType.DATE
import br.com.zup.beagle.android.widget.core.TextInputType.EMAIL
import br.com.zup.beagle.android.widget.core.TextInputType.NUMBER
import br.com.zup.beagle.android.widget.core.TextInputType.PASSWORD

private const val VALUE_KEY = "value"

/**
 * Input is a component that displays an editable text area for the user. These text fields are used to collect
 * inputs that the user insert using the keyboard.
 *
 * @param value Required. Item referring to the input value that will be entered in the editable text area of the
 * Text Input component.
 * @param placeholder The Placeholder is a text that is displayed when nothing has been entered in the editable
 * text field.
 * @param readOnly Check if the Input will be editable or read only.
 * @param type This attribute identifies the type of text that we will receive in the editable text area.
 * On Android and iOS, this field also assigns the type of keyboard that will be displayed to the us.
 * @param error is a text that should be rendered, below the text input. It tells the user about the error.
 * This text is visible only if showError is true
 * @param showError controls weather to make the error of the input visible or not.
 * The error will be visible only if showError is true.
 * @param styleId This attribute receives a key that is registered in the Design System of each platform that
 * customizes the component.
 * @param onChange Actions array that this field can trigger when its value is altered.
 * @param onFocus Actions array that this field can trigger when this field is on focus.
 * @param onBlur Action array that this field can trigger when its focus is removed
 * @param enabled Enables or disables the field.
 */
@RegisterWidget("textInput")
data class TextInput(
    val value: Bind<String>? = null,
    val placeholder: Bind<String>? = null,
    val readOnly: Bind<Boolean>? = null,
    val type: Bind<TextInputType>? = null,
    val error: Bind<String>? = null,
    val showError: Bind<Boolean>? = null,
    val styleId: String? = null,
    val onChange: List<Action>? = null,
    val onFocus: List<Action>? = null,
    val onBlur: List<Action>? = null,
    val enabled: Bind<Boolean>? = null,
) : WidgetView() {

    @Transient
    private lateinit var textInputView: EditText

    @Transient
    private var textWatcher: TextWatcher? = null

    @Transient
    var errorTextValuated: String? = null
        private set


    override fun buildView(rootView: RootView): View =
        createEditText(rootView)
            .apply {
                beagleComponent = this@TextInput
                textInputView = this
                setData(this@TextInput, rootView)
                setUpOnTextChange(rootView)
                if (onFocus != null || onBlur != null) setUpOnFocusChange(rootView)
            }

    private fun createEditText(rootView: RootView): EditText {
        return if (styleId.isNullOrEmpty()) ViewFactory.makeInputText(rootView.getContext())
        else ViewFactory.makeInputText(
            rootView.getContext(),
            styleManagerFactory.getInputTextStyle(styleId)
        )
    }

    private fun EditText.setUpOnTextChange(rootView: RootView) {
        textWatcher = doOnTextChanged { newText, _, _, _ ->
            onChange?.let {
                this@TextInput.handleEvent(
                    rootView,
                    this,
                    onChange,
                    ContextData(
                        id = "onChange",
                        value = mapOf(VALUE_KEY to newText.toString())
                    ),
                    analyticsValue = "onChange"
                )
            }
        }
    }

    private fun EditText.removeOnTextChange() {
        removeTextChangedListener(textWatcher)
    }

    private fun EditText.setUpOnFocusChange(rootView: RootView) {
        setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                onFocus?.let {
                    this@TextInput.handleEvent(
                        rootView,
                        view,
                        onFocus,
                        ContextData(
                            id = "onFocus",
                            value = mapOf(VALUE_KEY to this.text.toString())
                        ),
                        analyticsValue = "onFocus"
                    )
                }
            } else {
                onBlur?.let {
                    this@TextInput.handleEvent(
                        rootView,
                        view,
                        onBlur,
                        ContextData(
                            id = "onBlur",
                            value = mapOf(VALUE_KEY to this.text.toString())
                        ),
                        analyticsValue = "onBlur"
                    )
                }
            }
        }
    }

    private fun EditText.setData(textInput: TextInput, rootView: RootView) {
        isFocusable = true
        isFocusableInTouchMode = true
        textInput.placeholder?.let { bind ->
            observeBindChanges(
                rootView,
                this,
                bind
            ) { it?.let { hint = it } }
        }
        textInput.value?.let { bind ->
            observeBindChanges(
                rootView,
                this,
                bind
            ) { it?.let { setValue(it, rootView) } }
        }
        textInput.readOnly?.let { bind ->
            observeBindChanges(
                rootView,
                this,
                bind
            ) { setEnabledConfig(it?.not()) }
        }
        textInput.enabled?.let { bind ->
            observeBindChanges(
                rootView,
                this,
                bind
            ) {
                setEnabledConfig(it)
            }
        }

        textInput.type?.let { bind ->
            observeBindChanges(
                rootView,
                this,
                bind
            ) { it?.let { setInputType(it) } }
        }

        observeBindError(textInput, rootView, this)
    }

    private fun observeBindError(textInput: TextInput, rootView: RootView, editText: EditText) {
        textInput.error?.let { bind ->
            observeBindChanges(rootView, editText, bind) {
                errorTextValuated = it
            }
        }

        textInput.showError?.let { bind ->
            observeBindChanges(rootView, editText, bind) { showError ->
                editText.error = getMessageError(showError)
            }

        }
    }

    private fun getMessageError(showError: Boolean?): String? {
        return if (showError == true && !errorTextValuated.isNullOrEmpty()) {
            errorTextValuated
        } else {
            null
        }
    }

    private fun EditText.setEnabledConfig(isEnabled: Boolean?) {
        isEnabled?.let { this.isEnabled = it }
    }

    private fun EditText.setValue(text: String, rootView: RootView) {
        if (text == this.text.toString()) return
        removeOnTextChange()
        setText(text)
        setSelection(text.length)
        setUpOnTextChange(rootView)
    }

    private fun EditText.setInputType(textInputType: TextInputType) {
        when (textInputType) {
            DATE -> setRawInputType(InputType.TYPE_CLASS_DATETIME)
            EMAIL -> setRawInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
            PASSWORD -> inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            NUMBER -> setRawInputType(InputType.TYPE_CLASS_NUMBER)
            else -> setRawInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES)
        }
    }
}
