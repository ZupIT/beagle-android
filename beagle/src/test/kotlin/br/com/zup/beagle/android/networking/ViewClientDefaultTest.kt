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

package br.com.zup.beagle.android.networking

import br.com.zup.beagle.android.BaseTest
import br.com.zup.beagle.android.utils.doRequest
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("Given a ViewClientDefault")
class ViewClientDefaultTest : BaseTest() {

    private val url = "https://www.test.com/test"
    private val requestData: RequestData = mockk()
    private val httpClient: HttpClient = mockk(relaxed = true)
    private val onSuccess: OnSuccess = mockk()
    private val onError: OnError = mockk()
    private val requestCall: RequestCall = mockk()

    val viewClientDefault = ViewClientDefault

    @BeforeAll
    override fun setUp() {
        super.setUp()
        every { beagleSdk.httpClientFactory } returns mockk() {
            every { create() } returns httpClient
        }
        every { requestData.url } returns url
    }

    @Nested
    @DisplayName("When fetch is called")
    inner class Fetch {

        @Test
        @DisplayName("Then should call doRequest if cache is empty")
        fun testFetchWithoutCache() {
            // Given
//            every { requestData.doRequest(httpClient, onSuccess, onError) } returns requestCall

            // When
//            val call = viewClientDefault.fetch(requestData, onSuccess, onError)

            // Then
//            assertEquals(requestCall, call)
//            verify(exactly = 1) { requestData.doRequest(httpClient, onSuccess, onError) }
        }

        @Test
        @DisplayName("Then should return and remove the cached response")
        fun testFetchWithCache() {
            // Given
//            viewClientDefault.prefetch(requestData, onSuccess, onError)

            // When
//            val call = viewClientDefault.fetch(requestData, onSuccess, onError)

            // Then
//            assertEquals(null, call)
            // check onSuccess with cached value
        }
    }

    @Nested
    @DisplayName("When prefetch is called")
    inner class Prefetch {
        // same asserts with prefetch method
    }
}
