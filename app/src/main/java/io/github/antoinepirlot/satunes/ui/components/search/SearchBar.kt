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

package io.github.antoinepirlot.satunes.ui.components.search

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.jetpack_libs.components.texts.NormalText
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.data.states.SearchUiState
import io.github.antoinepirlot.satunes.data.viewmodels.DataViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.SearchViewModel
import io.github.antoinepirlot.satunes.models.SearchChips
import io.github.antoinepirlot.satunes.models.search.SearchSection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * @author Antoine Pirlot 13/12/2025
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    searchViewModel: SearchViewModel = viewModel(),
    dataViewModel: DataViewModel = viewModel(),
) {
    val searchUiState: SearchUiState by searchViewModel.uiState.collectAsState()

    val selectedSection: SearchSection = searchUiState.selectedSection
    val isSearchRequested: Boolean = searchViewModel.isSearchRequested
    val query: String = searchViewModel.query
    val selectedSearchChips: List<SearchChips> = searchViewModel.selectedSearchChips
    val searchCoroutine: CoroutineScope = rememberCoroutineScope()

    var searchJob: Job? = null
    LaunchedEffect(key1 = query, key2 = selectedSearchChips.size, key3 = isSearchRequested) {
        if (selectedSection != SearchSection.LOCAL && !isSearchRequested) return@LaunchedEffect

        if (searchJob != null && searchJob!!.isActive)
            searchJob!!.cancel()

        searchJob = searchCoroutine.launch {
            searchViewModel.search(
                selectedSection = selectedSection,
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

    SearchBar(
        modifier = modifier.focusRequester(focusRequester),
        inputField = {
            SearchBarDefaults.InputField(
                modifier = Modifier.focusRequester(focusRequester),
                query = query,
                onQueryChange = { searchViewModel.updateQuery(value = it) },
                onSearch = {
                    keyboard?.hide()
                    if (selectedSection != SearchSection.LOCAL)
                        searchViewModel.requestSearch()
                },
                placeholder = { NormalText(text = stringResource(id = R.string.search_placeholder)) },
                expanded = false,
                onExpandedChange = { /* Do not use expanded mode */ },
            )
        },
        expanded = false,
        onExpandedChange = { /* Do not use expanded mode */ },
        windowInsets = WindowInsets(top = 0.dp), //Remove top padding of search bar introduced in API 35 (Android 15 Vanilla Ice Cream)
        content = { /* Content if expanded is true but never used */ }
    )
}