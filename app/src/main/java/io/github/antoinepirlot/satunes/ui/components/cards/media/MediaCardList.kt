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

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.jetpack_libs.components.texts.Title
import io.github.antoinepirlot.satunes.data.states.DataUiState
import io.github.antoinepirlot.satunes.data.viewmodels.DataViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.PlaybackViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.SortListViewModel
import io.github.antoinepirlot.satunes.database.models.MediaImpl
import io.github.antoinepirlot.satunes.database.models.Playlist
import io.github.antoinepirlot.satunes.models.radio_buttons.SortOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.Normalizer

/**
 * @author Antoine Pirlot on 16/01/24
 */

@Composable
internal fun MediaCardList(
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    dataViewModel: DataViewModel = viewModel(),
    playbackViewModel: PlaybackViewModel = viewModel(),
    sortListViewModel: SortListViewModel = viewModel(),
    header: @Composable (() -> Unit)? = null,
    openMedia: (mediaImpl: MediaImpl) -> Unit,
    openedPlaylist: Playlist? = null,
    scrollToMusicPlaying: Boolean = false,
) {
    val dataUiState: DataUiState by dataViewModel.uiState.collectAsState()
    val mediaImplList: List<MediaImpl> = dataUiState.mediaImplList
    if (mediaImplList.isEmpty()) return // It fixes issue while accessing last folder in chain
    val sortOption: SortOptions = dataViewModel.currentSortOption
    if (sortListViewModel.currentSortOption != sortOption)
        dataViewModel.sortMediaImplListBy(sortOption = sortListViewModel.currentSortOption)

    LaunchedEffect(key1 = dataViewModel.currentSortOption) {
        CoroutineScope(Dispatchers.Main).launch {
            lazyListState.scrollToItem(0)
        }
    }

    LazyColumn(
        modifier = modifier,
        state = lazyListState
    ) {
        //Use to store dynamically the first media impl linked to the first occurrence of a letter.
        val letterMediaImplMap: MutableMap<Char, MediaImpl> = mutableMapOf()
        items(
            items = mediaImplList,
            key = { it.javaClass.name + '-' + it.id }
        ) { media: MediaImpl ->
            if (media == mediaImplList.first()) header?.invoke()

            /* Show the letter of the first mediaImpl containing it */
            val charToCompare: Char =
                Normalizer.normalize(media.title.first().uppercase(), Normalizer.Form.NFD).first()
            if (!letterMediaImplMap.contains(charToCompare)) {
                letterMediaImplMap[charToCompare] = mediaImplList.first {
                    Normalizer.normalize(
                        it.title.first().uppercase(),
                        Normalizer.Form.NFD
                    ).first() == charToCompare
                }
            }
            if (media == letterMediaImplMap.getValue(key = charToCompare)) {
                Title(
                    modifier = Modifier.padding(start = 34.dp),
                    bottomPadding = 0.dp,
                    fontSize = 30.sp,
                    textAlign = TextAlign.Left,
                    text = charToCompare.toString()
                )
            }
            /* End of first letter */

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