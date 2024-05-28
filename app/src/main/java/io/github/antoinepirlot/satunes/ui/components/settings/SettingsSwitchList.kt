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
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.database.models.MenuTitle
import io.github.antoinepirlot.satunes.database.services.settings.SettingsManager
import io.github.antoinepirlot.satunes.icons.SatunesIcons
import io.github.antoinepirlot.satunes.ui.components.cards.ListItem
import io.github.antoinepirlot.satunes.ui.components.texts.NormalText
import io.github.antoinepirlot.satunes.ui.views.settings.Settings

/**
 *   @author Antoine Pirlot 06/03/2024
 */

@Composable
fun SettingsSwitchList(
    modifier: Modifier = Modifier,
    checkedMap: Map<Settings, MutableState<Boolean>>,
) {
    val context: Context = LocalContext.current

    //Used to show modal for restart needed setting
    var clickedSetting: Settings? by rememberSaveable { mutableStateOf(null) }

    Column(
        modifier = modifier
    ) {
        for (setting: Settings in checkedMap.keys.toList()) {
            val isRestartNeeded: Boolean = when (setting) {
                Settings.EXCLUDE_RINGTONES, Settings.PAUSE_IF_NOISY,
                Settings.PAUSE_IF_ANOTHER_PLAYBACK -> true

                else -> false
            }
            ListItem(
                headlineContent = {
                    SettingWithSwitch(
                        text = stringResource(id = setting.stringId),
                        checked = checkedMap[setting]!!.value,
                        icon = if (isRestartNeeded) SatunesIcons.INFO else null,
                        onCheckedChange = {
                            if (isRestartNeeded) {
                                clickedSetting = setting
                            } else {
                                switchSetting(
                                    context = context,
                                    setting = setting
                                )
                            }
                        }
                    )
                }
            )
        }
    }

    //TODO use info composable in v1.1.0
    if (clickedSetting != null) {
        AlertDialog(
            icon = {
                val icon: SatunesIcons = SatunesIcons.INFO
                Icon(imageVector = icon.imageVector, contentDescription = icon.description)
            },
            title = {
                NormalText(text = stringResource(id = R.string.restart_required))
            },
            onDismissRequest = { clickedSetting = null },
            confirmButton = {
                TextButton(onClick = {
                    switchSetting(
                        context = context,
                        setting = clickedSetting!!
                    )
                    clickedSetting = null
                }) {
                    NormalText(text = stringResource(id = R.string.ok))
                }
            }
        )
    }
}

@Composable
@Preview
fun SettingsSwitchListPreview() {
    SettingsSwitchList(checkedMap = mapOf())
}

private fun switchSetting(context: Context, setting: Settings) {
    when (setting) {
        Settings.FOLDERS_CHECKED -> {
            SettingsManager.switchMenuTitle(
                context = context,
                menuTitle = MenuTitle.FOLDERS
            )
        }

        Settings.ARTISTS_CHECKED -> {
            SettingsManager.switchMenuTitle(
                context = context,
                menuTitle = MenuTitle.ARTISTS
            )
        }

        Settings.ALBUMS_CHECKED -> {
            SettingsManager.switchMenuTitle(
                context = context,
                menuTitle = MenuTitle.ALBUMS
            )
        }

        Settings.GENRES_CHECKED -> {
            SettingsManager.switchMenuTitle(
                context = context,
                menuTitle = MenuTitle.GENRES
            )
        }

        Settings.PLAYLISTS_CHECKED -> {
            SettingsManager.switchMenuTitle(
                context = context,
                menuTitle = MenuTitle.PLAYLISTS
            )
        }

        Settings.PLAYBACK_WHEN_CLOSED -> {
            SettingsManager.switchPlaybackWhenClosedChecked(context = context)
        }

        Settings.PAUSE_IF_NOISY -> {
            SettingsManager.switchPauseIfNoisy(context = context)
        }

        Settings.EXCLUDE_RINGTONES -> {
            SettingsManager.switchExcludeRingtones(context = context)
        }

        Settings.SHUFFLE_MODE -> {
            SettingsManager.switchShuffleMode(context = context)
        }

        Settings.PAUSE_IF_ANOTHER_PLAYBACK -> {
            SettingsManager.switchPauseIfPlayback(context = context)
        }

        else -> { /* Not a switch */
        }
    }
}