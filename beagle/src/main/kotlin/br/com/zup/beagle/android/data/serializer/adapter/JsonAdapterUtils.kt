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

package br.com.zup.beagle.android.data.serializer.adapter

import com.squareup.moshi.internal.Util
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.Properties
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

internal fun mapKeyAndValueTypes(context: Type, contextRawType: Class<*>?): Array<Type> {
    // Work around a problem with the declaration of java.util.Properties. That class should extend
    // Hashtable<String, String>, but it's declared to extend Hashtable<Object, Object>.
    if (context === Properties::class.java) return arrayOf(String::class.java, String::class.java)
    val mapType = getSupertype(context, contextRawType!!, MutableMap::class.java)
    if (mapType is ParameterizedType) {
        return mapType.actualTypeArguments
    }
    return arrayOf(Any::class.java, Any::class.java)
}

internal fun getSupertype(context: Type?, contextRawType: Class<*>, supertype: Class<*>): Type {
    require(supertype.isAssignableFrom(contextRawType))
    return Util.resolve(
        context, contextRawType, Util.getGenericSupertype(context, contextRawType, supertype))
}

@Suppress("NOTHING_TO_INLINE")
internal inline fun <T> knownNotNull(value: T?): T {
    markNotNull(value)
    return value
}

@OptIn(ExperimentalContracts::class)
@Suppress("NOTHING_TO_INLINE")
internal inline fun <T> markNotNull(value: T?) {
    contract {
        returns() implies (value != null)
    }
}
