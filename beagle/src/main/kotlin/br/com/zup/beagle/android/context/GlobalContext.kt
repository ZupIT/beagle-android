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

import br.com.zup.beagle.android.action.SetContextInternal
import br.com.zup.beagle.android.data.serializer.BeagleMoshi
import br.com.zup.beagle.android.setup.BeagleSdkWrapper

import com.squareup.moshi.Moshi

typealias GlobalContextObserver = (ContextData) -> Unit
internal typealias InternalContextObserver = (SetContextInternal) -> Unit

/**
 * A Global Context is a object that can assume as value of any type of variable, like a map defines a subset
 * of key/value or complex JSONs objects that defines object trees.
 *
 * It works exactly like the Context, however in a global scope, meaning that it will exists while the application is
 * still running (even on the background), which allows it to be accessed from any application point, being a component
 * or an action linked to a component or even programmatically.
 */
object GlobalContext {

    internal const val GLOBAL_KEY = "global"

    private var globalContext = ContextData(id = GLOBAL_KEY, value = "")
    private val globalContextObservers = mutableListOf<GlobalContextObserver>()
    private val contextDataManipulator = ContextDataManipulator()

    /**
     *  Get the content in context.
     *
     * @param path Represents the path that it will contain the information.
     */
    fun get(path: String? = null): Any? {
        if (path.isNullOrEmpty()) {
            return globalContext.value
        }

        return contextDataManipulator.get(globalContext, path)
    }

    /**
     * Set the content in context.
     *
     * @param value represents content that can be any kind.
     * @param path represents the path that it will save this information.
     */
    fun set(value: Any, path: String? = null, config: BeagleSdkWrapper? = null) {
        set(value, path, config?.let { BeagleMoshi.moshiFactory(config) } ?: BeagleMoshi.moshi )
    }

    /**
     * Set the content in context.
     *
     * @param value represents content that can be any kind.
     * @param path represents the path that it will save this information.
     */
    internal fun set(value: Any, path: String? = null, moshi: Moshi = BeagleMoshi.moshi) {
        val result = contextDataManipulator.set(globalContext, path, value.normalizeContextValue(moshi))
        notifyContextChanges(result)
    }

    /**
     * Clear content has in the context
     *
     * @param path Optional. Represents the path you want to remove.
     */
    fun clear(path: String? = null) {
        val result = contextDataManipulator.clear(globalContext, path)
        notifyContextChanges(result)
    }

    internal fun observeGlobalContextChange(observer: GlobalContextObserver) {
        globalContextObservers.add(observer)
    }

    internal fun clearObserverGlobalContext(observer: GlobalContextObserver) {
        globalContextObservers.remove(observer)
    }

    internal fun getContext() = globalContext

    private fun notifyContextChanges(result: ContextSetResult) {
        if (result is ContextSetResult.Succeed) {
            globalContext = result.newContext
            globalContextObservers.forEach {
                it.invoke(globalContext)
            }
        }
    }
}
