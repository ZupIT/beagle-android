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
import br.com.zup.beagle.android.data.serializer.adapter.generic.TypeAdapterResolver
import br.com.zup.beagle.android.mockdata.TypeAdapterResolverImpl
import br.com.zup.beagle.android.testutil.withoutWhiteSpaces
import br.com.zup.beagle.android.widget.WidgetView
import io.mockk.every
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll

abstract class BaseSerializerTest<T>(private val clazz: Class<T>) : BaseTest() {

    protected var registeredWidgets = listOf<Class<WidgetView>>()
    protected var registeredActions = listOf<Class<Action>>()
    protected var typeAdapterResolver: TypeAdapterResolver? = TypeAdapterResolverImpl()

    @BeforeAll
    override fun setUp() {
        super.setUp()

        moshi = BeagleMoshi.createMoshi(registeredWidgets = registeredWidgets,
            registeredActions = registeredActions,
            typeAdapterResolver = typeAdapterResolver)
    }

    fun serialize(anObject: T): String = moshi.adapter(clazz).toJson(anObject)
    fun deserialize(json: String) = moshi.adapter(clazz).fromJson(json)

    fun testDeserializeJson(json: String, expectedComponent: T) {
        // When
        val deserializedComponent = deserialize(json)

        // Then
        Assertions.assertNotNull(deserializedComponent)
        Assertions.assertEquals(expectedComponent, deserializedComponent)
    }

    fun testSerializeObject(expectedJson: String, objectComponent: T) {
        // When
        val serializedJson = serialize(objectComponent)

        // Then
        Assertions.assertNotNull(serializedJson)
        Assertions.assertEquals(expectedJson.withoutWhiteSpaces(), serializedJson.withoutWhiteSpaces())
    }
}