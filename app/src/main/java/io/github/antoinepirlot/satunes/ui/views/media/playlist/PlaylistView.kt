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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.jetpack_libs.components.texts.Title
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.data.states.SubsonicUiState
import io.github.antoinepirlot.satunes.data.viewmodels.DataViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.SatunesViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.SubsonicViewModel
import io.github.antoinepirlot.satunes.database.daos.LIKES_PLAYLIST_TITLE
import io.github.antoinepirlot.satunes.database.models.media.Playlist
import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicPlaylist
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
    dataViewModel: DataViewModel = viewModel(),
    subsonicViewModel: SubsonicViewModel = viewModel(),
    playlist: Playlist,
) {
    val subsonicUiState: SubsonicUiState by subsonicViewModel.uiState.collectAsState()
    val isLoading: Boolean = subsonicUiState.isFetching

    LaunchedEffect(key1 = Unit) {
        if (playlist.isSubsonic())
            subsonicViewModel.loadPlaylistMusics(id = (playlist as SubsonicPlaylist).subsonicId)
    }

    LaunchedEffect(key1 = playlist.musicCollection.size) {
        if (!isLoading)
            dataViewModel.loadMediaImplList(collection = playlist.musicCollection)
    }

    LaunchedEffect(key1 = dataViewModel.mediaListOnScreen.size) {
        if (dataViewModel.mediaListOnScreen.isNotEmpty())
            satunesViewModel.replaceExtraButtons(
                extraButtons = {
                    //It's in a column
                    PlaylistExtraButtonList(playlist = playlist)
                    ExtraButtonList()
                }
            )
        else
            satunesViewModel.replaceExtraButtons(
                extraButtons = {
                    PlaylistExtraButtonList(playlist = playlist)
                }
            )
    }

    MediaListView(
        modifier = modifier,
        header = {
            val title: String = if (playlist.title == LIKES_PLAYLIST_TITLE) {
                stringResource(id = RDb.string.likes_playlist_title)
            } else {
                playlist.title
            }
            Title(text = title)
        },
        isLoading = isLoading,
        emptyViewText = stringResource(R.string.no_music_in_playlist)
    )
}

@Preview
@Composable
private fun PlaylistViewPreview() {
    PlaylistView(
        playlist = Playlist(id = 0, title = "PlaylistDB")
    )
}