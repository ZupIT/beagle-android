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

package br.com.zup.beagle.android.action

import android.view.View
import br.com.zup.beagle.android.analytics.ActionAnalyticsConfig
import br.com.zup.beagle.android.context.normalizeContextValue
import br.com.zup.beagle.android.logger.BeagleLoggerProxy
import br.com.zup.beagle.android.utils.evaluateExpression
import br.com.zup.beagle.android.utils.generateViewModelInstance
import br.com.zup.beagle.android.view.viewmodel.ScreenContextViewModel
import br.com.zup.beagle.android.widget.RootView
import br.com.zup.beagle.android.widget.core.BeagleJson
import br.com.zup.beagle.android.annotation.ContextDataValue

internal data class SetContextInternal(
    val contextId: String,
    val value: Any,
    val path: String? = null,
)

/**
 * The setContext class is responsible for changing the value of a context.
 *
 * @param contextId Required. Wait context id.
 * @param value Required. New value to be applied in the context.
 * @param path Specific context point to be changed in the case of arrays and maps <key, value>.
 */

@BeagleJson(name = "setContext")
data class SetContext(
    val contextId: String,
    @ContextDataValue
    val value: Any,
    val path: String? = null,
    override var analytics: ActionAnalyticsConfig? = null,
) : AnalyticsAction {

    override fun execute(rootView: RootView, origin: View) {
        val viewModel = rootView.generateViewModelInstance<ScreenContextViewModel>(
            ScreenContextViewModel.provideFactory(rootView.getBeagleConfigurator())
        )
        try {
            val value = toInternalSetContext(rootView, origin)
            viewModel.updateContext(origin, value)
        } catch (ex: Exception) {
            BeagleLoggerProxy.warning(ex.message ?: "")
        }
    }

    private fun toInternalSetContext(rootView: RootView, origin: View) = SetContextInternal(
        contextId = this.contextId,
        value = evaluateExpression(rootView, origin, this.value)?.normalizeContextValue(
            rootView.getBeagleConfigurator().moshi)
            ?: throw IllegalStateException("SetContext with id=${this.contextId} evaluated to null"),
        path = this.path
    )
}
