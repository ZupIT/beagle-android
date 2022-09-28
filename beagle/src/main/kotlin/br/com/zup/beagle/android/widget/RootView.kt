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

package br.com.zup.beagle.android.widget

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelStoreOwner
import br.com.zup.beagle.android.setup.BeagleConfigurator

/**
 * Interface RootView holder the reference of activity or fragment.
 */
interface RootView {
    /**
     * Returns the application context.
     */
    fun getContext(): Context

    /**
     * Returns A class that has an Android lifecycle.
     */
    fun getLifecycleOwner(): LifecycleOwner

    /**
     *  Retrieve a ViewModelStore for activities and fragments.
     */
    fun getViewModelStoreOwner(): ViewModelStoreOwner

    /**
     * Returns the parent id of View that encapsulates all the content rendered by server-driven.
     */
    fun getParentId(): Int

    /**
     * Returns the screen url or screen id of Screen that encapsulates all the content rendered by server-driven.
     */
    fun getScreenId(): String

    /**
     * Returns the Beagle config that should be used for this server-driven flow.
     */
    fun getBeagleConfigurator(): BeagleConfigurator
}

/**
 * The FragmentRootView holder the reference of a fragment.
 *
 * @param fragment that is the parent of a view.
 * @param parentId parent view id.
 */
class FragmentRootView(
    val fragment: Fragment,
    private val parentId: Int,
    private val screenId : String,
    private val config: BeagleConfigurator = BeagleConfigurator.factory()
) : RootView {

    override fun getContext(): Context = fragment.requireContext()

    override fun getLifecycleOwner(): LifecycleOwner = fragment.viewLifecycleOwner

    override fun getViewModelStoreOwner(): ViewModelStoreOwner = fragment

    override fun getParentId(): Int = parentId

    override fun getScreenId(): String = screenId

    override fun getBeagleConfigurator(): BeagleConfigurator = config
}

/**
 * The ActivityRootView holder the reference of activity.
 *
 * @param activity that is the parent of a view.
 * @param parentId parent view id.
 */
class ActivityRootView(
    val activity: AppCompatActivity,
    private val parentId: Int,
    private val screenId: String,
    private val config: BeagleConfigurator = BeagleConfigurator.factory(),
) : RootView {

    override fun getContext(): Context = activity

    override fun getLifecycleOwner(): LifecycleOwner = activity

    override fun getViewModelStoreOwner(): ViewModelStoreOwner = activity

    override fun getParentId(): Int = parentId

    override fun getScreenId(): String = screenId

    override fun getBeagleConfigurator(): BeagleConfigurator = config
}

