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

package io.github.antoinepirlot.satunes.ui.components.buttons.playback.custom_actions

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.data.local.LocalSnackBarHostState
import io.github.antoinepirlot.satunes.data.viewmodels.DataViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.MediaSelectionViewModel
import io.github.antoinepirlot.satunes.database.models.Music
import io.github.antoinepirlot.satunes.database.models.Playlist
import io.github.antoinepirlot.satunes.icons.SatunesIcons
import io.github.antoinepirlot.satunes.ui.components.dialog.MediaSelectionDialog
import kotlinx.coroutines.CoroutineScope

/**
 * @author Antoine Pirlot on 01/06/2024
 */

@Composable
internal fun AddToPlaylistCustomAction(
    modifier: Modifier = Modifier,
    dataViewModel: DataViewModel = viewModel(),
    mediaSelectionViewModel: MediaSelectionViewModel = viewModel(),
    music: Music,
) {
    val snackBarHostState: SnackbarHostState = LocalSnackBarHostState.current
    val scope: CoroutineScope = rememberCoroutineScope()
    var showForm: Boolean by rememberSaveable { mutableStateOf(false) }

    CustomActionButton(
        modifier = modifier,
        icon = SatunesIcons.PLAYLIST_ADD,
        text = stringResource(id = R.string.add_to_playlist),
        onClick = { showForm = true }
    )

    if (showForm) {
        val playlistMap: Set<Playlist> = dataViewModel.getPlaylistSet()

        //Recompose if data changed
        val mapChanged: Boolean = dataViewModel.playlistSetUpdated
        if (mapChanged) {
            dataViewModel.playlistSetUpdated()
        }
        //

        MediaSelectionDialog(
            onDismissRequest = { showForm = false },
            onConfirm = {
                addMusicPlayingToPlaylist(
                    scope = scope,
                    snackBarHostState = snackBarHostState,
                    dataViewModel = dataViewModel,
                    checkedPlaylists = mediaSelectionViewModel.getCheckedPlaylistWithMusics(),
                    music = music
                )
                showForm = false
            },
            mediaImplCollection = playlistMap,
            icon = SatunesIcons.PLAYLIST_ADD
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
    dataViewModel.insertMusicToPlaylists(
        scope = scope,
        snackBarHostState = snackBarHostState,
        music = music,
        playlists = checkedPlaylists
    )
}