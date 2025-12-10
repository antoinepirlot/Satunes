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

package io.github.antoinepirlot.satunes.ui.components.buttons.playback.custom_actions

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.jetpack_libs.models.JetpackLibsIcons
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.data.local.LocalSnackBarHostState
import io.github.antoinepirlot.satunes.data.states.SatunesUiState
import io.github.antoinepirlot.satunes.data.viewmodels.DataViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.MediaSelectionViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.SatunesViewModel
import io.github.antoinepirlot.satunes.database.models.media.Music
import io.github.antoinepirlot.satunes.database.models.media.Playlist
import io.github.antoinepirlot.satunes.ui.components.dialog.MediaSelectionDialog
import kotlinx.coroutines.CoroutineScope

/**
 * @author Antoine Pirlot on 01/06/2024
 */

@Composable
internal fun AddToPlaylistCustomAction(
    modifier: Modifier = Modifier,
    satunesViewModel: SatunesViewModel = viewModel(),
    dataViewModel: DataViewModel = viewModel(),
    mediaSelectionViewModel: MediaSelectionViewModel = viewModel(),
    music: Music,
) {
    val satunesUiState: SatunesUiState by satunesViewModel.uiState.collectAsState()
    val snackBarHostState: SnackbarHostState = LocalSnackBarHostState.current
    val scope: CoroutineScope = rememberCoroutineScope()

    CustomActionButton(
        modifier = modifier,
        jetpackLibsIcons = JetpackLibsIcons.PLAYLIST_ADD,
        text = stringResource(id = R.string.add_to_playlist),
        onClick = { satunesViewModel.showMediaSelectionDialog() }
    )

    if (satunesUiState.showMediaSelectionDialog) {
        val playlistMap: Set<Playlist> = dataViewModel.getPlaylistSet()

        //Recompose if data changed
        val mapChanged: Boolean = dataViewModel.playlistSetUpdated
        if (mapChanged) {
            dataViewModel.playlistSetUpdated()
            dataViewModel.listSetUpdatedUnprocessed()
        }
        //

        MediaSelectionDialog(
            onDismissRequest = { satunesViewModel.hideMediaSelectionDialog() },
            onConfirm = {
                addMusicPlayingToPlaylist(
                    scope = scope,
                    snackBarHostState = snackBarHostState,
                    dataViewModel = dataViewModel,
                    checkedPlaylists = mediaSelectionViewModel.getCheckedPlaylistWithMusics(),
                    music = music
                )
                satunesViewModel.hideMediaSelectionDialog()
            },
            mediaImplCollection = playlistMap,
            mediaDestination = music,
            jetpackLibsIcons = JetpackLibsIcons.PLAYLIST_ADD
        )
    }
}

private fun addMusicPlayingToPlaylist(
    scope: CoroutineScope,
    snackBarHostState: SnackbarHostState,
    dataViewModel: DataViewModel,
    checkedPlaylists: List<Playlist>,
    music: Music,
) {
    dataViewModel.updateMusicPlaylist(
        scope = scope,
        snackBarHostState = snackBarHostState,
        music = music,
        playlists = checkedPlaylists
    )
}