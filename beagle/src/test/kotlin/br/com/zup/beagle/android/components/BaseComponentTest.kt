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

import android.view.View
import br.com.zup.beagle.android.BaseConfigurationTest
import br.com.zup.beagle.android.BaseTest
import br.com.zup.beagle.android.engine.renderer.ViewRenderer
import br.com.zup.beagle.android.engine.renderer.ViewRendererFactory
import br.com.zup.beagle.android.view.ViewFactory
import br.com.zup.beagle.android.view.custom.BeagleFlexView
import br.com.zup.beagle.android.widget.core.ServerDrivenComponent
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkObject
import org.junit.jupiter.api.BeforeAll

abstract class BaseComponentTest : BaseConfigurationTest() {

    private val viewRender: ViewRenderer<ServerDrivenComponent> = mockk()

    internal val beagleFlexView: BeagleFlexView = mockk(relaxed = true)

    protected val view = mockk<View>(relaxed = true)

    @BeforeAll
    override fun setUp() {
        super.setUp()

        mockkConstructor(ViewRendererFactory::class)
        mockkObject(ViewFactory)

        every { anyConstructed<ViewRendererFactory>().make(any()) } returns viewRender
        every { ViewFactory.makeBeagleFlexView(any()) } returns beagleFlexView
        every { ViewFactory.makeBeagleFlexView(any(), any()) } returns beagleFlexView
        every { ViewFactory.makeBeagleFlexView(any(), any(), any()) } returns beagleFlexView
        every { viewRender.build(any()) } returns view
        every { view.setTag(any(), any()) } just Runs
    }
}