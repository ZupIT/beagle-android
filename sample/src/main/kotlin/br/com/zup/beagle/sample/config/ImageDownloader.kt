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

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.DisplayMetrics
import android.util.LruCache
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import br.com.zup.beagle.android.imagedownloader.BeagleImageDownloader
import br.com.zup.beagle.android.widget.RootView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

val imageDownloaderObject = object : BeagleImageDownloader {
    private val imageDownloader: ImageDownloader = ImageDownloader()
    override fun download(url: String, imageView: ImageView, rootView: RootView) {
        imageView.post {
            rootView.getLifecycleOwner().lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val formattedUrl = rootView.getBeagleConfigurator().baseUrl + "/" + url
                        .takeIf { it.isNotEmpty() } ?: url
                    val bitmap = imageDownloader.getRemoteImage(
                        formattedUrl,
                        imageView.width,
                        imageView.height,
                        rootView.getContext(),
                    )
                    setImage(imageView, bitmap)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private suspend fun setImage(view: ImageView, bitmap: Bitmap?) {
        withContext(Dispatchers.Main) {
            view.setImageBitmap(bitmap)
        }
    }
}

internal class ImageDownloader {

    suspend fun getRemoteImage(
        url: String,
        contentWidth: Int,
        contentHeight: Int,
        context: Context,
    ): Bitmap? {
        val cacheId = LruImageCache.generateBitmapId(url, contentWidth, contentHeight)

        return withContext(Dispatchers.IO) {
            val bitmapCached = LruImageCache.get(cacheId)

            bitmapCached ?: downloadBitmap(url, context)?.apply {
                LruImageCache.put(cacheId, this)
            }
        }
    }

    private fun downloadBitmap(url: String, context: Context): Bitmap? {
        val inputStream = URL(url).openStream()
        val options = BitmapFactory.Options().apply {
            inDensity = DisplayMetrics.DENSITY_DEFAULT
            inScreenDensity = context.resources.displayMetrics.densityDpi
            inTargetDensity = context.resources.displayMetrics.densityDpi
        }
        return BitmapFactory.decodeStream(inputStream, null, options)
    }
}

internal object LruImageCache {

    private val cache: LruCache<String, Bitmap> by lazy {
        LruCache<String, Bitmap>(anEighthOfMemory())
    }

    fun put(key: String, bitmap: Bitmap?) {
        if (bitmap != null) {
            cache.put(key, bitmap)
        }
    }

    fun get(key: String?): Bitmap? = cache.get(key)

    private fun anEighthOfMemory() = ((Runtime.getRuntime().maxMemory() / 1024).toInt() / 8)

    fun generateBitmapId(
        url: String?,
        contentWidth: Int,
        contentHeight: Int
    ) = StringBuilder().append(url).append(contentWidth).append(contentHeight).toString()
}