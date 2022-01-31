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

package br.com.zup.beagle.android.engine.mapper

import android.view.View
import br.com.zup.beagle.android.components.utils.EdgeValueHelper
import br.com.zup.beagle.android.components.utils.UnitValueConstant
import br.com.zup.beagle.android.utils.dp
import br.com.zup.beagle.android.utils.internalObserveBindChanges
import br.com.zup.beagle.android.widget.RootView
import br.com.zup.beagle.android.widget.core.EdgeValue
import br.com.zup.beagle.android.widget.core.Size
import br.com.zup.beagle.android.widget.core.Style
import br.com.zup.beagle.android.widget.core.UnitType
import br.com.zup.beagle.android.widget.core.UnitValue
import com.facebook.yoga.YogaAlign
import com.facebook.yoga.YogaDisplay
import com.facebook.yoga.YogaEdge
import com.facebook.yoga.YogaFlexDirection
import com.facebook.yoga.YogaJustify
import com.facebook.yoga.YogaNode
import com.facebook.yoga.YogaNodeFactory
import com.facebook.yoga.YogaPositionType
import com.facebook.yoga.YogaWrap

internal class FlexMapper {

    fun makeYogaNode(style: Style): YogaNode = YogaNodeFactory.create().apply {
        flexDirection = makeYogaFlexDirection(style.flex?.flexDirection) ?: YogaFlexDirection.COLUMN
        wrap = makeYogaWrap(style.flex?.flexWrap) ?: YogaWrap.NO_WRAP
        justifyContent = makeYogaJustify(style.flex?.justifyContent) ?: YogaJustify.FLEX_START
        alignItems = makeYogaAlignItems(style.flex?.alignItems) ?: YogaAlign.STRETCH
        alignSelf = makeYogaAlignSelf(style.flex?.alignSelf) ?: YogaAlign.AUTO
        alignContent = makeYogaAlignContent(style.flex?.alignContent) ?: YogaAlign.FLEX_START
        if (style.flex?.flex == null) {
            flexGrow = style.flex?.grow?.toFloat() ?: 0.0f
            flexShrink = style.flex?.shrink?.toFloat() ?: 1.0f
        }
        style.flex?.flex?.toFloat()?.let { flex = it }

        display = YogaDisplay.FLEX
        positionType = makeYogaPositionType(style.positionType) ?: YogaPositionType.RELATIVE
        setAspectRatio(style.size?.aspectRatio, this)
    }

    fun observeBindChangesFlex(
        style: Style,
        rootView: RootView,
        view: View,
        yogaNode: YogaNode,
    ) {

        if (style.display != null) {
            internalObserveBindChanges(rootView, view, style.display) {
                if (it != null) {
                    yogaNode.display = makeYogaDisplay(it) ?: YogaDisplay.FLEX
                    view.requestLayout()
                }
            }
        }

        observeSize(style.size, rootView, view, yogaNode)

        observeUnitValue(rootView, view, style.flex?.basis) { value: Double, unitType: UnitType ->
            applyNodeBasis(value, unitType, yogaNode)
        }

        observeEdgeValue(rootView, view, style.margin) { yogaEdge: EdgeValueHelper ->
            applyNodeMargin(yogaEdge, yogaNode)
            view.requestLayout()
        }

        observeEdgeValue(rootView, view, style.padding) { yogaEdge: EdgeValueHelper ->
            applyNodePadding(yogaEdge, yogaNode)
            view.requestLayout()
        }

        observeEdgeValue(rootView, view, style.position) { yogaEdge: EdgeValueHelper ->
            applyNodePosition(yogaEdge, yogaNode)
            view.requestLayout()
        }
    }

    private fun observeSize(
        size: Size?,
        rootView: RootView,
        view: View,
        yogaNode: YogaNode
    ) {
        size?.let { size ->
            observeUnitValue(rootView, view, size.width) { value: Double, unitType: UnitType ->
                applyNodeWidth(value, unitType, yogaNode)
            }

            observeUnitValue(rootView, view, size.height) { value: Double, unitType: UnitType ->
                applyNodeHeight(value, unitType, yogaNode)
            }

            observeUnitValue(rootView, view, size.maxWidth) { value: Double, unitType: UnitType ->
                applyNodeMaxWidth(value, unitType, yogaNode)
            }

            observeUnitValue(rootView, view, size.maxHeight) { value: Double, unitType: UnitType ->
                applyNodeMaxHeight(value, unitType, yogaNode)
            }

            observeUnitValue(rootView, view, size.minWidth) { value: Double, unitType: UnitType ->
                applyNodeMinWidth(value, unitType, yogaNode)
            }

            observeUnitValue(rootView, view, size.minHeight) { value: Double, unitType: UnitType ->
                applyNodeMinHeight(value, unitType, yogaNode)
            }
        }
    }

    private fun observeUnitValue(
        rootView: RootView,
        view: View,
        unitValue: UnitValue?,
        finish: (value: Double, unitType: UnitType) -> Unit
    ) {
        if (unitValue != null) {
            internalObserveBindChanges(rootView, view, unitValue.value) {
                if (it != null) {
                    finish(it, unitValue.type)
                    view.requestLayout()
                }
            }
        }
    }

    private fun observeEdgeValue(
        rootView: RootView,
        view: View,
        edgeValue: EdgeValue?,
        finish: (yogaEdge: EdgeValueHelper) -> Unit
    ) {
        if (edgeValue != null) {
            val edgeValueHelper = EdgeValueHelper()
            if (edgeValue.top != null) {
                internalObserveBindChanges(rootView, view, edgeValue.top.value) {
                    edgeValueHelper.top = UnitValueConstant(type = edgeValue.top.type, value = it)
                    finish(edgeValueHelper)
                }
            }

            if (edgeValue.all != null) {
                internalObserveBindChanges(rootView, view, edgeValue.all.value) {
                    edgeValueHelper.all = UnitValueConstant(type = edgeValue.all.type, value = it)
                    finish(edgeValueHelper)
                }
            }

            if (edgeValue.bottom != null) {
                internalObserveBindChanges(rootView, view, edgeValue.bottom.value) {
                    edgeValueHelper.bottom = UnitValueConstant(type = edgeValue.bottom.type, value = it)
                    finish(edgeValueHelper)
                }
            }

            if (edgeValue.horizontal != null) {
                internalObserveBindChanges(rootView, view, edgeValue.horizontal.value) {
                    edgeValueHelper.horizontal = UnitValueConstant(type = edgeValue.horizontal.type, value = it)
                    finish(edgeValueHelper)
                }
            }

            if (edgeValue.left != null) {
                internalObserveBindChanges(rootView, view, edgeValue.left.value) {
                    edgeValueHelper.left = UnitValueConstant(type = edgeValue.left.type, value = it)
                    finish(edgeValueHelper)
                }
            }

            if (edgeValue.right != null) {
                internalObserveBindChanges(rootView, view, edgeValue.right.value) {
                    edgeValueHelper.right = UnitValueConstant(type = edgeValue.right.type, value = it)
                    finish(edgeValueHelper)
                }
            }

            if (edgeValue.vertical != null) {
                internalObserveBindChanges(rootView, view, edgeValue.vertical.value) {
                    edgeValueHelper.vertical = UnitValueConstant(type = edgeValue.vertical.type, value = it)
                    finish(edgeValueHelper)
                }
            }
        }
    }

    private fun applyNodeWidth(
        value: Double?,
        type: UnitType,
        yogaNode: YogaNode
    ) {
        value?.let { v ->
            if (type == UnitType.REAL) {
                yogaNode.setWidth(v.dp().toFloat())
            } else if (type == UnitType.PERCENT) {
                yogaNode.setWidthPercent(v.toFloat())
            }
        }
    }

    private fun applyNodeHeight(
        value: Double?,
        type: UnitType,
        yogaNode: YogaNode
    ) {
        value?.let { v ->
            if (type == UnitType.REAL) {
                yogaNode.setHeight(v.dp().toFloat())
            } else if (type == UnitType.PERCENT) {
                yogaNode.setHeightPercent(v.toFloat())
            }
        }
    }

    private fun applyNodeMaxHeight(
        value: Double?,
        type: UnitType,
        yogaNode: YogaNode
    ) {
        value?.let { v ->
            if (type == UnitType.REAL) {
                yogaNode.setMaxHeight(v.dp().toFloat())
            } else if (type == UnitType.PERCENT) {
                yogaNode.setMaxHeightPercent(v.toFloat())
            }
        }
    }

    private fun applyNodeMaxWidth(
        value: Double?,
        type: UnitType,
        yogaNode: YogaNode
    ) {
        value?.let { v ->
            if (type == UnitType.REAL) {
                yogaNode.setMaxWidth(v.dp().toFloat())
            } else if (type == UnitType.PERCENT) {
                yogaNode.setMaxWidthPercent(v.toFloat())
            }
        }
    }

    private fun applyNodeMinWidth(
        value: Double?,
        type: UnitType,
        yogaNode: YogaNode
    ) {
        value?.let { v ->
            if (type == UnitType.REAL) {
                yogaNode.setMinWidth(v.dp().toFloat())
            } else if (type == UnitType.PERCENT) {
                yogaNode.setMinWidthPercent(v.toFloat())
            }
        }
    }

    private fun applyNodeMinHeight(
        value: Double?,
        type: UnitType,
        yogaNode: YogaNode
    ) {
        value?.let { v ->
            if (type == UnitType.REAL) {
                yogaNode.setMinHeight(v.dp().toFloat())
            } else if (type == UnitType.PERCENT) {
                yogaNode.setMinHeightPercent(v.toFloat())
            }
        }
    }

    private fun applyNodeMargin(
        edgeValue: EdgeValueHelper,
        yogaNode: YogaNode
    ) {
        applyEdgeValue(edgeValue) { yogaEdge, unitValue ->
            unitValue.value?.let { v ->
                if (unitValue.type == UnitType.REAL) {
                    yogaNode.setMargin(yogaEdge, v.dp().toFloat())
                } else if (unitValue.type == UnitType.PERCENT) {
                    yogaNode.setMarginPercent(yogaEdge, v.toFloat())
                }
            }
        }
    }

    private fun applyNodePadding(
        edgeValue: EdgeValueHelper,
        yogaNode: YogaNode
    ) {
        applyEdgeValue(edgeValue) { yogaEdge, unitValue ->
            unitValue.value?.let { v ->
                if (unitValue.type == UnitType.REAL) {
                    yogaNode.setPadding(yogaEdge, v.dp().toFloat())
                } else if (unitValue.type == UnitType.PERCENT) {
                    yogaNode.setPaddingPercent(yogaEdge, v.toFloat())
                }
            }
        }
    }

    private fun applyNodePosition(
        edgeValue: EdgeValueHelper,
        yogaNode: YogaNode
    ) {
        applyEdgeValue(edgeValue) { yogaEdge, unitValue ->
            unitValue.value?.let { v ->
                if (unitValue.type == UnitType.REAL) {
                    yogaNode.setPosition(yogaEdge, v.dp().toFloat())
                } else if (unitValue.type == UnitType.PERCENT) {
                    yogaNode.setPositionPercent(yogaEdge, v.toFloat())
                }
            }
        }
    }

    private fun applyNodeBasis(
        value: Double?,
        type: UnitType,
        yogaNode: YogaNode
    ) {
        value?.let { v ->
            when (type) {
                UnitType.REAL -> {
                    yogaNode.setFlexBasis(v.toFloat())
                }
                UnitType.PERCENT -> {
                    yogaNode.setFlexBasisPercent(v.toFloat())
                }
                else -> {
                    yogaNode.setFlexBasisAuto()
                }
            }
        }
    }

    private fun setAspectRatio(aspectRatio: Double?, yogaNode: YogaNode) {
        aspectRatio?.let {
            yogaNode.aspectRatio = aspectRatio.dp().toFloat()
        }
    }

    private fun applyEdgeValue(
        edgeValue: EdgeValueHelper?,
        finish: (yogaEdge: YogaEdge, unitValue: UnitValueConstant) -> Unit,
    ) {
        edgeValue?.top?.let {
            finish(YogaEdge.TOP, it)
        }
        edgeValue?.left?.let {
            finish(YogaEdge.LEFT, it)
        }
        edgeValue?.right?.let {
            finish(YogaEdge.RIGHT, it)
        }
        edgeValue?.bottom?.let {
            finish(YogaEdge.BOTTOM, it)
        }
        edgeValue?.vertical?.let {
            finish(YogaEdge.VERTICAL, it)
        }
        edgeValue?.horizontal?.let {
            finish(YogaEdge.HORIZONTAL, it)
        }
        edgeValue?.all?.let {
            finish(YogaEdge.ALL, it)
        }
    }
}
