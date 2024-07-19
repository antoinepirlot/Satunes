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

package io.github.antoinepirlot.satunes.ui.views.search

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.database.daos.LIKES_PLAYLIST_TITLE
import io.github.antoinepirlot.satunes.database.models.MediaImpl
import io.github.antoinepirlot.satunes.database.models.Music
import io.github.antoinepirlot.satunes.database.models.Playlist
import io.github.antoinepirlot.satunes.database.services.data.DataManager
import io.github.antoinepirlot.satunes.models.SearchChips
import io.github.antoinepirlot.satunes.router.utils.openCurrentMusic
import io.github.antoinepirlot.satunes.router.utils.openMedia
import io.github.antoinepirlot.satunes.services.search.SearchChipsManager
import io.github.antoinepirlot.satunes.ui.components.chips.MediaChipList
import io.github.antoinepirlot.satunes.ui.components.texts.NormalText
import io.github.antoinepirlot.satunes.ui.viewmodels.PlaybackViewModel
import io.github.antoinepirlot.satunes.ui.views.media.MediaListView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import io.github.antoinepirlot.satunes.database.R as RDb

/**
 * @author Antoine Pirlot on 27/06/2024
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SearchView(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    playbackViewModel: PlaybackViewModel = PlaybackViewModel(context = LocalContext.current),
) {
    val context: Context = LocalContext.current
    var query: String by rememberSaveable { mutableStateOf("") }
    val mediaImplList: MutableList<MediaImpl> = remember { SnapshotStateList() }
    val selectedSearchChips: List<SearchChips> = remember { SearchChipsManager.selectedSearchChips }

    val searchCoroutine: CoroutineScope = rememberCoroutineScope()
    var searchJob: Job? = null
    LaunchedEffect(key1 = query, key2 = selectedSearchChips.size) {
        if (searchJob != null && searchJob!!.isActive) {
            searchJob!!.cancel()
        }
        searchJob = searchCoroutine.launch {
            search(context = context, mediaImplList = mediaImplList, query = query)
        }
    }

    var resetSelectedChips: Boolean by rememberSaveable { mutableStateOf(true) }
    if (resetSelectedChips) {
        LaunchedEffect(key1 = true) {
            SearchChipsManager.resetSelectedChips(context = context)
        }
        resetSelectedChips = false
    }

    val focusRequester: FocusRequester = remember { FocusRequester() }
    LaunchedEffect(key1 = true) {
        // Request focus after composable becomes visible
        focusRequester.requestFocus()
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SearchBar(
            modifier = Modifier.focusRequester(focusRequester),
            query = query,
            onQueryChange = { query = it },
            onSearch = { query = it },
            active = false,
            onActiveChange = { /* Do not use active mode */ },
            placeholder = { NormalText(text = stringResource(id = R.string.search_placeholder)) },
            content = { /* Content if active is true but never used */ }
        )
        Spacer(modifier = Modifier.size(16.dp))
        MediaChipList()
        MediaListView(
            navController = navController,
            mediaImplList = mediaImplList,
            openMedia = { mediaImpl: MediaImpl ->
                if (mediaImpl is Music) {
                    playbackViewModel.loadMusic(musicMediaItemSortedMap = DataManager.getMusicMap())
                }
                openMedia(
                    playbackViewModel = playbackViewModel,
                    media = mediaImpl,
                    navController = navController
                )
            },
            onFABClick = {
                openCurrentMusic(
                    playbackViewModel = playbackViewModel,
                    navController = navController
                )
            },
            emptyViewText = stringResource(id = R.string.no_result)
        )
    }
}

private fun search(context: Context, mediaImplList: MutableList<MediaImpl>, query: String) {
    mediaImplList.clear()
    if (query.isBlank()) {
        // Prevent loop if string is "" or " "
        return
    }

    @Suppress("NAME_SHADOWING")
    val query: String = query.lowercase()

    for (searchChip: SearchChips in SearchChipsManager.selectedSearchChips) {
        DataManager.getMusicMap().keys.forEach { music: Music ->
            when (searchChip) {
                SearchChips.MUSICS -> {
                    if (music.title.lowercase().contains(query)) {
                        if (!mediaImplList.contains(music)) {
                            mediaImplList.add(element = music)
                        }
                    }
                }

                SearchChips.ARTISTS -> {
                    if (music.artist.title.lowercase().contains(query)) {
                        if (!mediaImplList.contains(music.artist)) {
                            mediaImplList.add(element = music.artist)
                        }
                    }
                }

                SearchChips.ALBUMS -> {
                    if (music.album.title.lowercase().contains(query)) {
                        if (!mediaImplList.contains(music.album)) {
                            mediaImplList.add(element = music.album)
                        }
                    }
                }

                SearchChips.GENRES -> {
                    if (music.genre.title.lowercase().contains(query)) {
                        if (!mediaImplList.contains(music.genre)) {
                            mediaImplList.add(element = music.genre)
                        }
                    }
                }

                SearchChips.FOLDERS -> {
                    if (music.folder.title.lowercase().contains(query)) {
                        if (!mediaImplList.contains(music.folder)) {
                            mediaImplList.add(element = music.folder)
                        }
                    }
                }

                SearchChips.PLAYLISTS -> { /* Nothing at this stage, see below */
                }
            }
        }
        if (searchChip == SearchChips.PLAYLISTS) {
            DataManager.getPlaylistMap().forEach { (playlistTitle: String, playlist: Playlist) ->
                @Suppress("NAME_SHADOWING")
                var playlistTitle: String = playlistTitle

                if (playlistTitle == LIKES_PLAYLIST_TITLE) {
                    playlistTitle = context.getString(RDb.string.likes_playlist_title)
                }
                if (playlistTitle.lowercase().contains(query)) {
                    mediaImplList.add(element = playlist)
                }
            }
        }
    }
    mediaImplList.sort()
}

@Preview
@Composable
private fun SearchViewPreview() {
    val navController: NavHostController = rememberNavController()
    SearchView(navController = navController)
}