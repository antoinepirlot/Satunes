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

package io.github.antoinepirlot.satunes.ui.components.images

import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import io.github.antoinepirlot.satunes.icons.SatunesIcons

/**
 * @author Antoine Pirlot on 09/08/2024
 */

@Composable
internal fun Icon(
    modifier: Modifier = Modifier,
    icon: SatunesIcons,
    tint: Color = LocalContentColor.current,
) {
    androidx.compose.material3.Icon(
        modifier = modifier,
        imageVector = icon.imageVector,
        contentDescription = icon.description,
        tint = tint
    )
}

@Preview
@Composable
private fun IconPreview() {
    Icon(icon = SatunesIcons.ADD)
}