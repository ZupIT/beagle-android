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

package br.com.zup.beagle.sample.widgets

import android.view.View
import android.widget.LinearLayout
import br.com.zup.beagle.android.action.Action
import br.com.zup.beagle.android.annotation.RegisterWidget
import br.com.zup.beagle.android.context.Bind
import br.com.zup.beagle.android.utils.handleEvent
import br.com.zup.beagle.android.utils.observeBindChanges
import br.com.zup.beagle.android.widget.RootView
import br.com.zup.beagle.android.widget.WidgetView

enum class ActionExecutorType{ NAVIGATE, NOTHING }

@RegisterWidget("actionExecutor")
class ActionExecutor(
    var trigger: Bind<ActionExecutorType>,
    val actions: List<Action>
): WidgetView(){
    override fun buildView(rootView: RootView): View {
        val view = LinearLayout(rootView.getContext())
        trigger.let {
            this@ActionExecutor.observeBindChanges(
                rootView = rootView,
                view = view,
                bind = it,
                observes = { shouldExecute ->
                    shouldExecute?.apply {
                        if (this == ActionExecutorType.NAVIGATE){
                                this@ActionExecutor.handleEvent(
                                    rootView = rootView,
                                    origin = view,
                                    actions = actions,
                                    context = null,
                                    analyticsValue = null
                                )
                        }
                    }
                }
            )
        }

        return view
    }

}