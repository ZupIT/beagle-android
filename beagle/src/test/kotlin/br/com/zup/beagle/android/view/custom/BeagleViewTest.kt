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

package br.com.zup.beagle.android.view.custom

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.zup.beagle.android.BaseSoLoaderTest
import br.com.zup.beagle.android.BaseTest
import br.com.zup.beagle.android.MyBeagleSetup
import br.com.zup.beagle.android.components.Text
import br.com.zup.beagle.android.context.constant
import br.com.zup.beagle.android.data.formatUrl
import br.com.zup.beagle.android.networking.RequestData
import br.com.zup.beagle.android.setup.BeagleConfigurator
import br.com.zup.beagle.android.setup.BeagleSdk
import br.com.zup.beagle.android.utils.ObjectWrapperForBinder
import br.com.zup.beagle.android.view.ApplicationTest
import br.com.zup.beagle.android.view.BeagleActivity
import br.com.zup.beagle.android.view.ServerDrivenActivity
import br.com.zup.beagle.android.view.viewmodel.AnalyticsViewModel
import br.com.zup.beagle.android.view.viewmodel.BeagleViewModel
import br.com.zup.beagle.android.view.viewmodel.ViewState
import br.com.zup.beagle.android.widget.ActivityRootView
import br.com.zup.beagle.android.widget.RootView
import com.facebook.yoga.YogaNode
import com.facebook.yoga.YogaNodeFactory
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.verify
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@Config(application = ApplicationTest::class)
@RunWith(AndroidJUnit4::class)
internal class BeagleViewTest : BaseSoLoaderTest() {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private val viewModel: BeagleViewModel = mockk()
    private val analyticsViewModel = mockk<AnalyticsViewModel>()

    private val screenIdentifierSlot = slot<RootView>()
    private val rootIdSlot = slot<String>()

    private val mutableLiveData = MutableLiveData<ViewState>()
    private lateinit var url: String
    private lateinit var component: Text

    private lateinit var beagleView: BeagleView

    @Before
    override fun setUp() {
        super.setUp()

        val application: Application = ApplicationProvider.getApplicationContext() as Application
        mockYoga(application)
        BeagleSdk.setInTestMode()
        MyBeagleSetup().init(application)
        prepareViewModelMock(analyticsViewModel)
        every { analyticsViewModel.createScreenReport(capture(screenIdentifierSlot), capture(rootIdSlot)) } just Runs
        every { viewModel.fetchComponent(any(), any()) } returns mutableLiveData
        val intent = Intent(application, ServerDrivenActivity::class.java)
        val bundle = Bundle().apply {
            putBinder(BeagleActivity.BEAGLE_CONFIGURATOR, ObjectWrapperForBinder(beagleConfigurator))
        }
        intent.putExtras(bundle)
        val activityScenario: ActivityScenario<ServerDrivenActivity> =
            ActivityScenario.launch(intent)
        activityScenario.onActivity {
            val rootView = ActivityRootView(it, 10, "")
            beagleView = BeagleView(rootView, viewModel)
        }

        url = "/url".formatUrl(beagleConfigurator)
        component = Text(constant("Test component"))
    }

    @After
    override fun tearDown() {
        super.tearDown()
        BeagleSdk.deinitForTest()
    }

    private fun mockYoga(application: Application) {
        val yogaNode = mockk<YogaNode>(relaxed = true, relaxUnitFun = true)
        val view = View(application)
        mockkStatic(YogaNode::class)
        mockkStatic(YogaNodeFactory::class)
        every { YogaNodeFactory.create() } returns yogaNode
        every { yogaNode.data } returns view
    }

    @Test
    fun `Given a DoRenderState with a ScreenIdentifier not null When loadView Then Should ReportScreen`() {
        // Given
        mutableLiveData.postValue(ViewState.DoRender(url, component))

        // when
        beagleView.loadView(RequestData(url))

        // Then
        Assert.assertEquals(url, screenIdentifierSlot.captured.getScreenId())
    }

    @Test
    fun `Given a DoRenderState with screen id null When loadView Then Should not ReportScreen`() {
        // Given
        mutableLiveData.postValue(ViewState.DoRender(null, component))

        // when
        beagleView.loadView(RequestData(url))

        // Then
        Assert.assertEquals(false, screenIdentifierSlot.isCaptured)
    }

    @Test
    fun `Given a Beagle View When loadView with Request Data Then should call fetch component`() {
        // Given
        val requestDataFake = RequestData(url = url)

        // when
        beagleView.loadView(requestDataFake)

        verify { viewModel.fetchComponent(requestData = requestDataFake) }
    }

}