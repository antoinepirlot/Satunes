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

package io.github.antoinepirlot.satunes.ui.views.media.playlist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.jetpack_libs.models.JetpackLibsIcons
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.data.states.SubsonicUiState
import io.github.antoinepirlot.satunes.data.viewmodels.DataViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.SatunesViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.SubsonicViewModel
import io.github.antoinepirlot.satunes.ui.components.bars.ModeTabSelector
import io.github.antoinepirlot.satunes.ui.components.buttons.fab.ExtraButton
import io.github.antoinepirlot.satunes.ui.components.forms.PlaylistCreationForm
import io.github.antoinepirlot.satunes.ui.views.media.MediaListView

/**
 * @author Antoine Pirlot on 30/03/2024
 */

@Composable
internal fun PlaylistListView(
    modifier: Modifier = Modifier,
    satunesViewModel: SatunesViewModel = viewModel(),
    subsonicViewModel: SubsonicViewModel = viewModel(),
    dataViewModel: DataViewModel = viewModel(),
) {
    var openAlertDialog by remember { mutableStateOf(false) }

    val view: @Composable (isLoading: Boolean) -> Unit = { isLoading: Boolean ->
        MediaListView(
            modifier = modifier,
            canBeSorted = false,
            isLoading = isLoading,
            emptyViewText = stringResource(id = R.string.no_playlists),
        )
    }

    LaunchedEffect(key1 = Unit) {
        satunesViewModel.replaceExtraButtons {
            ExtraButton(
                jetpackLibsIcons = JetpackLibsIcons.EXPORT,
                onClick = {
                    dataViewModel.openExportPlaylistDialog()
                }
            )
            ExtraButton(
                jetpackLibsIcons = JetpackLibsIcons.IMPORT,
                onClick = { dataViewModel.openImportPlaylistDialog() }
            )
            ExtraButton(
                jetpackLibsIcons = JetpackLibsIcons.PLAYLIST_ADD,
                onClick = { openAlertDialog = true })
        }
    }

    ModeTabSelector(
        localView = {
            LaunchedEffect(key1 = Unit) {
                dataViewModel.loadMediaImplList(collection = dataViewModel.getPlaylistSet())
            }
            view(false)
        },
        cloudView = {
            val subsonicUiState: SubsonicUiState by subsonicViewModel.uiState.collectAsState()

            LaunchedEffect(key1 = Unit) {
                subsonicViewModel.getPlaylists(
                    onDataRetrieved = {
                        dataViewModel.loadMediaImplList(collection = it)
                    }
                )
            }
            view(subsonicUiState.isFetching)
        }
    )

    if (openAlertDialog) {
        PlaylistCreationForm(
            onConfirm = { openAlertDialog = false },
            onDismissRequest = { openAlertDialog = false }
        )
    }
}

@Preview
@Composable
private fun PlaylistListViewPreview() {
    PlaylistListView()
}