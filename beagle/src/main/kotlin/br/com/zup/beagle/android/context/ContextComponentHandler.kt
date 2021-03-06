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

package br.com.zup.beagle.android.context

import android.view.View
import br.com.zup.beagle.android.view.custom.InternalBeagleFlexView
import br.com.zup.beagle.android.view.viewmodel.ScreenContextViewModel
import br.com.zup.beagle.android.widget.core.ServerDrivenComponent

internal class ContextComponentHandler {

    fun handleComponent(
        builtView: View,
        viewModel: ScreenContextViewModel,
        component: ServerDrivenComponent
    ) {
        addListenerToHandleContext(viewModel, builtView)
        addContext(viewModel, builtView, component)
    }

    private fun addListenerToHandleContext(viewModel: ScreenContextViewModel, view: View) {
        if (view !is InternalBeagleFlexView) {
            view.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                override fun onViewDetachedFromWindow(v: View?) {}

                override fun onViewAttachedToWindow(v: View?) {
                    v?.let {
                        viewModel.linkBindingToContextAndEvaluateThem(it)
                    }
                }
            })
        }
    }

    private fun addContext(viewModel: ScreenContextViewModel, view: View, component: ServerDrivenComponent) {
        if (component is ContextComponent) {
            component.context?.let { context ->
                viewModel.addContext(view, context)
            }
        }
    }
}
