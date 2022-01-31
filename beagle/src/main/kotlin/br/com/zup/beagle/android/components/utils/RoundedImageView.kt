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

package br.com.zup.beagle.android.components.utils

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import androidx.appcompat.widget.AppCompatImageView
import br.com.zup.beagle.android.utils.FLOAT_ZERO
import br.com.zup.beagle.android.utils.internalObserveBindChanges
import br.com.zup.beagle.android.widget.RootView
import br.com.zup.beagle.android.widget.core.CornerRadius

@SuppressLint("ViewConstructor")
internal class RoundedImageView(
    private val rootView: RootView,
    cornerRadius: CornerRadius,
) : AppCompatImageView(rootView.getContext()) {

    private val cornerRadiusAux = CornerRadiusHelper()
    private val path = Path()
    private val rect = RectF()
    init {
        cornerRadius.radius?.let { r ->
            internalObserveBindChanges(rootView, this, r) { radius ->
                cornerRadiusAux.radius = radius
                requestLayout()
            }
        }

        cornerRadius.topLeft?.let { t ->
            internalObserveBindChanges(rootView, this, t) { topLeft ->
                cornerRadiusAux.topLeft = topLeft
                requestLayout()
            }
        }

        cornerRadius.topRight?.let { r ->
            internalObserveBindChanges(rootView, this, r) { topRight ->
                cornerRadiusAux.topRight = topRight
                requestLayout()
            }
        }

        cornerRadius.bottomLeft?.let { l ->
            internalObserveBindChanges(rootView, this, l) { bottomLeft ->
                cornerRadiusAux.bottomLeft = bottomLeft
                requestLayout()
            }
        }

        cornerRadius.bottomRight?.let { r ->
            internalObserveBindChanges(rootView, this, r) { bottomRight ->
                cornerRadiusAux.bottomRight = bottomRight
                requestLayout()
            }
        }

    }

    override fun onDraw(canvas: Canvas?) {
        rect.set(FLOAT_ZERO, FLOAT_ZERO, width.toFloat(), height.toFloat())
        path.reset()
        path.addRoundRect(rect, cornerRadiusAux.getFloatArray(), Path.Direction.CW)
        canvas?.clipPath(path)
        super.onDraw(canvas)
    }
}
