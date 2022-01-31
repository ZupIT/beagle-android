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

package br.com.zup.beagle.android.networking

import android.os.Parcel
import android.os.Parcelable

/**
 * RequestData is used to do requests.
 *
 * @param url Server URL in string format.
 * @param httpAdditionalData pass additional data to the request.
 *
 */
data class RequestData(
    var url: String = "",
    val httpAdditionalData: HttpAdditionalData = HttpAdditionalData(),
) : Parcelable {


    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(url)
        dest?.writeParcelable(httpAdditionalData, flags)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<RequestData> = object : Parcelable.Creator<RequestData> {
            override fun createFromParcel(source: Parcel) =
                RequestData(
                    url = source.readString() ?: "",
                    httpAdditionalData = source.readParcelable(HttpAdditionalData::class.java.classLoader)
                        ?: HttpAdditionalData(),
                )

            override fun newArray(size: Int) = arrayOfNulls<RequestData?>(size)
        }
    }
}
