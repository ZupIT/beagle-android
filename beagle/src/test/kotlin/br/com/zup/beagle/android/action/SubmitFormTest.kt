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
import android.view.ViewGroup
import android.view.ViewParent
import br.com.zup.beagle.android.BaseTest
import br.com.zup.beagle.android.components.form.SimpleForm
import br.com.zup.beagle.android.logger.BeagleLoggerProxy
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class SubmitFormTest : BaseTest() {

    private val view = mockk<View>()
    private val parent = mockk<ViewGroup>()
    private val simpleForm = mockk<SimpleForm>(relaxed = true)

    @BeforeAll
    override fun setUp() {
        super.setUp()
        every { beagleSdk.logger } returns null
        mockkObject(BeagleLoggerProxy)
    }

    @Test
    fun `should call submit in simple form when execute`() {
        // When
        val action = SubmitForm()
        every { parent.getTag(any()) } returns simpleForm
        every { view.parent } returns parent as ViewParent

        action.execute(rootView, view)

        // Then
        verify(exactly = 1) { simpleForm.submit(rootView, view) }
    }

    @Test
    fun `should send log when not found simple form in parent`() {
        // When
        val action = SubmitForm()
        every { view.parent } returns null

        action.execute(rootView, view)

        // Then
        verify(exactly = 1) {
            BeagleLoggerProxy.error("Not found simple form in the parents")
        }
    }

}