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

package br.com.zup.beagle.android.data.serializer.components

import br.com.zup.beagle.android.components.page.PageView
import br.com.zup.beagle.android.context.constant
import br.com.zup.beagle.android.data.serializer.makeActionAlertJson
import br.com.zup.beagle.android.data.serializer.makeActionAlertObject
import br.com.zup.beagle.android.data.serializer.makeButtonJson
import br.com.zup.beagle.android.data.serializer.makeContextWithPrimitiveValueJson
import br.com.zup.beagle.android.data.serializer.makeObjectButton
import br.com.zup.beagle.android.data.serializer.makeObjectContextWithPrimitiveValue
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("Given a PageView")
class PageViewSerializerTest : BaseServerDrivenComponentSerializerTest() {

    @DisplayName("When try to deserialize json")
    @Nested
    inner class PageViewDeserializationTests {

        @DisplayName("Then should return correct object")
        @Test
        fun testPageViewDeserialization() {
            testDeserializeJson(makePageViewJson(), makeObjectPageView())
        }

        @DisplayName("Then should return correct object with PageIndicator")
        @Test
        fun testPageViewDeserializationWithPageIndicator() {
            // Given
            val expectedComponent = makeObjectPageViewWithPageIndicator()
            val json = makePageViewWithPageIndicatorJson()

            // When
            val deserializedComponent = deserialize(json) as PageView

            // Then
            Assertions.assertNotNull(deserializedComponent)
            Assertions.assertEquals(expectedComponent.children, deserializedComponent.children)
            Assertions.assertEquals(expectedComponent.context, deserializedComponent.context)
        }
    }

    @DisplayName("When try to serialize object")
    @Nested
    inner class PageViewSerializationTests {

        @DisplayName("Then should return correct json")
        @Test
        fun testPageViewSerialization() {
            testSerializeObject(makePageViewJson(), makeObjectPageView())
        }

        @DisplayName("Then should return correct json with PageIndicator")
        @Test
        fun testPageViewSerializationWithPageIndicator() {
            testSerializeObject(
                makePageViewWithPageIndicatorJson(),
                makeObjectPageViewWithPageIndicator()
            )
        }
    }

    private fun makePageViewJson() = """
    {
        "_beagleComponent_": "beagle:pageview",
        "children": [
            ${makeButtonJson()},
            ${makeButtonJson()},
            ${makeButtonJson()}
        ],
        "context": ${makeContextWithPrimitiveValueJson()},
        "onPageChange": [${makeActionAlertJson()}],
        "currentPage": 1
    }
"""

    private fun makePageViewWithPageIndicatorJson() = """
    {
        "_beagleComponent_": "beagle:pageview",
        "children": [${makeButtonJson()}],
        "context": ${makeContextWithPrimitiveValueJson()}
    }
"""

    private fun makeObjectPageView() = PageView(
        children = listOf(
            makeObjectButton(),
            makeObjectButton(),
            makeObjectButton()
        ),
        context = makeObjectContextWithPrimitiveValue(),
        onPageChange = listOf(makeActionAlertObject()),
        currentPage = constant(1)
    )

    private fun makeObjectPageViewWithPageIndicator() = PageView(
        children = listOf(makeObjectButton()),
        context = makeObjectContextWithPrimitiveValue(),
    )
}