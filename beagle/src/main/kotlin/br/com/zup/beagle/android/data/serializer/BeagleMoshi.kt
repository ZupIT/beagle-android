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

package br.com.zup.beagle.android.data.serializer

import android.annotation.SuppressLint
import androidx.annotation.VisibleForTesting
import br.com.zup.beagle.android.action.Action
import br.com.zup.beagle.android.data.serializer.adapter.AnalyticsActionConfigAdapterFactory
import br.com.zup.beagle.android.data.serializer.adapter.AndroidActionJsonAdapterFactory
import br.com.zup.beagle.android.data.serializer.adapter.BeagleKotlinJsonAdapterFactory
import br.com.zup.beagle.android.data.serializer.adapter.BindAdapterFactory
import br.com.zup.beagle.android.data.serializer.adapter.ComponentJsonAdapterFactory
import br.com.zup.beagle.android.data.serializer.adapter.ContextDataAdapterFactory
import br.com.zup.beagle.android.data.serializer.adapter.HttpAdditionalDataAdapter
import br.com.zup.beagle.android.data.serializer.adapter.ImagePathTypeJsonAdapterFactory
import br.com.zup.beagle.android.data.serializer.adapter.RouteAdapterFactory
import br.com.zup.beagle.android.data.serializer.adapter.SimpleJsonAdapter
import br.com.zup.beagle.android.data.serializer.adapter.SimpleJsonArrayAdapter
import br.com.zup.beagle.android.data.serializer.adapter.defaults.CharSequenceAdapter
import br.com.zup.beagle.android.data.serializer.adapter.defaults.MoshiArrayListJsonAdapter
import br.com.zup.beagle.android.data.serializer.adapter.defaults.MoshiMapJsonAdapter
import br.com.zup.beagle.android.data.serializer.adapter.defaults.PairAdapterFactory
import br.com.zup.beagle.android.data.serializer.adapter.generic.BeagleGenericAdapterFactory
import br.com.zup.beagle.android.data.serializer.adapter.generic.TypeAdapterResolver
import br.com.zup.beagle.android.setup.BeagleEnvironment
import br.com.zup.beagle.android.setup.BeagleSdkWrapper
import br.com.zup.beagle.android.setup.asBeagleSdk
import br.com.zup.beagle.android.widget.WidgetView
import br.com.zup.beagle.android.widget.core.ServerDrivenComponent
import com.squareup.moshi.Moshi

internal object BeagleMoshi {

    val moshi: Moshi by lazy {
        createMoshi(registeredWidgets = BeagleEnvironment.beagleSdk.registeredWidgets(),
            registeredActions = BeagleEnvironment.beagleSdk.registeredActions(),
        typeAdapterResolver = BeagleEnvironment.beagleSdk.typeAdapterResolver)
    }

    @SuppressLint("CheckResult")
    fun moshiFactory(config: BeagleSdkWrapper): Moshi {
        val moshi = createMoshi(config.typeAdapterResolver?.create(
            config
        ),
        registeredWidgets = config.asBeagleSdk().registeredWidgets(),
        registeredActions = config.asBeagleSdk().registeredActions())
        moshi.adapter(ServerDrivenComponent::class.java)
        return moshi
    }

    @VisibleForTesting
    fun createMoshi(
        typeAdapterResolver: TypeAdapterResolver? = null,
        registeredWidgets: List<Class<WidgetView>> = emptyList(),
        registeredActions: List<Class<Action>> = emptyList(),
    ): Moshi = Moshi.Builder()
        .add(BindAdapterFactory())
        .add(ImagePathTypeJsonAdapterFactory.make())
        .add(ComponentJsonAdapterFactory.make(registeredWidgets))
        .add(HttpAdditionalDataAdapter())
        .add(RouteAdapterFactory())
        .add(AnalyticsActionConfigAdapterFactory())
        .add(AndroidActionJsonAdapterFactory.make(registeredActions))
        .add(ContextDataAdapterFactory())
        .add(MoshiArrayListJsonAdapter.FACTORY)
        .add(MoshiMapJsonAdapter.FACTORY)
        .add(CharSequenceAdapter())
        .add(PairAdapterFactory)
        .apply {
            typeAdapterResolver?.let {
                add(BeagleGenericAdapterFactory(it))
            }
        }
        .add(BeagleKotlinJsonAdapterFactory())
        .add(SimpleJsonAdapter())
        .add(SimpleJsonArrayAdapter())
        .build()
}
