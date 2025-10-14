/*
 * This file is part of Satunes.
 *
 * Satunes is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * Satunes is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License along with Satunes.
 *
 * If not, see <https://www.gnu.org/licenses/>.
 *
 * **** INFORMATION ABOUT THE AUTHOR *****
 * The author of this file is Antoine Pirlot, the owner of this project.
 * You find this original project on Codeberg.
 *
 * My Codeberg link is: https://codeberg.org/antoinepirlot
 * This current project's link is: https://codeberg.org/antoinepirlot/Satunes
 */

package io.github.antoinepirlot.satunes.ui.components.buttons.folders

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.antoinepirlot.jetpack_libs.components.texts.NormalText
import io.github.antoinepirlot.satunes.database.models.media.Folder
import io.github.antoinepirlot.satunes.icons.SatunesIcons
import io.github.antoinepirlot.satunes.ui.components.images.Icon

/**
 * @author Antoine Pirlot 12/10/2025
 */
@Composable
fun FolderPathButton(
    modifier: Modifier = Modifier,
    folder: Folder,
    onClick: (() -> Unit)?
) {
    val padding: Dp = 5.dp
    val fontSize: TextUnit = 20.sp
    val boxModifier: Modifier =
        if (onClick != null)
            Modifier
                .clip(shape = CircleShape)
                .clickable(onClick = onClick) //Do not use clip after clickable as it will be ignored
        else
            Modifier

    Row(modifier = modifier) {
        Icon(
            Modifier.padding(vertical = padding),
            icon = SatunesIcons.RIGHT_ARROW
        )
        Box(
            modifier = boxModifier
                .background(
                    color = MaterialTheme.colorScheme.inversePrimary,
                    shape = CircleShape
                )
                .padding(all = padding)
        ) {
            NormalText(
                text = folder.title,
                color = MaterialTheme.colorScheme.primary,
                fontSize = fontSize,
                maxLines = 2
            )
        }
    }
}