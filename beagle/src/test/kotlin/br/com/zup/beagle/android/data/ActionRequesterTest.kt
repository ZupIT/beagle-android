/*
 * Copyright 2020 ZUP IT SERVICOS EM TECNOLOGIA E INOVACAO SA
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

package br.com.zup.beagle.android.data

import br.com.zup.beagle.android.BaseTest
import br.com.zup.beagle.android.action.Action
import br.com.zup.beagle.android.data.serializer.BeagleSerializer
import br.com.zup.beagle.android.data.serializer.makeActionFormLocalActionJson
import br.com.zup.beagle.android.networking.RequestData
import br.com.zup.beagle.android.networking.ResponseData
import br.com.zup.beagle.android.setup.BeagleEnvironment
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

private val JSON_SUCCESS = makeActionFormLocalActionJson()

@ExperimentalCoroutinesApi
class ActionRequesterTest : BaseTest() {

    private val beagleApi: BeagleApi = mockk()
    private val serializer: BeagleSerializer = mockk()

    private val actionRequester = ActionRequester(beagleApi, serializer)

    @BeforeAll
    override fun setUp() {
        super.setUp()
        MockKAnnotations.init(this)

        mockkObject(BeagleEnvironment)
        mockkStatic("br.com.zup.beagle.android.data.StringExtensionsKt")
    }

    @Test
    fun `should action response when fetch action`() = runBlockingTest {
        // Given
        val action = mockk<Action>()
        val responseData = mockk<ResponseData> {
            every { data } returns JSON_SUCCESS.toByteArray()
        }
        val requestData: RequestData = mockk()

        every { any<String>().toRequestData() } returns requestData

        coEvery { beagleApi.fetchData(requestData) } returns responseData
        every { serializer.deserializeAction(JSON_SUCCESS) } returns action

        // When
        val actionResult = actionRequester.fetchAction("")

        // Then
        verify(exactly = 1) { serializer.deserializeAction(JSON_SUCCESS) }
        assertEquals(action, actionResult)
    }

    @Test
    fun `should response data when fetch data`() = runBlockingTest {
        // Given
        val requestData: RequestData = mockk()
        val responseData: ResponseData = mockk()
        coEvery { beagleApi.fetchData(requestData) } returns responseData

        // When
        actionRequester.fetchData(requestData)

        // Then
        coVerify(exactly = 1) { beagleApi.fetchData(requestData) }
    }

}
