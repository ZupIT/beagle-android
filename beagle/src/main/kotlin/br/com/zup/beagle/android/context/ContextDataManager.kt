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

package br.com.zup.beagle.android.context

import android.view.View
import br.com.zup.beagle.android.action.SetContextInternal
import br.com.zup.beagle.android.context.tokenizer.Token
import br.com.zup.beagle.android.context.tokenizer.TokenBinding
import br.com.zup.beagle.android.context.tokenizer.TokenFunction
import br.com.zup.beagle.android.logger.BeagleMessageLogs
import br.com.zup.beagle.android.setup.BeagleConfigurator
import br.com.zup.beagle.android.utils.Observer
import br.com.zup.beagle.android.utils.findParentContextWithId
import br.com.zup.beagle.android.utils.getAllParentContexts
import br.com.zup.beagle.android.utils.getContextBinding
import br.com.zup.beagle.android.utils.getContextId
import br.com.zup.beagle.android.utils.setContextBinding
import br.com.zup.beagle.android.utils.setContextData

private const val GLOBAL_CONTEXT_ID = Int.MAX_VALUE

internal class ContextDataManager(
    private val beagleConfigurator: BeagleConfigurator = BeagleConfigurator.factory(),
    private val contextDataEvaluation: ContextDataEvaluation = ContextDataEvaluation(beagleConfigurator),
    private val contextDataManipulator: ContextDataManipulator = ContextDataManipulator()
) {
    private val contextObservers = mutableMapOf<String, InternalContextObserver>()

    private var globalContext: ContextBinding = ContextBinding(GlobalContext.getContext())
    private val contexts = mutableMapOf<Int, Set<ContextBinding>>()
    private val contextsWithoutId = mutableMapOf<View, Set<ContextBinding>>()
    private val viewBinding = mutableMapOf<View, MutableSet<Binding<*>>>()

    /**
     * When trying to add [Binding] and the context does not exist in the tree
     * to link then this [Binding] is saved in this list
     */
    private val bindingsWithoutContextData = mutableListOf<Binding<*>>()

    private val globalContextObserver: GlobalContextObserver = {
        updateGlobalContext(it)
    }

    init {
        contexts[GLOBAL_CONTEXT_ID] = setOf(globalContext)
        GlobalContext.observeGlobalContextChange(globalContextObserver)
    }

    fun getContextObserver(contextId: String): InternalContextObserver? = contextObservers[contextId]

    fun clearContexts() {
        contextsWithoutId.clear()
        contexts.clear()
        viewBinding.clear()
        GlobalContext.clearObserverGlobalContext(globalContextObserver)
    }

    fun setIdToViewWithContext(view: View) {
        contextsWithoutId[view]?.apply {
            contexts[view.id]?.let {
                it.forEach { binding ->
                    view.setContextData(binding.context, beagleConfigurator.moshi)
                }
            } ?: run {
                contexts[view.id] = this
            }
            contextsWithoutId.remove(view)
        }
    }

    fun onViewIdChanged(oldId: Int, newId: Int, view: View) {
        contexts[oldId]?.let { contextBinding ->
            if (!contexts.containsKey(newId)) {
                contexts.put(newId, contextBinding)
            } else {
                contexts[newId]?.let {
                    updateContextAndReference(view, it)
                }
            }
        }
        contexts.remove(oldId)
    }

    fun addContext(view: View, contextDataList: List<ContextData>, shouldOverrideExistingContext: Boolean = false) {
        contextDataList.forEach {
            addContext(view, it, shouldOverrideExistingContext)
        }
    }

    fun addContextObserver(contextId: String,
                           contextObserver: InternalContextObserver) =
        this.contextObservers.put(contextId, contextObserver)

    fun removeContextObserver(contextId: String) = this.contextObservers.remove(contextId)

    fun addContext(view: View, context: ContextData, shouldOverrideExistingContext: Boolean = false) {
        if (context.id == globalContext.context.id) {
            BeagleMessageLogs.globalKeywordIsReservedForGlobalContext()
            return
        }

        val existingContextList = contexts[view.id]

        if (existingContextList != null) {
            if (shouldOverrideExistingContext) {
                updateContextAndReference(view, context)
            } else {
                view.setContextBinding(existingContextList)
                existingContextList.forEach { it.bindings.clear() }
            }
        } else {
            updateContextAndReference(view, context)
        }

        tryLinkContextInBindWithoutContext(view)
    }

    private fun updateContextAndReference(view: View, contexts: Set<ContextBinding>) {
        contexts.forEach {
            updateContextAndReference(view, it.context)
        }
    }

    private fun updateContextAndReference(view: View, context: ContextData) {
        view.setContextData(context, beagleConfigurator.moshi)
        view.getContextBinding()?.let {
            if (view.id != View.NO_ID) {
                contexts[view.id] = it
            } else {
                contextsWithoutId[view] = it
            }
        }
    }

    fun getListContextData(view: View) = contexts[view.id]?.map { contextBinding -> contextBinding.context }

    fun getContextData(contextDataId: String): ContextData? = contexts.firstNotNullOfOrNull { contextIdAndBinding ->
        contextIdAndBinding.value.firstOrNull { contextBinding -> contextBinding.context.id == contextDataId }?.context
    }

    fun restoreContext(view: View) {
        contexts[view.id]?.let {
            view.setContextBinding(it)
        }
    }

    fun <T> addBinding(view: View, bind: Bind.Expression<T>, observer: Observer<T?>) {
        val bindings: MutableSet<Binding<*>> = viewBinding[view] ?: mutableSetOf()
        bindings.add(Binding(
            observer = observer,
            bind = bind
        ))
        viewBinding[view] = bindings
    }

    fun linkBindingToContextAndEvaluateThem(view: View) {
        if (viewBinding.contains(view)) {
            val contextStack = view.getAllParentContextWithGlobal()
            viewBinding[view]?.forEach { binding ->
                val bindingTokens = binding.bind.filterBindingTokens()
                val result = tryNotifyBindingTokens(bindingTokens, contextStack, binding)
                if (!result) {
                    bindingsWithoutContextData.add(binding)
                }
            }
            viewBinding.remove(view)
        } else {
            view.getContextBinding()?.forEach {
                notifyBindingChanges(it)
            }
        }
    }

    /**
     * the @return is `false` when doesn't find the [ContextData] in the tree
     *
     */
    private fun tryNotifyBindingTokens(
        bindingTokens: List<String>,
        contextStack: MutableList<ContextBinding>,
        binding: Binding<*>
    ): Boolean {
        return if (bindingTokens.isNotEmpty()) {
            var result = false
            bindingTokens.forEach { expression ->
                result = tryLinkBindingsToNotifyListeners(expression, contextStack, binding)
            }
            result
        } else {
            val value = contextDataEvaluation.evaluateBindExpression(listOf(), binding.bind)
            binding.notifyChanges(value)
            true
        }
    }

    /**
     * the @return is `false` when doesn't find the [ContextData] in the tree
     *
     */
    private fun tryLinkBindingsToNotifyListeners(
        expression: String,
        contextStack: MutableList<ContextBinding>,
        binding: Binding<*>
    ): Boolean {
        val contextId = expression.getContextId()

        val contextBinding = contextStack.find { contextBinding -> contextBinding.context.id == contextId }
            ?: return false

        contextBinding.bindings.add(binding)
        notifyBindingChanges(contextBinding)
        return true
    }

    fun getContextsFromBind(originView: View, binding: Bind.Expression<*>): List<ContextData> {
        val parentContexts = originView.getAllParentContextWithGlobal()
        val contextIds = binding.filterBindingTokens().map { it.getContextId() }
        return parentContexts
            .filter { contextBinding -> contextIds.contains(contextBinding.context.id) }
            .map { it.context }
    }

    @Suppress("NestedBlockDepth")
    fun updateContext(view: View, setContextInternal: SetContextInternal) {

        if (setContextInternal.contextId == globalContext.context.id) {
            GlobalContext.set(setContextInternal.value, setContextInternal.path, beagleConfigurator.moshi)
        } else {
            view.findParentContextWithId(setContextInternal.contextId)?.let { parentView ->
                val currentContextBinding = parentView.getContextBinding()
                currentContextBinding?.forEach { contextBinding ->
                    setContextValue(contextBinding, setContextInternal)
                }
            }
        }
        notifyContextChanges(setContextInternal)
    }

    private fun notifyContextChanges(
        context: SetContextInternal
    ) {
        contextObservers.filterKeys { it == context.contextId }.forEach {
            it.value.invoke(context)
        }
    }

    private fun setContextValue(
        contextBinding: ContextBinding,
        setContextInternal: SetContextInternal
    ) {

        if(contextBinding.context.id == setContextInternal.contextId) {
            val result = contextDataManipulator.set(
                contextBinding.context,
                setContextInternal.path,
                setContextInternal.value
            )

            if (result is ContextSetResult.Succeed) {
                contextBinding.context = result.newContext
                contextBinding.cache.evictAll()
                notifyBindingChanges(contextBinding)
            }
        }
    }

    internal fun notifyBindingChanges(contextBinding: ContextBinding) {
        val contextData = contextBinding.context
        val bindings = contextBinding.bindings

        bindings.forEach { binding ->
            val value = contextDataEvaluation.evaluateBindExpression(
                contextData,
                contextBinding.cache,
                binding.bind,
                binding.evaluatedExpressions
            )
            binding.notifyChanges(value)
        }
    }

    private fun View.getAllParentContextWithGlobal(): MutableList<ContextBinding> {
        val contexts = mutableListOf<ContextBinding>()
        contexts.addAll(getAllParentContexts())
        contexts.add(globalContext)
        return contexts
    }

    private fun updateGlobalContext(contextData: ContextData) {
        globalContext = globalContext.copy(context = contextData)
        globalContext.cache.evictAll()
        contexts[GLOBAL_CONTEXT_ID] = setOf(globalContext)
        notifyBindingChanges(globalContext)
    }

    private fun <T> Bind.Expression<T>.filterBindingTokens(): List<String> {
        val bindings = mutableListOf<String>()

        fun addBindings(token: Token) {
            if (token is TokenFunction) {
                token.value.forEach { paramToken ->
                    addBindings(paramToken)
                }
            } else if (token is TokenBinding) {
                bindings.add(token.value)
            }
        }

        expressions.forEach { expressionToken ->
            addBindings(expressionToken.token)
        }

        return bindings
    }

    /**
     * When adding new [ContextData] this function checks if existing [Binding] without link
     * and if the new [ContextData] match with the [Binding] the link will be made
     */
    fun tryLinkContextInBindWithoutContext(originView: View) {
        val contextStack = originView.getAllParentContextWithGlobal()
        val removeIndexes = mutableSetOf<Int>()
        bindingsWithoutContextData.forEachIndexed { index, it ->
            val bindingTokens = it.bind.filterBindingTokens()
            val result = tryNotifyBindingTokens(bindingTokens, contextStack, it)
            if (result) {
                removeIndexes.add(index)
            }
        }
        val itRemove = removeIndexes.iterator()
        itRemove.forEach {
            bindingsWithoutContextData.removeAt(it)
        }

    }
}
