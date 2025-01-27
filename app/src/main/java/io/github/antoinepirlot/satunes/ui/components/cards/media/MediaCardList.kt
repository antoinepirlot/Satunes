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
 * You find this original project on github.
 *
 * My github link is: https://github.com/antoinepirlot
 * This current project's link is: https://github.com/antoinepirlot/Satunes
 *
 * PS: I don't answer quickly.
 */

package io.github.antoinepirlot.satunes.ui.components.cards.media

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.jetpack_libs.components.texts.Title
import io.github.antoinepirlot.satunes.data.viewmodels.DataViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.PlaybackViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.SortListViewModel
import io.github.antoinepirlot.satunes.database.models.Album
import io.github.antoinepirlot.satunes.database.models.Genre
import io.github.antoinepirlot.satunes.database.models.MediaImpl
import io.github.antoinepirlot.satunes.database.models.Music
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
    mediaImplCollection: Collection<MediaImpl>,
    header: @Composable (() -> Unit)? = null,
    openMedia: (mediaImpl: MediaImpl) -> Unit,
    openedPlaylist: Playlist? = null,
    scrollToMusicPlaying: Boolean = false,
    showGroupIndication: Boolean = true,
) {
    if (mediaImplCollection.isEmpty()) return // It fixes issue while accessing last folder in chain

    val sortOption: SortOptions = sortListViewModel.currentSortOption
    var mediaImplList: List<MediaImpl> = dataViewModel.sortMediaImplListBy(
        sortOption = sortOption,
        mediaImplList = mediaImplCollection
    )

    LaunchedEffect(key1 = sortListViewModel.currentSortOption) {
        CoroutineScope(Dispatchers.Main).launch {
            lazyListState.scrollToItem(0)
        }
    }

    LazyColumn(
        modifier = modifier,
        state = lazyListState
    ) {
        //Used to store dynamically the first media impl linked to the first occurrence of a letter or media impl.
        val groupMap: MutableMap<Any, MediaImpl>? =
            if (showGroupIndication) mutableMapOf() else null

        items(
            items = mediaImplList,
            key = { it.javaClass.name + '-' + it.id }
        ) { media: MediaImpl ->
            if (media == mediaImplList.first()) header?.invoke()

            if (showGroupIndication) {
                if (sortOption == SortOptions.GENRE) {
                    if (media is Music) {
                        FirstGenre(
                            map = groupMap!!,
                            mediaImpl = media,
                            mediaImplList = mediaImplList,
                        )
                    }
                } else {
                    FirstLetter(
                        map = groupMap!!,
                        mediaImpl = media,
                        mediaImplList = mediaImplList,
                        sortOption = sortOption
                    )
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

/**
 * Show the first letter of the media if it is the first occurrence in the list.
 *
 * @param map the map containing the Char as key and the [MediaImpl] as the value.
 * @param mediaImpl the [MediaImpl] used to be checked
 * @param mediaImplList the [List] of [MediaImpl] where to check the first occurrence.
 */
@Suppress("NAME_SHADOWING")
@Composable
private fun FirstLetter(
    map: MutableMap<Any, MediaImpl>,
    mediaImpl: MediaImpl,
    mediaImplList: List<MediaImpl>,
    sortOption: SortOptions
) {
    val titleToCompare: String =
        getTitleToCompare(mediaImpl = mediaImpl, sortOption = sortOption) ?: return
    val charToCompare: Char = Normalizer
        .normalize(titleToCompare.first().uppercase(), Normalizer.Form.NFD)
        .first()
    if (!map.containsKey(charToCompare)) {
        map[charToCompare] = mediaImplList.first { mediaImpl: MediaImpl ->
            val itTitle: String =
                getTitleToCompare(mediaImpl = mediaImpl, sortOption = sortOption) ?: return
            Normalizer.normalize(
                itTitle.first().uppercase(),
                Normalizer.Form.NFD
            ).first() == charToCompare
        }
    }
    if (mediaImpl == map[charToCompare]) {
        Title(
            modifier = Modifier.padding(vertical = 15.dp),
            bottomPadding = 0.dp,
            fontSize = 30.sp,
            textAlign = TextAlign.Center,
            text = charToCompare.toString()
        )
    }
}

private fun getTitleToCompare(mediaImpl: MediaImpl, sortOption: SortOptions): String? {
    return when (sortOption) {
        SortOptions.TITLE -> mediaImpl.title
        SortOptions.ALBUM -> if (mediaImpl is Music) mediaImpl.album.title else null
        SortOptions.ARTIST -> when (mediaImpl) {
            is Music -> mediaImpl.artist.title
            is Album -> mediaImpl.artist.title
            else -> null
        }

        else -> null
    }
}

/**
 * Show the first [Genre]'s title of the [MediaImpl] if it is the first occurrence in the list.
 *
 * @param map the map containing the [Genre] as key and the [MediaImpl] as the value.
 * @param mediaImpl the [MediaImpl] used to be checked
 * @param mediaImplList the [List] of [MediaImpl] where to check the first occurrence
 */
@Composable
private fun FirstGenre(
    map: MutableMap<Any, MediaImpl>,
    mediaImpl: Music,
    mediaImplList: List<MediaImpl>
) {
    val mediaImplToCompare: Genre = mediaImpl.genre
    if (!map.containsKey(mediaImplToCompare))
        map[mediaImplToCompare] = mediaImplList.first { (it as Music).genre == mediaImplToCompare }
    if (mediaImpl == map[mediaImplToCompare]) {
        Title(
            modifier = Modifier.padding(start = 34.dp),
            bottomPadding = 0.dp,
            fontSize = 30.sp,
            textAlign = TextAlign.Center,
            text = mediaImplToCompare.title
        )
    }
}

@Composable
@Preview
private fun CardListPreview() {
    MediaCardList(
        mediaImplCollection = listOf(),
        header = {},
        openMedia = {},
        openedPlaylist = null,
        scrollToMusicPlaying = false
    )
}