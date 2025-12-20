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

import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.antoinepirlot.jetpack_libs.models.JetpackLibsIcons
import io.github.antoinepirlot.satunes.ui.components.cards.ListItem

/**
 * @author Antoine Pirlot on 20/04/2024
 */

@Composable
internal fun ClickableListItem(
    modifier: Modifier = Modifier,
    text: String,
    jetpackLibsIcons: JetpackLibsIcons,
    onClick: () -> Unit,
) {
    ListItem(
        modifier = modifier.clickable {
            onClick()
        },
        leadingContent = {
            Icon(
                imageVector = jetpackLibsIcons.imageVector,
                contentDescription = jetpackLibsIcons.description
            )
        },
        headlineContent = {
            Text(text = text)
        }
    )
}

@Preview
@Composable
private fun ClickableListItemPreview() {
    ClickableListItem(
        text = "Hello World!",
        jetpackLibsIcons = JetpackLibsIcons.SETTINGS,
        onClick = {})
}