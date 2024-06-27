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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.database.models.Music
import io.github.antoinepirlot.satunes.database.services.DataManager
import io.github.antoinepirlot.satunes.playback.services.PlaybackController
import io.github.antoinepirlot.satunes.router.utils.openMedia
import io.github.antoinepirlot.satunes.ui.components.cards.media.MediaCardList
import io.github.antoinepirlot.satunes.ui.components.texts.NormalText

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
    val musicList: MutableList<Music> = remember { SnapshotStateList() }

    LaunchedEffect(key1 = true) {
        musicList.addAll(DataManager.musicMediaItemSortedMap.keys)
    }

    Column(modifier = modifier.fillMaxSize()) {
        SearchBar(
            query = query,
            onQueryChange = {
                query = it
                musicList.clear()
                musicList.addAll(
                    DataManager.musicMediaItemSortedMap.keys.filter { music: Music ->
                        music.title.lowercase().contains(query.lowercase())
                    }
                )
            },
            onSearch = {
                query = it
                isSearchBarActive = false
            },
            active = isSearchBarActive,
            onActiveChange = { isSearchBarActive = it },
            placeholder = { NormalText(text = stringResource(id = R.string.search_placeholder)) }
        ) {
            if (musicList.isEmpty()) {
                NormalText(text = stringResource(id = R.string.no_result))
            } else {
                MediaCardList(mediaList = musicList, openMedia = {
                    PlaybackController.getInstance()
                        .loadMusic(musicMediaItemSortedMap = DataManager.musicMediaItemSortedMap)
                    openMedia(media = it)
                })
            }
        }

        if (!isSearchBarActive) {
            // Also show result when user leave search bar focus
            if (musicList.isEmpty()) {
                NormalText(text = stringResource(id = R.string.no_result))
            } else {
                MediaCardList(mediaList = musicList, openMedia = {
                    PlaybackController.getInstance()
                        .loadMusic(musicMediaItemSortedMap = DataManager.musicMediaItemSortedMap)
                    openMedia(media = it)
                })
            }
        }
    }
}

@Preview
@Composable
private fun SearchViewPreview() {
    SearchView()
}