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

package io.github.antoinepirlot.satunes.ui.components.forms.playlists

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.jetpack_libs.components.texts.NormalText
import io.github.antoinepirlot.satunes.data.states.DataUiState
import io.github.antoinepirlot.satunes.data.viewmodels.DataViewModel
import io.github.antoinepirlot.satunes.database.data.DEFAULT_ROOT_FILE_PATH
import io.github.antoinepirlot.satunes.icons.SatunesIcons
import io.github.antoinepirlot.satunes.models.SwitchSettings
import io.github.antoinepirlot.satunes.ui.components.settings.SettingWithSwitch

/**
 * @author Antoine Pirlot 17/08/2025
 */

@Composable
fun MusicsRootPathSelection(
    modifier: Modifier = Modifier,
    dataViewModel: DataViewModel = viewModel(),
) {
    val dataUiState: DataUiState by dataViewModel.uiState.collectAsState()

    Column(modifier = modifier) {
        SettingWithSwitch(
            modifier = modifier,
            setting = SwitchSettings.CHANGE_ROOT_PATH,
            icon = SatunesIcons.FOLDER,
            checked = dataUiState.changeFileRootPath,
            onCheckedChange = { dataViewModel.switchChangeFileRootPath() },
        )

        if (dataUiState.changeFileRootPath) {
            TextField(
                value = dataViewModel.rootPlaylistsFilesPath,
                onValueChange = { dataViewModel.updateRootPlaylistsFilesPath(newValue = it) },
                placeholder = { NormalText(text = DEFAULT_ROOT_FILE_PATH) },
            )
        }
    }
}

@Preview
@Composable
private fun MusicRootPathSelectionPreview() {
    MusicsRootPathSelection()
}