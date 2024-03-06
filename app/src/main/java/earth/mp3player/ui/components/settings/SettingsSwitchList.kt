/*
 * This file is part of MP3 Player
 *
 * MP3 Player is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * MP3 Player is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with MP3 Player.
 * If not, see <https://www.gnu.org/licenses/>.
 *
 *  ***** INFORMATIONS ABOUT THE AUTHOR *****
 * The author of this file is Antoine Pirlot, the owner of this project.
 * You find this original project on github.
 *
 * My github link is: https://github.com/antoinepirlot
 * This current project's link is: https://github.com/antoinepirlot/MP3-Player
 *
 * You can contact me via my email: pirlot.antoine@outlook.com
 * PS: I don't answer quickly.
 */

package earth.mp3player.ui.components.settings

import android.content.Context
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import earth.mp3player.services.SettingsManager
import earth.mp3player.ui.views.settings.Settings
import kotlinx.coroutines.runBlocking

/**
 *   @author Antoine Pirlot 06/03/2024
 */

@Composable
fun SettingsSwitchList(
    modifier: Modifier = Modifier,
    checkedMap: Map<Settings, MutableState<Boolean>>,
) {
    val context: Context = LocalContext.current
    LazyColumn {
        items(
            items = checkedMap.keys.toList(),
            key = { it.stringId }
        ) { setting: Settings ->
            ListItem(headlineContent = {
                SettingWithSwitch(
                    text = stringResource(id = setting.stringId),
                    checked = checkedMap[setting]!!.value,
                    onCheckedChange = {
                        runBlocking {
                            SettingsManager.switchClosedAppPlaybackChecked(context = context)
                        }
                    }
                )
            })
        }
    }
}

@Composable
@Preview
fun SettingsSwitchListPreview() {
    SettingsSwitchList(checkedMap = mapOf())
}