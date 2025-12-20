/*
 * This file is part of Satunes.
 *
 * Satunes is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * Satunes is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with Satunes.
 * If not, see <https://www.gnu.org/licenses/>.
 *
 * *** INFORMATION ABOUT THE AUTHOR *****
 * The author of this file is Antoine Pirlot, the owner of this project.
 * You find this original project on Codeberg.
 *
 * My Codeberg link is: https://codeberg.org/antoinepirlot
 * This current project's link is: https://codeberg.org/antoinepirlot/Satunes
 */

package io.github.antoinepirlot.satunes.ui.components.buttons.settings.library.folders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.jetpack_libs.components.buttons.ButtonWithIcon
import io.github.antoinepirlot.jetpack_libs.models.JetpackLibsIcons
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.data.states.FolderSelectionUiState
import io.github.antoinepirlot.satunes.data.viewmodels.FolderSelectionViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.PlaybackViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.SatunesViewModel

/**
 * @author Antoine Pirlot on 12/09/2024
 */

@Composable
internal fun FoldersPathButtons(
    modifier: Modifier = Modifier,
    satunesViewModel: SatunesViewModel = viewModel(),
    playbackViewModel: PlaybackViewModel = viewModel(),
    folderSelectionViewModel: FolderSelectionViewModel = viewModel()
) {
    val folderSelectionUiState: FolderSelectionUiState by folderSelectionViewModel.uiState.collectAsState()

    val spacerSize: Dp = 5.dp

    //Show text "Add path to exclude/include".
    Column(
        modifier = modifier.width(250.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        ButtonWithIcon(
            modifier = Modifier.fillMaxWidth(),
            jetpackLibsIcons = JetpackLibsIcons.ADD,
            onClick = { satunesViewModel.addPath(folderSelection = folderSelectionUiState.folderSelectionSelected) },
            text = stringResource(id = R.string.add_path_button)
        )

        Spacer(modifier = Modifier.size(spacerSize))

        ButtonWithIcon(
            modifier = Modifier.fillMaxWidth(),
            jetpackLibsIcons = JetpackLibsIcons.REFRESH,
            onClick = { satunesViewModel.reloadAllData(playbackViewModel = playbackViewModel) },
            enabled = !satunesViewModel.isLoadingData,
            isLoading = satunesViewModel.isLoadingData,
            text = stringResource(id = R.string.refresh_data_button)
        )
    }
}

@Preview
@Composable
private fun FoldersPathButtonsPreview() {
    FoldersPathButtons()
}