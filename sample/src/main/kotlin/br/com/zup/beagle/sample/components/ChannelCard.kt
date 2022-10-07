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

package br.com.zup.beagle.sample.components

import android.content.Context
import android.util.AttributeSet
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.AbstractComposeView

// Simple data class for our TV Channel.
data class Channel(val logo: String)

@Composable
fun ChannelCard(channel: Channel) {
    val painter =
        rememberAsyncImagePainter(model = channel.logo)

    Card(
        shape = RoundedCornerShape(8.dp),
        backgroundColor = Color.DarkGray,
        modifier = Modifier
            .height(150.dp)
            .width(150.dp)
            .padding(16.dp)
    ) {
        Image(

            painter = painter,
            contentDescription = null,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun ChannelCardRow(channels: List<Channel>) {
    // Create a scrollable lazy row (horizontal list) for each channel.
    LazyRow {
        items(channels) {
            // For each channel we add a new ChannelCard to our horizontal list.
            ChannelCard(channel = it)
        }
    }
}

class ChannelCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AbstractComposeView(context, attrs, defStyleAttr) {

    var channel: String?
        get() = channelsState.value
        set(value) {
            channelsState.value = value
        }

    private val channelsState = mutableStateOf<String?>(null)

    @Composable
    override fun Content() {
        channelsState.value?.let {
            ChannelCard(channel = Channel(it))
        }
    }
}

class ChannelCardRowView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AbstractComposeView(context, attrs, defStyleAttr) {

    // Our list of Channels to get and set to update the UI.
    var channels: List<Channel>
        get() = channelsState.value
        set(value) {
            channelsState.value = value
        }

    // State that will allow the UI to update when being set.
    private val channelsState = mutableStateOf<List<Channel>>(emptyList())

    @Composable
    override fun Content() {
        // Our content now uses our ChannelCardRow composable to display the list of channels.
        ChannelCardRow(channels = channelsState.value)
    }
}