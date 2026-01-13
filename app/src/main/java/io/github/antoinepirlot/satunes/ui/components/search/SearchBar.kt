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
import io.github.antoinepirlot.satunes.data.states.ModeTabSelectorUiState
import io.github.antoinepirlot.satunes.data.states.SatunesUiState
import io.github.antoinepirlot.satunes.data.viewmodels.DataViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.ModeTabSelectorViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.SatunesViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.SearchViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.SubsonicViewModel
import io.github.antoinepirlot.satunes.models.SearchChips
import io.github.antoinepirlot.satunes.models.search.ModeTabSelectorSection
import kotlinx.coroutines.CoroutineScope

/**
 * @author Antoine Pirlot 13/12/2025
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    searchViewModel: SearchViewModel = viewModel(),
    modeTabSelectorViewModel: ModeTabSelectorViewModel = viewModel(),
) {
    val modeTabSelectorUiState: ModeTabSelectorUiState by modeTabSelectorViewModel.uiState.collectAsState()
    val selectedSection: ModeTabSelectorSection = modeTabSelectorUiState.selectedSection
    val query: String = searchViewModel.query
    val keyboard: SoftwareKeyboardController? = LocalSoftwareKeyboardController.current
    val focusRequester: FocusRequester = remember { FocusRequester() }
    val searchCoroutine: CoroutineScope = rememberCoroutineScope()

    HandleUiChange(searchCoroutine = searchCoroutine)

    HandleQueryChange(searchCoroutine = searchCoroutine)

    LaunchedEffect(key1 = Unit) {
        // Request focus after composable becomes visible
        focusRequester.requestFocus()
    }

    SearchBar(
        modifier = modifier.focusRequester(focusRequester),
        inputField = {
            SearchBarDefaults.InputField(
                modifier = Modifier.focusRequester(focusRequester),
                query = query,
                onQueryChange = { searchViewModel.updateQuery(value = it, isLocal = selectedSection.isLocal()) },
                onSearch = {
                    keyboard?.hide()
                    if (selectedSection.isCloud())
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

@Composable
fun HandleQueryChange(
    searchViewModel: SearchViewModel = viewModel(),
    dataViewModel: DataViewModel = viewModel(),
    subsonicViewModel: SubsonicViewModel = viewModel(),
    modeTabSelectorViewModel: ModeTabSelectorViewModel = viewModel(),
    searchCoroutine: CoroutineScope,
) {
    val modeTabSelectorUiState: ModeTabSelectorUiState by modeTabSelectorViewModel.uiState.collectAsState()

    val selectedSearchChips: List<SearchChips> = searchViewModel.selectedSearchChips
    val isSearchRequested: Boolean = searchViewModel.isSearchRequested
    val selectedSection: ModeTabSelectorSection = modeTabSelectorUiState.selectedSection
    val query: String = searchViewModel.query

    LaunchedEffect(key1 = query, key2 = selectedSearchChips.size, key3 = isSearchRequested) {
        if (!isSearchRequested) return@LaunchedEffect
        searchViewModel.search(
            searchCoroutine = searchCoroutine,
            selectedSection = selectedSection,
            dataViewModel = dataViewModel,
            subsonicViewModel = subsonicViewModel,
            selectedSearchChips = selectedSearchChips,
        )
    }
}

@Composable
private fun HandleUiChange(
    satunesViewModel: SatunesViewModel = viewModel(),
    searchViewModel: SearchViewModel = viewModel(),
    dataViewModel: DataViewModel = viewModel(),
    subsonicViewModel: SubsonicViewModel = viewModel(),
    modeTabSelectorViewModel: ModeTabSelectorViewModel = viewModel(),
    searchCoroutine: CoroutineScope,
) {
    val satunesUiState: SatunesUiState by satunesViewModel.uiState.collectAsState()
    val modeTabSelectorUiState: ModeTabSelectorUiState by modeTabSelectorViewModel.uiState.collectAsState()

    val selectedSearchChips: List<SearchChips> = searchViewModel.selectedSearchChips
    val selectedSection: ModeTabSelectorSection = modeTabSelectorUiState.selectedSection

    LaunchedEffect(key1 = satunesUiState.mode) {
        if (satunesUiState.mode.isOffline())
            modeTabSelectorViewModel.selectSection(section = ModeTabSelectorSection.LOCAL)
    }


    LaunchedEffect(key1 = modeTabSelectorUiState.selectedSection) {
        searchViewModel.search(
            searchCoroutine = searchCoroutine,
            selectedSection = selectedSection,
            dataViewModel = dataViewModel,
            subsonicViewModel = subsonicViewModel,
            selectedSearchChips = selectedSearchChips,
        )
    }
}