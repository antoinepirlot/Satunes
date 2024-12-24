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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import io.github.antoinepirlot.jetpack_libs.components.texts.Title
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.data.local.LocalNavController
import io.github.antoinepirlot.satunes.data.states.SatunesUiState
import io.github.antoinepirlot.satunes.data.viewmodels.PlaybackViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.SatunesViewModel
import io.github.antoinepirlot.satunes.database.daos.LIKES_PLAYLIST_TITLE
import io.github.antoinepirlot.satunes.database.models.MediaImpl
import io.github.antoinepirlot.satunes.database.models.Music
import io.github.antoinepirlot.satunes.database.models.Playlist
import io.github.antoinepirlot.satunes.router.utils.openMedia
import io.github.antoinepirlot.satunes.ui.components.EmptyView
import io.github.antoinepirlot.satunes.ui.components.buttons.fab.ExtraButtonList
import io.github.antoinepirlot.satunes.ui.components.buttons.fab.PlaylistExtraButtonList
import io.github.antoinepirlot.satunes.ui.views.media.MediaListView
import io.github.antoinepirlot.satunes.database.R as RDb

/**
 * @author Antoine Pirlot on 01/04/2024
 */

@Composable
internal fun PlaylistView(
    modifier: Modifier = Modifier,
    satunesViewModel: SatunesViewModel = viewModel(),
    playbackViewModel: PlaybackViewModel = viewModel(),
    playlist: Playlist,
) {
    val satunesUiState: SatunesUiState by satunesViewModel.uiState.collectAsState()
    val navController: NavHostController = LocalNavController.current
    val musicSet: Set<Music> = playlist.getMusicSet()

    //Recompose if data changed
    if (playlist.title == LIKES_PLAYLIST_TITLE) {
        //This prevent the modal of music option closing after unlike press because the music is removed from the playlist
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

    if (musicSet.isNotEmpty())
        satunesViewModel.replaceExtraButtons(
            extraButtons = {
                //It's in a column
                ExtraButtonList(mediaImplCollection = musicSet)
                PlaylistExtraButtonList(playlist = playlist)
            }
        )
    else
        satunesViewModel.replaceExtraButtons(
            extraButtons = {
                PlaylistExtraButtonList(playlist = playlist)
            }
        )

    MediaListView(
        modifier = modifier,
        mediaImplCollection = musicSet,
        openMedia = { clickedMediaImpl: MediaImpl ->
            playbackViewModel.loadMusics(
                musics = playlist.getMusicSet(),
                musicToPlay = clickedMediaImpl as Music
            )
            openMedia(
                playbackViewModel = playbackViewModel,
                media = clickedMediaImpl,
                navController = navController
            )
        },
        openedPlaylistWithMusics = playlist,
        header = {
            val title: String = if (playlist.title == LIKES_PLAYLIST_TITLE) {
                stringResource(id = RDb.string.likes_playlist_title)
            } else {
                playlist.title
            }
            Title(text = title)
            if (playlist.isEmpty()) //TODO reformat how it is implemented
                EmptyView(text = stringResource(R.string.no_music_in_playlist))
        },
        emptyViewText = stringResource(id = R.string.no_music_in_playlist)
    )
}

@Preview
@Composable
private fun PlaylistViewPreview() {
    PlaylistView(
        playlist = Playlist(id = 0, title = "PlaylistDB")
    )
}