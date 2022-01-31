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

package br.com.zup.beagle.android.action

import android.view.View
import br.com.zup.beagle.android.BaseTest
import br.com.zup.beagle.android.context.Bind
import br.com.zup.beagle.android.context.ContextData
import br.com.zup.beagle.android.context.constant
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
    private val navigationContextStub = NavigationContext(value = "")

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
            val navigate = Navigate.OpenNativeRoute(constant(RandomData.httpUrl()))
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
            val url = constant(RandomData.httpUrl())
            val navigate = Navigate.OpenExternalURL(url)
            every { BeagleNavigator.openExternalURL(any(), any()) } just Runs

            // When
            navigate.execute(rootView, view)

            // Then
            verify(exactly = 1) { BeagleNavigator.openExternalURL(rootView.getContext(), url.value) }
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
            val route = constant(RandomData.httpUrl())
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
                    route.value,
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
            val route = Route.Remote(constant(RandomData.httpUrl()))
            val navigate = Navigate.ResetApplication(route, navigationContext = navigationContextStub)
            every { BeagleNavigator.resetApplication(any(), any(), any(), navigationContextStub) } just Runs

            // When
            navigate.execute(rootView, view)

            // Then
            verify(exactly = 1) {
                BeagleNavigator.resetApplication(
                    rootView.getContext(),
                    route,
                    null,
                    navigationContextStub
                )
            }
        }

        @DisplayName("Then should call resetStack if type is ResetStack")
        @Test
        fun testResetStack() {
            // Given
            val route = Route.Remote(constant(RandomData.httpUrl()))
            val navigate = Navigate.ResetStack(route, navigationContext = navigationContextStub)
            every { BeagleNavigator.resetStack(any(), any(), any(), navigationContextStub) } just Runs

            // When
            navigate.execute(rootView, view)

            // Then
            verify(exactly = 1) { BeagleNavigator.resetStack(rootView.getContext(), route, null, navigationContextStub) }
        }

        @DisplayName("Then should call pushView if type is PushView")
        @Test
        fun testPushView() {
            // Given
            val route = Route.Remote(constant(RandomData.httpUrl()))
            val navigate = Navigate.PushView(route, navigationContext = navigationContextStub)
            every { BeagleNavigator.pushView(any(), any(), navigationContextStub) } just Runs

            // When
            navigate.execute(rootView, view)

            // Then
            verify(exactly = 1) { BeagleNavigator.pushView(rootView.getContext(), route, navigationContextStub) }
        }

        @DisplayName("Then should call pushView with expression if type is PushView")
        @Test
        fun testPushViewWithExpression() {
            // Given
            val route = Route.Remote(expressionOf("@{test}"))
            val navigate = Navigate.PushView(route, navigationContext = navigationContextStub).apply {
                every {
                    evaluateExpression(rootView, view, any<Bind<Any>>())
                } returns "test"
            }
            every { BeagleNavigator.pushView(any(), any(), navigationContextStub) } just Runs

            // When
            navigate.execute(rootView, view)

            // Then
            verify(exactly = 1) {
                BeagleNavigator.pushView(
                    rootView.getContext(),
                    route.copy(url = Bind.Value("test")),
                    navigationContextStub
                )
            }
        }

        @DisplayName("Then should call popStack if type is PopStack")
        @Test
        fun testPopStack() {
            // Given
            val navigate = Navigate.PopStack(navigationContext = navigationContextStub)
            every { BeagleNavigator.popStack(any(), navigationContextStub) } just Runs

            // When
            navigate.execute(rootView, view)

            // Then
            verify(exactly = 1) { BeagleNavigator.popStack(rootView.getContext(), navigationContextStub) }
        }

        @DisplayName("Then should call popView if type is PopView")
        @Test
        fun testPopView() {
            // Given
            val navigate = Navigate.PopView(navigationContext = navigationContextStub)
            every { BeagleNavigator.popView(any()) } just Runs

            // When
            navigate.execute(rootView, view)

            // Then
            verify(exactly = 1) { BeagleNavigator.popView(rootView.getContext(), navigationContextStub) }
        }

        @DisplayName("Then should call popToView if type is PopToView")
        @Test
        fun testPopToView() {
            // Given
            val path = RandomData.httpUrl()
            val navigate = Navigate.PopToView(constant(path), navigationContext = navigationContextStub)
            every { BeagleNavigator.popToView(any(), any(), navigationContextStub) } just Runs

            // When
            navigate.execute(rootView, view)

            // Then
            verify(exactly = 1) { BeagleNavigator.popToView(rootView.getContext(), path, navigationContextStub) }
        }

        @DisplayName("Then should call popToView with expression if type is PopToView")
        @Test
        fun testPopToViewWithExpression() {
            // Given
            val path = expressionOf<String>("@{test}")
            val pathValue = "test"
            val navigate = Navigate.PopToView(path, navigationContext = navigationContextStub).apply {
                every {
                    evaluateExpression(rootView, view, any<Bind<Any>>())
                } returns pathValue
            }
            every { BeagleNavigator.popToView(any(), any(), navigationContextStub) } just Runs

            // When
            navigate.execute(rootView, view)

            // Then
            verify(exactly = 1) { BeagleNavigator.popToView(rootView.getContext(), pathValue, navigationContextStub) }
        }

        @DisplayName("Then should call pushStack if type is PushStack")
        @Test
        fun testPushStack() {
            // Given
            val route = Route.Remote(constant(RandomData.httpUrl()))
            val navigate = Navigate.PushStack(route, navigationContext = navigationContextStub)
            every { BeagleNavigator.pushStack(any(), any(), any(), navigationContextStub) } just Runs

            // When
            navigate.execute(rootView, view)

            // Then
            verify(exactly = 1) { BeagleNavigator.pushStack(rootView.getContext(), route, null, navigationContextStub) }
        }
    }
}
