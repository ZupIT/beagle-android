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

package br.com.zup.beagle.android

import br.com.zup.beagle.android.data.serializer.BeagleMoshi
import br.com.zup.beagle.android.data.serializer.BeagleSerializer
import br.com.zup.beagle.android.networking.HttpClient
import br.com.zup.beagle.android.networking.ViewClient
import br.com.zup.beagle.android.networking.urlbuilder.UrlBuilder
import br.com.zup.beagle.android.setup.BeagleConfigurator
import com.squareup.moshi.Moshi
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeAll

abstract class BaseConfigurationTest : BaseTest() {
    protected val baseUrl = "http://baseurl.com"
    protected val viewClient: ViewClient = mockk(relaxUnitFun = true, relaxed = true)
    internal val serializer: BeagleSerializer = mockk(relaxUnitFun = true, relaxed = true)
    protected val beagleConfigurator = mockk<BeagleConfigurator>(relaxUnitFun = true, relaxed = true)
    protected val urlBuilder = mockk<UrlBuilder>(relaxUnitFun = true, relaxed = true)
    protected val httpClient = mockk<HttpClient>(relaxUnitFun = true, relaxed = true)

    override lateinit var moshi: Moshi

    @BeforeAll
    override fun setUp() {
        super.setUp()
        moshi = BeagleMoshi.moshi
        every { beagleConfigurator.registeredOperations } returns emptyMap()
        every { beagleConfigurator.moshi } returns moshi
        every { beagleConfigurator.httpClient } returns httpClient
        every { beagleConfigurator.urlBuilder } returns urlBuilder
        every { beagleConfigurator.baseUrl } returns baseUrl
        every { beagleConfigurator.viewClient } returns viewClient
        every { beagleConfigurator.serializer } returns serializer
        every { rootView.getBeagleConfigurator() } returns beagleConfigurator
    }
}