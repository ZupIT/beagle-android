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

package br.com.zup.beagle.android.utils

import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import br.com.zup.beagle.android.data.serializer.BeagleJsonSerializerFactory
import br.com.zup.beagle.android.networking.RequestData
import br.com.zup.beagle.android.view.ViewFactory
import br.com.zup.beagle.android.view.custom.OnServerStateChanged
import br.com.zup.beagle.android.view.viewmodel.AnalyticsViewModel
import br.com.zup.beagle.android.view.viewmodel.ScreenContextViewModel
import br.com.zup.beagle.android.widget.ActivityRootView
import br.com.zup.beagle.android.widget.FragmentRootView
import br.com.zup.beagle.android.widget.RootView

internal var beagleSerializerFactory = BeagleJsonSerializerFactory.serializer

/**
 * Load a ServerDrivenComponent into this ViewGroup
 * @property activity that is parent of this view
 * @property requestData to create your request data to fetch the component
 */
fun ViewGroup.loadView(
    activity: AppCompatActivity,
    requestData: RequestData,
) {
    loadView(
        viewGroup = this,
        rootView = ActivityRootView(activity, this.id, requestData.url),
        requestData = requestData,
        listener = null,
    )
}

/**
 * Load a ServerDrivenComponent into this ViewGroup
 * @property fragment that is parent of this view
 * @property requestData to create your request data to fetch the component
 */
fun ViewGroup.loadView(
    fragment: Fragment,
    requestData: RequestData,
) {
    loadView(
        viewGroup = this,
        rootView = FragmentRootView(fragment, this.id, requestData.url),
        requestData = requestData,
        listener = null,
    )
}

/**
 * Load a ServerDrivenComponent into this ViewGroup
 * @property activity that is parent of this view
 * @property requestData to create your request data to fetch the component
 * @property listener is called when the loading is Started, Finished and Success
 */
fun ViewGroup.loadView(
    activity: AppCompatActivity,
    requestData: RequestData,
    listener: OnServerStateChanged? = null,
) {
    loadView(
        this,
        ActivityRootView(activity, this.id, requestData.url),
        requestData,
        listener,
    )
}

/**
 * Load a ServerDrivenComponent into this ViewGroup
 * @property fragment that is parent of this view
 * @property requestData to create your request data to fetch the component
 * @property listener is called when the loading is Started, Finished and Success
 */
fun ViewGroup.loadView(
    fragment: Fragment,
    requestData: RequestData,
    listener: OnServerStateChanged? = null,
) {
    loadView(
        this,
        FragmentRootView(fragment, this.id, requestData.url),
        requestData,
        listener
    )
}


@Suppress("LongParameterList")
private fun loadView(
    viewGroup: ViewGroup,
    rootView: RootView,
    requestData: RequestData,
    listener: OnServerStateChanged?,
    generateIdManager: GenerateIdManager = GenerateIdManager(rootView),
) {
    generateIdManager.createSingleManagerByRootViewId()
    val view = ViewFactory.makeBeagleView(rootView).apply {
        serverStateChangedListener = listener
        loadView(requestData)
    }
    view.loadCompletedListener = {
        viewGroup.removeAllViews()
        viewGroup.addView(view)
    }
    view.listenerOnViewDetachedFromWindow = {
        generateIdManager.onViewDetachedFromWindow(view)
    }
}

/**
 * Render a Json in String format from a ServerDrivenComponent into this ViewGroup
 * @param activity that is parent of this view.
 * Make sure to use this method if you are inside a Activity because of the lifecycle.
 * @param screenJson Json in String format that represents your component.
 * @param screenId that represents an screen identifier to create the analytics when the screen is created.
 * @param shouldResetContext when true, this clear at the time of calling this function all de context data
 * linked to the lifecycle owner.
 */
fun ViewGroup.loadView(
    activity: AppCompatActivity,
    screenJson: String,
    screenId: String = "",
    shouldResetContext: Boolean = false,
) {
    loadView(ActivityRootView(activity, this.id, screenId), screenJson, shouldResetContext)
}

/**
 * Render a Json in String format from a ServerDrivenComponent into this ViewGroup
 * @param fragment that is parent of this view.
 * Make sure to use this method if you are inside a Fragment because of the lifecycle.
 * @param screenJson Json in String format that represents your component.
 * @param screenId that represents an screen identifier to create the analytics when the screen is created.
 * @param shouldResetContext when true, this clear at the time of calling this function all de context data
 * linked to the lifecycle owner.
 */
fun ViewGroup.loadView(
    fragment: Fragment,
    screenJson: String,
    screenId: String = "",
    shouldResetContext: Boolean = false,
) {
    loadView(FragmentRootView(fragment, this.id, screenId), screenJson, shouldResetContext)
}

internal fun ViewGroup.loadView(
    rootView: RootView,
    screenJson: String,
    shouldResetContext: Boolean,
    generateIdManager: GenerateIdManager = GenerateIdManager(rootView),
) {
    if (shouldResetContext) {
        val viewModel = rootView.generateViewModelInstance<ScreenContextViewModel>()
        viewModel.clearContexts()
    }
    generateIdManager.createSingleManagerByRootViewId()
    val component = beagleSerializerFactory.deserializeComponent(screenJson)
    val view = ViewFactory.makeBeagleView(rootView).apply {
        addServerDrivenComponent(component)
        listenerOnViewDetachedFromWindow = {
            generateIdManager.onViewDetachedFromWindow(this)
        }
    }
    removeAllViews()
    addView(view)

    if (rootView.getScreenId().isNotEmpty()) {
        rootView.generateViewModelInstance<AnalyticsViewModel>().createScreenReport(
            rootView.getScreenId(), getRootId(component)
        )
    }
}