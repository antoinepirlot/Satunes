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

package io.github.antoinepirlot.satunes.ui.components.forms

import android.annotation.SuppressLint
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.text.isDigitsOnly
import io.github.antoinepirlot.jetpack_libs.components.texts.NormalText

/**
 * @author Antoine Pirlot on 13/10/2024
 */

@Composable
internal fun OutlinedNumberField(
    modifier: Modifier = Modifier,
    value: MutableIntState,
    label: String,
    maxValue: Int,
) {
    val keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    OutlinedTextField(
        modifier = modifier,
        value = if (value.intValue > 0) value.intValue.toString() else "",
        label = { NormalText(text = label) },
        onValueChange = {
            if (it.isBlank()) {
                value.intValue = 0
            } else if (it.isDigitsOnly()) {
                val itAsInt: Int = it.toInt()
                if (itAsInt <= maxValue) {
                    value.intValue = it.toInt()
                }
            }
        },
        keyboardOptions = keyboardOptions,
    )
}

@SuppressLint("UnrememberedMutableState")
@Preview
@Composable
private fun OutlinedNumberFieldPreview() {
    OutlinedNumberField(
        value = mutableIntStateOf(0),
        label = "Label",
        maxValue = 1
    )
}