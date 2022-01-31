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

import android.content.DialogInterface
import android.view.View
import androidx.appcompat.app.AlertDialog
import br.com.zup.beagle.android.context.constant
import br.com.zup.beagle.android.testutil.RandomData
import br.com.zup.beagle.android.utils.handleEvent
import br.com.zup.beagle.android.view.ViewFactory
import br.com.zup.beagle.android.widget.RootView
import io.mockk.Runs
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AlertTest {

    private val rootView: RootView = mockk(relaxed = true)

    private val builder = mockk<AlertDialog.Builder>()
    private val dialogBox = mockk<AlertDialog>()
    private val titleSlot = slot<String>()
    private val messageSlot = slot<String>()
    private val buttonTextSlot = slot<String>()
    private val listenerSlot = slot<DialogInterface.OnClickListener>()
    private val view: View = mockk()

    @BeforeAll
    fun setUp() {
        mockkObject(ViewFactory)

        every { ViewFactory.makeAlertDialogBuilder(any()) } returns builder
        every { builder.setTitle(capture(titleSlot)) } returns builder
        every { builder.setMessage(capture(messageSlot)) } returns builder
        every {
            builder.setPositiveButton(
                capture(buttonTextSlot),
                capture(listenerSlot)
            )
        } returns builder
        every { builder.show() } returns mockk()
    }

    @BeforeEach
    fun clear() {
        clearMocks(dialogBox)
    }

    @Test
    fun `execute should create a AlertAction`() {
        // Given
        val action = Alert(
            title = constant(RandomData.string()),
            message = constant(RandomData.string()),
            labelOk = RandomData.string()
        )

        // When
        action.execute(rootView, mockk())

        // Then
        assertEquals(action.title?.value, titleSlot.captured)
        assertEquals(action.message.value, messageSlot.captured)
        assertEquals(action.labelOk, buttonTextSlot.captured)
    }

    @Test
    fun `execute should create a AlertAction with text default`() {
        // Given
        val action = Alert(
            title = constant(RandomData.string()),
            message = constant(RandomData.string())
        )
        val randomLabel = RandomData.string()
        every { rootView.getContext().getString(android.R.string.ok) } returns randomLabel

        // When
        action.execute(rootView, view)

        // Then
        assertEquals(action.title?.value, titleSlot.captured)
        assertEquals(action.message.value, messageSlot.captured)
        assertEquals(randomLabel, buttonTextSlot.captured)
    }

    @Test
    fun `click should dismiss dialog`() {
        // Given
        val action = Alert(
            title = constant(RandomData.string()),
            message = constant(RandomData.string()),
            labelOk = RandomData.string()
        )
        every { dialogBox.dismiss() } just Runs

        // When
        action.execute(rootView, view)
        listenerSlot.captured.onClick(dialogBox, 0)

        // Then
        verify(exactly = 1) { dialogBox.dismiss() }
    }

    @Test
    fun `should handle onPressOk when click in button`() {
        // Given
        val onPressOk: List<Action> = listOf(mockk(relaxed = true))
        val action = Alert(
            title = constant(RandomData.string()),
            message = constant(RandomData.string()),
            labelOk = RandomData.string(),
            onPressOk = onPressOk
        )
        every { dialogBox.dismiss() } just Runs

        // When
        action.execute(rootView, view)
        listenerSlot.captured.onClick(dialogBox, 0)

        // Then
        verify(exactly = 1) {
            action.handleEvent(
                rootView,
                view,
                onPressOk,
                analyticsValue = "onPressOk"
            )
        }
    }

}