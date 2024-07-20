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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import io.github.antoinepirlot.satunes.database.models.MenuTitle
import io.github.antoinepirlot.satunes.database.services.settings.SettingsManager
import io.github.antoinepirlot.satunes.models.SearchChips
import io.github.antoinepirlot.satunes.models.SwitchSettings
import io.github.antoinepirlot.satunes.services.search.SearchChipsManager
import io.github.antoinepirlot.satunes.ui.components.cards.ListItem

/**
 *   @author Antoine Pirlot 06/03/2024
 */

@Composable
internal fun SettingsSwitchList(
    modifier: Modifier = Modifier,
    checkedMap: Map<SwitchSettings, Boolean>,
) {
    val context: Context = LocalContext.current

    Column(modifier = modifier) {
        for (setting: SwitchSettings in checkedMap.keys) {
            ListItem( //Has always horizontal padding of 16.dp
                headlineContent = {
                    SettingWithSwitch(
                        setting = setting,
                        checked = checkedMap[setting]!!,
                        onCheckedChange = {
                            switchSetting(
                                context = context,
                                setting = setting
                            )
                        }
                    )
                }
            )
        }
    }
}

@Composable
@Preview
private fun SettingsSwitchListPreview() {
    SettingsSwitchList(checkedMap = mapOf())
}

private fun switchSetting(context: Context, setting: SwitchSettings) {
    when (setting) {
        SwitchSettings.FOLDERS_CHECKED -> {
            SettingsManager.switchMenuTitle(
                context = context,
                menuTitle = MenuTitle.FOLDERS
            )
        }

        SwitchSettings.ARTISTS_CHECKED -> {
            SettingsManager.switchMenuTitle(
                context = context,
                menuTitle = MenuTitle.ARTISTS
            )
        }

        SwitchSettings.ALBUMS_CHECKED -> {
            SettingsManager.switchMenuTitle(
                context = context,
                menuTitle = MenuTitle.ALBUMS
            )
        }

        SwitchSettings.GENRES_CHECKED -> {
            SettingsManager.switchMenuTitle(
                context = context,
                menuTitle = MenuTitle.GENRES
            )
        }

        SwitchSettings.PLAYLISTS_CHECKED -> {
            SettingsManager.switchMenuTitle(
                context = context,
                menuTitle = MenuTitle.PLAYLISTS
            )
        }

        SwitchSettings.PLAYBACK_WHEN_CLOSED -> {
            SettingsManager.switchPlaybackWhenClosedChecked(context = context)
        }

        SwitchSettings.PAUSE_IF_NOISY -> {
            SettingsManager.switchPauseIfNoisy(context = context)
        }

        SwitchSettings.INCLUDE_RINGTONES -> {
            SettingsManager.switchIncludeRingtones(context = context)
        }

        SwitchSettings.SHUFFLE_MODE -> {
            SettingsManager.switchShuffleMode(context = context)
        }

        SwitchSettings.PAUSE_IF_ANOTHER_PLAYBACK -> {
            SettingsManager.switchPauseIfPlayback(context = context)
        }

        SwitchSettings.MUSICS_FILTER -> {
            SettingsManager.switchFilter(context = context, filterSetting = MenuTitle.MUSICS)
            val searchChip: SearchChips = SearchChips.MUSICS
            if (SettingsManager.musicsFilter.value != searchChip.enabled.value) {
                switchSearchChip(searchChip = searchChip)
            }
        }

        SwitchSettings.ARTISTS_FILTER -> {
            SettingsManager.switchFilter(context = context, filterSetting = MenuTitle.ARTISTS)
            val searchChip: SearchChips = SearchChips.ARTISTS
            if (SettingsManager.artistsFilter.value != searchChip.enabled.value) {
                switchSearchChip(searchChip = searchChip)
            }
        }

        SwitchSettings.ALBUMS_FILTER -> {
            SettingsManager.switchFilter(context = context, filterSetting = MenuTitle.ALBUMS)
            val searchChip: SearchChips = SearchChips.ALBUMS
            if (SettingsManager.albumsFilter.value != searchChip.enabled.value) {
                switchSearchChip(searchChip = searchChip)
            }
        }

        SwitchSettings.GENRES_FILTER -> {
            SettingsManager.switchFilter(context = context, filterSetting = MenuTitle.GENRES)
            val searchChip: SearchChips = SearchChips.GENRES
            if (SettingsManager.genresFilter.value != searchChip.enabled.value) {
                switchSearchChip(searchChip = searchChip)
            }
        }

        SwitchSettings.FOLDERS_FILTER -> {
            SettingsManager.switchFilter(context = context, filterSetting = MenuTitle.FOLDERS)
            val searchChip: SearchChips = SearchChips.FOLDERS
            if (SettingsManager.foldersFilter.value != searchChip.enabled.value) {
                switchSearchChip(searchChip = searchChip)
            }
        }

        SwitchSettings.PLAYLISTS_FILTER -> {
            SettingsManager.switchFilter(context = context, filterSetting = MenuTitle.PLAYLISTS)
            val searchChip: SearchChips = SearchChips.PLAYLISTS
            if (SettingsManager.playlistsFilter.value != searchChip.enabled.value) {
                switchSearchChip(searchChip = searchChip)
            }
        }

        else -> { /* Not a switch */
        }
    }
}

private fun switchSearchChip(searchChip: SearchChips) {
    if (searchChip.enabled.value) {
        SearchChipsManager.unselect(searchChip = searchChip)
    } else {
        SearchChipsManager.select(searchChip = searchChip)
    }
}