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

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
    var initialisation: Boolean by rememberSaveable { mutableStateOf(true) }
    var sortOptionChanged: Boolean by rememberSaveable { mutableStateOf(initialisation) }
    var destinationChanged: Boolean by rememberSaveable { mutableStateOf(initialisation) }
    val mediaImplListToShow: MutableState<List<MediaImpl>> =
        rememberSaveable { mutableStateOf(listOf()) }
    val isMediaOptionsOpened: Boolean = satunesUiState.mediaToShowOptions != null


    /**
     * WARNING do not touch the next LaunchedEffect as they works perfectly and took me a lot of times to find the fix.
     * To look for better code make HUGE testing on the feature (navigation, changing sort, position on the list, etc.)
     */
    LaunchedEffect(key1 = sortOption) {
        if (initialisation) return@LaunchedEffect
        if (!destinationChanged) sortOptionChanged = true
    }

    LaunchedEffect(key1 = satunesUiState.currentDestination) {
        if (initialisation) return@LaunchedEffect
        destinationChanged = true
    }

    LaunchedEffect(key1 = sortOption, key2 = isMediaOptionsOpened) {
        if (isMediaOptionsOpened) return@LaunchedEffect
        if (!sortOptionChanged) return@LaunchedEffect
        if (initialisation) {
            if (sort)
                sort(
                    satunesUiState = satunesUiState,
                    source = mediaImplCollection,
                    destination = mediaImplListToShow,
                    sortOption = sortOption
                )
            else
                mediaImplListToShow.value = mediaImplCollection.toList()
            initialisation = false
        } else {
            if (sort) {
                sort(
                    satunesUiState = satunesUiState,
                    source = mediaImplCollection,
                    destination = mediaImplListToShow,
                    sortOption = sortOption
                )
                lazyListState.requestScrollToItem(0)
            }
        }
        dataViewModel.setMediaImplListOnScreen(mediaImplCollection = mediaImplListToShow.value)
        sortOptionChanged = false
        destinationChanged = false
    }

    if (satunesUiState.showSortDialog)
        SortListDialog()

    if (mediaImplListToShow.value.isNotEmpty()) {
        MediaCardList(
            modifier = modifier,
            mediaImplList = mediaImplListToShow.value,
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

private fun sort(
    satunesUiState: SatunesUiState,
    source: Collection<MediaImpl>,
    destination: MutableState<List<MediaImpl>>,
    sortOption: SortOptions
) {
    if (sortOption == SortOptions.PLAYLIST_ADDED_DATE) {
        val playlist: Playlist = satunesUiState.currentMediaImpl as Playlist
        destination.value = source.sortedBy { mediaImpl: MediaImpl ->
            -(mediaImpl as Music).getOrder(playlist = playlist)
        }
    } else if (sortOption.comparator != null) {
        destination.value = source.sortedWith(comparator = sortOption.comparator)
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
        emptyViewText = "No data"
    )
}