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

package br.com.zup.beagle.android.utils

import android.view.View
import androidx.fragment.app.Fragment
import br.com.zup.beagle.R
import br.com.zup.beagle.android.action.Action
import br.com.zup.beagle.android.context.Bind
import br.com.zup.beagle.android.context.ContextData
import br.com.zup.beagle.android.setup.BeagleConfigurator
import br.com.zup.beagle.android.view.ViewFactory
import br.com.zup.beagle.android.widget.FragmentRootView
import br.com.zup.beagle.android.widget.RootView
import br.com.zup.beagle.android.widget.core.IdentifierComponent
import br.com.zup.beagle.android.widget.core.ServerDrivenComponent

/**
 * Execute a list of actions and create an implicit context with eventName and eventValue.
 * @property rootView from buildView
 * @property origin view that triggered the action
 * @property actions is the list of actions to be executed
 * @property context is the property that will contain the implicit context data, id and value in ContextData class
 * this could be a primitive or a object that will be serialized to JSON
 */
fun ServerDrivenComponent.handleEvent(
    rootView: RootView,
    origin: View,
    actions: List<Action>,
    context: ContextData? = null,
    analyticsValue: String? = null,

    ) {
    contextActionExecutor.executeActions(
        rootView,
        origin,
        this,
        actions,
        context,
        analyticsValue
    )
}

/**
 * Execute an action and create the implicit context with eventName and eventValue (optional).
 * @property rootView from buildView
 * @property origin view that triggered the action
 * @property action is the action to be executed
 * @property context is the property that will contain the implicit context data, id and value in ContextData class
 * this could be a primitive or a object that will be serialized to JSON
 */
fun ServerDrivenComponent.handleEvent(
    rootView: RootView,
    origin: View,
    action: Action,
    context: ContextData? = null,
    analyticsValue: String? = null,
) {
    contextActionExecutor.executeActions(
        rootView,
        origin,
        this,
        listOf(action),
        context,
        analyticsValue
    )
}

/**
 * Observe a specific Bind to changes. If the Bind is type of Value, then the actual value will be returned.
 * But if the value is an Expression, then the evaluation will be make.
 * @property rootView from buildView
 * @property view that will receive the binding
 * @property bind is the value that will retrieved or observed
 * @property observes is function that will be called when a expression is evaluated
 */
fun <T> ServerDrivenComponent.observeBindChanges(
    rootView: RootView,
    view: View,
    bind: Bind<T>,
    observes: Observer<T?>,
) {
    internalObserveBindChanges(rootView, view, bind, observes)
}

internal fun <T> internalObserveBindChanges(
    rootView: RootView,
    view: View,
    bind: Bind<T>,
    observes: Observer<T?>,
) {
    bind.observe(rootView, view, observes)
}

internal fun ServerDrivenComponent.toView(
    fragment: Fragment,
    idView: Int = R.id.beagle_default_id,
    screenIdentifier: String? = null,
    beagleConfigurator: BeagleConfigurator
): View =
    this.toView(
        FragmentRootView(
            fragment,
            idView,
            this.getServerDrivenIdentifier(screenIdentifier),
            config = beagleConfigurator
        )
    )

internal fun ServerDrivenComponent.toView(
    rootView: RootView,
    generateIdManager: GenerateIdManager = GenerateIdManager(rootView),
): View {
    generateIdManager.createSingleManagerByRootViewId()
    val view = ViewFactory.makeBeagleFlexView(rootView).apply {
        id = rootView.getParentId()
        addView(this@toView)
    }
    view.addListenerOnViewDetachedFromWindow {
        generateIdManager.onViewDetachedFromWindow(view)
    }
    return view
}

private fun ServerDrivenComponent.getServerDrivenIdentifier(screenIdentifier: String?): String {
    screenIdentifier?.let {
        return it
    }
    var identifier = ""
    if (this is IdentifierComponent) {
        this.id?.let {
            identifier = it
        }
    }
    return identifier
}

