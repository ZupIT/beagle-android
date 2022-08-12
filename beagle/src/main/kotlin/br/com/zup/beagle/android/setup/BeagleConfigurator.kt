/*
 * Copyright 2020 ZUP IT SERVICOS EM TECNOLOGIA E INOVACAO SA
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

package br.com.zup.beagle.android.setup

import android.app.Application
import br.com.zup.beagle.android.data.serializer.BeagleJsonSerializer
import br.com.zup.beagle.android.data.serializer.BeagleJsonSerializerFactory
import br.com.zup.beagle.android.data.serializer.BeagleMoshi
import br.com.zup.beagle.android.data.serializer.BeagleSerializer
import br.com.zup.beagle.android.networking.ViewClientDefault
import com.squareup.moshi.Moshi

class BeagleConfigurator(
    val beagleSdk: BeagleSdk,
    val moshi: Moshi,
    val application: Application,
    val serializer: BeagleJsonSerializer = BeagleJsonSerializerFactory.create(moshi),
) {
    companion object {
        internal fun factory(
            beagleSdk: BeagleSdk,
            moshi: Moshi = BeagleMoshi.moshiFactory(beagleSdk),
            application: Application = BeagleEnvironment.application,
        ) =
            BeagleConfigurator(beagleSdk = beagleSdk,
                moshi = moshi, application = application)
    }
}

internal fun BeagleConfigurator.getViewClient() =
    this.beagleSdk.viewClient ?: BeagleEnvironment.beagleSdk.viewClient ?:
    this.beagleSdk.httpClientFactory?.let {
        ViewClientDefault(it.create())
    }

internal fun BeagleConfigurator.getSerializer(): BeagleJsonSerializer = BeagleJsonSerializerFactory.create(moshi = this.moshi)
