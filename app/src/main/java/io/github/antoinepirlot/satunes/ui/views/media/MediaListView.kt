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

package io.github.antoinepirlot.satunes.ui.views.media

import android.annotation.SuppressLint
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.satunes.data.states.DataUiState
import io.github.antoinepirlot.satunes.data.states.SatunesUiState
import io.github.antoinepirlot.satunes.data.viewmodels.DataViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.SatunesViewModel
import io.github.antoinepirlot.satunes.database.models.Album
import io.github.antoinepirlot.satunes.database.models.Artist
import io.github.antoinepirlot.satunes.database.models.Folder
import io.github.antoinepirlot.satunes.database.models.Genre
import io.github.antoinepirlot.satunes.database.models.MediaImpl
import io.github.antoinepirlot.satunes.database.models.Music
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
    /**
     * Overrides the base onClick on media behavior
     */
    onMediaClick: ((MediaImpl) -> Unit)? = null
) {
    val satunesUiState: SatunesUiState by satunesViewModel.uiState.collectAsState()
    val dataUiState: DataUiState by dataViewModel.uiState.collectAsState()
    val lazyListState = rememberLazyListState()
    val listToShow: MutableList<MediaImpl> = remember { mediaImplCollection.toMutableStateList() }
    val sortOption: SortOptions = dataViewModel.sortOption

    LaunchedEffect(key1 = collectionChanged) {
        // Prevent doing twice the same thing at launching and showing empty text temporarily
        if (dataUiState.appliedSortOption == null) return@LaunchedEffect
        listToShow.clear()
        listToShow.addAll(elements = mediaImplCollection)
    }

    LaunchedEffect(key1 = sortOption, key2 = collectionChanged) {
        if (sort && sortOption != dataUiState.appliedSortOption || collectionChanged) {
            dataViewModel.sort(
                satunesUiState = satunesUiState,
                list = listToShow,
            )
            if (!collectionChanged)
                lazyListState.scrollToItem(0)
            else {
                lazyListState.scrollToItem(
                    index = lazyListState.firstVisibleItemIndex,
                    scrollOffset = lazyListState.firstVisibleItemScrollOffset
                ) //Prevent scroll to anywhere else when back gesture
            }
        }
        dataViewModel.setMediaImplListOnScreen(mediaImplCollection = listToShow)
    }

    if (satunesUiState.showSortDialog)
        SortListDialog()

    if (listToShow.isNotEmpty()) {
        MediaCardList(
            modifier = modifier,
            mediaImplList = listToShow,
            lazyListState = lazyListState,
            header = header,
            showGroupIndication = showGroupIndication,
            onMediaClick = onMediaClick
        )
    } else {
        EmptyView(
            modifier = modifier,
            header = header,
            text = emptyViewText
        )
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
@Preview
private fun MediaListViewPreview() {
    val list: SnapshotStateList<MediaImpl> = mutableStateListOf(
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
        mediaImplCollection = list,
        collectionChanged = false,
        emptyViewText = "No data"
    )
}