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

import br.com.zup.beagle.android.testutil.RandomData
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RequestDataTest {

    private lateinit var requestData: RequestData

    @BeforeEach
    fun setUp() {
        requestData = RequestData(url = RandomData.httpUrl())
    }

    @Test
    fun requestData_should_have_method_GET_as_default() {
        assertEquals(HttpMethod.GET, requestData.httpAdditionalData.method)
    }

    @Test
    fun requestData_should_have_empty_headers() {
        assertEquals(requestData.httpAdditionalData.headers, FixedHeaders.headers)
    }

    @Test
    fun requestData_should_have_data_null() {
        assertNull(requestData.httpAdditionalData.body)
    }
}