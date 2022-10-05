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

package br.com.zup.beagle.android.view.viewmodel

import android.view.View
import br.com.zup.beagle.android.BaseTest
import br.com.zup.beagle.android.action.AnalyticsAction
import br.com.zup.beagle.android.analytics.AnalyticsProvider
import br.com.zup.beagle.android.analytics.AnalyticsService
import br.com.zup.beagle.android.setup.BeagleConfigurator
import io.mockk.Runs
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("Given an Analytics View Model")
@ExperimentalCoroutinesApi
class AnalyticsViewModelTest : BaseTest() {

    private val analyticsViewModel = AnalyticsViewModel()
    private val beagleConfigurator: BeagleConfigurator = mockk(relaxed = true, relaxUnitFun = true)
    private val origin: View = mockk()
    private val action: AnalyticsAction = mockk()
    private val analyticsProvider: AnalyticsProvider = mockk(relaxed = true, relaxUnitFun = true)
    private val analyticsValue: String = "any"

    @BeforeEach
    fun clear() {
        clearMocks(rootView, beagleConfigurator, analyticsProvider, answers = false)
        mockkObject(AnalyticsService)
        every { rootView.getBeagleConfigurator() } returns beagleConfigurator
        every { rootView.getBeagleConfigurator().analyticsProvider } returns analyticsProvider
    }

    @DisplayName("When create action report")
    @Nested
    inner class CreateActionReport {

        @DisplayName("Should call Analytics Service with correct parameters")
        @Test
        fun testCreateActionReportShouldCallCorrectFun() = runBlockingTest {
            //given
            every {
                AnalyticsService.createActionRecord(
                    rootView,
                    origin,
                    action,
                    analyticsValue
                )
            } just Runs

            //when
            analyticsViewModel.createActionReport(rootView, origin, action, analyticsValue)

            //then
            verify {
                AnalyticsService.createActionRecord(rootView, origin, action, analyticsValue)
            }
        }
    }

    @DisplayName("When create screen report")
    @Nested
    inner class CreateScreenReport {

        @DisplayName("Should call Analytics Service with correct parameters")
        @Test
        fun testCreateScreenReportShouldCallCorrectFun() = runBlockingTest {
            //given
            val rootId = "rootId"
            every { AnalyticsService.createScreenRecord(screenIdentifier = screenId,
                analyticsProvider = analyticsProvider, rootId = rootId) } just Runs

            //when
            analyticsViewModel.createScreenReport(rootView, rootId)

            //then
            verify {
                AnalyticsService.createScreenRecord(screenIdentifier = screenId,
                    analyticsProvider = analyticsProvider, rootId = rootId)
            }
        }
    }
}
