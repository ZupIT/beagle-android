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

import android.os.Parcel
import android.os.Parcelable
import br.com.zup.beagle.android.widget.core.BeagleJson
import br.com.zup.beagle.android.annotation.ContextDataValue
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize

/**
 * Context is a variable of any type, including a map that defines a set of key/value pairs.
 * Through bindings, the value of a context can be accessed by any component or action on your scope.
 *
 * @param id a string that later has to be identified.
 * @param value is a parameter (data) of any kind.
 */
@BeagleJson
@Parcelize
data class ContextData(
    val id: String,
    @ContextDataValue
    val value: Any,
) : Parcelable {

    private companion object : Parceler<ContextData> {
        override fun ContextData.write(parcel: Parcel, flags: Int) {
            parcel.writeString(id)
            parcel.writeString(value.toString())
        }

        override fun create(parcel: Parcel): ContextData {
            val id = parcel.readString()!!
            val value = parcel.readString()!!.normalizeContextValue()

            return ContextData(id = id, value = value)
        }
    }
}