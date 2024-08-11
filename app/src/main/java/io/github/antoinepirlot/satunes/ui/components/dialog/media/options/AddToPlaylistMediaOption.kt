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

package io.github.antoinepirlot.satunes.ui.components.dialog.media.options

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.data.local.LocalMainScope
import io.github.antoinepirlot.satunes.data.local.LocalSnackBarHostState
import io.github.antoinepirlot.satunes.data.viewmodels.DataViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.MediaSelectionViewModel
import io.github.antoinepirlot.satunes.database.models.Folder
import io.github.antoinepirlot.satunes.database.models.MediaImpl
import io.github.antoinepirlot.satunes.database.models.Music
import io.github.antoinepirlot.satunes.database.models.Playlist
import io.github.antoinepirlot.satunes.icons.SatunesIcons
import io.github.antoinepirlot.satunes.ui.components.dialog.MediaSelectionDialog
import io.github.antoinepirlot.satunes.ui.components.dialog.options.DialogOption
import kotlinx.coroutines.CoroutineScope

/**
 * @author Antoine Pirlot on 01/06/2024
 */

@Composable
internal fun AddToPlaylistMediaOption(
    modifier: Modifier = Modifier,
    dataViewModel: DataViewModel = viewModel(),
    mediaSelectionViewModel: MediaSelectionViewModel = viewModel(),
    mediaImpl: MediaImpl,
    onFinished: () -> Unit
) {
    val scope: CoroutineScope = LocalMainScope.current
    val snackBarHostState: SnackbarHostState = LocalSnackBarHostState.current
    var showDialog: Boolean by rememberSaveable { mutableStateOf(false) }

    DialogOption(
        modifier = modifier,
        onClick = { showDialog = true },
        icon = SatunesIcons.PLAYLIST_ADD,
        text = stringResource(id = R.string.add_to_playlist)
    )
    if (showDialog) {
        val playlistSet: Set<Playlist> = dataViewModel.getPlaylistSet()
        //Recompose if data changed
        val mapChanged: Boolean = dataViewModel.playlistSetUpdated
        if (mapChanged) {
            dataViewModel.playlistSetUpdated()
        }
        //

        MediaSelectionDialog(
            onDismissRequest = {
                showDialog = false
            },
            onConfirm = {
                insertMediaToPlaylist(
                    scope = scope,
                    snackBarHostState = snackBarHostState,
                    dataViewModel = dataViewModel,
                    mediaSelectionViewModel = mediaSelectionViewModel,
                    mediaImpl = mediaImpl
                )
                onFinished()
            },
            mediaImplCollection = playlistSet,
            icon = SatunesIcons.PLAYLIST_ADD,
        )
    }
}

private fun insertMediaToPlaylist(
    dataViewModel: DataViewModel,
    scope: CoroutineScope,
    snackBarHostState: SnackbarHostState,
    mediaSelectionViewModel: MediaSelectionViewModel,
    mediaImpl: MediaImpl
) {
    if (mediaImpl is Music) {
        dataViewModel.insertMusicToPlaylists(
            scope = scope,
            snackBarHostState = snackBarHostState,
            music = mediaImpl,
            playlists = mediaSelectionViewModel.getCheckedPlaylistWithMusics()
        )
    } else {
        val musicList: Set<Music> = if (mediaImpl is Folder) {
            mediaImpl.getAllMusic()
        } else {
            mediaImpl.getMusicSet()
        }

        dataViewModel.insertMusicsToPlaylists(
            scope = scope,
            snackBarHostState = snackBarHostState,
            musics = musicList,
            playlists = mediaSelectionViewModel.getCheckedPlaylistWithMusics()
        )

    }
}