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

package io.github.antoinepirlot.satunes.ui.components.forms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.antoinepirlot.jetpack_libs.components.texts.NormalText
import io.github.antoinepirlot.satunes.icons.SatunesIcons
import io.github.antoinepirlot.satunes.ui.components.images.Icon

/**
 * @author Antoine Pirlot 21/04/2025
 */
@Composable
fun SatunesDropDownMenu(
    modifier: Modifier = Modifier,
    title: String,
    selectedItemText: String,
    menu: @Composable (expanded: Boolean, onDismissRequest: () -> Unit) -> Unit
) {
    var expanded: Boolean by remember { mutableStateOf(false) }
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        NormalText(text = title)
        Box {
            TextButton(onClick = { expanded = true }) {
                Row {
                    Icon(
                        icon = if (expanded) SatunesIcons.CLOSE_DROPDOWN_MENU
                        else SatunesIcons.OPEN_DROPDOWN_MENU
                    )
                    NormalText(text = selectedItemText)
                }
            }
            menu(expanded, { expanded = false })
        }
    }
}

@Preview
@Composable
private fun SatunesDropDownMenuPreview() {
    SatunesDropDownMenu(
        title = "Drop Down Menu",
        selectedItemText = "Selected Item"
    ) { _: Boolean, _: () -> Unit ->
    }
}