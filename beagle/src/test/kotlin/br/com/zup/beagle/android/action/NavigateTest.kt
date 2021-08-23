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

package br.com.zup.beagle.android.action

import android.view.View
import br.com.zup.beagle.android.BaseTest
import br.com.zup.beagle.android.context.Bind
import br.com.zup.beagle.android.context.ContextData
import br.com.zup.beagle.android.context.expressionOf
import br.com.zup.beagle.android.navigation.DeepLinkHandler
import br.com.zup.beagle.android.setup.BeagleEnvironment
import br.com.zup.beagle.android.testutil.RandomData
import br.com.zup.beagle.android.utils.evaluateExpression
import br.com.zup.beagle.android.view.custom.BeagleNavigator
import io.mockk.Runs
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("Given a Navigate class")
class NavigateTest : BaseTest() {

    private val deepLinkHandler: DeepLinkHandler = mockk()
    private val contextDataStub = ContextData(id = "test", value = "")

    private val view: View = mockk()

    @BeforeAll
    override fun setUp() {
        super.setUp()
        mockkObject(BeagleNavigator)
        mockkStatic("br.com.zup.beagle.android.utils.ActionExtensionsKt")
    }

    @BeforeEach
    fun clear() {
        clearMocks(rootView)
    }

    @DisplayName("When execute method is called")
    @Nested
    inner class Execute {

        @DisplayName("Then should not call deepLinkIntent if handler is null and type is OpenNativeRoute")
        @Test
        fun testDeepLinkHandlerNull() {
            // Given
            val navigate = Navigate.OpenNativeRoute(RandomData.httpUrl())
            every { BeagleEnvironment.beagleSdk.deepLinkHandler } returns null

            // When
            navigate.execute(rootView, view)

            // Then
            verify(exactly = 0) { deepLinkHandler.getDeepLinkIntent(any(), any(), any(), any()) }
        }

        @DisplayName("Then should call openExternalUrl if type is OpenExternalURL")
        @Test
        fun testOpenExternalURL() {
            // Given
            val url = RandomData.httpUrl()
            val navigate = Navigate.OpenExternalURL(url)
            every { BeagleNavigator.openExternalURL(any(), any()) } just Runs

            // When
            navigate.execute(rootView, view)

            // Then
            verify(exactly = 1) { BeagleNavigator.openExternalURL(rootView.getContext(), url) }
        }

        @DisplayName("Then should call openExternalUrl with expression if type is OpenExternalURL")
        @Test
        fun testOpenExternalURLWithExpression() {
            // Given
            val url = expressionOf<String>("@{test}")
            val urlValue = "test"
            val navigate = Navigate.OpenExternalURL(url).apply {
                every {
                    evaluateExpression(rootView, view, any<Bind<Any>>())
                } returns urlValue
            }
            every { BeagleNavigator.openExternalURL(any(), any()) } just Runs

            // When
            navigate.execute(rootView, view)

            // Then
            verify(exactly = 1) { BeagleNavigator.openExternalURL(rootView.getContext(), urlValue) }
        }

        @DisplayName("Then should call openNativeRoute if type is OpenNativeRoute")
        @Test
        fun testOpenNativeRoute() {
            // Given
            val route = RandomData.httpUrl()
            val data = mapOf("keyStub" to "valueStub")
            val shouldResetApplication = true
            val navigate = Navigate.OpenNativeRoute(route, shouldResetApplication, data)
            every { BeagleNavigator.openNativeRoute(any(), any(), any(), any()) } just Runs

            // When
            navigate.execute(rootView, view)

            // Then
            verify(exactly = 1) {
                BeagleNavigator.openNativeRoute(
                    rootView,
                    route,
                    data,
                    shouldResetApplication
                )
            }
        }

        @DisplayName("Then should call openNativeRoute with expression if type is OpenNativeRoute")
        @Test
        fun testOpenNativeRouteWithExpression() {
            // Given
            val url = expressionOf<String>("@{test}")
            val urlValue = "test"
            val data = mapOf("keyStub" to "valueStub")
            val shouldResetApplication = true
            val navigate = Navigate.OpenNativeRoute(url, shouldResetApplication, data).apply {
                every {
                    evaluateExpression(rootView, view, any<Bind<Any>>())
                } returns urlValue
            }
            every { BeagleNavigator.openNativeRoute(any(), any(), any(), any()) } just Runs

            // When
            navigate.execute(rootView, view)

            // Then
            verify(exactly = 1) {
                BeagleNavigator.openNativeRoute(
                    rootView,
                    urlValue,
                    data,
                    shouldResetApplication,

                    )
            }
        }

        @DisplayName("Then should call resetApplication if type is ResetApplication")
        @Test
        fun testResetApplication() {
            // Given
            val route = Route.Remote(RandomData.httpUrl())
            val navigate = Navigate.ResetApplication(route, context = contextDataStub)
            every { BeagleNavigator.resetApplication(any(), any(), any(), contextDataStub) } just Runs

            // When
            navigate.execute(rootView, view)

            // Then
            verify(exactly = 1) {
                BeagleNavigator.resetApplication(
                    rootView.getContext(),
                    route,
                    null,
                    contextDataStub
                )
            }
        }

        @DisplayName("Then should call resetStack if type is ResetStack")
        @Test
        fun testResetStack() {
            // Given
            val route = Route.Remote(RandomData.httpUrl())
            val navigate = Navigate.ResetStack(route, context = contextDataStub)
            every { BeagleNavigator.resetStack(any(), any(), any(), contextDataStub) } just Runs

            // When
            navigate.execute(rootView, view)

            // Then
            verify(exactly = 1) { BeagleNavigator.resetStack(rootView.getContext(), route, null, contextDataStub) }
        }

        @DisplayName("Then should call pushView if type is PushView")
        @Test
        fun testPushView() {
            // Given
            val route = Route.Remote(RandomData.httpUrl())
            val navigate = Navigate.PushView(route, context = contextDataStub)
            every { BeagleNavigator.pushView(any(), any(), contextDataStub) } just Runs

            // When
            navigate.execute(rootView, view)

            // Then
            verify(exactly = 1) { BeagleNavigator.pushView(rootView.getContext(), route, contextDataStub) }
        }

        @DisplayName("Then should call pushView with expression if type is PushView")
        @Test
        fun testPushViewWithExpression() {
            // Given
            val route = Route.Remote(expressionOf("@{test}"))
            val navigate = Navigate.PushView(route, context = contextDataStub).apply {
                every {
                    evaluateExpression(rootView, view, any<Bind<Any>>())
                } returns "test"
            }
            every { BeagleNavigator.pushView(any(), any(), contextDataStub) } just Runs

            // When
            navigate.execute(rootView, view)

            // Then
            verify(exactly = 1) {
                BeagleNavigator.pushView(
                    rootView.getContext(),
                    route.copy(url = Bind.Value("test")),
                    contextDataStub
                )
            }
        }

        @DisplayName("Then should call popStack if type is PopStack")
        @Test
        fun testPopStack() {
            // Given
            val navigate = Navigate.PopStack(context = contextDataStub)
            every { BeagleNavigator.popStack(any(), contextDataStub) } just Runs

            // When
            navigate.execute(rootView, view)

            // Then
            verify(exactly = 1) { BeagleNavigator.popStack(rootView.getContext(), contextDataStub) }
        }

        @DisplayName("Then should call popView if type is PopView")
        @Test
        fun testPopView() {
            // Given
            val navigate = Navigate.PopView(context = contextDataStub)
            every { BeagleNavigator.popView(any()) } just Runs

            // When
            navigate.execute(rootView, view)

            // Then
            verify(exactly = 1) { BeagleNavigator.popView(rootView.getContext(), contextDataStub) }
        }

        @DisplayName("Then should call popToView if type is PopToView")
        @Test
        fun testPopToView() {
            // Given
            val path = RandomData.httpUrl()
            val navigate = Navigate.PopToView(path, context = contextDataStub)
            every { BeagleNavigator.popToView(any(), any(), contextDataStub) } just Runs

            // When
            navigate.execute(rootView, view)

            // Then
            verify(exactly = 1) { BeagleNavigator.popToView(rootView.getContext(), path, contextDataStub) }
        }

        @DisplayName("Then should call popToView with expression if type is PopToView")
        @Test
        fun testPopToViewWithExpression() {
            // Given
            val path = expressionOf<String>("@{test}")
            val pathValue = "test"
            val navigate = Navigate.PopToView(path, context = contextDataStub).apply {
                every {
                    evaluateExpression(rootView, view, any<Bind<Any>>())
                } returns pathValue
            }
            every { BeagleNavigator.popToView(any(), any(), contextDataStub) } just Runs

            // When
            navigate.execute(rootView, view)

            // Then
            verify(exactly = 1) { BeagleNavigator.popToView(rootView.getContext(), pathValue, contextDataStub) }
        }

        @DisplayName("Then should call pushStack if type is PushStack")
        @Test
        fun testPushStack() {
            // Given
            val route = Route.Remote(RandomData.httpUrl())
            val navigate = Navigate.PushStack(route, context = contextDataStub)
            every { BeagleNavigator.pushStack(any(), any(), any(), contextDataStub) } just Runs

            // When
            navigate.execute(rootView, view)

            // Then
            verify(exactly = 1) { BeagleNavigator.pushStack(rootView.getContext(), route, null, contextDataStub) }
        }
    }
}
