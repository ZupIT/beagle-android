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

package br.com.zup.beagle.android.components

import br.com.zup.beagle.android.setup.BeagleEnvironment
import br.com.zup.beagle.android.setup.Environment
import br.com.zup.beagle.android.view.BeagleActivity
import br.com.zup.beagle.android.view.ServerDrivenState
import br.com.zup.beagle.android.view.ViewFactory
import io.mockk.CapturingSlot
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkClass
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

private const val MOCKED_URL = "http://mocked.com"

class WebViewTest : BaseComponentTest() {

    private val webView: android.webkit.WebView = mockk(relaxed = true)

    private val urlSlot = slot<String>()

    private lateinit var webViewComponent: WebView

    @BeforeAll
    override fun setUp() {
        super.setUp()

        every { BeagleEnvironment.beagleSdk.config.environment } returns Environment.DEBUG
        every { webView.loadUrl(capture(urlSlot)) } just Runs
        every { ViewFactory.makeWebView(rootView.getContext()) } returns webView

        webViewComponent = WebView(MOCKED_URL)
    }

    @Test
    fun build_should_create_a_WebView_and_load_url_and_set_WebViewClient() {
        // When
        webViewComponent.buildView(rootView)

        // Then
        assertEquals(MOCKED_URL, urlSlot.captured)
        assertNotNull(webView.webViewClient)
    }

    @Test
    fun webViewClient_should_notify_when_page_starts_loading() {
        // Given
        val stateSlot = slot<ServerDrivenState>()
        val webViewClient = createMockedWebViewClient(stateSlot)

        // When
        webViewClient.onPageStarted(null, null, null)

        // Then
        assertEquals(ServerDrivenState.Started, stateSlot.captured)
    }

    @Test
    fun webViewClient_should_notify_when_page_stops_loading() {
        // Given
        val stateSlot = slot<ServerDrivenState>()
        val webViewClient = createMockedWebViewClient(stateSlot)

        // When
        webViewClient.onPageFinished(webView, null)

        // Then
        assertEquals(ServerDrivenState.Finished, stateSlot.captured)
        verify(exactly = 1) { webView.requestLayout() }
    }

    @Test
    fun webViewClient_should_notify_when_page_WebViewError() {
        // Given
        val stateSlot = slot<ServerDrivenState>()
        val webViewClient = createMockedWebViewClient(stateSlot)

        // When
        webViewClient.onReceivedError(webView, null, null)
        (stateSlot.captured as ServerDrivenState.WebViewError).retry.invoke()

        // Then
        verify(exactly = 1) { webView.reload() }
    }

    private fun createMockedWebViewClient(stateSlot: CapturingSlot<ServerDrivenState>): WebView.BeagleWebViewClient {
        val mockedActivity = mockkClass(BeagleActivity::class)
        every { mockedActivity.onServerDrivenContainerStateChanged(capture(stateSlot)) } just Runs
        return WebView.BeagleWebViewClient(mockedActivity)
    }
}