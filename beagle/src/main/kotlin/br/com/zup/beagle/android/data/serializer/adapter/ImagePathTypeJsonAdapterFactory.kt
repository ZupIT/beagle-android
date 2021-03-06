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

import br.com.zup.beagle.android.components.ImagePath
import br.com.zup.beagle.android.data.serializer.PolymorphicJsonAdapterFactory

private const val BEAGLE_IMAGE_TYPE = "_beagleImagePath_"

internal object ImagePathTypeJsonAdapterFactory {
    fun make(): PolymorphicJsonAdapterFactory<ImagePath> =
        PolymorphicJsonAdapterFactory.of(ImagePath::class.java, BEAGLE_IMAGE_TYPE)
            .withSubtype(ImagePath.Local::class.java, "local")
            .withSubtype(ImagePath.Remote::class.java, "remote")

}
