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

package br.com.zup.beagle.android

import android.app.Application
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.test.core.app.ApplicationProvider
import br.com.zup.beagle.android.setup.BeagleConfigurator
import br.com.zup.beagle.android.setup.BeagleEnvironment
import br.com.zup.beagle.android.setup.BeagleSdk
import br.com.zup.beagle.android.widget.ActivityRootView
import com.facebook.yoga.YogaNode
import com.facebook.yoga.YogaNodeFactory
import com.squareup.moshi.Moshi
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class BaseTest {

    protected val rootView = mockk<ActivityRootView>(relaxed = true, relaxUnitFun = true)
    protected var moshi: Moshi = mockk(relaxed = true, relaxUnitFun = true)
    protected val beagleSdk = mockk<BeagleSdk>(relaxed = true)
    private val activity: AppCompatActivity = mockk(relaxed = true)
    protected val application = ApplicationProvider.getApplicationContext() as Application
    protected lateinit var beagleConfigurator: BeagleConfigurator
    @BeforeAll
    open fun setUp() {
        mockBeagleEnvironment()
        every { rootView.activity } returns activity
        every { rootView.getScreenId() } returns "screen_id"
        mockYoga()
        BeagleSdk.setInTestMode()
        MyBeagleSetup().init(application)
        beagleConfigurator = BeagleConfigurator.configurator
    }

    protected fun mockYoga() {
        val yogaNode = mockk<YogaNode>(relaxed = true, relaxUnitFun = true)
        val view = View(application)
        mockkStatic(YogaNode::class)
        mockkStatic(YogaNodeFactory::class)
        every { YogaNodeFactory.create() } returns yogaNode
        every { yogaNode.data } returns view
    }

    @AfterAll
    open fun tearDown() {
        unmockkAll()
    }

    protected fun prepareViewModelMock(viewModel: ViewModel) {
        mockkConstructor(ViewModelProvider::class)
        every { anyConstructed<ViewModelProvider>().get(viewModel::class.java) } returns viewModel
    }

    protected fun prepareViewModelMock(vararg viewModels: ViewModel) {
        mockkConstructor(ViewModelProvider::class)

        viewModels.forEach { viewModel ->
            every { anyConstructed<ViewModelProvider>().get(viewModel::class.java) } returns viewModel
        }
    }

    protected fun mockBeagleEnvironment(){
        mockkObject(BeagleEnvironment)
        every { BeagleEnvironment.beagleSdk } returns beagleSdk
        every { beagleSdk.typeAdapterResolver } returns null
        every { beagleSdk.registeredWidgets() } returns listOf()
        every { beagleSdk.registeredActions() } returns listOf()
        every { beagleSdk.registeredOperations() } returns mapOf()
    }

}
