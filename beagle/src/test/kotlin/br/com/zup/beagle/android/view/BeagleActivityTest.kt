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

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.zup.beagle.R
import br.com.zup.beagle.android.BaseSoLoaderTest
import br.com.zup.beagle.android.action.NavigationContext
import br.com.zup.beagle.android.components.Text
import br.com.zup.beagle.android.components.layout.Screen
import br.com.zup.beagle.android.context.ContextData
import br.com.zup.beagle.android.context.constant
import br.com.zup.beagle.android.data.ComponentRequester
import br.com.zup.beagle.android.networking.RequestData
import br.com.zup.beagle.android.testutil.CoroutinesTestExtension
import br.com.zup.beagle.android.testutil.InstantExecutorExtension
import br.com.zup.beagle.android.view.BeagleFragment.Companion.NAVIGATION_CONTEXT_DATA_ID
import br.com.zup.beagle.android.view.BeagleFragment.Companion.NAVIGATION_CONTEXT_DATA_KEY
import br.com.zup.beagle.android.view.viewmodel.AnalyticsViewModel
import br.com.zup.beagle.android.view.viewmodel.BeagleScreenViewModel
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@Config(application = ApplicationTest::class)
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@ExtendWith(InstantExecutorExtension::class, CoroutinesTestExtension::class)
class BeagleActivityTest : BaseSoLoaderTest() {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private val component by lazy { Text(constant("Test component")) }
    private val componentRequester: ComponentRequester = mockk()
    private lateinit var beagleViewModel: BeagleScreenViewModel
    private var activity: ServerDrivenActivity? = null
    private val analyticsViewModel = mockk<AnalyticsViewModel>()
    private val screenIdentifierSlot = slot<String>()
    private val navigationContext = NavigationContext(value = "test")
    private val navigationContextData = ContextData(id = NAVIGATION_CONTEXT_DATA_ID, value = "test")
    lateinit var activityScenario: ActivityScenario<ServerDrivenActivity>
    private val rootIdSlot = slot<String>()

    @Before
    fun mockBeforeTest() {
        coEvery { componentRequester.fetchComponent(RequestData(url = "/url")) } returns component
        beagleViewModel =
            BeagleScreenViewModel(ioDispatcher = TestCoroutineDispatcher(), componentRequester)
        prepareViewModelMock(beagleViewModel)
        activityScenario =
            ActivityScenario.launch(ServerDrivenActivity::class.java)
        activityScenario.onActivity {
            activityScenario.moveToState(Lifecycle.State.STARTED)
            activity = it
        }
    }

    @Test
    fun `Given a request data When navigate to Then should call BeagleFragment newInstance with right parameters`() =
        runBlockingTest {
            // Given
            val url = "/url"
            val screenRequest = RequestData(url = url)
            prepareViewModelMock(analyticsViewModel)
            every { analyticsViewModel.createScreenReport(capture(screenIdentifierSlot), capture(rootIdSlot)) } just Runs

            //When
            activity?.navigateTo(screenRequest, null, navigationContext)
            activityScenario.moveToState(Lifecycle.State.RESUMED)
            val fragment = activity?.supportFragmentManager!!.fragments.first() as BeagleFragment
            fragment.onDestroyView()

            //Then
            val contextData: ContextData = fragment.savedState.getParcelable(NAVIGATION_CONTEXT_DATA_KEY)!!
            assertEquals(url, screenIdentifierSlot.captured)
            assertEquals(navigationContextData, contextData)
        }


    @Test
    fun `Given a request data id When navigate to Then should call BeagleFragment newInstance with right parameters`() =
        runBlockingTest {
            // Given
            val screenRequest = RequestData(url = "")
            val screenId = "myScreen"
            val screen = Screen(child = component).apply { id = screenId }


            prepareViewModelMock(analyticsViewModel)
            every { analyticsViewModel.createScreenReport(capture(screenIdentifierSlot), capture(rootIdSlot)) } just Runs

            //When
            activity?.navigateTo(screenRequest, screen, navigationContext)
            activityScenario.moveToState(Lifecycle.State.RESUMED)
            val fragment = activity?.supportFragmentManager!!.fragments.first() as BeagleFragment
            fragment.onDestroyView()

            // THEN
            val contextData: ContextData = fragment.savedState.getParcelable(NAVIGATION_CONTEXT_DATA_KEY)!!
            assertEquals(screenId, screenIdentifierSlot.captured)
            assertEquals(navigationContextData, contextData)
        }


}

class ApplicationTest : Application() {
    override fun onCreate() {
        super.onCreate()
        setTheme(R.style.Theme_AppCompat)
    }
}

