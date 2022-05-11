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

private data class ImplicitContext(
    val sender: Any,
    val context: ContextData,
    val caller: Any
)

internal class ImplicitContextManager {

    private val implicitContextData = mutableListOf<ImplicitContext>()

    // Sender is who created the implicit context
    fun addImplicitContext(contextData: ContextData, sender: Any, originView: Any) {
        implicitContextData.removeAll { it.sender == sender  ||
            it.caller === originView && contextData.id == it.context.id }
        implicitContextData += ImplicitContext(
            sender = sender,
            context = contextData,
            caller = originView
        )
    }

    // BindCaller is who owns the Bind Attribute
    fun getImplicitContextForView(origin: View): List<ContextData> {
        val contexts = mutableListOf<ContextData>()
        findMoreContexts(origin, contexts)

        return contexts
    }

    private fun findMoreContexts(
        toCompare: Any,
        contexts: MutableList<ContextData>
    ) {
        implicitContextData.forEach { implicitContext ->
            if (implicitContext.caller === toCompare) {
                contexts += implicitContext.context
            }
        }
    }
}