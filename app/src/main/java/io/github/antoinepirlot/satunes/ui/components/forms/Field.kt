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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Form field.
 * @author Antoine Pirlot 12/10/2025
 */
@Composable
fun Field(
    modifier: Modifier = Modifier,
    farLeft: Boolean = false,
    farRight: Boolean = false,
    enabled: Boolean,
    value: MutableIntState,
    label: String,
    maxValue: Int
) {
    val fieldModifier: Modifier =
        if (farLeft)
            Modifier.padding(end = 4.dp)
        else if (farRight)
            Modifier.padding(start = 4.dp)
        else
            Modifier.padding(horizontal = 4.dp)

    Box(modifier = modifier) {
        OutlinedNumberField(
            modifier = fieldModifier,
            enabled = enabled,
            value = value,
            label = label,
            maxValue = maxValue
        )
    }
}