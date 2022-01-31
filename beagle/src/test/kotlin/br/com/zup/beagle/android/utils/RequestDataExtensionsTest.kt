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

package br.com.zup.beagle.android.utils

import br.com.zup.beagle.android.BaseTest
import br.com.zup.beagle.android.data.formatUrl
import br.com.zup.beagle.android.exception.BeagleApiException
import br.com.zup.beagle.android.logger.BeagleMessageLogs
import br.com.zup.beagle.android.networking.HttpClient
import br.com.zup.beagle.android.networking.OnError
import br.com.zup.beagle.android.networking.OnSuccess
import br.com.zup.beagle.android.networking.RequestCall
import br.com.zup.beagle.android.networking.RequestData
import br.com.zup.beagle.android.networking.ResponseData
import br.com.zup.beagle.android.networking.urlbuilder.UrlBuilder
import br.com.zup.beagle.android.networking.urlbuilder.UrlBuilderFactory
import br.com.zup.beagle.android.testutil.RandomData
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("Given a RequestData extension function")
@ExperimentalCoroutinesApi
class RequestDataExtensionsTest : BaseTest() {

    private val httpClient: HttpClient = mockk()
    private val urlBuilder: UrlBuilder = mockk()
    private val requestCall: RequestCall = mockk()
    private val responseData: ResponseData = mockk()
    private val url = RandomData.string()
    private val requestData = RequestData(url = url)

    private val requestDataSlot = mutableListOf<RequestData>()
    private val onSuccessSlot = slot<OnSuccess>()
    private val onErrorSlot = slot<OnError>()

    @BeforeAll
    override fun setUp() {
        super.setUp()

        mockkObject(BeagleMessageLogs)
        mockkConstructor(UrlBuilderFactory::class)
        mockkStatic("br.com.zup.beagle.android.data.StringExtensionsKt")

        every { any<String>().formatUrl(any(), any()) } returns url
        every { anyConstructed<UrlBuilderFactory>().make() } returns urlBuilder

        every { BeagleMessageLogs.logHttpRequestData(any()) } just Runs
        every { BeagleMessageLogs.logHttpResponseData(any()) } just Runs
        every { BeagleMessageLogs.logUnknownHttpError(any()) } just Runs
    }

    @BeforeEach
    fun clear() {
        requestDataSlot.clear()
    }

    @DisplayName("When doRequest successfully")
    @Nested
    inner class DoRequestTest {

        @DisplayName("Then should call log http response with response data")
        @Test
        fun testResponse() = runBlockingTest {
            // Given
            mockListenersAndExecuteHttpClient()

            // When
            requestData.doRequest(httpClient, {
                assertEquals(responseData, it)
            }, {})

            // Then
            verify(exactly = 1) { BeagleMessageLogs.logHttpResponseData(responseData) }
        }

        @DisplayName("Then should call request log and return requestCall")
        @Test
        fun testRequest() = runBlockingTest {
            // Given
            mockListenersAndExecuteHttpClient()

            // When
            val call = requestData.doRequest(httpClient, {}, {})

            // Then
            verify { BeagleMessageLogs.logHttpRequestData(requestDataSlot.first()) }
            assertEquals(call, requestCall)
        }
    }

    @DisplayName("When doRequest with error")
    @Nested
    inner class FetchDataExceptionTest {

        @DisplayName("Then should log the exception")
        @Test
        fun testException() = runBlockingTest {
            // Given
            val responseData: ResponseData = mockk()
            val message = "FetchData error for url ${requestData.url}"
            val expectedException = BeagleApiException(responseData, requestData, message)
            mockListenersAndExecuteHttpClient { onErrorSlot.captured(responseData) }

            // When
            requestData.doRequest(httpClient, {}, {})

            // Then
            verify(exactly = 1) { BeagleMessageLogs.logUnknownHttpError(expectedException) }
        }

        @DisplayName("Then should return an exception")
        @Test
        fun testExceptionReturnedInHttpCall() = runBlockingTest {
            // Given
            val statusCode = -1
            val data = "An instance of HttpClient was not found.".toByteArray()
            val responseData = ResponseData(statusCode, data)
            val expectedException = BeagleApiException(responseData, requestData)
            mockListenersAndExecuteHttpClient()

            // When
            val exceptionThrown = assertThrows<BeagleApiException> {
                requestData.doRequest(null, {}, {})
            }

            // Then
            assertEquals(expectedException.message, exceptionThrown.message)
        }
    }

    private fun mockListenersAndExecuteHttpClient(executionLambda: (() -> Unit)? = null) {
        every {
            httpClient.execute(
                capture(requestDataSlot),
                onSuccess = capture(onSuccessSlot),
                onError = capture(onErrorSlot)
            )
        } answers {
            if (executionLambda != null) {
                executionLambda()
            } else {
                onSuccessSlot.captured(responseData)
            }
            requestCall
        }
    }
}
