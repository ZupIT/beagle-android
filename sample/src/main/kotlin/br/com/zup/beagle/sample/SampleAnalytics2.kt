/*
 * Copyright 2020 ZUP IT SERVICOS EM TECNOLOGIA E INOVACAO SA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.beaglesampleandroid.beagle

import android.util.Log
import br.com.zup.beagle.android.annotation.BeagleComponent
import br.com.zup.beagle.newanalytics.AnalyticsConfig
import br.com.zup.beagle.newanalytics.AnalyticsProvider
import br.com.zup.beagle.newanalytics.AnalyticsRecord

@BeagleComponent
class SampleAnalytics2:AnalyticsProvider {
    override fun getConfig(): AnalyticsConfig = object: AnalyticsConfig{
        override var actions: Map<String, List<String>>? = hashMapOf(
            "beagle:alert" to listOf("message", "title")
        )
        override var enableScreenAnalytics: Boolean? = true
    }

    override fun createRecord(record: AnalyticsRecord) {
        Log.i("analytics2", record.toString())
    }
}