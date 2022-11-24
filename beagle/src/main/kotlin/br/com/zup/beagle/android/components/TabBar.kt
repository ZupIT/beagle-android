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

package br.com.zup.beagle.android.components

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import br.com.zup.beagle.R
import br.com.zup.beagle.android.action.Action
import br.com.zup.beagle.android.annotation.RegisterWidget
import br.com.zup.beagle.android.context.Bind
import br.com.zup.beagle.android.context.ContextData
import br.com.zup.beagle.android.utils.dp
import br.com.zup.beagle.android.utils.handleEvent
import br.com.zup.beagle.android.utils.observeBindChanges
import br.com.zup.beagle.android.utils.styleManagerFactory
import br.com.zup.beagle.android.view.ViewFactory
import br.com.zup.beagle.android.view.custom.BeagleFlexView
import br.com.zup.beagle.android.widget.RootView
import br.com.zup.beagle.android.widget.WidgetView
import br.com.zup.beagle.android.widget.core.Flex
import br.com.zup.beagle.android.widget.core.Style
import com.google.android.material.tabs.TabLayout

private val TAB_BAR_HEIGHT = 48.dp()

/**
 * TabBar is a component responsible to display a tab layout.
 * It works by displaying tabs that can change a context when clicked.
 *
 * @param items define yours tabs title and icon
 * @param styleId reference a native style in your local styles file to be applied on this view.
 * @param currentTab define the expression that is observer to change the current tab selected
 * @param onTabSelection define a list of action that will be executed when a tab is selected
 *
 */
@RegisterWidget("tabBar")
data class TabBar(
    val items: List<TabBarItem>,
    val styleId: String? = null,
    val currentTab: Bind<Int>? = null,
    val onTabSelection: List<Action>? = null,
) : WidgetView() {

    override fun buildView(rootView: RootView): View {
        val containerFlex = Style(flex = Flex(grow = 1.0))

        val container = ViewFactory.makeBeagleFlexView(rootView, containerFlex)
        val tabBar = makeTabLayout(rootView, container)
        configTabSelectedListener(tabBar, rootView)
        configCurrentTabObserver(tabBar, container, rootView)
        container.addView(tabBar)
        return container
    }

    private fun makeTabLayout(rootView: RootView, container: BeagleFlexView): TabLayout = ViewFactory.makeTabLayout(
        rootView.getContext(),
        styleManagerFactory.getTabViewStyle(styleId)
    ).apply {
        layoutParams =
            ViewFactory.makeFrameLayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                TAB_BAR_HEIGHT
            )
        tabMode = TabLayout.MODE_SCROLLABLE
        tabGravity = TabLayout.GRAVITY_FILL
        configTabBarStyle()
        addTabs(rootView, container)
    }

    private fun TabLayout.configTabBarStyle() {
        styleManagerFactory.getTabBarTypedArray(context, styleId).apply {
            setSelectedTabIndicatorColor(
                getColor(
                    R.styleable.BeagleTabBarStyle_tabIndicatorColor,
                    styleManagerFactory.getTypedValueByResId(R.attr.colorAccent, context).data
                )
            )
            tabIconTint = getColorStateList(R.styleable.BeagleTabBarStyle_tabIconTint)
            recycle()
        }
    }

    private fun TabLayout.addTabs(rootView: RootView, container: BeagleFlexView) {
        for (i in items.indices) {
            addTab(newTab().apply {
                text = items[i].title
                items[i].icon?.let { imagePath ->

                    observeBindChanges(rootView, container, imagePath.mobileId) { iconPath ->
                        iconPath?.let {
                            icon = getIconFromResources(rootView, iconPath)
                        }
                    }
                }
            })
        }
    }

    private fun getIconFromResources(rootView: RootView, icon: String): Drawable? {
        val context = rootView.getContext()
        return rootView.getBeagleConfigurator().designSystem?.image(icon)?.let {
            ContextCompat.getDrawable(context, it)
        }
    }

    private fun configTabSelectedListener(tabBar: TabLayout, rootView: RootView) {
        tabBar.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                onTabSelection?.let {
                    tab?.let { tab ->
                        handleEvent(
                            rootView,
                            tabBar,
                            it,
                            ContextData("onTabSelection", value = tab.position),
                            analyticsValue = "onTabSelected"
                        )
                    }
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun configCurrentTabObserver(tabBar: TabLayout, container: BeagleFlexView, rootView: RootView) {
        currentTab?.let {
            observeBindChanges(rootView, container, it) { position ->
                position?.let { newPosition ->
                    tabBar.getTabAt(newPosition)?.select()
                }
            }
        }
    }
}

/**
 * Define the view has in the tab view
 *
 * @param title displays the text on the TabView component. If it is null or not declared it won't display any text.
 * @param icon
 *                  display an icon image on the TabView component.
 *                  If it is left as null or not declared it won't display any icon.
 *
 */
data class TabBarItem(
    val title: String? = null,
    val icon: ImagePath.Local? = null,
)
