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
import br.com.zup.beagle.android.action.SendRequestInternal
import br.com.zup.beagle.android.data.ActionRequester
import br.com.zup.beagle.android.exception.BeagleApiException
import br.com.zup.beagle.android.networking.ResponseData
import br.com.zup.beagle.android.testutil.CoroutinesTestExtension
import br.com.zup.beagle.android.testutil.InstantExecutorExtension
import br.com.zup.beagle.android.view.mapper.toRequestData
import br.com.zup.beagle.android.view.mapper.toResponse
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@ExtendWith(InstantExecutorExtension::class, CoroutinesTestExtension::class)
class ActionRequestViewModelTest : BaseConfigurationTest() {

    private val actionRequester: ActionRequester = mockk()

    private val observer: Observer<FetchViewState> = mockk()

    private val action: SendRequestInternal = mockk()

    private lateinit var viewModel: ActionRequestViewModel

    @BeforeAll
    override fun setUp() {
        super.setUp()

        MockKAnnotations.init(this)

        mockkStatic("br.com.zup.beagle.android.view.mapper.SendRequestActionMapperKt")

        viewModel = ActionRequestViewModel(beagleConfigurator = beagleConfigurator,
            actionRequester = actionRequester)

        every { observer.onChanged(any()) } just Runs
    }

    @Test
    fun `should emit success when fetch data`() {
        // Given
        val response: ResponseData = mockk(relaxed = true)
        val responseMapped: Response = mockk()
        every { action.toRequestData() } returns mockk()
        every { response.toResponse() } returns responseMapped
        coEvery { actionRequester.fetchAction(any()) } returns response

        // When
        viewModel.fetch(action).observeForever(observer)

        // Then
        verify(exactly = 1) {
            observer.onChanged(FetchViewState.Success(responseMapped))
        }
    }

    @Test
    fun `should emit fail when fetch data`() {
        // Given
        val error: BeagleApiException = mockk()
        val responseData: ResponseData = mockk(relaxed = true)
        val responseMapped: Response = mockk()
        every { action.toRequestData() } returns mockk()
        every { responseData.toResponse() } returns responseMapped
        every { error.responseData } returns responseData
        coEvery { actionRequester.fetchAction(any()) } throws error

        // When
        viewModel.fetch(action).observeForever(observer)

        // Then
        verify(exactly = 1) {
            observer.onChanged(FetchViewState.Error(responseMapped))
        }
    }
}
