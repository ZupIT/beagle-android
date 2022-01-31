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

import br.com.zup.beagle.android.networking.HttpAdditionalData
import br.com.zup.beagle.android.networking.HttpMethod
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import org.json.JSONObject
import java.lang.Exception

@Suppress("UNCHECKED_CAST")
class HttpAdditionalDataAdapter {

    @ToJson
    fun toJson(httpAdditionalData: HttpAdditionalData) = JSONObject().apply {
        put("method", httpAdditionalData.method)
        put("headers", httpAdditionalData.headers)
        put("body", httpAdditionalData.body)
    }

    @FromJson
    fun fromJson(httpAdditionalData: JSONObject) = HttpAdditionalData(
        method = getMethod(httpAdditionalData),
        headers = getHeaders(httpAdditionalData),
        body = getBody(httpAdditionalData),
    )

    private fun getMethod(httpAdditionalData: JSONObject) = try {
        HttpMethod.valueOf(httpAdditionalData["method"].toString())
    } catch (exception: Exception) {
        HttpMethod.GET
    }

    private fun getHeaders(httpAdditionalData: JSONObject) = try {
        val headersMap = mutableMapOf<String, String>()
        val headers = httpAdditionalData["headers"] as JSONObject
        headers.keys().forEach { key ->
            headersMap[key] = headers[key].toString()
        }
        headersMap
    } catch (exception: Exception) {
        mutableMapOf()
    }

    private fun getBody(httpAdditionalData: JSONObject) = try {
        httpAdditionalData["body"]
    } catch (exception: Exception) {
        null
    }
}
