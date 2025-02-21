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
    enabled: Boolean = true,
    label: String,
    maxValue: Int? = null,
    onValueChanged: ((newValue: Int) -> Unit)? = null,
) {
    val keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    OutlinedTextField(
        modifier = modifier,
        enabled = enabled,
        value = if (value.intValue > 0) value.intValue.toString() else "",
        label = { NormalText(text = label) },
        onValueChange = {
            if (it.isBlank()) {
                value.intValue = 0
            } else if (it.isDigitsOnly()) {
                try {
                    val itAsInt: Int = it.toInt()
                    if (maxValue != null) {
                        if (itAsInt <= maxValue) {
                            value.intValue = itAsInt
                            onValueChanged?.invoke(itAsInt)
                        }
                    } else {
                        value.intValue = itAsInt
                        onValueChanged?.invoke(itAsInt)
                    }
                } catch (e: NumberFormatException) {
                    /*It's not a int but a long or something else, so do nothing*/
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