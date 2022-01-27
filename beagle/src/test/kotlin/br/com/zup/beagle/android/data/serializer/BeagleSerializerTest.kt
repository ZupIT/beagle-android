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

package br.com.zup.beagle.android.data.serializer

import br.com.zup.beagle.android.BaseTest
import br.com.zup.beagle.android.action.Action
import br.com.zup.beagle.android.action.Navigate
import br.com.zup.beagle.android.action.Route
import br.com.zup.beagle.android.components.Button
import br.com.zup.beagle.android.context.constant
import br.com.zup.beagle.android.exception.BeagleException
import br.com.zup.beagle.android.logger.BeagleMessageLogs
import br.com.zup.beagle.android.testutil.RandomData
import br.com.zup.beagle.android.widget.core.ServerDrivenComponent
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.IOException

@DisplayName("Given a BeagleSerializer")
class BeagleSerializerTest : BaseTest() {

    private val moshi: Moshi = mockk()

    private val jsonAdapter: JsonAdapter<ServerDrivenComponent> = mockk()

    private val actionJsonAdapter: JsonAdapter<Action> = mockk()

    private lateinit var beagleSerializer: BeagleSerializer

    @BeforeAll
    override fun setUp() {
        super.setUp()
        beagleSerializer = BeagleSerializer(BeagleMoshi)

        mockkObject(BeagleMessageLogs)
        mockkObject(BeagleMoshi)

        every { BeagleMessageLogs.logDeserializationError(any(), any()) } just Runs
        every { BeagleMoshi.moshi } returns moshi
        every { moshi.adapter(ServerDrivenComponent::class.java) } returns jsonAdapter
        every { moshi.adapter(Action::class.java) } returns actionJsonAdapter
    }

    @DisplayName("When try to serialize json")
    @Nested
    inner class SerializeTest {

        @DisplayName("Then should return correct json")
        @Test
        fun testSerializeObject() {
            // Given
            val json = "{}"
            val button = Button(constant(RandomData.string()))
            every { jsonAdapter.toJson(button) } returns json

            // When
            val actual = beagleSerializer.serializeComponent(button)

            // Then
            assertEquals(json, actual)
        }

        @DisplayName("Then should throw BeagleException when toJson returns null")
        @Test
        fun testSerializeObjectException() {
            // Given
            val button = Button(constant(RandomData.string()))
            every { jsonAdapter.toJson(button) } returns null

            // Then
            assertThrows<BeagleException> { beagleSerializer.serializeComponent(button) }
        }

        @DisplayName("Then should throw BeagleException when toJson throws exception")
        @Test
        fun testSerializeObjectWhenToJsonThrowsException() {
            // Given
            val exception = IOException()
            val button = Button(constant(RandomData.string()))
            every { jsonAdapter.toJson(any()) } throws exception

            // Then
            assertThrows<BeagleException> { beagleSerializer.serializeComponent(button) }
        }
    }

    @DisplayName("When try to deserialize object")
    @Nested
    inner class DeserializeTest {

        @DisplayName("Then should return correct object")
        @Test
        fun testDeserializeJson() {
            // Given
            val json = "{}"
            val button = Button(constant(RandomData.string()))
            every { jsonAdapter.fromJson(json) } returns button

            // When
            val actual = beagleSerializer.deserializeComponent(json)

            // Then
            assertEquals(button, actual)
        }

        @DisplayName("Then should throw BeagleException when fromJson returns null")
        @Test
        fun testDeserializeJsonWhenFromJsonReturnsNull() {
            // Given
            val json = "{}"
            every { jsonAdapter.fromJson(json) } returns null

            // Then
            assertThrows<BeagleException> { beagleSerializer.deserializeComponent(json) }
        }

        @DisplayName("Then should throw BeagleException when fromJson throws exception")
        @Test
        fun testDeserializeJsonWhenFromJsonThrowsException() {
            // Given
            val exception = IOException()
            every { jsonAdapter.fromJson(any<String>()) } throws exception

            // Then
            assertThrows<BeagleException> { beagleSerializer.deserializeComponent(RandomData.string()) }
        }

        @DisplayName("Then should return correct action")
        @Test
        fun testDeserializeActionJson() {
            // Given
            val json = "{}"
            val navigate = Navigate.PushView(Route.Remote(constant("")))
            every { actionJsonAdapter.fromJson(json) } returns navigate

            // When
            val actual = beagleSerializer.deserializeAction(json)

            // Then
            assertEquals(navigate, actual)
        }

        @DisplayName("Then should throw BeagleException when fromJson returns null")
        @Test
        fun testDeserializeActionJsonWhenFromJsonReturnsNull() {
            // Given
            val json = "{}"
            every { actionJsonAdapter.fromJson(json) } returns null

            // Then
            assertThrows<BeagleException> { beagleSerializer.deserializeAction(json) }
        }

        @DisplayName("Then should throw BeagleException when fromJson throws exception")
        @Test
        fun testDeserializeActionJsonFromJsonThrowsException() {
            // Given
            val exception = IOException()
            every { actionJsonAdapter.fromJson(any<String>()) } throws exception

            // Then
            assertThrows<BeagleException> { beagleSerializer.deserializeAction(RandomData.string()) }
        }
    }
}
