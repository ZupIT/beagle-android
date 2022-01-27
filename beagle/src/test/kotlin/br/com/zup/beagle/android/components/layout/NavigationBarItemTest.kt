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

package br.com.zup.beagle.android.components.layout

import br.com.zup.beagle.android.action.Action
import br.com.zup.beagle.android.context.Bind
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NavigationBarItemTest {

    @Nested
    inner class `Given a creation of NavigationBarItem` {

        @DisplayName("When call constructor")
        @Test
        fun `Should be set correct params`() {

            //GIVEN
            val textMock = "textMock"
            val imageMock = mockk<Bind<String>>()
            val actionMock = mockk<Action>()
            val actionsMock = listOf(actionMock)
            val accessibilityMock = null


            val expectedNavigationBarItem = mockk<NavigationBarItem>()
            every { expectedNavigationBarItem.text } returns textMock
            every { expectedNavigationBarItem.image } returns imageMock
            every { expectedNavigationBarItem.onPress } returns actionsMock
            every { expectedNavigationBarItem.accessibility } returns accessibilityMock

            //WHEN
            val navigationBarItem = NavigationBarItem(textMock, imageMock, actionsMock, accessibilityMock)

            //THEN
            Assertions.assertEquals(expectedNavigationBarItem.text, navigationBarItem.text)
            Assertions.assertEquals(expectedNavigationBarItem.image, navigationBarItem.image)
            Assertions.assertEquals(expectedNavigationBarItem.onPress, navigationBarItem.onPress)
            Assertions.assertEquals(expectedNavigationBarItem.accessibility, navigationBarItem.accessibility)

        }
    }


}