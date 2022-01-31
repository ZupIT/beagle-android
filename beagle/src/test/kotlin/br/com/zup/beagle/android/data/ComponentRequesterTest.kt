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
import br.com.zup.beagle.android.data.serializer.BeagleSerializer
import br.com.zup.beagle.android.exception.BeagleApiException
import br.com.zup.beagle.android.networking.OnError
import br.com.zup.beagle.android.networking.OnSuccess
import br.com.zup.beagle.android.networking.RequestCall
import br.com.zup.beagle.android.networking.RequestData
import br.com.zup.beagle.android.networking.ResponseData
import br.com.zup.beagle.android.networking.ViewClient
import br.com.zup.beagle.android.testutil.RandomData
import br.com.zup.beagle.android.widget.core.ServerDrivenComponent
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.verifySequence
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("Given a ComponentRequester")
@ExperimentalCoroutinesApi
class ComponentRequesterTest : BaseTest() {

    private val viewClient: ViewClient = mockk(relaxed = true)
    private val serializer: BeagleSerializer = mockk()
    private val requestCall: RequestCall = mockk()
    private val requestData: RequestData = mockk(relaxed = true)
    private val requestDataCopy: RequestData = RequestData("/test")
    private val responseData: ResponseData = mockk(relaxed = true)
    private val onSuccessSlot = slot<OnSuccess>()
    private val onErrorSlot = slot<OnError>()
    val component = mockk<ServerDrivenComponent>()

    private lateinit var componentRequester: ComponentRequester

    @BeforeAll
    override fun setUp() {
        super.setUp()
        every { serializer.deserializeComponent(any()) } returns component
        every { requestData.copy(any()) } returns requestDataCopy

        componentRequester = ComponentRequester(viewClient, serializer)
    }

    @BeforeEach
    fun clear() {
        clearMocks(viewClient, serializer, answers = false)
    }

    @DisplayName("When fetchComponent is called")
    @Nested
    inner class FetchComponent {

        @DisplayName("Then it should call fetch and deserializeComponent with success")
        @Test
        fun testFetchComponentSuccess() = runBlockingTest {
            // Given
            every { viewClient.fetch(requestDataCopy, capture(onSuccessSlot), any()) } answers {
                onSuccessSlot.captured(responseData)
                requestCall
            }

            // When
            val fetchedComponent = componentRequester.fetchComponent(requestData)

            // Then
            verifySequence {
                viewClient.fetch(requestDataCopy, onSuccessSlot.captured, any())
                serializer.deserializeComponent(any())
            }
            assertEquals(component, fetchedComponent)
        }

        @DisplayName("Then it should call fetch and deserializeComponent with error")
        @Test
        fun testFetchComponentError() = runBlockingTest {
            // Given
            every { viewClient.fetch(requestDataCopy, any(), capture(onErrorSlot)) } answers {
                onErrorSlot.captured(responseData)
                requestCall
            }

            // When
            val fetchedComponent = componentRequester.fetchComponent(requestData)

            // Then
            verifySequence {
                viewClient.fetch(requestDataCopy, any(), onErrorSlot.captured)
                serializer.deserializeComponent(any())
            }
            assertEquals(component, fetchedComponent)
        }

        @DisplayName("Then it should call fetch with exception")
        @Test
        fun testFetchComponentException() = runBlockingTest {
            // Given
            val exception = BeagleApiException(responseData, requestData)

            // When
            every { viewClient.fetch(requestDataCopy, any(), any()) } throws exception

            // Then
            assertThrows<BeagleApiException> {
                componentRequester.fetchComponent(requestData)
            }
        }
    }

    @DisplayName("When prefetchComponent is called")
    @Nested
    inner class PrefetchComponent {

        @DisplayName("Then it should call prefetch and deserializeComponent with success")
        @Test
        fun testPrefetchComponentSuccess() = runBlockingTest {
            // Given
            every { viewClient.prefetch(requestDataCopy, capture(onSuccessSlot), any()) } answers {
                onSuccessSlot.captured(responseData)
                requestCall
            }

            // When
            val prefetchedComponent = componentRequester.prefetchComponent(requestData)

            // Then
            verifySequence {
                viewClient.prefetch(requestDataCopy, onSuccessSlot.captured, any())
                serializer.deserializeComponent(any())
            }
            assertEquals(component, prefetchedComponent)
        }

        @DisplayName("Then it should call prefetch and deserializeComponent with error")
        @Test
        fun testPrefetchComponentError() = runBlockingTest {
            // Given
            every { viewClient.prefetch(requestDataCopy, any(), capture(onErrorSlot)) } answers {
                onErrorSlot.captured(responseData)
                requestCall
            }

            // When
            val prefetchedComponent = componentRequester.prefetchComponent(requestData)

            // Then
            verifySequence {
                viewClient.prefetch(requestDataCopy, any(), onErrorSlot.captured)
                serializer.deserializeComponent(any())
            }
            assertEquals(component, prefetchedComponent)
        }

        @DisplayName("Then it should call prefetch with exception")
        @Test
        fun testPrefetchComponentException() = runBlockingTest {
            // Given
            val exception = BeagleApiException(responseData, requestData)

            // When
            every { viewClient.prefetch(requestDataCopy, any(), any()) } throws exception

            // Then
            assertThrows<BeagleApiException> {
                componentRequester.prefetchComponent(requestData)
            }
        }
    }
}
