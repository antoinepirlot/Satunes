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
import io.github.antoinepirlot.satunes.database.models.NavBarSection
import io.github.antoinepirlot.satunes.models.SearchChips
import io.github.antoinepirlot.satunes.models.SwitchSettings
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
                                satunesUiState = satunesUiState,
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
    satunesUiState: SatunesUiState,
    setting: SwitchSettings
) {
    when (setting) {
        SwitchSettings.FOLDERS_CHECKED -> {
            satunesViewModel.switchNavBarSection(
                navBarSection = NavBarSection.FOLDERS
            )
        }

        SwitchSettings.ARTISTS_CHECKED -> {
            satunesViewModel.switchNavBarSection(
                navBarSection = NavBarSection.ARTISTS
            )
        }

        SwitchSettings.ALBUMS_CHECKED -> {
            satunesViewModel.switchNavBarSection(
                navBarSection = NavBarSection.ALBUMS
            )
        }

        SwitchSettings.GENRES_CHECKED -> {
            satunesViewModel.switchNavBarSection(
                navBarSection = NavBarSection.GENRES
            )
        }

        SwitchSettings.PLAYLISTS_CHECKED -> {
            satunesViewModel.switchNavBarSection(
                navBarSection = NavBarSection.PLAYLISTS
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
            satunesViewModel.switchFilter(filterSetting = NavBarSection.MUSICS)
            val searchChip: SearchChips = SearchChips.MUSICS
            if (satunesUiState.musicsFilter != satunesViewModel.selectedSearchChips.contains(
                    searchChip
                )
            ) {
                switchSearchChip(
                    satunesViewModel = satunesViewModel,
                    searchChip = searchChip,
                    selected = satunesUiState.musicsFilter
                )
            }
        }

        SwitchSettings.ARTISTS_FILTER -> {
            satunesViewModel.switchFilter(filterSetting = NavBarSection.ARTISTS)
            val searchChip: SearchChips = SearchChips.ARTISTS
            if (satunesUiState.artistsFilter != satunesViewModel.selectedSearchChips.contains(
                    searchChip
                )
            ) {
                switchSearchChip(
                    satunesViewModel = satunesViewModel,
                    searchChip = searchChip,
                    selected = satunesUiState.artistsFilter
                )
            }
        }

        SwitchSettings.ALBUMS_FILTER -> {
            satunesViewModel.switchFilter(filterSetting = NavBarSection.ALBUMS)
            val searchChip: SearchChips = SearchChips.ALBUMS
            if (satunesUiState.albumsFilter != satunesViewModel.selectedSearchChips.contains(
                    searchChip
                )
            ) {
                switchSearchChip(
                    satunesViewModel = satunesViewModel,
                    searchChip = searchChip,
                    selected = satunesUiState.albumsFilter
                )
            }
        }

        SwitchSettings.GENRES_FILTER -> {
            satunesViewModel.switchFilter(filterSetting = NavBarSection.GENRES)
            val searchChip: SearchChips = SearchChips.GENRES
            if (satunesUiState.genresFilter != satunesViewModel.selectedSearchChips.contains(
                    searchChip
                )
            ) {
                switchSearchChip(
                    satunesViewModel = satunesViewModel,
                    searchChip = searchChip,
                    selected = satunesUiState.genresFilter
                )
            }
        }

        SwitchSettings.FOLDERS_FILTER -> {
            satunesViewModel.switchFilter(filterSetting = NavBarSection.FOLDERS)
            val searchChip: SearchChips = SearchChips.FOLDERS
            if (satunesUiState.foldersChecked != satunesViewModel.selectedSearchChips.contains(
                    searchChip
                )
            ) {
                switchSearchChip(
                    satunesViewModel = satunesViewModel,
                    searchChip = searchChip,
                    selected = satunesUiState.foldersChecked
                )
            }
        }

        SwitchSettings.PLAYLISTS_FILTER -> {
            satunesViewModel.switchFilter(filterSetting = NavBarSection.PLAYLISTS)
            val searchChip: SearchChips = SearchChips.PLAYLISTS
            if (satunesUiState.playlistsFilter != satunesViewModel.selectedSearchChips.contains(
                    searchChip
                )
            ) {
                switchSearchChip(
                    satunesViewModel = satunesViewModel,
                    searchChip = searchChip,
                    selected = satunesUiState.playlistsFilter
                )
            }
        }

        else -> {
            /* Not a switch */
        }
    }
}

private fun switchSearchChip(
    satunesViewModel: SatunesViewModel,
    searchChip: SearchChips,
    selected: Boolean
) {
    if (selected) {
        satunesViewModel.unselect(searchChip = searchChip)
    } else {
        satunesViewModel.select(searchChip = searchChip)
    }
}