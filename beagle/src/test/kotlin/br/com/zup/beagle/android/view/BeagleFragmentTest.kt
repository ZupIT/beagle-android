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

package br.com.zup.beagle.android.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.fragment.app.testing.withFragment
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.zup.beagle.android.BaseSoLoaderTest
import br.com.zup.beagle.android.action.NavigationContext
import br.com.zup.beagle.android.context.ContextData
import br.com.zup.beagle.android.utils.ObjectWrapperForBinder
import br.com.zup.beagle.android.view.viewmodel.AnalyticsViewModel
import br.com.zup.beagle.android.widget.RootView
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@Config(application = ApplicationTest::class)
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class BeagleFragmentTest : BaseSoLoaderTest() {

    private val analyticsViewModel = mockk<AnalyticsViewModel>()
    private val screenIdentifierSlot = slot<RootView>()
    private val rootIdSlot = slot<String>()
    private val json = """{
                "_beagleComponent_" : "beagle:screenComponent",
                "child" : {
                "_beagleComponent_" : "beagle:text",
                "text" : "hello"
            }
            }"""
    private val jsonWithIdentifier = """{
                "_beagleComponent_" : "beagle:screenComponent",
                "id": "This is an identifier",
                "child" : {
                "_beagleComponent_" : "beagle:text",
                "text" : "hello"
            }
            }"""
    private val url = "/url"
    private var activity: ServerDrivenActivity? = null
    private val navigationContext = NavigationContext(value = "test")
    private val navigationContextData = ContextData(id = BeagleFragment.NAVIGATION_CONTEXT_DATA_ID, value = "testtwo")

    @Before
    override fun setUp() {
        super.setUp()
        prepareViewModelMock(analyticsViewModel)
        every { analyticsViewModel.createScreenReport(capture(screenIdentifierSlot), capture(rootIdSlot)) } just Runs

        val intent = Intent(application, ServerDrivenActivity::class.java)
        val bundle = Bundle().apply {
            putBinder(BeagleActivity.BEAGLE_CONFIGURATOR, ObjectWrapperForBinder(beagleConfigurator))
        }
        intent.putExtras(bundle)
        val activityScenario: ActivityScenario<ServerDrivenActivity> = ActivityScenario.launch(intent)
        activityScenario.onActivity {
            activityScenario.moveToState(Lifecycle.State.RESUMED)
            activity = it
        }
    }

    @Test
    fun `Given a BeagleFragment with screen identifier When BeagleFragment is resumed Then should report screen`() {
        // When
        val scenario =
            launchFragmentInContainer<BeagleFragment>(BeagleFragment.newBundle(json, url, navigationContext,
            beagleConfigurator))
        scenario.moveToState(Lifecycle.State.RESUMED)

        // Then
        assertEquals(url, screenIdentifierSlot.captured.getScreenId())
    }

    @Test
    fun `Given a BeagleFragment without screen identifier When BeagleFragment is resumed Then should not report screen`() {
        // When
        val scenario =
            launchFragmentInContainer<BeagleFragment>(BeagleFragment.newBundle(json, null, navigationContext,
            beagleConfigurator))
        scenario.moveToState(Lifecycle.State.RESUMED)

        // then
        assertEquals(false, screenIdentifierSlot.isCaptured)
    }

    @Test
    fun `Given a BeagleFragment When call updateNavigationContext Then should update context`() {
        // When
        val scenario =
            launchFragmentInContainer<BeagleFragment>(BeagleFragment.newBundle(json, null, navigationContext,
             beagleConfigurator))

        scenario.moveToState(Lifecycle.State.RESUMED)
        scenario.withFragment {
            updateNavigationContext(NavigationContext(value = "testtwo"))
            onDestroyView()
        }

        // Then
        scenario.withFragment {
            val contextData: ContextData = savedState.getParcelable(BeagleFragment.NAVIGATION_CONTEXT_DATA_KEY)!!
            assertEquals(navigationContextData, contextData)
        }

        assertEquals(false, screenIdentifierSlot.isCaptured)
    }

    @Test
    fun `Given a Tree with identifier screen analytics should be called with root Id as identifier`() {
        //When

        val scenario =
            launchFragmentInContainer<BeagleFragment>(BeagleFragment.newBundle(jsonWithIdentifier, "This is an identifier", navigationContext,
             beagleConfigurator))

        scenario.moveToState(Lifecycle.State.RESUMED)

        //then
        assertEquals("This is an identifier", rootIdSlot.captured)
    }
}