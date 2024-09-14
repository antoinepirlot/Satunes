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
import io.github.antoinepirlot.satunes.data.states.SearchUiState
import io.github.antoinepirlot.satunes.data.viewmodels.SatunesViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.SearchViewModel
import io.github.antoinepirlot.satunes.database.models.NavBarSection
import io.github.antoinepirlot.satunes.models.SearchChips
import io.github.antoinepirlot.satunes.models.SwitchSettings
import io.github.antoinepirlot.satunes.ui.components.cards.ListItem

/**
 *   @author Antoine Pirlot 06/03/2024
 */

@Composable
internal fun SettingsSwitchList(
    modifier: Modifier = Modifier,
    satunesViewModel: SatunesViewModel = viewModel(),
    searchViewModel: SearchViewModel = viewModel(),
    checkedMap: Map<SwitchSettings, Boolean>,
) {
    val searchUiState: SearchUiState by searchViewModel.uiState.collectAsState()
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
                                searchViewModel = searchViewModel,
                                searchUiState = searchUiState,
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

//TODO remove this way as it adds complexity. Keep things simple
private fun switchSetting(
    satunesViewModel: SatunesViewModel,
    searchViewModel: SearchViewModel,
    searchUiState: SearchUiState,
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

        SwitchSettings.PAUSE_IF_ANOTHER_PLAYBACK -> {
            satunesViewModel.switchPauseIfAnotherPlayback()
        }

        SwitchSettings.MUSICS_FILTER -> {
            searchViewModel.switchFilter(filterSetting = NavBarSection.MUSICS)
            val searchChip: SearchChips = SearchChips.MUSICS
            if (searchUiState.musicsFilter != searchViewModel.selectedSearchChips.contains(
                    searchChip
                )
            ) {
                switchSearchChip(
                    searchViewModel = searchViewModel,
                    searchChip = searchChip,
                    selected = searchUiState.musicsFilter
                )
            }
        }

        SwitchSettings.ARTISTS_FILTER -> {
            searchViewModel.switchFilter(filterSetting = NavBarSection.ARTISTS)
            val searchChip: SearchChips = SearchChips.ARTISTS
            if (searchUiState.artistsFilter != searchViewModel.selectedSearchChips.contains(
                    searchChip
                )
            ) {
                switchSearchChip(
                    searchViewModel = searchViewModel,
                    searchChip = searchChip,
                    selected = searchUiState.artistsFilter
                )
            }
        }

        SwitchSettings.ALBUMS_FILTER -> {
            searchViewModel.switchFilter(filterSetting = NavBarSection.ALBUMS)
            val searchChip: SearchChips = SearchChips.ALBUMS
            if (searchUiState.albumsFilter != searchViewModel.selectedSearchChips.contains(
                    searchChip
                )
            ) {
                switchSearchChip(
                    searchViewModel = searchViewModel,
                    searchChip = searchChip,
                    selected = searchUiState.albumsFilter
                )
            }
        }

        SwitchSettings.GENRES_FILTER -> {
            searchViewModel.switchFilter(filterSetting = NavBarSection.GENRES)
            val searchChip: SearchChips = SearchChips.GENRES
            if (searchUiState.genresFilter != searchViewModel.selectedSearchChips.contains(
                    searchChip
                )
            ) {
                switchSearchChip(
                    searchViewModel = searchViewModel,
                    searchChip = searchChip,
                    selected = searchUiState.genresFilter
                )
            }
        }

        SwitchSettings.FOLDERS_FILTER -> {
            searchViewModel.switchFilter(filterSetting = NavBarSection.FOLDERS)
            val searchChip: SearchChips = SearchChips.FOLDERS
            if (searchUiState.foldersFilter != searchViewModel.selectedSearchChips.contains(
                    searchChip
                )
            ) {
                switchSearchChip(
                    searchViewModel = searchViewModel,
                    searchChip = searchChip,
                    selected = searchUiState.foldersFilter
                )
            }
        }

        SwitchSettings.PLAYLISTS_FILTER -> {
            searchViewModel.switchFilter(filterSetting = NavBarSection.PLAYLISTS)
            val searchChip: SearchChips = SearchChips.PLAYLISTS
            if (searchUiState.playlistsFilter != searchViewModel.selectedSearchChips.contains(
                    searchChip
                )
            ) {
                switchSearchChip(
                    searchViewModel = searchViewModel,
                    searchChip = searchChip,
                    selected = searchUiState.playlistsFilter
                )
            }
        }

        else -> {
            /* Not a switch */
        }
    }
}

private fun switchSearchChip(
    searchViewModel: SearchViewModel,
    searchChip: SearchChips,
    selected: Boolean
) {
    if (selected) {
        searchViewModel.unselect(searchChip = searchChip)
    } else {
        searchViewModel.select(searchChip = searchChip)
    }
}