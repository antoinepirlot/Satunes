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

package io.github.antoinepirlot.satunes.ui.views.settings

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import io.github.antoinepirlot.satunes.database.services.settings.SettingsManager
import io.github.antoinepirlot.satunes.icons.SatunesIcons

/**
 * @author Antoine Pirlot on 13/05/2024
 */

@Composable
fun RepeatModeRadioButtons(
    modifier: Modifier = Modifier,
) {
    val iconsList: List<SatunesIcons> = listOf(
        SatunesIcons.REPEAT, // i = 0
        SatunesIcons.REPEAT_ON, // i = 1
        SatunesIcons.REPEAT_ONE_ON // i = 2
    )
    var state: Int by remember { SettingsManager.repeatMode }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .selectableGroup(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        for (i: Int in iconsList.indices) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = iconsList[i].imageVector,
                    contentDescription = iconsList[i].description
                )
                val context: Context = LocalContext.current
                RadioButton(
                    selected = state == i,
                    onClick = {
                        state = i
                        SettingsManager.updateRepeatMode(context = context, newValue = i)
                    }
                )
            }
        }
    }
}

@Preview
@Composable
private fun RepeatModeRadioButtonsPreview() {
    RepeatModeRadioButtons()
}