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

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import io.github.antoinepirlot.satunes.database.services.data.DataManager
import io.github.antoinepirlot.satunes.database.services.database.DatabaseManager
import io.github.antoinepirlot.satunes.icons.SatunesIcons
import io.github.antoinepirlot.satunes.router.utils.openCurrentMusic
import io.github.antoinepirlot.satunes.router.utils.openMedia
import io.github.antoinepirlot.satunes.services.MediaSelectionManager
import io.github.antoinepirlot.satunes.ui.components.buttons.ExtraButton
import io.github.antoinepirlot.satunes.ui.components.dialog.MediaSelectionDialog
import io.github.antoinepirlot.satunes.ui.components.texts.Title
import io.github.antoinepirlot.satunes.ui.viewmodels.PlaybackViewModel
import io.github.antoinepirlot.satunes.ui.views.media.MediaCollectionView
import io.github.antoinepirlot.satunes.database.R as RDb

/**
 * @author Antoine Pirlot on 01/04/2024
 */

@Composable
internal fun PlaylistView(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    playbackViewModel: PlaybackViewModel = viewModel(),
    playlist: Playlist,
) {
    //TODO try using nav controller instead try to remember it in an object if possible
    var openAddMusicsDialog: Boolean by rememberSaveable { mutableStateOf(false) }
    val musicSet: Set<Music> = playlist.getMusicSet()

    //Recompose if data changed
    var mapChanged: Boolean by rememberSaveable { playlist.musicSetUpdated }
    if (mapChanged) {
        mapChanged = false
    }
    //

    MediaCollectionView(
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
        val allMusic: Set<Music> = DataManager.getMusicSet()
        val context: Context = LocalContext.current
        MediaSelectionDialog(
            onDismissRequest = { openAddMusicsDialog = false },
            onConfirm = {
                val db = DatabaseManager(context = context)
                db.insertMusicsToPlaylist(
                    musics = MediaSelectionManager.getCheckedMusics(),
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