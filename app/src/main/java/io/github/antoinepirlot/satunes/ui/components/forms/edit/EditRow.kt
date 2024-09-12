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

package io.github.antoinepirlot.satunes.ui.components.forms.edit

import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.antoinepirlot.jetpack_libs.components.texts.NormalText

/**
 * @author Antoine Pirlot on 12/09/2024
 */

@Composable
fun EditRow(
    modifier: Modifier = Modifier,
    value: String,
    placeholder: String,
    label: String,
    onValueChange: (value: String) -> Unit,
    enabled: Boolean = true,
    readOnly: Boolean = false
) {
    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        readOnly = readOnly,
        placeholder = { NormalText(text = placeholder) },
        label = { NormalText(text = label) }
    )
}

@Preview
@Composable
private fun EditRowPreview() {
    EditRow(value = "Value", onValueChange = {}, placeholder = "Placeholder", label = "Label")
}