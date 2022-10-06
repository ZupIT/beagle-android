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

import android.content.Context
import android.util.AttributeSet
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AbstractComposeView

class MyComposeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AbstractComposeView(context, attrs, defStyleAttr) {

    // Create a State for the title text so any changes can be observed and reflected automatically
    // in our Composable Text.
    private val titleText = mutableStateOf("")

    // Public getter/setter for our title that delegates to the State value.
    var title: String
        get() = titleText.value
        set(value) {
            titleText.value = value
        }

    @Composable
    override fun Content() {
        Text(
            // The Text now uses the value from the state so whenever it gets set the UI will
            // automatically reflect the changes.
            text = titleText.value,
            modifier = Modifier.fillMaxSize()
        )
    }
}