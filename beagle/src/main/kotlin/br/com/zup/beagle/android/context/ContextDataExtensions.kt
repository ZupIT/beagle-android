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

package br.com.zup.beagle.android.context

import com.squareup.moshi.Moshi
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/*
 * This function should be used in cases where user created the context implicit or explicit
 * because if user pass a map, list or any kind of object, this should be normalized
 * to a JSONArray or JSONObject.
 */
internal fun ContextData.normalize(moshi: Moshi): ContextData {
    return ContextData(this.id, value.normalizeContextValue(moshi))
}

internal fun Any.normalizeContextValue(moshi: Moshi): Any {

    return when {
        isValueNormalized() -> {
            this
        }
        isEnum(this::class) -> {
            this.toString()
        }
        else -> {
            val newValue = moshi.adapter(Any::class.java).toJson(this) ?: ""
            newValue.normalizeContextValue()
        }
    }
}

private fun isEnum(type: KClass<out Any>) = type.isSubclassOf(Enum::class)


internal fun String.normalizeContextValue(): Any {
    return try {
        JSONObject(this)
    } catch (ex: JSONException) {
        try {
            JSONArray(this)
        } catch (ex1: JSONException) {
            this
        }
    }
}

private fun Any.isValueNormalized(): Boolean {
    return this is String || this is Number || this is Boolean || this is JSONArray || this is JSONObject
}