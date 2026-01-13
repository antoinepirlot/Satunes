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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.satunes.data.states.DataUiState
import io.github.antoinepirlot.satunes.data.states.NavigationUiState
import io.github.antoinepirlot.satunes.data.states.SatunesUiState
import io.github.antoinepirlot.satunes.data.viewmodels.DataViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.NavigationViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.SatunesViewModel
import io.github.antoinepirlot.satunes.database.models.media.Media
import io.github.antoinepirlot.satunes.models.radio_buttons.SortOptions
import io.github.antoinepirlot.satunes.ui.components.EmptyView
import io.github.antoinepirlot.satunes.ui.components.cards.media.MediaCardList
import io.github.antoinepirlot.satunes.ui.components.dialog.SortListDialog
import io.github.antoinepirlot.satunes.ui.views.LoadingView

/**
 * @author Antoine Pirlot on 01/02/24
 */

@Composable
internal fun MediaListView(
    modifier: Modifier = Modifier,
    satunesViewModel: SatunesViewModel = viewModel(),
    dataViewModel: DataViewModel = viewModel(),
    navigationViewModel: NavigationViewModel = viewModel(),
    header: (@Composable () -> Unit)? = null,
    isLoading: Boolean = false,
    emptyViewText: String,
    canBeSorted: Boolean = true,
    showGroupIndication: Boolean = true,
    /**
     * Overrides the base onClick on media behavior
     */
    onMediaClick: ((Media) -> Unit)? = null
) {
    val satunesUiState: SatunesUiState by satunesViewModel.uiState.collectAsState()
    val dataUiState: DataUiState by dataViewModel.uiState.collectAsState()
    val navigationUiState: NavigationUiState by navigationViewModel.uiState.collectAsState()
    val lazyListState = rememberLazyListState()
    val listToShow: List<Media> = dataViewModel.mediaListOnScreen
    val sortOption: SortOptions = dataViewModel.sortOption
    val reverseOrder: Boolean = dataViewModel.reverseSortedOrder
    val previousReverseOrder: Boolean = dataViewModel.previousReverseOrder

    LaunchedEffect(key1 = sortOption, key2 = reverseOrder) {
        if (canBeSorted && (sortOption != dataUiState.appliedSortOption || reverseOrder != previousReverseOrder)) {
            dataViewModel.sort(navigationUiState = navigationUiState)
                lazyListState.requestScrollToItem(
                    index = lazyListState.firstVisibleItemIndex,
                    scrollOffset = lazyListState.firstVisibleItemScrollOffset
                ) //Prevent scroll to anywhere else when back gesture
        }
        if (reverseOrder != previousReverseOrder)
            dataViewModel.orderChanged()
    }

    if (isLoading) {
        LoadingView()
    } else if (listToShow.isNotEmpty()) {
        MediaCardList(
            modifier = modifier,
            mediaImplList = listToShow,
            lazyListState = lazyListState,
            header = header,
            onMediaClick = onMediaClick,
            showGroupIndication = showGroupIndication
        )
    } else {
        EmptyView(
            modifier = modifier,
            header = header,
            text = emptyViewText
        )
    }

    if (satunesUiState.showSortDialog)
        SortListDialog()
}

@SuppressLint("UnrememberedMutableState")
@Composable
@Preview
private fun MediaListViewPreview() {
    MediaListView(emptyViewText = "No data")
}