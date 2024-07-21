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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.models.SwitchSettings
import io.github.antoinepirlot.satunes.ui.states.SatunesUiState
import io.github.antoinepirlot.satunes.ui.viewmodels.SatunesViewModel

/**
 * @author Antoine Pirlot on 08/07/2024
 */



@Composable
internal fun DefaultSearchFiltersSetting(
    modifier: Modifier = Modifier,
    satunesViewModel: SatunesViewModel = viewModel()
) {
    val satunesUiState: SatunesUiState by satunesViewModel.uiState.collectAsState()

    val filterSettingsChecked: Map<SwitchSettings, Boolean> = mapOf(
        Pair(SwitchSettings.MUSICS_FILTER, satunesUiState.musicsFilter),
        Pair(SwitchSettings.ALBUMS_FILTER, satunesUiState.albumsFilter),
        Pair(SwitchSettings.ARTISTS_FILTER, satunesUiState.artistsFilter),
        Pair(SwitchSettings.GENRES_FILTER, satunesUiState.genresFilter),
        Pair(SwitchSettings.FOLDERS_FILTER, satunesUiState.foldersFilter),
        Pair(SwitchSettings.PLAYLISTS_FILTER, satunesUiState.playlistsFilter)
    )

    SubSettings(
        modifier = modifier,
        title = stringResource(id = R.string.default_filter_setting_title)
    ) {
        SettingsSwitchList(checkedMap = filterSettingsChecked)
    }
}

@Preview
@Composable
private fun DefaultSearchFiltersSettingPreview() {
    DefaultSearchFiltersSetting()
}