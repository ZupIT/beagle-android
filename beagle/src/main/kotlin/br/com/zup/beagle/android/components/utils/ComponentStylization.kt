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

package br.com.zup.beagle.android.components.utils

import android.view.View
import br.com.zup.beagle.R
import br.com.zup.beagle.android.annotation.RegisterWidget
import br.com.zup.beagle.android.data.serializer.createNamespace
import br.com.zup.beagle.android.utils.applyStyle
import br.com.zup.beagle.android.utils.toAndroidId
import br.com.zup.beagle.android.widget.RootView
import br.com.zup.beagle.android.widget.WidgetView
import br.com.zup.beagle.android.widget.core.BeagleJson
import br.com.zup.beagle.android.widget.core.IdentifierComponent
import br.com.zup.beagle.android.widget.core.ServerDrivenComponent

class ComponentStylization<T : ServerDrivenComponent>(
    private val accessibilitySetup: AccessibilitySetup = AccessibilitySetup(),
) {
    fun apply(rootView: RootView, view: View, component: T) {
        view.applyStyle(component, rootView.getBeagleConfigurator().designSystem)
        (component as? IdentifierComponent)?.id?.let {
            view.id = it.toAndroidId()
            view.setTag(R.id.beagle_component_id, it)
        }
        view.setTag(R.id.beagle_component_type, getComponentType(rootView, component))
        accessibilitySetup.applyAccessibility(view, component)
    }

    private fun getComponentType(rootView: RootView, component: ServerDrivenComponent) =
        if (isCustomWidget(component, rootView.getBeagleConfigurator().registeredWidgets))
            getWidgetName("custom", component::class.java)
        else
            getWidgetName("beagle", component::class.java)

    private fun isCustomWidget(component: ServerDrivenComponent, registeredWidgets: List<Class<WidgetView>>) =
        registeredWidgets.contains(component::class.java)

    private fun getWidgetName(appNameSpace: String, clazz: Class<*>): String {
        var name = ""
        clazz.getAnnotation(RegisterWidget::class.java)?.let {
            name = it.name
        }
        if (name.isEmpty()) {
            clazz.getAnnotation(BeagleJson::class.java)?.let {
                name = it.name
            }
        }
        if (name.isEmpty()) {
            name = clazz.simpleName
        }
        return createNamespace(appNameSpace, clazz, name)
    }
}
