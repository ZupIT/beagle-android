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

package br.com.zup.beagle.android.data.serializer.adapter.defaults

import br.com.zup.beagle.android.data.serializer.adapter.knownNotNull
import br.com.zup.beagle.android.data.serializer.adapter.mapKeyAndValueTypes
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonAdapter.Factory
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.rawType
import java.lang.reflect.Type

/**
 * This adapter replaces moshi MapJsonAdapter for all Map<K,V> mappings.
 * With this new implementation we now instantiate a LinkedHashMap instead a LinkedHashTreeMap(internal moshi class).
 * The error can be reproduced by trying to navigate recursively through the memberProperties of a LinkedHashTreeMap.
 * At some point we have a AbstractMap$2 that causes the function(memberProperties) below crash for being an
 * anonymous class.
 * @see kotlin.reflect.full.memberProperties
 * @see br.com.zup.beagle.android.analytics.ActionReportFactory
 * @see com.squareup.moshi.LinkedHashTreeMap
 */
internal class MoshiMapJsonAdapter<K, V>(moshi: Moshi, keyType: Type, valueType: Type) : JsonAdapter<Map<K, V?>>() {
    private val keyAdapter: JsonAdapter<K> = moshi.adapter(keyType)
    private val valueAdapter: JsonAdapter<V> = moshi.adapter(valueType)

    override fun toJson(writer: JsonWriter, value: Map<K, V?>?) {
        writer.beginObject()
        // Never null because we wrap in nullSafe()
        for ((k, v) in knownNotNull(value)) {
            if (k == null) {
                throw JsonDataException("Map key is null at ${writer.path}")
            }
            writer.promoteValueToName()
            keyAdapter.toJson(writer, k)
            valueAdapter.toJson(writer, v)
        }
        writer.endObject()
    }

    override fun fromJson(reader: JsonReader): Map<K, V?> {
        val result = LinkedHashMap<K, V?>()
        reader.beginObject()
        while (reader.hasNext()) {
            reader.promoteNameToValue()
            val name = keyAdapter.fromJson(reader) ?: throw JsonDataException("Map key is null at ${reader.path}")
            val value = valueAdapter.fromJson(reader)
            val replaced = result.put(name, value)
            if (replaced != null) {
                throw JsonDataException(
                    "Map key '$name' has multiple values at path ${reader.path}: $replaced and $value"
                )
            }
        }
        reader.endObject()
        return result
    }

    override fun toString() = "JsonAdapter($keyAdapter=$valueAdapter)"

    companion object {
        val FACTORY = Factory { type, _, moshi ->
            val rawType = Types.getRawType(type)
            if (rawType == Map::class.java) {
                return@Factory newMoshiMapAdapter<Any>(
                    type,
                    moshi
                ).nullSafe()
            }
            null
        }

        private fun <T> newMoshiMapAdapter(type: Type, moshi: Moshi): JsonAdapter<*> {
            val rawType = type.rawType
            val keyAndValue = mapKeyAndValueTypes(type, rawType)

            return MoshiMapJsonAdapter<Any, Any>(moshi, keyAndValue[0], keyAndValue[1]).nullSafe()
        }
    }
}