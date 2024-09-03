/*
 * This file is part of Satunes.
 *
 *  Satunes is free software: you can redistribute it and/or modify it under
 *  the terms of the GNU General Public License as published by the Free Software Foundation,
 *  either version 3 of the License, or (at your option) any later version.
 *
 *  Satunes is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with Satunes.
 *  If not, see <https://www.gnu.org/licenses/>.
 *
 *  **** INFORMATIONS ABOUT THE AUTHOR *****
 *  The author of this file is Antoine Pirlot, the owner of this project.
 *  You find this original project on github.
 *
 *  My github link is: https://github.com/antoinepirlot
 *  This current project's link is: https://github.com/antoinepirlot/Satunes
 *
 *  You can contact me via my email: pirlot.antoine@outlook.com
 *  PS: I don't answer quickly.
 */

package io.github.antoinepirlot.satunes.ui.components.bars.scroll_bar

import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import io.github.antoinepirlot.satunes.ui.theme.SatunesTheme
import kotlin.math.roundToInt

/**
 * @author Antoine Pirlot on 03/09/2024
 */

@Composable
fun ScrollBar(
    modifier: Modifier = Modifier,
    barWidth: Dp = 15.dp,
    color: Color = MaterialTheme.colorScheme.onPrimary,
    shape: Shape = CircleShape
) {
    val heightOfSliderButton: Dp = 150.dp
    var height: Dp = 0.dp

    Box(
        modifier = modifier
            .fillMaxHeight()
            .width(barWidth)
            .onGloballyPositioned { coordinates: LayoutCoordinates ->
                height = coordinates.size.height.dp
            }
    ) {
        var yPosition: Float by remember { mutableFloatStateOf(0f) }

        Box(
            modifier = Modifier

                .offset { IntOffset(0, yPosition.roundToInt()) }
                .pointerInput(Unit) {
                    detectDragGestures { change: PointerInputChange, dragAmount: Offset ->
                        change.consume()
                        val newYPosition: Float = yPosition + dragAmount.y
                        if (newYPosition >= 0f && newYPosition + heightOfSliderButton.toPx() <= height.value) {
                            yPosition = newYPosition
                        }
                    }
                }
                .height(heightOfSliderButton)
                .width(barWidth)
                .border(width = barWidth, color = color, shape = shape)
        )
    }
}

@Preview
@Composable
fun ScrollBarPreview() {
    SatunesTheme {
        ScrollBar()
    }
}