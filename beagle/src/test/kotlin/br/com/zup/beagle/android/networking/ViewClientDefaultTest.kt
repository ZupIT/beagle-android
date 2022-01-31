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

package br.com.zup.beagle.android.networking

import br.com.zup.beagle.android.BaseTest
import br.com.zup.beagle.android.logger.BeagleLoggerProxy
import br.com.zup.beagle.android.mockdata.makeResponseData
import io.mockk.Runs
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("Given a ViewClientDefault")
class ViewClientDefaultTest : BaseTest() {

    private val requestData: RequestData = RequestData(url = "https://www.test.com/test")
    private val httpClient: HttpClient = mockk(
        relaxed = true,
        relaxUnitFun = true,
    )
    private val onSuccess: OnSuccess = mockk(relaxed = true, relaxUnitFun = true)
    private val onError: OnError = mockk()
    private val requestCall: RequestCall = mockk()

    private lateinit var viewClientDefault: ViewClientDefault

    @BeforeAll
    override fun setUp() {
        super.setUp()

        mockkObject(BeagleLoggerProxy)
        every { BeagleLoggerProxy.info(any()) } just Runs
    }

    @BeforeEach
    fun clear() {
        clearMocks(httpClient)

        viewClientDefault = ViewClientDefault(httpClient)
    }

    @Nested
    @DisplayName("When fetch is called")
    inner class Fetch {

        @Test
        @DisplayName("Then should call doRequest if cache is empty")
        fun testFetchWithoutCache() {
            // Given
            every { httpClient.execute(requestData, any(), any()) } returns requestCall

            // When
            val call = viewClientDefault.fetch(requestData, onSuccess, onError)

            // Then
            assertEquals(requestCall, call)
        }

        @Test
        @DisplayName("Then should return and remove the cached response")
        fun testFetchWithCache() {
            // Given
            val onSuccessSlot = slot<OnSuccess>()
            every { httpClient.execute(requestData, capture(onSuccessSlot), any()) } returns requestCall

            // When
            viewClientDefault.prefetch(requestData, onSuccess, onError)
            onSuccessSlot.captured.invoke(makeResponseData())
            val call = viewClientDefault.fetch(requestData, onSuccess, onError)

            // Then
            verify(exactly = 1) { httpClient.execute(requestData, any(), any()) }
            assertEquals(null, call)
        }
    }

    @Nested
    @DisplayName("When prefetch is called")
    inner class Prefetch {
        @Test
        @DisplayName("Then should call doRequest if cache is empty")
        fun testPreFetchWithoutCache() {
            // Given
            every { httpClient.execute(requestData, any(), any()) } returns requestCall

            // When
            val call = viewClientDefault.prefetch(requestData, onSuccess, onError)

            // Then
            verify(exactly = 1) { httpClient.execute(requestData, any(), any()) }
            assertEquals(requestCall, call)
        }


        @Test
        @DisplayName("Then should return from cache")
        fun testPreFetchWithCache() {
            // Given
            val onSuccessSlot = slot<OnSuccess>()
            every { httpClient.execute(requestData, capture(onSuccessSlot), any()) } returns requestCall

            // When
            viewClientDefault.prefetch(requestData, onSuccess, onError)
            onSuccessSlot.captured.invoke(makeResponseData())
            val call = viewClientDefault.prefetch(requestData, onSuccess, onError)

            // Then
            verify(exactly = 1) { httpClient.execute(requestData, any(), any()) }
            assertEquals(null, call)
        }
    }
}
