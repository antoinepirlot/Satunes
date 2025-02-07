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

package io.github.antoinepirlot.satunes.ui.views.media

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.satunes.data.states.SatunesUiState
import io.github.antoinepirlot.satunes.data.viewmodels.DataViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.SatunesViewModel
import io.github.antoinepirlot.satunes.database.models.Album
import io.github.antoinepirlot.satunes.database.models.Artist
import io.github.antoinepirlot.satunes.database.models.Folder
import io.github.antoinepirlot.satunes.database.models.Genre
import io.github.antoinepirlot.satunes.database.models.MediaImpl
import io.github.antoinepirlot.satunes.database.models.Music
import io.github.antoinepirlot.satunes.database.models.Playlist
import io.github.antoinepirlot.satunes.models.radio_buttons.SortOptions
import io.github.antoinepirlot.satunes.ui.components.EmptyView
import io.github.antoinepirlot.satunes.ui.components.cards.media.MediaCardList
import io.github.antoinepirlot.satunes.ui.components.dialog.SortListDialog

/**
 * @author Antoine Pirlot on 01/02/24
 */

@Composable
internal fun MediaListView(
    modifier: Modifier = Modifier,
    satunesViewModel: SatunesViewModel = viewModel(),
    dataViewModel: DataViewModel = viewModel(),
    mediaImplCollection: Collection<MediaImpl>,
    collectionChanged: Boolean = false,
    header: (@Composable () -> Unit)? = null,
    emptyViewText: String,
    showGroupIndication: Boolean = true,
    sort: Boolean = true,
) {
    val satunesUiState: SatunesUiState by satunesViewModel.uiState.collectAsState()
    val lazyListState = rememberLazyListState()
    val sortOption: SortOptions = dataViewModel.sortOption

    //Do not put the list to show in ui state as it will reset list scroll when user goes back to the last page
    //Plus this system insure the list can be scrolled correctly to the first item of the list (copying a new list make it not working as expected)
    val mediaImplListToShow: MutableList<MediaImpl> = remember { mutableStateListOf() }

    val isMediaOptionsOpened: Boolean = satunesUiState.mediaToShowOptions != null

    LaunchedEffect(key1 = sortOption, key2 = isMediaOptionsOpened, key3 = collectionChanged) {
        if (isMediaOptionsOpened) return@LaunchedEffect
        else if (collectionChanged) mediaImplListToShow.clear()
        else if (!dataViewModel.listSetUpdatedProcessed) {
            mediaImplListToShow.clear()
            dataViewModel.listSetUpdatedProcessed()
        } else if (mediaImplListToShow.isNotEmpty()) {
            lazyListState.scrollToItem(0)
            mediaImplListToShow.clear()
        }

        if (sort) {
            sort(
                satunesUiState = satunesUiState,
                lazyListState = lazyListState,
                source = mediaImplCollection,
                destination = mediaImplListToShow,
                sortOption = sortOption
            )
        } else {
            mediaImplListToShow.addAll(elements = mediaImplCollection)
        }
        dataViewModel.setMediaImplListOnScreen(mediaImplCollection = mediaImplListToShow)
    }

    if (satunesUiState.showSortDialog)
        SortListDialog()

    if (mediaImplCollection.isNotEmpty()) { //Prevent showing the empty view if the list is not empty
        MediaCardList(
            modifier = modifier,
            mediaImplList = mediaImplListToShow,
            lazyListState = lazyListState,
            header = header,
            showGroupIndication = showGroupIndication,
        )
    } else {
        EmptyView(
            modifier = modifier,
            header = header,
            text = emptyViewText
        )
    }
}

private suspend fun sort(
    satunesUiState: SatunesUiState,
    lazyListState: LazyListState,
    source: Collection<MediaImpl>,
    destination: MutableList<MediaImpl>,
    sortOption: SortOptions
) {
    if (destination.isEmpty()) {
        if (sortOption == SortOptions.PLAYLIST_ADDED_DATE) {
            val playlist: Playlist = satunesUiState.currentMediaImpl as Playlist
            destination.addAll(
                source.sortedBy { mediaImpl: MediaImpl ->
                    -(mediaImpl as Music).getOrder(playlist = playlist)
                }
            )
        } else if (sortOption.comparator != null) {
            destination.addAll(source.sortedWith(comparator = sortOption.comparator))
        }
    } else {
        if (sortOption == SortOptions.PLAYLIST_ADDED_DATE) {
            val playlist: Playlist = satunesUiState.currentMediaImpl as Playlist
            destination.sortBy { mediaImpl: MediaImpl ->
                -(mediaImpl as Music).getOrder(playlist = playlist)
            }
        } else if (sortOption.comparator != null) {
            destination.sortWith(comparator = sortOption.comparator)
        }
        lazyListState.scrollToItem(0)
    }
}

@Composable
@Preview
private fun MediaListViewPreview() {
    val map = listOf(
        Music(
            1,
            title = "title",
            displayName = "Musique",
            absolutePath = "",
            duration = 0,
            size = 0,
            folder = Folder(title = "Folder"),
            artist = Artist(title = "Artist Title"),
            album = Album(title = "Album Title", artist = Artist(title = "Artist Title")),
            genre = Genre(title = "Genre Title"),
        )
    )
    MediaListView(
        mediaImplCollection = map,
        collectionChanged = false,
        emptyViewText = "No data"
    )
}