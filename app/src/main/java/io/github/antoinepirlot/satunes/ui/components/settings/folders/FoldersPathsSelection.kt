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

package io.github.antoinepirlot.satunes.ui.components.settings.folders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import io.github.antoinepirlot.jetpack_libs.components.texts.NormalText
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.data.states.SatunesUiState
import io.github.antoinepirlot.satunes.data.viewmodels.PlaybackViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.SatunesViewModel
import io.github.antoinepirlot.satunes.icons.SatunesIcons
import io.github.antoinepirlot.satunes.ui.components.buttons.ButtonWithIcon
import io.github.antoinepirlot.satunes.ui.components.buttons.IconButton

/**
 * @author Antoine Pirlot on 09/08/2024
 */

@Composable
internal fun FoldersPathsSelection(
    modifier: Modifier = Modifier,
    satunesViewModel: SatunesViewModel = viewModel(),
) {
    val satunesUiState: SatunesUiState by satunesViewModel.uiState.collectAsState()

    LazyColumn(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        items(
            items = satunesViewModel.foldersPathsSelectedSet.toList(),
            key = { it },
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                NormalText(
                    modifier = Modifier.fillMaxWidth(fraction = 0.8f),
                    text = it.removeSuffix("%")
                )
                IconButton(
                    icon = SatunesIcons.REMOVE_ICON,
                    onClick = { satunesViewModel.removePath(path = it) }
                )
            }

            if (it == satunesViewModel.foldersPathsSelectedSet.last()) {
                Footer()
            }
        }
    }
    if (satunesViewModel.foldersPathsSelectedSet.isEmpty()) {
        NormalText(text = stringResource(id = R.string.path_set_empty))
        Footer()
    }
}

@Composable
private fun Footer(
    modifier: Modifier = Modifier,
    satunesViewModel: SatunesViewModel = viewModel(),
    playbackViewModel: PlaybackViewModel = viewModel(),
) {
    val satunesUiState: SatunesUiState by satunesViewModel.uiState.collectAsState()
    val spacerSize: Dp = 5.dp

    Spacer(modifier = Modifier.size(spacerSize))

    //Show text "Add path to exclude/include".
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ButtonWithIcon(
            icon = SatunesIcons.ADD,
            onClick = { satunesViewModel.addPath() },
            text = stringResource(
                id = R.string.add_path_button,
                stringResource(
                    id = satunesUiState.foldersSelectionSelected.stringId
                ).lowercase()
            )
        )
        Spacer(modifier = Modifier.size(spacerSize))
        ButtonWithIcon(
            icon = SatunesIcons.REFRESH,
            onClick = {
                satunesViewModel.resetAllData(playbackViewModel = playbackViewModel)
                satunesViewModel.loadAllData()
            },
            enabled = !satunesViewModel.isLoadingData,
            isLoading = satunesViewModel.isLoadingData,
            text = stringResource(id = R.string.refresh_data_button)
        )
    }
    Spacer(modifier = Modifier.size(spacerSize))
}

@Preview
@Composable
private fun FoldersPathsSelectionPreview() {
    FoldersPathsSelection()
}

@Preview
@Composable
private fun FooterPreview() {
    Footer()
}