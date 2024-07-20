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

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.satunes.database.models.MenuTitle
import io.github.antoinepirlot.satunes.models.SearchChips
import io.github.antoinepirlot.satunes.models.SwitchSettings
import io.github.antoinepirlot.satunes.services.search.SearchChipsManager
import io.github.antoinepirlot.satunes.ui.components.cards.ListItem
import io.github.antoinepirlot.satunes.ui.states.SatunesUiState
import io.github.antoinepirlot.satunes.ui.viewmodels.SatunesViewModel

/**
 *   @author Antoine Pirlot 06/03/2024
 */

@Composable
internal fun SettingsSwitchList(
    modifier: Modifier = Modifier,
    satunesViewModel: SatunesViewModel = viewModel(),
    checkedMap: Map<SwitchSettings, Boolean>,
) {
    val satunesUiState: SatunesUiState by satunesViewModel.uiState.collectAsState()

    Column(modifier = modifier) {
        for (setting: SwitchSettings in checkedMap.keys) {
            ListItem( //Has always horizontal padding of 16.dp
                headlineContent = {
                    SettingWithSwitch(
                        setting = setting,
                        checked = checkedMap[setting]!!,
                        onCheckedChange = {
                            switchSetting(
                                satunesViewModel = satunesViewModel,
                                uiState = satunesUiState,
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

private fun switchSetting(
    satunesViewModel: SatunesViewModel,
    uiState: SatunesUiState,
    setting: SwitchSettings
) {
    when (setting) {
        SwitchSettings.FOLDERS_CHECKED -> {
            satunesViewModel.switchMenuTitle(
                menuTitle = MenuTitle.FOLDERS
            )
        }

        SwitchSettings.ARTISTS_CHECKED -> {
            satunesViewModel.switchMenuTitle(
                menuTitle = MenuTitle.ARTISTS
            )
        }

        SwitchSettings.ALBUMS_CHECKED -> {
            satunesViewModel.switchMenuTitle(
                menuTitle = MenuTitle.ALBUMS
            )
        }

        SwitchSettings.GENRES_CHECKED -> {
            satunesViewModel.switchMenuTitle(
                menuTitle = MenuTitle.GENRES
            )
        }

        SwitchSettings.PLAYLISTS_CHECKED -> {
            satunesViewModel.switchMenuTitle(
                menuTitle = MenuTitle.PLAYLISTS
            )
        }

        SwitchSettings.PLAYBACK_WHEN_CLOSED -> {
            satunesViewModel.switchPlaybackWhenClosedChecked()
        }

        SwitchSettings.PAUSE_IF_NOISY -> {
            satunesViewModel.switchPauseIfNoisy()
        }

        SwitchSettings.INCLUDE_RINGTONES -> {
            satunesViewModel.switchIncludeRingtones()
        }

        SwitchSettings.SHUFFLE_MODE -> {
            satunesViewModel.switchShuffleMode()
        }

        SwitchSettings.PAUSE_IF_ANOTHER_PLAYBACK -> {
            satunesViewModel.switchPauseIfAnotherPlayback()
        }

        SwitchSettings.MUSICS_FILTER -> {
            satunesViewModel.switchFilter(filterSetting = MenuTitle.MUSICS)
            val searchChip: SearchChips = SearchChips.MUSICS
            if (uiState.musicsFilter != searchChip.enabled.value) {
                switchSearchChip(searchChip = searchChip)
            }
        }

        SwitchSettings.ARTISTS_FILTER -> {
            satunesViewModel.switchFilter(filterSetting = MenuTitle.ARTISTS)
            val searchChip: SearchChips = SearchChips.ARTISTS
            if (uiState.artistsFilter != searchChip.enabled.value) {
                switchSearchChip(searchChip = searchChip)
            }
        }

        SwitchSettings.ALBUMS_FILTER -> {
            satunesViewModel.switchFilter(filterSetting = MenuTitle.ALBUMS)
            val searchChip: SearchChips = SearchChips.ALBUMS
            if (uiState.albumsFilter != searchChip.enabled.value) {
                switchSearchChip(searchChip = searchChip)
            }
        }

        SwitchSettings.GENRES_FILTER -> {
            satunesViewModel.switchFilter(filterSetting = MenuTitle.GENRES)
            val searchChip: SearchChips = SearchChips.GENRES
            if (uiState.genresFilter != searchChip.enabled.value) {
                switchSearchChip(searchChip = searchChip)
            }
        }

        SwitchSettings.FOLDERS_FILTER -> {
            satunesViewModel.switchFilter(filterSetting = MenuTitle.FOLDERS)
            val searchChip: SearchChips = SearchChips.FOLDERS
            if (uiState.foldersFilter != searchChip.enabled.value) {
                switchSearchChip(searchChip = searchChip)
            }
        }

        SwitchSettings.PLAYLISTS_FILTER -> {
            satunesViewModel.switchFilter(filterSetting = MenuTitle.PLAYLISTS)
            val searchChip: SearchChips = SearchChips.PLAYLISTS
            if (uiState.playlistsFilter != searchChip.enabled.value) {
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