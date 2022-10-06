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

package br.com.zup.beagle.compose.sample

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GuideViewModel : ViewModel() {
    val channels: MutableLiveData<List<Channel>> = MutableLiveData()
    init {
        channels.postValue(
            listOf(
                Channel("https://upload.wikimedia.org/wikipedia/commons/2/27/Channel_A_Logo.jpg"),
                Channel("https://upload.wikimedia.org/wikipedia/commons/2/27/Channel_A_Logo.jpg"),
                Channel("https://upload.wikimedia.org/wikipedia/commons/2/27/Channel_A_Logo.jpg"),
                Channel("https://upload.wikimedia.org/wikipedia/commons/2/27/Channel_A_Logo.jpg"),
                Channel("https://upload.wikimedia.org/wikipedia/commons/2/27/Channel_A_Logo.jpg")
            )

        )
    }
}