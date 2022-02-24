/*
 * Copyright 2020, 2022 ZUP IT SERVICOS EM TECNOLOGIA E INOVACAO SA
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

package br.com.zup.beagle.sample

import android.util.Log
import br.com.zup.beagle.android.analytics.AnalyticsConfig
import br.com.zup.beagle.android.analytics.AnalyticsProvider
import br.com.zup.beagle.android.analytics.AnalyticsRecord
import br.com.zup.beagle.android.annotation.BeagleComponent

@BeagleComponent
class AppAnalytics : AnalyticsProvider {
    override fun getConfig(): AnalyticsConfig = object : AnalyticsConfig {
        override var actions: Map<String, List<String>>? = hashMapOf(
            "beagle:alert" to listOf("message", "title")
        )
        override var enableScreenAnalytics: Boolean? = true
    }

    override fun createRecord(record: AnalyticsRecord) {
        Log.i("analytics2", record.toString())
    }
}