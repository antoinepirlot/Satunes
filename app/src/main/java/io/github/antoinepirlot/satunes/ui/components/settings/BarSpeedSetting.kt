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

package io.github.antoinepirlot.satunes.ui.components.settings

import android.content.Context
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.database.services.settings.SettingsManager
import io.github.antoinepirlot.satunes.ui.components.texts.NormalText
import kotlin.math.floor

/**
 * @author Antoine Pirlot on 27/04/2024
 */

@Composable
fun BarSpeedSetting(
    modifier: Modifier = Modifier,
) {
    val context: Context = LocalContext.current
    val currentBarSpeed: Float by remember { SettingsManager.barSpeed }
    var isUpdating: Boolean by remember { mutableStateOf(false) }
    var newBarSpeed: Float by remember { mutableFloatStateOf(currentBarSpeed) }
    Row(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        NormalText(text = stringResource(id = R.string.bar_speed))
        if (isUpdating) {
            Text(text = (floor(newBarSpeed * 100) / 100).toString() + ' ' + stringResource(id = R.string.second))
        } else {
            Text(text = (floor(currentBarSpeed * 100) / 100).toString() + ' ' + stringResource(id = R.string.second))
        }
        Slider(
            modifier = modifier.padding(start = 16.dp),
            value = if (isUpdating) newBarSpeed else currentBarSpeed,
            onValueChange = {
                isUpdating = true
                newBarSpeed = it
            },
            onValueChangeFinished = {
                SettingsManager.updateBarSpeed(context = context, newValue = newBarSpeed)
                isUpdating = false
            },
            valueRange = 0.1f..1f,
            steps = 20
        )
    }
}

@Preview
@Composable
fun BarSpeedSettingPreview() {
    BarSpeedSetting()
}