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

package br.com.zup.beagle.android.components.layout

import android.view.View
import br.com.zup.beagle.android.action.Action
import br.com.zup.beagle.android.annotation.RegisterWidget
import br.com.zup.beagle.android.context.Bind
import br.com.zup.beagle.android.context.ContextComponent
import br.com.zup.beagle.android.context.ContextData
import br.com.zup.beagle.android.context.expressionOrValueOfNullable
import br.com.zup.beagle.android.utils.ToolbarManager
import br.com.zup.beagle.android.view.BeagleActivity
import br.com.zup.beagle.android.view.ViewFactory
import br.com.zup.beagle.android.view.custom.BeagleFlexView
import br.com.zup.beagle.android.widget.RootView
import br.com.zup.beagle.android.widget.WidgetView
import br.com.zup.beagle.android.widget.core.Accessibility
import br.com.zup.beagle.android.widget.core.BeagleJson
import br.com.zup.beagle.android.widget.core.Flex
import br.com.zup.beagle.android.widget.core.ServerDrivenComponent
import br.com.zup.beagle.android.widget.core.SingleChildComponent
import br.com.zup.beagle.android.widget.core.Style

/**
 * The SafeArea will enable Safe areas to help you place your views within the visible portion of the overall interface.
 * <p>
 * Note: This class is only used to iOS SafeArea
 * </p>
 *
 * @param top enable the safeArea constraint only on the TOP of the screen view.
 * @param leading enable the safeArea constraint only on the LEFT side of the screen view.
 * @param bottom enable the safeArea constraint only on the BOTTOM of the screen view.
 * @param trailing enable the safeArea constraint only on the RIGHT of the screen view.
 *
 */
@BeagleJson(name = "safeArea")
data class SafeArea(
    val top: Boolean? = null,
    val leading: Boolean? = null,
    val bottom: Boolean? = null,
    val trailing: Boolean? = null,
)

/**
 *  Defines a list of navigation bar items.
 *
 * @see Accessibility
 *
 * @param text defines the description for the item
 * @param image defines the local image for the item
 * @param action defines an action to be called when the item is clicked on
 * @param accessibility defines the accessibility details for the item
 *
 */
@BeagleJson(name = "navigationBarItem")
data class NavigationBarItem(
    val text: String,
    val image: Bind<String>?,
    val onPress: List<Action>,
    val accessibility: Accessibility? = null,
) {
    constructor(
        text: String,
        image: String? = null,
        onPress: List<Action>,
        accessibility: Accessibility? = null,
    ) : this(text, expressionOrValueOfNullable(image), onPress, accessibility)
}

/**
 *  Typically displayed at the top of the window, containing buttons for navigating within a hierarchy of screens
 *
 * @see Accessibility
 * @see NavigationBarItem
 *
 * @param title define the Title on the navigation bar
 * @param showBackButton enable a back button into your action bar/navigation bar
 * @param styleId could define a custom layout for your action bar/navigation  bar
 * @param navigationBarItems defines a List of navigation bar items.
 * @param backButtonAccessibility define accessibility details for the item
 *
 */
@BeagleJson(name = "navigationBar")
data class NavigationBar(
    val title: String,
    val showBackButton: Boolean = true,
    val styleId: String? = null,
    val navigationBarItems: List<NavigationBarItem>? = null,
    val backButtonAccessibility: Accessibility? = null,
)

/**
 * The screen element will help you define the screen view structure.
 * By using this component you can define configurations like whether or
 * not you want to use safe areas or display a tool bar/navigation bar.
 *
 * @see NavigationBar
 * @see ServerDrivenComponent
 * @see Style
 *
 * @param safeArea
 *                      enable Safe areas to help you place your views within the visible
 *                      portion of the overall interface.
 *                      By default it is not enabled and it wont constrain considering any safe area.
 * @param navigationBar enable a action bar/navigation bar into your view. By default it is set as null.
 * @param child
 *                  define the child elements on this screen.
 *                  It could be any visual component that extends the ServerDrivenComponent.1
 * @param context define the contextData that be set to screen.
 *
 */
@RegisterWidget("screenComponent")
data class Screen(
    val safeArea: SafeArea? = null,
    val navigationBar: NavigationBar? = null,
    override val child: ServerDrivenComponent,
    override val context: ContextData? = null,
) : WidgetView(), ContextComponent, SingleChildComponent {

    @Transient
    private val toolbarManager: ToolbarManager = ToolbarManager()

    override fun buildView(rootView: RootView): View {
        val container = ViewFactory.makeBeagleFlexView(rootView, Style(flex = Flex(grow = 1.0)))

        addNavigationBarIfNecessary(rootView, navigationBar, container)

        container.addView(child)

        return container
    }

    private fun addNavigationBarIfNecessary(
        rootView: RootView,
        navigationBar: NavigationBar?,
        container: BeagleFlexView,
    ) {

        (rootView.getContext() as? BeagleActivity)?.let {
            if (navigationBar != null) {
                configNavigationBar(rootView, navigationBar, container)
            } else {
                hideNavigationBar(it)
            }
        }
    }

    private fun hideNavigationBar(context: BeagleActivity) {
        context.supportActionBar?.apply {
            hide()
        }

        context.getToolbar().visibility = View.GONE
    }

    private fun configNavigationBar(
        rootView: RootView,
        navigationBar: NavigationBar,
        container: BeagleFlexView,
    ) {
        (rootView.getContext() as? BeagleActivity)?.let {
            toolbarManager.configureNavigationBarForScreen(it, navigationBar)
            toolbarManager.configureToolbar(rootView, navigationBar, container, this)
        }
    }
}
