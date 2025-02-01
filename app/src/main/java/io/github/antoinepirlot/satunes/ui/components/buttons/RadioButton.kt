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

package io.github.antoinepirlot.satunes.ui.components.buttons

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.RadioButtonColors
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.antoinepirlot.jetpack_libs.components.texts.NormalText
import io.github.antoinepirlot.satunes.icons.SatunesIcons
import io.github.antoinepirlot.satunes.ui.components.images.Icon

/**
 * @author Antoine Pirlot on 30/11/2024
 */

val spacerSize: Dp = 5.dp

@Composable
internal fun RadioButton(
    modifier: Modifier = Modifier,
    selected: Boolean,
    onClick: () -> Unit,
    enabled: Boolean = true,
    colors: RadioButtonColors = RadioButtonDefaults.colors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    icon: SatunesIcons? = null,
    text: String? = null
) {

    Row(
        modifier = modifier
            .clip(shape = CircleShape)
            .clickable(onClick = onClick)
            .padding(end = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        androidx.compose.material3.RadioButton(
            selected = selected,
            onClick = onClick,
            enabled = enabled,
            colors = colors,
            interactionSource = interactionSource
        )
        if (icon != null) Icon(icon = icon)
        if (icon != null && text != null) Spacer(modifier = Modifier.size(size = spacerSize))
        if (text != null) NormalText(text = text)
    }
}

@Preview
@Composable
private fun RadioButtonPreview() {
    Column {
        RadioButton(selected = true, onClick = {})
        RadioButton(icon = SatunesIcons.SORT, selected = false, onClick = {})
        RadioButton(text = "Hello RadioButton!", selected = false, onClick = {})
        RadioButton(
            icon = SatunesIcons.SORT,
            text = "Hello RadioButtonWithIcon",
            selected = true,
            onClick = {})
    }
}