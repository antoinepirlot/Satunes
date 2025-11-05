/*
 * This file is part of Satunes.
 *
 * Satunes is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * Satunes is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with Satunes.
 * If not, see <https://www.gnu.org/licenses/>.
 *
 * *** INFORMATION ABOUT THE AUTHOR *****
 * The author of this file is Antoine Pirlot, the owner of this project.
 * You find this original project on Codeberg.
 *
 * My Codeberg link is: https://codeberg.org/antoinepirlot
 * This current project's link is: https://codeberg.org/antoinepirlot/Satunes
 */

package io.github.antoinepirlot.satunes.ui.components.buttons

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.antoinepirlot.jetpack_libs.components.JetpackLibsIcons
import io.github.antoinepirlot.jetpack_libs.components.texts.NormalText
import io.github.antoinepirlot.satunes.ui.components.LoadingCircle

/**
 * @author Antoine Pirlot on 09/08/2024
 */

@Composable
internal fun ButtonWithIcon(
    modifier: Modifier = Modifier,
    jetpackLibsIcons: JetpackLibsIcons,
    onClick: () -> Unit,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    shape: Shape = ButtonDefaults.shape,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    text: String?,
) {
    val widthSize: Dp = 150.dp
    val heightSize: Dp = 40.dp

    Button(
        onClick = onClick,
        modifier = modifier.size(width = widthSize, height = heightSize),
        enabled = enabled,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (isLoading) {
                LoadingCircle(modifier = Modifier.size(20.dp))
            } else {
                Icon(
                    imageVector = jetpackLibsIcons.imageVector,
                    contentDescription = jetpackLibsIcons.description
                )
                if (text != null) {
                    Spacer(modifier = Modifier.size(10.dp))
                    NormalText(
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        text = text
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun ButtonWithIconPreview() {
    Column {
        ButtonWithIcon(
            modifier = Modifier.fillMaxWidth(),
            jetpackLibsIcons = JetpackLibsIcons.REMOVE_ICON,
            onClick = {},
            text = "Hello World !"
        )
        ButtonWithIcon(
            modifier = Modifier.fillMaxWidth(),
            jetpackLibsIcons = JetpackLibsIcons.TIMER,
            onClick = {},
            text = null
        )
    }
}