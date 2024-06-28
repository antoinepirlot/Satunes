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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.database.models.Media
import io.github.antoinepirlot.satunes.database.models.Music
import io.github.antoinepirlot.satunes.database.services.DataManager
import io.github.antoinepirlot.satunes.playback.services.PlaybackController
import io.github.antoinepirlot.satunes.router.utils.openMedia
import io.github.antoinepirlot.satunes.services.search.ChipSelectionManager
import io.github.antoinepirlot.satunes.ui.components.cards.media.MediaCardList
import io.github.antoinepirlot.satunes.ui.components.chips.MediaChipList
import io.github.antoinepirlot.satunes.ui.components.texts.NormalText
import io.github.antoinepirlot.satunes.database.R as RDb

/**
 * @author Antoine Pirlot on 27/06/2024
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SearchView(
    modifier: Modifier = Modifier
) {
    var query: String by rememberSaveable { mutableStateOf("") }
    var isSearchBarActive: Boolean by rememberSaveable { mutableStateOf(false) }
    val mediaList: MutableList<Media> = remember { SnapshotStateList() }
    val selectedChips: List<Int> = remember { ChipSelectionManager.selectedChips }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SearchBar(
            query = query,
            onQueryChange = {
                query = it
                mediaList.clear()
                // TODO make it more simple with each chip
                DataManager.musicMediaItemSortedMap.keys.forEach { music: Music ->
                    selectedChips.forEach { chipName: Int ->
                        when (chipName) {
                            RDb.string.musics -> {
                                if (music.title.lowercase().contains(query.lowercase())) {
                                    if (!mediaList.contains(music)) {
                                        mediaList.add(element = music)
                                    }
                                }
                            }

                            RDb.string.artists -> {
                                if (music.artist.title.lowercase().contains(query.lowercase())) {
                                    if (!mediaList.contains(music.artist)) {
                                        mediaList.add(element = music.artist)
                                    }
                                }
                            }

                            RDb.string.albums -> {
                                if (music.album.title.lowercase().contains(query.lowercase())) {
                                    if (!mediaList.contains(music.album)) {
                                        mediaList.add(element = music.album)
                                    }
                                }
                            }

                            RDb.string.genres -> {
                                if (music.genre.title.lowercase().contains(query.lowercase())) {
                                    if (!mediaList.contains(music.genre)) {
                                        mediaList.add(element = music.genre)
                                    }
                                }
                            }

                            RDb.string.folders -> {
                                if (music.folder.title.lowercase().contains(query.lowercase())) {
                                    if (!mediaList.contains(music.folder)) {
                                        mediaList.add(element = music.folder)
                                    }
                                }
                            }
                        }
                    }
                }
                mediaList.sort()
            },
            onSearch = {
                query = it
                isSearchBarActive = false
            },
            active = false,
            onActiveChange = { /* Do not use active mode */ },
            placeholder = { NormalText(text = stringResource(id = R.string.search_placeholder)) },
            content = { /* Content if active is true but never used */ }
        )
        Spacer(modifier = Modifier.size(16.dp))
        MediaChipList(modifier = Modifier.padding(horizontal = 16.dp))
        Content(mediaList = mediaList)
    }
}

@Composable
private fun Content(mediaList: List<Media>) {
    if (mediaList.isEmpty()) {
        NormalText(text = stringResource(id = R.string.no_result))
    } else {
        MediaCardList(mediaList = mediaList, openMedia = {
            PlaybackController.getInstance()
                .loadMusic(musicMediaItemSortedMap = DataManager.musicMediaItemSortedMap)
            openMedia(media = it)
        })
    }
}

@Preview
@Composable
private fun SearchViewPreview() {
    SearchView()
}