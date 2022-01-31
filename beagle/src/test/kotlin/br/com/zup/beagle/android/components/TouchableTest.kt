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
import br.com.zup.beagle.android.action.Action
import br.com.zup.beagle.android.action.Navigate
import br.com.zup.beagle.android.data.PreFetchHelper
import br.com.zup.beagle.android.utils.handleEvent
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TouchableTest : BaseComponentTest() {

    private val onClickListenerSlot = slot<View.OnClickListener>()

    private val actions = listOf(Navigate.PopView())

    private lateinit var touchable: Touchable

    @BeforeAll
    override fun setUp() {
        super.setUp()

        mockkStatic("br.com.zup.beagle.android.utils.WidgetExtensionsKt")

        every { view.context } returns mockk()
        every { view.setOnClickListener(capture(onClickListenerSlot)) } just Runs
    }

    @BeforeEach
    fun clear() {
        mockkConstructor(PreFetchHelper::class)
        every {
            anyConstructed<PreFetchHelper>().handlePreFetch(
                any(),
                any<List<Action>>()
            )
        } just Runs
    }

    @Test
    fun build_should_make_child_view() {
        // Given
        touchable = Touchable(actions, mockk(relaxed = true))
        val actual = touchable.buildView(rootView)

        // Then
        verify(exactly = 1) {
            anyConstructed<PreFetchHelper>().handlePreFetch(
                rootView,
                actions
            )
        }
        assertEquals(view, actual)
    }

    @Test
    fun build_should_call_onClickListener() {
        // Given
        touchable = Touchable(actions, mockk(relaxed = true))

        // When
        callBuildAndClick()

        // Then
        verify(exactly = 1) {
            touchable.handleEvent(rootView, view, actions, analyticsValue = "onPress")
        }
    }

    private fun callBuildAndClick() {
        touchable.buildView(rootView)
        onClickListenerSlot.captured.onClick(view)
    }
}
