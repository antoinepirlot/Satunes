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

package io.github.antoinepirlot.satunes.ui.components.buttons.fab

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.jetpack_libs.models.JetpackLibsIcons
import io.github.antoinepirlot.satunes.data.local.LocalMainScope
import io.github.antoinepirlot.satunes.data.local.LocalSnackBarHostState
import io.github.antoinepirlot.satunes.data.states.SatunesUiState
import io.github.antoinepirlot.satunes.data.viewmodels.DataViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.MediaSelectionViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.SatunesViewModel
import io.github.antoinepirlot.satunes.database.models.media.Playlist
import io.github.antoinepirlot.satunes.ui.components.dialog.MediaSelectionDialog
import kotlinx.coroutines.CoroutineScope

@Composable
internal fun PlaylistExtraButtonList(
    modifier: Modifier = Modifier,
    satunesViewModel: SatunesViewModel = viewModel(),
    dataViewModel: DataViewModel = viewModel(),
    mediaSelectionViewModel: MediaSelectionViewModel = viewModel(),
    playlist: Playlist,
) {
    val satunesUiState: SatunesUiState by satunesViewModel.uiState.collectAsState()
    val scope: CoroutineScope = LocalMainScope.current
    val snackbarHostState: SnackbarHostState = LocalSnackBarHostState.current

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ExtraButton(
            jetpackLibsIcons = JetpackLibsIcons.EXPORT,
            onClick = { dataViewModel.openExportPlaylistDialog(playlist = playlist) }
        )
        ExtraButton(
            jetpackLibsIcons = JetpackLibsIcons.ADD,
            onClick = { satunesViewModel.showMediaSelectionDialog() },
        )
    }

    if (satunesUiState.showMediaSelectionDialog) {
        MediaSelectionDialog(
            onDismissRequest = { satunesViewModel.hideMediaSelectionDialog() },
            onConfirm = {
                dataViewModel.updatePlaylistMusics(
                    scope = scope,
                    snackBarHostState = snackbarHostState,
                    musics = mediaSelectionViewModel.getCheckedMusics(),
                    playlist = playlist
                )
                satunesViewModel.hideMediaSelectionDialog()
            },
            mediaImplCollection = dataViewModel.getMusicSet(),
            jetpackLibsIcons = JetpackLibsIcons.PLAYLIST_ADD,
            mediaDestination = playlist,
            playlistTitle = playlist.title
        )
    }
}