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

import androidx.lifecycle.Observer
import br.com.zup.beagle.android.BaseConfigurationTest
import br.com.zup.beagle.android.testutil.CoroutinesTestExtension
import br.com.zup.beagle.android.testutil.InstantExecutorExtension
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@ExtendWith(InstantExecutorExtension::class, CoroutinesTestExtension::class)
class BeagleScreenViewModelTest : BaseConfigurationTest() {

    private val observer: Observer<Boolean> = mockk()

    private lateinit var beagleScreenViewModel: BeagleScreenViewModel

    @BeforeAll
    override fun setUp() {
        super.setUp()

        beagleScreenViewModel = BeagleScreenViewModel(beagleConfigurator)
        every { observer.onChanged(any()) } just Runs
    }

    @Test
    fun `GIVEN a BeagleScreenViewModel WHEN onScreenLoadFinished THEN post screenLoadFinished should be called`() {

        //Given
        beagleScreenViewModel.screenLoadFinished.observeForever(observer)

        // When
        beagleScreenViewModel.onScreenLoadFinished()

        // Then
        verify {
            observer.onChanged(true)
        }
    }
}
