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

import br.com.zup.beagle.android.action.Action
import br.com.zup.beagle.android.annotation.RegisterWidget
import br.com.zup.beagle.android.context.Bind
import br.com.zup.beagle.android.context.ContextData
import br.com.zup.beagle.android.utils.handleEvent
import br.com.zup.beagle.android.utils.observeBindChanges
import br.com.zup.beagle.android.widget.RootView
import br.com.zup.beagle.android.widget.WidgetView
import br.com.zup.beagle.sample.components.ChannelCardView
import br.com.zup.beagle.sample.components.ComposeTextData
import br.com.zup.beagle.sample.components.ComposeTextView

@RegisterWidget
data class ComposeText(
    val text: Bind<String>,
    val onClick: List<Action>? = null
) : WidgetView() {
    override fun buildView(rootView: RootView): ComposeTextView =
        ComposeTextView(rootView.getContext()).also {
        val composeTextData = ComposeTextData()
            composeTextData.onClick = {
                val actions = onClick ?: emptyList()
                this@ComposeText.handleEvent(
                    rootView = rootView,
                    origin = it,
                    actions = actions
                )
            }
        observeBindChanges(rootView, it, this@ComposeText.text) { newText ->
            composeTextData.text = newText ?: ""
            it.value = composeTextData
        }

    }
}
