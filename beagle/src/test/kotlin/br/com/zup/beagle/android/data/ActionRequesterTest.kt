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

package br.com.zup.beagle.android.data

import br.com.zup.beagle.android.BaseTest
import br.com.zup.beagle.android.exception.BeagleApiException
import br.com.zup.beagle.android.networking.HttpClient
import br.com.zup.beagle.android.networking.OnError
import br.com.zup.beagle.android.networking.OnSuccess
import br.com.zup.beagle.android.networking.RequestCall
import br.com.zup.beagle.android.networking.RequestData
import br.com.zup.beagle.android.networking.ResponseData
import br.com.zup.beagle.android.utils.doRequest
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("Given an ActionRequester")
@ExperimentalCoroutinesApi
class ActionRequesterTest : BaseTest() {

    private val httpClient: HttpClient = mockk(relaxed = true)
    private val requestCall: RequestCall = mockk()
    private val requestData: RequestData = mockk()
    private val responseData: ResponseData = mockk()

    private val onSuccessSlot = slot<OnSuccess>()
    private val onErrorSlot = slot<OnError>()

    private val actionRequester = ActionRequester(httpClient)

    @BeforeAll
    override fun setUp() {
        super.setUp()

        mockkStatic("br.com.zup.beagle.android.utils.RequestDataExtensionsKt")
    }

    @DisplayName("When fetchAction is called")
    @Nested
    inner class FetchAction {

        @DisplayName("Then it should execute doRequest with success")
        @Test
        fun testDoRequestSuccess() = runBlockingTest {
            // Given
            every { requestData.doRequest(httpClient, capture(onSuccessSlot), any()) } answers {
                onSuccessSlot.captured(responseData)
                requestCall
            }

            // When
            val response = actionRequester.fetchAction(requestData)

            // Then
            assertEquals(responseData, response)
            verify(exactly = 1) { requestData.doRequest(httpClient, onSuccessSlot.captured, any()) }
        }

        @DisplayName("Then it should execute doRequest with error")
        @Test
        fun testDoRequestError() = runBlockingTest {
            // Given
            every { requestData.doRequest(httpClient, any(), capture(onErrorSlot)) } answers {
                onErrorSlot.captured(responseData)
                requestCall
            }

            // When
            val exception = assertThrows<BeagleApiException> {
                actionRequester.fetchAction(requestData)
            }

            // Then
            assertEquals(responseData, exception.responseData)
            assertEquals(requestData, exception.requestData)
            verify(exactly = 1) { requestData.doRequest(httpClient, any(), onErrorSlot.captured) }
        }

        @DisplayName("Then it should execute doRequest with exception")
        @Test
        fun testDoRequestException() = runBlockingTest {
            // Given
            val exception = BeagleApiException(responseData, requestData)

            // When
            every { requestData.doRequest(httpClient, any(), any()) } throws exception

            // Then
            assertThrows<BeagleApiException> {
                actionRequester.fetchAction(requestData)
            }
        }
    }
}
