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

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.jetpack_libs.components.images.Icon
import io.github.antoinepirlot.jetpack_libs.components.texts.NormalText
import io.github.antoinepirlot.satunes.data.states.SearchUiState
import io.github.antoinepirlot.satunes.data.viewmodels.SearchViewModel
import io.github.antoinepirlot.satunes.models.search.SearchSection

/**
 * @author Antoine Pirlot 13/12/2025
 */
@Composable
fun SearchModeTabSelector(
    modifier: Modifier = Modifier,
    searchViewModel: SearchViewModel = viewModel(),
) {
    val searchUiState: SearchUiState by searchViewModel.uiState.collectAsState()

    PrimaryTabRow(
        modifier = modifier,
        selectedTabIndex = searchUiState.selectedSection.ordinal
    ) {
        SearchSection.entries.forEachIndexed { index: Int, section: SearchSection ->
            Tab(
                selected = searchUiState.selectedSection.ordinal == index,
                onClick = {
                    searchViewModel.selectSection(section = section)
                },
                text = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(jetpackLibsIcons = section.icon)
                        NormalText(text = stringResource(section.stringId))
                    }
                }
            )
        }
    }
}