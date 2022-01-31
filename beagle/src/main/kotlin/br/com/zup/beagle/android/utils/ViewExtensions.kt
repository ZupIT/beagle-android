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

@file:Suppress("TooManyFunctions")

package br.com.zup.beagle.android.utils

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import br.com.zup.beagle.R
import br.com.zup.beagle.android.components.utils.CornerRadiusHelper
import br.com.zup.beagle.android.components.utils.StrokeHelper
import br.com.zup.beagle.android.components.utils.applyStroke
import br.com.zup.beagle.android.components.utils.getFloatArray
import br.com.zup.beagle.android.context.ContextBinding
import br.com.zup.beagle.android.context.ContextData
import br.com.zup.beagle.android.context.normalize
import br.com.zup.beagle.android.widget.RootView
import br.com.zup.beagle.android.widget.core.ServerDrivenComponent
import br.com.zup.beagle.android.widget.core.StyleComponent

internal var styleManagerFactory = StyleManager()
const val FLOAT_ZERO = 0.0f

internal fun View.findParentContextWithId(contextId: String): View? {
    var parentView: View? = this.getParentContextData()
    do {
        val contextBinding = parentView?.getContextBinding()
        if (contextBinding != null && contextBinding.find { it.context.id == contextId } != null) {
            return parentView
        }
        parentView = (parentView?.parent as? ViewGroup)?.getParentContextData()
    } while (parentView != null)

    return null
}

internal fun View.getAllParentContexts(): MutableList<ContextBinding> {
    val contexts = mutableListOf<ContextBinding>()

    var parentView: View? = getParentContextData()
    do {
        val contextBinding = parentView?.getContextBinding()
        if (contextBinding != null) {
            contexts.addAll(contextBinding)
        }
        parentView = (parentView?.parent as? ViewGroup)?.getParentContextData()
    } while (parentView != null)

    return contexts
}

internal fun View.getParentContextData(): View? {
    if (this.getListContextData() != null) {
        return this
    }

    var parentView: View? = this.parent as? ViewGroup
    do {
        if (parentView?.getListContextData() != null) {
            break
        }
        parentView = parentView?.parent as? ViewGroup
    } while (parentView != null)

    return parentView
}

internal fun View.setContextData(context: ContextData) {
    val normalizedContext = context.normalize()
    val contextBindings = getContextBinding()
    if (contextBindings != null && contextBindings.isNotEmpty()) {
        val contextBindingsMutable = contextBindings.toMutableSet()
        val currentContextBinding = contextBindingsMutable.find { contextInsideBinding ->
            contextInsideBinding.context.id == normalizedContext.id
        }
        contextBindingsMutable.remove(currentContextBinding)
        contextBindingsMutable.add(ContextBinding(normalizedContext,
            currentContextBinding?.bindings ?: mutableSetOf()))
        setContextBinding(contextBindingsMutable)
    } else {
        setContextBinding(setOf(ContextBinding(normalizedContext)))
    }
}

internal fun View.getListContextData(): List<ContextData>? {
    return getContextBinding()?.map { it.context }
}

internal fun View.setContextBinding(contextBindings: Set<ContextBinding>) {
    setTag(R.id.beagle_context_view, contextBindings)
}

internal fun View.getContextBinding(): Set<ContextBinding>? {
    @Suppress("UNCHECKED_CAST")
    return getTag(R.id.beagle_context_view) as? Set<ContextBinding>?
}

internal fun View.setIsAutoGenerateIdEnabled(autoGenerateId: Boolean) {
    setTag(R.id.beagle_auto_generate_id_enabled, autoGenerateId)
}

internal fun View.isAutoGenerateIdEnabled(): Boolean {
    return getTag(R.id.beagle_auto_generate_id_enabled) as? Boolean ?: true
}

internal fun View.setIsInitiableComponent(isInitiableComponent: Boolean) {
    setTag(R.id.beagle_initiable_component, isInitiableComponent)
}

internal fun View.isInitiableComponent(): Boolean {
    return getTag(R.id.beagle_initiable_component) as? Boolean ?: false
}

internal fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

internal fun View.applyStyle(component: ServerDrivenComponent) {
    (component as? StyleComponent)?.let {
        if (it.style?.backgroundColor != null) {
            this.background = GradientDrawable()
            applyBackgroundColor(it)
            applyCornerRadius(it)
        } else {
            styleManagerFactory.applyStyleComponent(component = it, view = this)
        }
        applyStroke(it)
    }
}

internal fun View.applyViewBackgroundAndCorner(backgroundColor: Int?, component: StyleComponent) {
    if (backgroundColor != null) {
        this.background = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(backgroundColor, backgroundColor)
        )

        this.applyCornerRadius(component)
    }
}

internal fun View.applyBackgroundColor(styleWidget: StyleComponent) {
    this.beagleRootView?.let { rootView ->
        styleWidget.style?.backgroundColor?.let { backgroundColor ->
            internalObserveBindChanges(rootView, this, backgroundColor) {
                it?.toAndroidColor()?.let { androidColor ->
                    (this.background as? GradientDrawable)?.setColor(androidColor)
                }
                requestLayout()
            }
        }

    }
}

internal fun View.applyStroke(styleWidget: StyleComponent) {
    val strokeHelper = StrokeHelper()
    this.beagleRootView?.let { rootView ->
        styleWidget.style?.borderColor?.let { borderColor ->
            internalObserveBindChanges(rootView, this, borderColor) {
                strokeHelper.borderColor = it
                strokeHelper.applyStroke(this)
            }
        }

        styleWidget.style?.borderWidth?.let { borderWidth ->
            internalObserveBindChanges(rootView, this, borderWidth) {
                strokeHelper.borderWidth = it
                strokeHelper.applyStroke(this)
            }
        }
    }
}

internal fun View.applyCornerRadius(styleWidget: StyleComponent) {
    val cornerRadiusAux = CornerRadiusHelper()

    this.beagleRootView?.let { rootView ->
            styleWidget.style?.cornerRadius?.let { cornerRadius ->
                cornerRadius.radius?.let { r ->
                    internalObserveBindChanges(rootView, this, r) { radius ->
                        cornerRadiusAux.radius = radius
                        updateCornerRadius(cornerRadiusAux)
                    }
                }

                cornerRadius.topLeft?.let { t ->
                    internalObserveBindChanges(rootView, this, t) { topLeft ->
                        cornerRadiusAux.topLeft = topLeft
                        updateCornerRadius(cornerRadiusAux)
                    }
                }

                cornerRadius.topRight?.let { r ->
                    internalObserveBindChanges(rootView, this, r) { topRight ->
                        cornerRadiusAux.topRight = topRight
                        updateCornerRadius(cornerRadiusAux)
                    }
                }

                cornerRadius.bottomLeft?.let { l ->
                    internalObserveBindChanges(rootView, this, l) { bottomLeft ->
                        cornerRadiusAux.bottomLeft = bottomLeft
                        updateCornerRadius(cornerRadiusAux)
                    }
                }

                cornerRadius.bottomRight?.let { r ->
                    internalObserveBindChanges(rootView, this, r) { bottomRight ->
                        cornerRadiusAux.bottomRight = bottomRight
                        updateCornerRadius(cornerRadiusAux)
                    }
                }
            }
    }
}

private fun View.updateCornerRadius(cornerRadiusAux: CornerRadiusHelper) {
    background?.mutate()
    (this.background as? GradientDrawable)?.cornerRadii = cornerRadiusAux.getFloatArray()
    requestLayout()
}

internal fun View.applyBackgroundFromWindowBackgroundTheme(context: Context) {
    val typedValue = styleManagerFactory
        .getTypedValueByResId(android.R.attr.windowBackground, context)
    if (typedValue.type >= TypedValue.TYPE_FIRST_COLOR_INT &&
        typedValue.type <= TypedValue.TYPE_LAST_COLOR_INT
    ) {
        setBackgroundColor(typedValue.data)
    } else {
        background = ContextCompat.getDrawable(context, typedValue.resourceId)
    }
}

internal var View.beagleRootView: RootView?
    get() = this.getTag(R.id.beagle_root_view_tag) as? RootView
    set(rootView) = this.setTag(R.id.beagle_root_view_tag, rootView)

internal var View.beagleComponent: ServerDrivenComponent?
    get() = this.getTag(R.id.beagle_component_tag) as? ServerDrivenComponent
    set(component) = this.setTag(R.id.beagle_component_tag, component)