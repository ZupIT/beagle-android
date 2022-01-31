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
import br.com.zup.beagle.android.context.constant
import br.com.zup.beagle.android.utils.Observer
import br.com.zup.beagle.android.utils.dp
import br.com.zup.beagle.android.utils.internalObserveBindChanges
import br.com.zup.beagle.android.widget.RootView
import br.com.zup.beagle.android.widget.core.AlignContent
import br.com.zup.beagle.android.widget.core.AlignItems
import br.com.zup.beagle.android.widget.core.AlignSelf
import br.com.zup.beagle.android.widget.core.Display
import br.com.zup.beagle.android.widget.core.EdgeValue
import br.com.zup.beagle.android.widget.core.Flex
import br.com.zup.beagle.android.widget.core.FlexDirection
import br.com.zup.beagle.android.widget.core.FlexWrap
import br.com.zup.beagle.android.widget.core.JustifyContent
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
import com.facebook.yoga.YogaWrap
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import io.mockk.verifyOrder
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

private const val HUNDRED_UNIT_VALUE = 100.0
private const val ONE_UNIT_VALUE = 1.0

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FlexMapperTest {

    private val yogaNodeMock: YogaNode = mockk(relaxed = true, relaxUnitFun = true)
    private val rootViewMock: RootView = mockk()
    private val viewMock: View = mockk(relaxUnitFun = true, relaxed = true)

    private val observeSlot = slot<Observer<Any?>>()

    private lateinit var flexMapper: FlexMapper

    @BeforeAll
    fun setUp() {
        MockKAnnotations.init(this)

        flexMapper = FlexMapper()

        mockkStatic(YogaNode::class)
        mockkStatic(YogaNodeFactory::class)
        mockkStatic("br.com.zup.beagle.android.utils.NumberExtensionsKt")
        mockkStatic("br.com.zup.beagle.android.utils.WidgetExtensionsKt")

        every {
            internalObserveBindChanges(
                rootView = rootViewMock,
                view = viewMock,
                bind = any(),
                observes = capture(observeSlot)
            )
        } just Runs

        every { HUNDRED_UNIT_VALUE.dp() } returns HUNDRED_UNIT_VALUE
        every { ONE_UNIT_VALUE.dp() } returns ONE_UNIT_VALUE
        every { YogaNodeFactory.create() } returns yogaNodeMock
    }

    @BeforeEach
    fun clear() {
        clearMocks(
            yogaNodeMock,
            rootViewMock,
            viewMock,
            answers = false
        )
        observeSlot.clear()
    }

    @AfterAll
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun makeYogaNode_should_set_flexDirection_as_COLUMN() {
        // Given
        val flex = Flex(
            flexDirection = FlexDirection.COLUMN
        )

        // When
        val yogaNode = flexMapper.makeYogaNode(Style(flex = flex))

        // Then
        verify(exactly = 1) { yogaNode.flexDirection = YogaFlexDirection.COLUMN }
    }

    @Test
    fun makeYogaNode_should_set_wrap_as_WRAP() {
        // Given
        val flex = Flex(
            flexWrap = FlexWrap.WRAP
        )

        // When
        val yogaNode = flexMapper.makeYogaNode(Style(flex = flex))

        // Then
        verify(exactly = 1) { yogaNode.wrap = YogaWrap.WRAP }
    }

    @Test
    fun makeYogaNode_should_set_justifyContent_as_SPACE_BETWEEN() {
        // Given
        val flex = Flex(
            justifyContent = JustifyContent.SPACE_BETWEEN
        )

        // When
        val yogaNode = flexMapper.makeYogaNode(Style(flex = flex))

        // Then
        verify(exactly = 1) { yogaNode.justifyContent = YogaJustify.SPACE_BETWEEN }
    }

    @Test
    fun makeYogaNode_should_set_alignItems_as_FLEX_START() {
        // Given
        val flex = Flex(
            alignItems = AlignItems.FLEX_START
        )

        // When
        val yogaNode = flexMapper.makeYogaNode(Style(flex = flex))

        // Then
        verify(exactly = 1) { yogaNode.alignItems = YogaAlign.FLEX_START }
    }

    @Test
    fun makeYogaNode_should_set_alignSelf_as_FLEX_START() {
        // Given
        val flex = Flex(
            alignSelf = AlignSelf.FLEX_START
        )

        // When
        val yogaNode = flexMapper.makeYogaNode(Style(flex = flex))

        // Then
        verify(exactly = 1) { yogaNode.alignSelf = YogaAlign.FLEX_START }
    }

    @Test
    fun makeYogaNode_should_set_alignContent_as_FLEX_START() {
        // Given
        val flex = Flex(
            alignContent = AlignContent.FLEX_START
        )

        // When
        val yogaNode = flexMapper.makeYogaNode(Style(flex = flex))

        // Then
        verify(exactly = 1) { yogaNode.alignContent = YogaAlign.FLEX_START }
    }

    @Test
    fun makeYogaNode_should_set_flex_as_1() {
        //Given
        val flex = Flex(
            flex = ONE_UNIT_VALUE
        )

        //When
        val yogaNode = flexMapper.makeYogaNode(Style(flex = flex))

        //Then
        verify(exactly = 1) { yogaNode.flex = ONE_UNIT_VALUE.toFloat() }
    }

    @Test
    fun makeYogaNode_should_set_flexGrow_as_SPACE_BETWEEN() {
        // Given
        val flex = Flex(
            grow = ONE_UNIT_VALUE
        )

        // When
        val yogaNode = flexMapper.makeYogaNode(Style(flex = flex))

        // Then
        verify(exactly = 1) { yogaNode.flexGrow = ONE_UNIT_VALUE.toFloat() }
    }

    @Test
    fun makeYogaNode_should_set_shrink_as_1() {
        // Given
        val flex = Flex(
            shrink = ONE_UNIT_VALUE
        )

        // When
        val yogaNode = flexMapper.makeYogaNode(Style(flex = flex))

        // Then
        verify(exactly = 1) { yogaNode.flexShrink = ONE_UNIT_VALUE.toFloat() }
    }

    @Test
    fun `GIVEN display NONE WHEN call observe bind changes THEN it should set in yoga node correct display`() {
        // Given
        val style = Style(
            display = constant(Display.NONE)
        )

        // When
        flexMapper.observeBindChangesFlex(style, rootViewMock, viewMock, yogaNodeMock)
        observeSlot.captured.invoke(Display.NONE)

        // Then
        verifyOrder {
            yogaNodeMock.display = YogaDisplay.NONE
            viewMock.requestLayout()
        }
    }

    @Test
    fun `GIVEN display FLEX WHEN call observe bind changes THEN it should set in yoga node correct display`() {
        // Given
        val style = Style(
            display = constant(Display.FLEX)
        )

        // When
        flexMapper.observeBindChangesFlex(style, rootViewMock, viewMock, yogaNodeMock)
        observeSlot.captured.invoke(Display.FLEX)

        // Then
        verifyOrder {
            yogaNodeMock.display = YogaDisplay.FLEX
            viewMock.requestLayout()
        }
    }

    @Test
    fun `GIVEN display NULL WHEN call observe bind changes THEN it should never call bind changes`() {
        // Given
        val style = Style(
            display = null
        )

        // When
        flexMapper.observeBindChangesFlex(style, rootViewMock, viewMock, yogaNodeMock)

        // Then
        assertFalse(observeSlot.isCaptured)
    }

    @Test
    fun makeYogaNode_should_set_width_as_100_0() {
        // Given
        val style = Style(
            size = Size(width = UnitValue(constant(HUNDRED_UNIT_VALUE), UnitType.REAL))
        )

        // When
        flexMapper.observeBindChangesFlex(style, rootViewMock, viewMock, yogaNodeMock)
        observeSlot.captured.invoke(HUNDRED_UNIT_VALUE)

        // Then
        verify(exactly = 1) { yogaNodeMock.setWidth(HUNDRED_UNIT_VALUE.toFloat()) }
    }

    @Test
    fun makeYogaNode_should_set_widthPercent_as_100_0() {
        // Given
        val style = Style(
            size = Size(width = UnitValue(constant(HUNDRED_UNIT_VALUE), UnitType.PERCENT))
        )

        // When
        flexMapper.observeBindChangesFlex(style, rootViewMock, viewMock, yogaNodeMock)
        observeSlot.captured.invoke(HUNDRED_UNIT_VALUE)

        // Then
        verify(exactly = 1) { yogaNodeMock.setWidthPercent(HUNDRED_UNIT_VALUE.toFloat()) }
    }

    @Test
    fun makeYogaNode_should_set_height_as_100_0() {
        // Given
        val style = Style(
            size = Size(height = UnitValue(constant(HUNDRED_UNIT_VALUE), UnitType.REAL))
        )

        // When
        flexMapper.observeBindChangesFlex(style, rootViewMock, viewMock, yogaNodeMock)
        observeSlot.captured.invoke(HUNDRED_UNIT_VALUE)


        // Then
        verify(exactly = 1) { yogaNodeMock.setHeight(HUNDRED_UNIT_VALUE.toFloat()) }
    }

    @Test
    fun makeYogaNode_should_set_heightPercent_as_100_0() {
        // Given
        val style = Style(
            size = Size(height = UnitValue(constant(HUNDRED_UNIT_VALUE), UnitType.PERCENT))
        )

        // When
        flexMapper.observeBindChangesFlex(style, rootViewMock, viewMock, yogaNodeMock)
        observeSlot.captured.invoke(HUNDRED_UNIT_VALUE)


        // Then
        verify(exactly = 1) { yogaNodeMock.setHeightPercent(HUNDRED_UNIT_VALUE.toFloat()) }
    }

    @Test
    fun makeYogaNode_should_set_maxWidth_as_100_0() {
        // Given
        val style = Style(
            size = Size(maxWidth = UnitValue(constant(HUNDRED_UNIT_VALUE), UnitType.REAL))
        )

        // When
        flexMapper.observeBindChangesFlex(style, rootViewMock, viewMock, yogaNodeMock)
        observeSlot.captured.invoke(HUNDRED_UNIT_VALUE)


        // Then
        verify(exactly = 1) { yogaNodeMock.setMaxWidth(HUNDRED_UNIT_VALUE.toFloat()) }
    }

    @Test
    fun makeYogaNode_should_set_maxWidthPercent_as_100_0() {
        // Given
        val style = Style(
            size = Size(maxWidth = UnitValue(constant(HUNDRED_UNIT_VALUE), UnitType.PERCENT))
        )

        // When
        flexMapper.observeBindChangesFlex(style, rootViewMock, viewMock, yogaNodeMock)
        observeSlot.captured.invoke(HUNDRED_UNIT_VALUE)


        // Then
        verify(exactly = 1) { yogaNodeMock.setMaxWidthPercent(HUNDRED_UNIT_VALUE.toFloat()) }
    }

    @Test
    fun makeYogaNode_should_set_maxHeight_as_100_0() {
        // Given
        val style = Style(
            size = Size(maxHeight = UnitValue(constant(HUNDRED_UNIT_VALUE), UnitType.REAL))
        )

        // When
        flexMapper.observeBindChangesFlex(style, rootViewMock, viewMock, yogaNodeMock)
        observeSlot.captured.invoke(HUNDRED_UNIT_VALUE)


        // Then
        verify(exactly = 1) { yogaNodeMock.setMaxHeight(HUNDRED_UNIT_VALUE.toFloat()) }
    }

    @Test
    fun makeYogaNode_should_set_maxHeightPercent_as_100_0() {
        // Given
        val style = Style(
            size = Size(maxHeight = UnitValue(constant(HUNDRED_UNIT_VALUE), UnitType.PERCENT))
        )

        // When
        flexMapper.observeBindChangesFlex(style, rootViewMock, viewMock, yogaNodeMock)
        observeSlot.captured.invoke(HUNDRED_UNIT_VALUE)

        // Then
        verify(exactly = 1) { yogaNodeMock.setMaxHeightPercent(HUNDRED_UNIT_VALUE.toFloat()) }
    }

    @Test
    fun makeYogaNode_should_set_minWidth_as_100_0() {
        // Given
        val style = Style(
            size = Size(minWidth = UnitValue(constant(HUNDRED_UNIT_VALUE), UnitType.REAL))
        )

        // When
        flexMapper.observeBindChangesFlex(style, rootViewMock, viewMock, yogaNodeMock)
        observeSlot.captured.invoke(HUNDRED_UNIT_VALUE)

        // Then
        verify(exactly = 1) { yogaNodeMock.setMinWidth(HUNDRED_UNIT_VALUE.toFloat()) }
    }

    @Test
    fun makeYogaNode_should_set_minWidthPercent_as_100_0() {
        // Given
        val style = Style(
            size = Size(minWidth = UnitValue(constant(HUNDRED_UNIT_VALUE), UnitType.PERCENT))
        )

        // When
        flexMapper.observeBindChangesFlex(style, rootViewMock, viewMock, yogaNodeMock)
        observeSlot.captured.invoke(HUNDRED_UNIT_VALUE)

        // Then
        verify(exactly = 1) { yogaNodeMock.setMinWidthPercent(HUNDRED_UNIT_VALUE.toFloat()) }
    }

    @Test
    fun makeYogaNode_should_set_minHeight_as_100_0() {
        // Given
        val style = Style(
            size = Size(minHeight = UnitValue(constant(HUNDRED_UNIT_VALUE), UnitType.REAL))
        )

        // When
        flexMapper.observeBindChangesFlex(style, rootViewMock, viewMock, yogaNodeMock)
        observeSlot.captured.invoke(HUNDRED_UNIT_VALUE)

        // Then
        verify(exactly = 1) { yogaNodeMock.setMinHeight(HUNDRED_UNIT_VALUE.toFloat()) }
    }

    @Test
    fun makeYogaNode_should_set_minHeightPercent_as_100_0() {
        // Given
        val style = Style(
            size = Size(minHeight = UnitValue(constant(HUNDRED_UNIT_VALUE), UnitType.PERCENT))
        )

        // When
        flexMapper.observeBindChangesFlex(style, rootViewMock, viewMock, yogaNodeMock)
        observeSlot.captured.invoke(HUNDRED_UNIT_VALUE)

        // Then
        verify(exactly = 1) { yogaNodeMock.setMinHeightPercent(HUNDRED_UNIT_VALUE.toFloat()) }
    }

    @Test
    fun makeYogaNode_should_set_basis_as_1() {
        // Given
        val flex = Flex(
            basis = UnitValue(constant(ONE_UNIT_VALUE), UnitType.REAL)
        )

        // When
        flexMapper.observeBindChangesFlex(Style(flex = flex), rootViewMock, viewMock, yogaNodeMock)
        observeSlot.captured.invoke(ONE_UNIT_VALUE)

        // Then
        verify(exactly = 1) { yogaNodeMock.setFlexBasis(ONE_UNIT_VALUE.toFloat()) }
    }

    @Test
    fun makeYogaNode_should_set_basisPercent_as_1() {
        // Given
        val flex = Flex(
            basis = UnitValue(constant(ONE_UNIT_VALUE), UnitType.PERCENT)
        )

        // When
        flexMapper.observeBindChangesFlex(Style(flex = flex), rootViewMock, viewMock, yogaNodeMock)
        observeSlot.captured.invoke(ONE_UNIT_VALUE)

        // Then
        verify(exactly = 1) { yogaNodeMock.setFlexBasisPercent(ONE_UNIT_VALUE.toFloat()) }
    }

    @Test
    fun makeYogaNode_should_set_basisAuto() {
        // Given
        val flex = Flex(
            basis = UnitValue(constant(ONE_UNIT_VALUE), UnitType.AUTO)
        )

        // When
        flexMapper.observeBindChangesFlex(Style(flex = flex), rootViewMock, viewMock, yogaNodeMock)
        observeSlot.captured.invoke(ONE_UNIT_VALUE)

        // Then
        verify(exactly = 1) { yogaNodeMock.setFlexBasisAuto() }
    }

    @Test
    fun makeYogaNode_should_set_aspectRatio_as_1() {
        // Given
        val style = Style(
            size = Size(aspectRatio = ONE_UNIT_VALUE)
        )

        // When
        val yogaNode = flexMapper.makeYogaNode(style)

        // Then
        verify(exactly = 1) { yogaNode.aspectRatio = ONE_UNIT_VALUE.toFloat() }
    }

    @Test
    fun makeYogaNode_should_set_margin_as_TOP_and_1() {
        // Given
        val style = Style(
            margin = EdgeValue(top = UnitValue(constant(ONE_UNIT_VALUE), UnitType.REAL))
        )

        // When
        flexMapper.observeBindChangesFlex(style, rootViewMock, viewMock, yogaNodeMock)
        observeSlot.captured.invoke(ONE_UNIT_VALUE)


        // Then
        verify(exactly = 1) { yogaNodeMock.setMargin(YogaEdge.TOP, ONE_UNIT_VALUE.toFloat()) }
    }

    @Test
    fun makeYogaNode_should_set_margin_as_LEFT_and_1() {
        // Given
        val style = Style(
            margin = EdgeValue(left = UnitValue(constant(ONE_UNIT_VALUE), UnitType.REAL))
        )

        // When
        flexMapper.observeBindChangesFlex(style, rootViewMock, viewMock, yogaNodeMock)
        observeSlot.captured.invoke(ONE_UNIT_VALUE)


        // Then
        verify(exactly = 1) { yogaNodeMock.setMargin(YogaEdge.LEFT, ONE_UNIT_VALUE.toFloat()) }
    }

    @Test
    fun makeYogaNode_should_set_margin_as_RIGHT_and_1() {
        // Given
        val style = Style(
            margin = EdgeValue(right = UnitValue(constant(ONE_UNIT_VALUE), UnitType.REAL))
        )

        // When
        flexMapper.observeBindChangesFlex(style, rootViewMock, viewMock, yogaNodeMock)
        observeSlot.captured.invoke(ONE_UNIT_VALUE)
        // Then
        verify(exactly = 1) { yogaNodeMock.setMargin(YogaEdge.RIGHT, ONE_UNIT_VALUE.toFloat()) }
    }

    @Test
    fun makeYogaNode_should_set_margin_as_BOTTOM_and_1() {
        // Given
        val style = Style(
            margin = EdgeValue(bottom = UnitValue(constant(ONE_UNIT_VALUE), UnitType.REAL))
        )

        // When
        flexMapper.observeBindChangesFlex(style, rootViewMock, viewMock, yogaNodeMock)
        observeSlot.captured.invoke(ONE_UNIT_VALUE)

        // Then
        verify(exactly = 1) { yogaNodeMock.setMargin(YogaEdge.BOTTOM, ONE_UNIT_VALUE.toFloat()) }
    }

    @Test
    fun makeYogaNode_should_set_margin_as_START_and_1() {
        // Given
        val style = Style(
            margin = EdgeValue(left = UnitValue(constant(ONE_UNIT_VALUE), UnitType.REAL))
        )

        // When
        flexMapper.observeBindChangesFlex(style, rootViewMock, viewMock, yogaNodeMock)
        observeSlot.captured.invoke(ONE_UNIT_VALUE)

        // Then
        verify(exactly = 1) { yogaNodeMock.setMargin(YogaEdge.LEFT, ONE_UNIT_VALUE.toFloat()) }
    }

    @Test
    fun makeYogaNode_should_set_margin_as_END_and_1() {
        // Given
        val style = Style(
            margin = EdgeValue(right = UnitValue(constant(ONE_UNIT_VALUE), UnitType.REAL))
        )

        // When
        flexMapper.observeBindChangesFlex(style, rootViewMock, viewMock, yogaNodeMock)
        observeSlot.captured.invoke(ONE_UNIT_VALUE)

        // Then
        verify(exactly = 1) { yogaNodeMock.setMargin(YogaEdge.RIGHT, ONE_UNIT_VALUE.toFloat()) }
    }

    @Test
    fun makeYogaNode_should_set_margin_as_ALL_and_1() {
        // Given
        val style = Style(
            margin = EdgeValue(all = UnitValue(constant(ONE_UNIT_VALUE), UnitType.REAL))
        )

        // When
        flexMapper.observeBindChangesFlex(style, rootViewMock, viewMock, yogaNodeMock)
        observeSlot.captured.invoke(ONE_UNIT_VALUE)


        // Then
        verify(exactly = 1) { yogaNodeMock.setMargin(YogaEdge.ALL, ONE_UNIT_VALUE.toFloat()) }
    }

    @Test
    fun makeYogaNode_should_set_margin_as_VERTICAL_and_1() {
        // Given
        val style = Style(
            margin = EdgeValue(vertical = UnitValue(constant(ONE_UNIT_VALUE), UnitType.REAL))
        )

        // When
        flexMapper.observeBindChangesFlex(style, rootViewMock, viewMock, yogaNodeMock)
        observeSlot.captured.invoke(ONE_UNIT_VALUE)

        // Then
        verify(exactly = 1) { yogaNodeMock.setMargin(YogaEdge.VERTICAL, ONE_UNIT_VALUE.toFloat()) }
    }

    @Test
    fun makeYogaNode_should_set_margin_as_HORIZONTAL_and_1() {
        // Given
        val style = Style(
            margin = EdgeValue(horizontal = UnitValue(constant(ONE_UNIT_VALUE), UnitType.REAL))
        )

        // When
        flexMapper.observeBindChangesFlex(style, rootViewMock, viewMock, yogaNodeMock)
        observeSlot.captured.invoke(ONE_UNIT_VALUE)

        // Then
        verify(exactly = 1) {
            yogaNodeMock.setMargin(
                YogaEdge.HORIZONTAL,
                ONE_UNIT_VALUE.toFloat()
            )
        }
    }

    @Test
    fun makeYogaNode_should_set_marginPercent_as_TOP_and_1() {
        // Given
        val style = Style(
            margin = EdgeValue(top = UnitValue(constant(ONE_UNIT_VALUE), UnitType.PERCENT))
        )

        // When
        flexMapper.observeBindChangesFlex(style, rootViewMock, viewMock, yogaNodeMock)
        observeSlot.captured.invoke(ONE_UNIT_VALUE)

        // Then
        verify(exactly = 1) {
            yogaNodeMock.setMarginPercent(
                YogaEdge.TOP,
                ONE_UNIT_VALUE.toFloat()
            )
        }
    }

    @Test
    fun makeYogaNode_should_set_padding_as_TOP_and_1() {
        // Given
        val style = Style(
            padding = EdgeValue(top = UnitValue(constant(ONE_UNIT_VALUE), UnitType.REAL))
        )

        // When
        flexMapper.observeBindChangesFlex(style, rootViewMock, viewMock, yogaNodeMock)
        observeSlot.captured.invoke(ONE_UNIT_VALUE)

        // Then
        verify(exactly = 1) { yogaNodeMock.setPadding(YogaEdge.TOP, ONE_UNIT_VALUE.toFloat()) }
    }

    @Test
    fun makeYogaNode_should_set_paddingPercent_as_TOP_and_1() {
        // Given
        val style = Style(
            padding = EdgeValue(top = UnitValue(constant(ONE_UNIT_VALUE), UnitType.PERCENT))
        )

        // When
        flexMapper.observeBindChangesFlex(style, rootViewMock, viewMock, yogaNodeMock)
        observeSlot.captured.invoke(ONE_UNIT_VALUE)

        // Then
        verify(exactly = 1) {
            yogaNodeMock.setPaddingPercent(
                YogaEdge.TOP,
                ONE_UNIT_VALUE.toFloat()
            )
        }
    }

    @Test
    fun makeYogaNode_should_set_position_as_TOP_and_1() {
        // Given
        val style = Style(
            position = EdgeValue(top = UnitValue(constant(ONE_UNIT_VALUE), UnitType.REAL))
        )

        // When
        flexMapper.observeBindChangesFlex(style, rootViewMock, viewMock, yogaNodeMock)
        observeSlot.captured.invoke(ONE_UNIT_VALUE)

        // Then
        verify(exactly = 1) { yogaNodeMock.setPosition(YogaEdge.TOP, ONE_UNIT_VALUE.toFloat()) }
    }

    @Test
    fun makeYogaNode_should_set_positionPercent_as_TOP_and_1() {
        // Given
        val style = Style(
            position = EdgeValue(top = UnitValue(constant(ONE_UNIT_VALUE), UnitType.PERCENT))
        )

        // When
        flexMapper.observeBindChangesFlex(style, rootViewMock, viewMock, yogaNodeMock)
        observeSlot.captured.invoke(ONE_UNIT_VALUE)

        // Then
        verify(exactly = 1) {
            yogaNodeMock.setPositionPercent(
                YogaEdge.TOP,
                ONE_UNIT_VALUE.toFloat()
            )
        }
    }
}