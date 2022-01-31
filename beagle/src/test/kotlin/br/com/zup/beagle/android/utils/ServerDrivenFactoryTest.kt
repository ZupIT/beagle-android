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

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.zup.beagle.android.action.NavigationContext
import br.com.zup.beagle.android.networking.HttpAdditionalData
import br.com.zup.beagle.android.networking.HttpMethod
import br.com.zup.beagle.android.networking.RequestData
import br.com.zup.beagle.android.testutil.RandomData
import br.com.zup.beagle.android.view.BeagleActivity
import br.com.zup.beagle.android.view.ServerDrivenActivity
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ServerDrivenFactoryTest {

    private val navigationContext = NavigationContext(value = "")

    @Test
    fun `Given a Context When call newServerDrivenIntent Then should RequestData through bundle`() {
        // Given
        val requestData = RequestData(
            url = RandomData.string(),
            httpAdditionalData = HttpAdditionalData(
                method = HttpMethod.POST,
                headers = mapOf("key" to "value"),
                body = RandomData
            ),
        )
        val context = ApplicationProvider.getApplicationContext<Context>()

        // When
        val result = context.newServerDrivenIntent<ServerDrivenActivity>(requestData, navigationContext)

        // Then
        val expected = Intent()
        val bundle = Bundle()
        bundle.putParcelable(BeagleActivity.FIRST_SCREEN_REQUEST_KEY, requestData)
        bundle.putParcelable(BeagleActivity.NAVIGATION_CONTEXT_KEY, navigationContext)

        expected.putExtras(bundle)


        assertEquals(expected.extras!!.size(), result.extras!!.size())
        assertEquals(expected.extras!!.get(BeagleActivity.FIRST_SCREEN_REQUEST_KEY), result.extras!!.get(BeagleActivity.FIRST_SCREEN_REQUEST_KEY))
        assertEquals(expected.extras!!.get(BeagleActivity.NAVIGATION_CONTEXT_KEY),
            result.extras!!.get(BeagleActivity.NAVIGATION_CONTEXT_KEY))
    }

    @Test
    fun `Given a Context When call newServerDrivenIntent Then should json screen string through bundle`() {
        // Given
        val screenJson = "test"
        val context = ApplicationProvider.getApplicationContext<Context>()

        // When
        val result = context.newServerDrivenIntent<ServerDrivenActivity>(screenJson, navigationContext)

        // Then
        val expected = Intent()
        val bundle = Bundle()
        bundle.putString(BeagleActivity.FIRST_SCREEN_KEY, screenJson)
        bundle.putParcelable(BeagleActivity.NAVIGATION_CONTEXT_KEY, navigationContext)

        expected.putExtras(bundle)

        assertEquals(expected.extras!!.size(), result.extras!!.size())
        assertEquals(expected.extras!!.get(BeagleActivity.FIRST_SCREEN_KEY), result.extras!!.get(BeagleActivity.FIRST_SCREEN_KEY))
        assertEquals(expected.extras!!.get(BeagleActivity.NAVIGATION_CONTEXT_KEY),
            result.extras!!.get(BeagleActivity.NAVIGATION_CONTEXT_KEY))
    }
}
