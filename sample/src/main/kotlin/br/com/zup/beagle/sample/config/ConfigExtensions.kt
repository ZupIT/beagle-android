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

package br.com.zup.beagle.sample.config

import br.com.zup.beagle.android.setup.BeagleSdkWrapper
import br.com.zup.beagle.android.setup.asBeagleSdk

internal fun BeagleSdkWrapper.logInfo(message: String) {
    this.asBeagleSdk().logger?.info(message)
}

internal fun BeagleSdkWrapper.logWarning(message: String) {
    this.asBeagleSdk().logger?.warning(message)
}

internal fun BeagleSdkWrapper.logError(message: String, throwable: Throwable? = null) {
    if (throwable != null) {
        this.asBeagleSdk().logger?.error(message, throwable)
    } else {
        this.asBeagleSdk().logger?.error(message)
    }
}