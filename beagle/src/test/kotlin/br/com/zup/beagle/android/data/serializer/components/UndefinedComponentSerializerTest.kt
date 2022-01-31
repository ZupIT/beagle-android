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

import br.com.zup.beagle.android.widget.UndefinedWidget
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("Given an UndefinedComponent")
class UndefinedComponentSerializerTest : BaseServerDrivenComponentSerializerTest() {

    @DisplayName("When try to deserialize json")
    @Nested
    inner class UndefinedComponentDeserializationTests {

        @DisplayName("Then should return correct object")
        @Test
        fun testUndefinedComponentDeserialization() {
            // Given
            val json = makeUndefinedComponentJson()

            // When
            val deserializedComponent = deserialize(json)

            // Then
            Assertions.assertNotNull(deserializedComponent)
            Assertions.assertTrue(deserializedComponent is UndefinedWidget)
        }

    }

    @DisplayName("When try to serialize object")
    @Nested
    inner class UndefinedComponentSerializationTests {

        @DisplayName("Then should return correct json")
        @Test
        fun testUndefinedComponentSerialization() {
            testSerializeObject(
                makeUndefinedWidgetJson(),
                makeObjectUndefinedComponent()
            )
        }
    }

    private fun makeUndefinedComponentJson() = """
    {
        "_beagleComponent_": "custom:new"
    }
"""

    private fun makeUndefinedWidgetJson() = """
    {
        "_beagleComponent_": "beagle:undefinedwidget"
    }
"""

    private fun makeObjectUndefinedComponent() = UndefinedWidget()

}