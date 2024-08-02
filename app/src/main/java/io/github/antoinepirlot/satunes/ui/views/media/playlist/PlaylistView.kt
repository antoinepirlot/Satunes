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

package io.github.antoinepirlot.satunes.ui.views.media.playlist

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.database.daos.LIKES_PLAYLIST_TITLE
import io.github.antoinepirlot.satunes.database.models.MediaImpl
import io.github.antoinepirlot.satunes.database.models.Music
import io.github.antoinepirlot.satunes.database.models.Playlist
import io.github.antoinepirlot.satunes.icons.SatunesIcons
import io.github.antoinepirlot.satunes.router.utils.openCurrentMusic
import io.github.antoinepirlot.satunes.router.utils.openMedia
import io.github.antoinepirlot.satunes.ui.components.buttons.ExtraButton
import io.github.antoinepirlot.satunes.ui.components.dialog.MediaSelectionDialog
import io.github.antoinepirlot.satunes.ui.components.texts.Title
import io.github.antoinepirlot.satunes.data.local.LocalMainScope
import io.github.antoinepirlot.satunes.data.local.LocalSnackBarHostState
import io.github.antoinepirlot.satunes.data.states.SatunesUiState
import io.github.antoinepirlot.satunes.data.viewmodels.DataViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.MediaSelectionViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.PlaybackViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.SatunesViewModel
import io.github.antoinepirlot.satunes.ui.views.media.MediaListView
import kotlinx.coroutines.CoroutineScope
import io.github.antoinepirlot.satunes.database.R as RDb

/**
 * @author Antoine Pirlot on 01/04/2024
 */

@Composable
internal fun PlaylistView(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    satunesViewModel: SatunesViewModel = viewModel(),
    dataViewModel: DataViewModel = viewModel(),
    mediaSelectionViewModel: MediaSelectionViewModel = viewModel(),
    playbackViewModel: PlaybackViewModel = viewModel(),
    playlist: Playlist,
) {
    val satunesUiState: SatunesUiState by satunesViewModel.uiState.collectAsState()
    val maineScope: CoroutineScope = LocalMainScope.current
    val snackBarHostState: SnackbarHostState = LocalSnackBarHostState.current

    //TODO try using nav controller instead try to remember it in an object if possible
    var openAddMusicsDialog: Boolean by rememberSaveable { mutableStateOf(false) }
    val musicSet: Set<Music> = playlist.getMusicSet()

    //Recompose if data changed
    if (playlist.title == LIKES_PLAYLIST_TITLE) {
        //This prevent the modal of music option closing after unlike press becuase the music is removed from the playlist
        if (!satunesUiState.isMediaOptionsOpened) {
            var mapChanged: Boolean by rememberSaveable { playlist.musicSetUpdated }
            if (mapChanged) {
                mapChanged = false
            }
        }
    } else {
        var mapChanged: Boolean by rememberSaveable { playlist.musicSetUpdated }
        if (mapChanged) {
            mapChanged = false
        }
    }
    //


    MediaListView(
        modifier = modifier,
        navController = navController,
        mediaImplCollection = musicSet,
        openMedia = { clickedMediaImpl: MediaImpl ->
            playbackViewModel.loadMusic(
                musicSet = playlist.getMusicSet(),
                musicToPlay = clickedMediaImpl as Music
            )
            openMedia(
                playbackViewModel = playbackViewModel,
                media = clickedMediaImpl,
                navController = navController
            )
        },
        openedPlaylistWithMusics = playlist,
        onFABClick = {
            openCurrentMusic(
                playbackViewModel = playbackViewModel,
                navController = navController
            )
        },
        header = {
            val title: String = if (playlist.title == LIKES_PLAYLIST_TITLE) {
                stringResource(id = RDb.string.likes_playlist_title)
            } else {
                playlist.title
            }
            Title(text = title)
        },
        extraButtons = {
            ExtraButton(icon = SatunesIcons.ADD, onClick = { openAddMusicsDialog = true })
            if (playlist.getMusicSet().isNotEmpty()) {
                ExtraButton(icon = SatunesIcons.PLAY, onClick = {
                    playbackViewModel.loadMusic(musicSet = musicSet)
                    openMedia(playbackViewModel = playbackViewModel, navController = navController)
                })
                ExtraButton(icon = SatunesIcons.SHUFFLE, onClick = {
                    playbackViewModel.loadMusic(
                        musicSet = musicSet,
                        shuffleMode = true
                    )
                    openMedia(playbackViewModel = playbackViewModel, navController = navController)
                })
            }
        },
        emptyViewText = stringResource(id = R.string.no_music_in_playlist)
    )
    if (openAddMusicsDialog) {
        val allMusic: Set<Music> = dataViewModel.getMusicSet()
        MediaSelectionDialog(
            onDismissRequest = { openAddMusicsDialog = false },
            onConfirm = {
                dataViewModel.insertMusicsToPlaylist(
                    scope = maineScope,
                    snackBarHostState = snackBarHostState,
                    musics = mediaSelectionViewModel.getCheckedMusics(),
                    playlist = playlist
                )
                openAddMusicsDialog = false
            },
            mediaImplCollection = allMusic,
            icon = SatunesIcons.PLAYLIST_ADD,
            playlistTitle = playlist.title
        )
    }
}

@Preview
@Composable
private fun PlaylistViewPreview() {
    val navController: NavHostController = rememberNavController()
    PlaylistView(
        navController = navController,
        playlist = Playlist(id = 0, title = "PlaylistDB")
    )
}