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
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import io.github.antoinepirlot.jetpack_libs.components.texts.NormalText
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.data.viewmodels.DataViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.PlaybackViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.SearchViewModel
import io.github.antoinepirlot.satunes.database.models.MediaImpl
import io.github.antoinepirlot.satunes.database.models.Music
import io.github.antoinepirlot.satunes.models.SearchChips
import io.github.antoinepirlot.satunes.router.utils.openCurrentMusic
import io.github.antoinepirlot.satunes.router.utils.openMedia
import io.github.antoinepirlot.satunes.ui.components.chips.MediaChipList
import io.github.antoinepirlot.satunes.ui.views.media.MediaListView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * @author Antoine Pirlot on 27/06/2024
 */

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
internal fun SearchView(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    dataViewModel: DataViewModel = viewModel(),
    playbackViewModel: PlaybackViewModel = viewModel(),
    searchViewModel: SearchViewModel = viewModel(),
) {
    val query: String = searchViewModel.query
    val mediaImplList: Set<MediaImpl> = searchViewModel.mediaImplSet
    val selectedSearchChips: List<SearchChips> = searchViewModel.selectedSearchChips

    val searchCoroutine: CoroutineScope = rememberCoroutineScope()
    var searchJob: Job? = null
    LaunchedEffect(key1 = query, key2 = selectedSearchChips.size) {
        if (searchJob != null && searchJob!!.isActive) {
            searchJob!!.cancel()
        }
        searchJob = searchCoroutine.launch {
            searchViewModel.search(
                dataViewModel = dataViewModel,
                selectedSearchChips = selectedSearchChips,
            )
        }
    }

    val focusRequester: FocusRequester = remember { FocusRequester() }
    LaunchedEffect(key1 = true) {
        // Request focus after composable becomes visible
        focusRequester.requestFocus()
    }

    val keyboard: SoftwareKeyboardController? = LocalSoftwareKeyboardController.current

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SearchBar(
            modifier = Modifier.focusRequester(focusRequester),
            query = query,
            onQueryChange = { searchViewModel.updateQuery(value = it) },
            onSearch = {
                searchViewModel.updateQuery(value = it)
                keyboard?.hide()
            },
            active = false,
            onActiveChange = { /* Do not use active mode */ },
            placeholder = { NormalText(text = stringResource(id = R.string.search_placeholder)) },
            content = { /* Content if active is true but never used */ }
        )
        Spacer(modifier = Modifier.size(16.dp))
        MediaChipList()
        MediaListView(
            navController = navController,
            mediaImplCollection = mediaImplList,
            openMedia = { mediaImpl: MediaImpl ->
                if (mediaImpl is Music) {
                    playbackViewModel.loadMusic(
                        musicSet = dataViewModel.getMusicSet(),
                        musicToPlay = mediaImpl
                    )
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

@Preview
@Composable
private fun SearchViewPreview() {
    val navController: NavHostController = rememberNavController()
    SearchView(navController = navController)
}