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

package br.com.zup.beagle.android.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import br.com.zup.beagle.android.BaseTest
import br.com.zup.beagle.android.action.NavigationContext
import br.com.zup.beagle.android.action.Route
import br.com.zup.beagle.android.components.Text
import br.com.zup.beagle.android.components.layout.Screen
import br.com.zup.beagle.android.context.ContextData
import br.com.zup.beagle.android.context.constant
import br.com.zup.beagle.android.data.formatUrl
import br.com.zup.beagle.android.logger.BeagleLoggerProxy
import br.com.zup.beagle.android.navigation.DeepLinkHandler
import br.com.zup.beagle.android.networking.RequestData
import br.com.zup.beagle.android.networking.urlbuilder.UrlBuilder
import br.com.zup.beagle.android.setup.BeagleEnvironment
import br.com.zup.beagle.android.testutil.RandomData
import br.com.zup.beagle.android.view.custom.BeagleNavigator
import io.mockk.Runs
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private val route = Route.Remote(constant(RandomData.httpUrl()))
private val url = RandomData.httpUrl()

@DisplayName("Given a BeagleNavigator")
class BeagleNavigatorTest : BaseTest() {

    private val context: BeagleActivity = mockk()

    private val fragmentTransaction: FragmentTransaction = mockk()

    private val supportFragmentManager = mockk<FragmentManager>()
    private val fragment: BeagleFragment = mockk(relaxed = true)

    private val intent: Intent = mockk(relaxed = true, relaxUnitFun = true)

    private val deepLinkHandler: DeepLinkHandler = mockk()

    private val navigationContext = NavigationContext(value = "")

    @BeforeAll
    override fun setUp() {
        super.setUp()
        mockkStatic("android.net.Uri")

        mockkObject(BeagleFragment.Companion)
        mockkObject(BeagleActivity.Companion)
        mockkObject(BeagleLoggerProxy)
    }

    @BeforeEach
    fun clear() {

        clearMocks(
            context,
            rootView,
            deepLinkHandler,
            fragmentTransaction,
            intent,
            supportFragmentManager,
            fragment,
            BeagleLoggerProxy,
            BeagleFragment.Companion,
            BeagleActivity.Companion,
            BeagleEnvironment,
            answers = false
        )

        mockBeagleEnvironment()

        mockkStatic("android.net.Uri")

        mockkConstructor(Intent::class)

        every { anyConstructed<Intent>().putExtras(any<Bundle>()) } returns intent
        every { anyConstructed<Intent>().addFlags(any()) } returns intent
        every { BeagleActivity.bundleOf(any<RequestData>()) } returns mockk()
        every { BeagleActivity.bundleOf(any<Screen>()) } returns mockk()
        every { BeagleActivity.bundleOf(any<NavigationContext>()) } returns mockk()
        every { BeagleActivity.bundleOf(any<RequestData>(), any<NavigationContext>()) } returns mockk()
        every { context.setResult(any(), any()) } just runs

        every { context.supportFragmentManager } returns supportFragmentManager
        every { supportFragmentManager.fragments } returns mutableListOf<Fragment>(fragment)
        every { supportFragmentManager.beginTransaction() } returns fragmentTransaction
        every {
            fragmentTransaction.setCustomAnimations(any(), any(), any(), any())
        } returns fragmentTransaction
        every { fragmentTransaction.replace(any(), any()) } returns fragmentTransaction
        every { fragmentTransaction.addToBackStack(any()) } returns fragmentTransaction
        every { fragmentTransaction.commit() } returns 0
    }


    @DisplayName("When openExternalURL")
    @Nested
    inner class OpenExternalURL {
        @DisplayName("Then should call intent")
        @Test
        fun testOpenExternalURLShouldCallIntent() {
            // Given
            val webPage: Uri = mockk()
            val url = RandomData.httpUrl()
            every { context.startActivity(any()) } just Runs
            every { Uri.parse(url) } returns webPage

            // When
            BeagleNavigator.openExternalURL(context, url)

            // Then
            verify(exactly = 1) { Uri.parse(url) }
            verify(exactly = 1) { context.startActivity(any()) }
        }

        @DisplayName("Then should catch when url is invalid")
        @Test
        fun testOpenExternalURLShouldCatchWhenUrlIsInvalid() {
            // Given
            val webPage: Uri = mockk()
            val url = "invalid url"
            every { context.startActivity(any()) } throws Exception()
            every { Uri.parse(url) } returns webPage
            every { BeagleLoggerProxy.error(any()) } just Runs

            // When
            BeagleNavigator.openExternalURL(context, url)

            // Then
            verify(exactly = 1) { BeagleLoggerProxy.error(any()) }
        }
    }

    @DisplayName("When OpenNativeRoute")
    @Nested
    inner class OpenNativeRoute {
        @DisplayName("Then should call startActivity")
        @Test
        fun testOpenNativeRouteShouldCallStartActivity() {
            // Given
            val map = mapOf("keyStub" to "valueStub")
            val intent = mockk<Intent>(relaxUnitFun = true, relaxed = true)
            every { context.startActivity(any()) } just Runs
            every { BeagleEnvironment.beagleSdk.deepLinkHandler } returns deepLinkHandler
            every { deepLinkHandler.getDeepLinkIntent(any(), any(), any(), any()) } returns intent

            // When
            BeagleNavigator.openNativeRoute(rootView, url, map, false)

            // Then
            verify(exactly = 1) { rootView.getContext().startActivity(intent) }
        }
    }

    @DisplayName("When PopStack")
    @Nested
    inner class PopStack {
        @DisplayName("Then should finish the activity")
        @Test
        fun testPopStackShouldFinishTheActivity() {
            // Given
            every { context.finish() } just Runs

            // When
            BeagleNavigator.popStack(context, navigationContext)

            // Then
            verify(exactly = 1) {
                context.setResult(Activity.RESULT_OK, any())
                context.finish()
            }
        }
    }

    @DisplayName("When PopView")
    @Nested
    inner class PopView {
        @DisplayName("Then should call activity onBackPressed")
        @Test
        fun testPopViewShouldCallActivityOnBackPressed() {
            // Given
            every { context.onBackPressed() } just Runs
            every { supportFragmentManager.backStackEntryCount } returns 2

            // When
            BeagleNavigator.popView(context, navigationContext)

            // Then
            verify(exactly = 1) {
                context.onBackPressed()
                fragment.updateNavigationContext(navigationContext)
            }
        }


        @DisplayName("Then should call set result onBackPressed")
        @Test
        fun testPopViewShouldCallSetResultOnBackPressed() {
            // Given
            every { context.onBackPressed() } just Runs
            every { supportFragmentManager.backStackEntryCount } returns 1

            // When
            BeagleNavigator.popView(context, navigationContext)

            // Then
            verify(exactly = 1) {
                context.setResult(Activity.RESULT_OK, any())
                context.onBackPressed()
            }
        }

        @Test
        fun testPopViewShouldCallDialogDismiss() {
            // Given
            every { context.onBackPressed() } just Runs
            val supportFragmentManager = mockk<FragmentManager>()
            val dialogFragment = mockk<DialogFragment>()
            every { context.supportFragmentManager } returns supportFragmentManager
            every { supportFragmentManager.fragments } returns listOf(dialogFragment)
            every { dialogFragment.dismiss() } just Runs

            // When
            BeagleNavigator.popView(context)

            // Then
            verify(exactly = 1) { dialogFragment.dismiss() }
        }
    }

    @DisplayName("When PushView")
    @Nested
    inner class PushView {
        @DisplayName("Then should call BeagleActivity navigateTo")
        @Test
        fun testPushViewShouldCallBeagleActivityNavigateTo() {
            // Given
            val url = route.url.value as String

            val requestData = RequestData(url = url)
            every { context.navigateTo(requestData, null, navigationContext) } just Runs

            // When
            BeagleNavigator.pushView(context, route, navigationContext)

            // Then
            verify(exactly = 1) { context.navigateTo(requestData, null, navigationContext) }
        }

        @DisplayName("Then should start BeagleActivity")
        @Test
        fun testPushViewShouldStartBeagleActivity() {
            // Given
            val context = mockk<Activity>()
            val route = Route.Local(Screen(child = Text(constant("stub"))))
            every { context.startActivity(any()) } just Runs

            // When
            BeagleNavigator.pushView(context, route, navigationContext)

            // Then
            verify(exactly = 1) { context.startActivity(any()) }
        }
    }

    @DisplayName("When ResetApplication")
    @Nested
    inner class ResetApplication {

        @DisplayName("Then should start BeagleActivity and clear stack")
        @Test
        fun testResetApplicationShouldStartBeagleActivityAndClearStack() {
            // Given
            every { context.startActivity(any()) } just Runs
            val flagSlot = slot<Int>()
            every { anyConstructed<Intent>().addFlags(capture(flagSlot)) } returns intent

            // When
            BeagleNavigator.resetApplication(context, route, null, navigationContext)

            // Then
            verify(exactly = 1) { context.startActivity(any()) }
            assertEquals(
                flagSlot.captured,
                (Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
    }

    @DisplayName("When ResetStack")
    @Nested
    inner class ResetStack {

        @DisplayName("Then should start next activity and clear stack")
        @Test
        fun testResetStackShouldLaunchActivityAndClearStack() {
            // Given
            val nextActivity: ActivityResultLauncher<Intent> = mockk(relaxed = true)

            every { context.finish() } just Runs
            every { context.nextActivity } returns nextActivity

            // When
            BeagleNavigator.resetStack(context, route, null, navigationContext)

            // Then
            verify(exactly = 1) {
                context.finish()
                nextActivity.launch(any())
            }
        }

        @DisplayName("Then should start activity and clear stack")
        @Test
        fun testResetStackShouldStartActivityAndClearStack() {
            // Given
            val context = mockk<AppCompatActivity>()
            every { context.finish() } just Runs
            every { context.startActivity(any()) } just Runs

            // When
            BeagleNavigator.resetStack(context, route, null, navigationContext)

            // Then
            verify(exactly = 1) {
                context.finish()
                context.startActivity(any())
            }
        }

    }

    @DisplayName("When PopToView")
    @Nested
    inner class PopToView {
        private val relativePath = "/relative-path"
        private val baseUrl = "http://baseurl.com"
        private val urlBuilder: UrlBuilder = mockk(relaxed = true)

        @DisplayName("Then should call popToBackStack")
        @Test
        fun testPopToViewShouldCallPopToBackStack() {
            // Given
            commonMock()
            val backStackEntry: FragmentManager.BackStackEntry = mockk(relaxed = true)
            every { supportFragmentManager.getBackStackEntryAt(0) } returns backStackEntry
            every { backStackEntry.name } returns relativePath

            // When
            BeagleNavigator.popToView(context, relativePath, navigationContext)

            // Then
            verify(exactly = 1) {
                supportFragmentManager.popBackStackImmediate(relativePath, 0)
                fragment.updateNavigationContext(navigationContext)
            }
        }

        @DisplayName("Then should formatted the route and call popToBackStack with the name on backStack ")
        @Test
        fun testPopToViewWithRelativePathAndFullPathOnBackStackShouldCallPopToBackStack() {
            // Given
            commonMock()
            val fullPath = baseUrl + relativePath
            val backStackEntry: FragmentManager.BackStackEntry = mockk(relaxed = true)
            every { supportFragmentManager.getBackStackEntryAt(0) } returns backStackEntry
            every { backStackEntry.name } returns fullPath

            // When
            BeagleNavigator.popToView(context, relativePath, navigationContext)

            // Then
            verify {
                urlBuilder.format(baseUrl, relativePath)
                urlBuilder.format(baseUrl, fullPath)
                supportFragmentManager.popBackStackImmediate(fullPath, 0)
            }
        }

        @DisplayName("Then should formatted the route and call popToBackStack with the name on backStack ")
        @Test
        fun testPopToViewWithRelativePathAndRelativePathOnBackStackShouldCallPopToBackStack() {
            // Given
            commonMock()
            val backStackEntry: FragmentManager.BackStackEntry = mockk(relaxed = true)
            every { supportFragmentManager.getBackStackEntryAt(0) } returns backStackEntry
            every { backStackEntry.name } returns relativePath

            // When
            BeagleNavigator.popToView(context, relativePath, navigationContext)

            // Then
            verify {
                urlBuilder.format(baseUrl, relativePath)
                urlBuilder.format(baseUrl, relativePath)
                supportFragmentManager.popBackStackImmediate(relativePath, 0)
            }
        }

        @DisplayName("Then should formatted the route and call popToBackStack with the name on backStack ")
        @Test
        fun testPopToViewWithFullPathAndRelativePathOnBackStackShouldCallPopToBackStack() {
            // Given
            commonMock()
            val fullPath = baseUrl + relativePath
            val backStackEntry: FragmentManager.BackStackEntry = mockk(relaxed = true)
            every { supportFragmentManager.getBackStackEntryAt(0) } returns backStackEntry
            every { backStackEntry.name } returns relativePath

            // When
            BeagleNavigator.popToView(context, fullPath, navigationContext)

            // Then
            verify {
                urlBuilder.format(baseUrl, fullPath)
                urlBuilder.format(baseUrl, relativePath)
                supportFragmentManager.popBackStackImmediate(relativePath, 0)
            }
        }

        @DisplayName("Then should formatted the route and call popToBackStack with the name on backStack ")
        @Test
        fun testPopToViewWithFullPathAndFullPathOnBackStackShouldCallPopToBackStack() {
            // Given
            commonMock()
            val fullPath = baseUrl + relativePath
            val backStackEntry: FragmentManager.BackStackEntry = mockk(relaxed = true)
            every { supportFragmentManager.getBackStackEntryAt(0) } returns backStackEntry
            every { backStackEntry.name } returns fullPath

            // When
            BeagleNavigator.popToView(context, fullPath, navigationContext)

            // Then
            verify {
                urlBuilder.format(baseUrl, fullPath)
                urlBuilder.format(baseUrl, fullPath)
                supportFragmentManager.popBackStackImmediate(fullPath, 0)
            }
        }

        private fun commonMock() {
            every { beagleSdk.config.baseUrl } returns baseUrl
            every { beagleSdk.urlBuilder } returns urlBuilder
            every { supportFragmentManager.popBackStackImmediate(any<String>(), any()) } returns true
            every { supportFragmentManager.backStackEntryCount } returns 1
        }
    }

    @DisplayName("When PushStack")
    @Nested
    inner class PushStack {
        @DisplayName("Then should start BeagleActivity")
        @Test
        fun testPushStackShouldStartBeagleActivity() {
            // Given
            val context = mockk<Activity>()
            every { context.startActivity(any()) } just Runs

            // When
            BeagleNavigator.pushStack(context, route, null, navigationContext)

            // Then
            verify(exactly = 1) { context.startActivity(any()) }
        }

        @DisplayName("Then should start next activity with launch")
        @Test
        fun testPushStackShouldStarLaunch() {
            // Given
            val nextActivity: ActivityResultLauncher<Intent> = mockk(relaxed = true)

            every { context.nextActivity } returns nextActivity

            // When
            BeagleNavigator.pushStack(context, route, null, navigationContext)

            // Then
            verify(exactly = 1) {
                nextActivity.launch(any())
            }
        }
    }
}
