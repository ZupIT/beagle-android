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
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import br.com.zup.beagle.android.BaseTest
import br.com.zup.beagle.android.action.Action
import br.com.zup.beagle.android.action.AsyncActionStatus
import br.com.zup.beagle.android.action.Navigate
import br.com.zup.beagle.android.action.SendRequest
import br.com.zup.beagle.android.components.layout.Container
import br.com.zup.beagle.android.testutil.InstantExecutorExtension
import br.com.zup.beagle.android.utils.handleEvent
import br.com.zup.beagle.android.utils.setIsInitiableComponent
import br.com.zup.beagle.android.view.viewmodel.OnInitViewModel
import io.mockk.Runs
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.spyk
import io.mockk.verify
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@DisplayName("Given an OnInitiableComponent")
@ExtendWith(InstantExecutorExtension::class)
class OnInitiableComponentTest : BaseTest() {

    private lateinit var onInitViewModel: OnInitViewModel
    private val origin = mockk<View>(relaxed = true)
    private val listenerSlot = slot<View.OnAttachStateChangeListener>()
    private val id = 10

    @BeforeAll
    override fun setUp() {
        super.setUp()

        every { origin.id } returns id
        every { origin.addOnAttachStateChangeListener(capture(listenerSlot)) } just Runs
    }

    @BeforeEach
    fun clear() {
        clearMocks(
            rootView,
            origin,
            answers = false
        )

        onInitViewModel = spyk(OnInitViewModel())
        prepareViewModelMock(onInitViewModel)
    }

    @DisplayName("When handleOnInit is called")
    @Nested
    inner class HandleOnInit {

        @DisplayName("Then shouldn't call addOnAttachStateChangeListener with empty onInit actions list")
        @Test
        fun handleOnInitCallWithEmptyOnInitActionsList() {
            // Given
            val initiableWidget = Container(children = listOf())

            // When
            initiableWidget.handleOnInit(rootView, origin)

            // Then
            verify(exactly = 0) { origin.addOnAttachStateChangeListener(any()) }
        }

        @DisplayName("Then should call addOnAttachStateChangeListener with at least one onInit action")
        @Test
        fun handleOnInitCallWithAtLeastOneOnInitAction() {
            // Given
            val initiableWidget =
                Container(children = listOf(), onInit = listOf(Navigate.PopView()))

            // When
            initiableWidget.handleOnInit(rootView, origin)

            // Then
            verify(exactly = 1) { origin.addOnAttachStateChangeListener(listenerSlot.captured) }
        }

        @DisplayName("Then if onInit has at least one action, it should tag view as initiable_component")
        @Test
        fun checkIfViewIsTaggedAsInitiableComponent() {
            // Given
            val initiableWidget =
                Container(children = listOf(), onInit = listOf(Navigate.PopView()))

            // When
            initiableWidget.handleOnInit(rootView, origin)

            // Then
            verify(exactly = 1) { origin.setIsInitiableComponent(true) }
        }

        @DisplayName("Then if onInit actions list is empty, it shouldn't tag view as initiable_component")
        @Test
        fun checkIfViewIsNotTaggedAsInitiableComponent() {
            // Given
            val initiableWidget = Container(children = listOf())

            // When
            initiableWidget.handleOnInit(rootView, origin)

            // Then
            verify(exactly = 0) { origin.setIsInitiableComponent(any()) }
        }
    }

    @DisplayName("When onViewAttachedToWindow")
    @Nested
    inner class OnViewAttached {

        @DisplayName("Then should setOnInitActionStatus true")
        @Test
        fun onViewAttachedToWindow() {
            // Given
            val action = Navigate.PopView()
            val initiableWidget = Container(children = listOf(), onInit = listOf(action))

            // When
            initiableWidget.handleOnInit(rootView, origin)
            listenerSlot.captured.onViewAttachedToWindow(origin)

            // Then
            verify(exactly = 1) { onInitViewModel.setOnInitCalled(id, true) }
        }

        @DisplayName("Then should observe the action")
        @Test
        fun onViewAttachedToWindowObserve() {
            // Given
            val status = mockk<LiveData<AsyncActionStatus>>(relaxed = true)
            val action = mockk<SendRequest>(relaxed = true)
            every { action.status } returns status
            val initiableWidget = Container(children = listOf(), onInit = listOf(action))

            // When
            initiableWidget.handleOnInit(rootView, origin)
            listenerSlot.captured.onViewAttachedToWindow(origin)

            // Then
            verify(exactly = 1) { status.observe(rootView.getLifecycleOwner(), any()) }
        }

        @DisplayName("Then should executeActions only once")
        @Test
        fun onViewAttachedToWindowExecute() {
            // Given
            val action = mockk<Action>()
            every { action.execute(rootView, origin) } just Runs
            val initiableWidget = Container(children = listOf(), onInit = listOf(action))

            // When
            initiableWidget.handleOnInit(rootView, origin)
            listenerSlot.captured.onViewAttachedToWindow(origin)
            listenerSlot.captured.onViewAttachedToWindow(origin)

            // Then
            verify(exactly = 1) {
                action.handleEvent(
                    rootView,
                    origin,
                    action,
                    analyticsValue = "onInit"
                )
            }
        }

        @DisplayName("Then should setOnInitFinished true to FINISHED AsyncAction")
        @Test
        fun onViewAttachedToWindowActionStatus() {
            // Given
            val status = mockk<LiveData<AsyncActionStatus>>(relaxed = true)
            val action = mockk<SendRequest>(relaxed = true)
            every { action.status } returns status
            val observerSlot = slot<Observer<AsyncActionStatus>>()
            every { status.observe(rootView.getLifecycleOwner(), capture(observerSlot)) } just Runs
            val initiableWidget = Container(children = listOf(), onInit = listOf(action))

            // When
            initiableWidget.handleOnInit(rootView, origin)
            listenerSlot.captured.onViewAttachedToWindow(origin)
            observerSlot.captured.onChanged(AsyncActionStatus.FINISHED)

            // Then
            verify(exactly = 1) { onInitViewModel.setOnInitFinished(id, true) }
        }
    }

    @DisplayName("When markToRerunOnInit")
    @Nested
    inner class MarkToRerun {

        @DisplayName("Then should setOnInitActionStatus false")
        @Test
        fun markToRerunOnInit() {
            // Given
            val action = Navigate.PopView()
            val initiableWidget = Container(children = listOf(), onInit = listOf(action))

            // When
            initiableWidget.handleOnInit(rootView, origin)
            initiableWidget.markToRerunOnInit()

            // Then
            verify(exactly = 1) { onInitViewModel.setOnInitCalled(id, false) }
        }

        @DisplayName("Then should be able to executeActions again")
        @Test
        fun markToRerunOnInitAgain() {
            // Given
            val action = mockk<Action>()
            every { action.execute(rootView, origin) } just Runs
            val initiableWidget = Container(children = listOf(), onInit = listOf(action))

            // When
            initiableWidget.handleOnInit(rootView, origin)
            listenerSlot.captured.onViewAttachedToWindow(origin)
            initiableWidget.markToRerunOnInit()
            listenerSlot.captured.onViewAttachedToWindow(origin)

            // Then
            verify(exactly = 2) {
                action.handleEvent(
                    rootView,
                    origin,
                    action,
                    analyticsValue = "onInit"
                )
            }
        }
    }
}
