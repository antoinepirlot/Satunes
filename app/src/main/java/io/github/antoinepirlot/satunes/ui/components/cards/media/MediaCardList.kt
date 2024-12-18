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

package io.github.antoinepirlot.satunes.ui.components.cards.media

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.satunes.data.states.DataUiState
import io.github.antoinepirlot.satunes.data.viewmodels.DataViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.PlaybackViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.SortListViewModel
import io.github.antoinepirlot.satunes.database.models.MediaImpl
import io.github.antoinepirlot.satunes.database.models.Playlist

/**
 * @author Antoine Pirlot on 16/01/24
 */

@Composable
internal fun MediaCardList(
    modifier: Modifier = Modifier,
//    lazyListState: LazyListState = rememberLazyListState(),
    dataViewModel: DataViewModel = viewModel(),
    playbackViewModel: PlaybackViewModel = viewModel(),
    sortListViewModel: SortListViewModel = viewModel(),
    header: @Composable (() -> Unit)? = null,
    mediaImplCollection: Collection<MediaImpl>, //TODO for v3.0.0 move the list into a view model
    openMedia: (mediaImpl: MediaImpl) -> Unit,
    openedPlaylist: Playlist? = null,
    scrollToMusicPlaying: Boolean = false,
) {
    val dataUiState: DataUiState by dataViewModel.uiState.collectAsState()
    if (dataUiState.mediaImplList.isEmpty()) return // It fixes issue while accessing last folder in chain
    val lazyListState = rememberLazyListState()
    dataViewModel.sortMediaImplListBy(sortOption = sortListViewModel.currentSortOption)

    LazyColumn(
        modifier = modifier,
        state = lazyListState
    ) {
        items(
            items = dataUiState.mediaImplList,
            key = { it.javaClass.name + '-' + it.id }
        ) { media: MediaImpl ->
            if (media == dataUiState.mediaImplList.first()) {
                if (header != null) {
                    header()
                }
            }
            MediaCard(
                modifier = modifier,
                media = media,
                onClick = { openMedia(media) },
                openedPlaylist = openedPlaylist,
            )
        }
    }

    if (scrollToMusicPlaying) {
        LaunchedEffect(key1 = Unit) {
            lazyListState.scrollToItem(
                playbackViewModel.getMusicPlayingIndexPosition()
            )
        }
    }
}

@Composable
@Preview
private fun CardListPreview() {
    MediaCardList(
        header = {},
        openMedia = {},
        openedPlaylist = null,
        scrollToMusicPlaying = false
    )
}