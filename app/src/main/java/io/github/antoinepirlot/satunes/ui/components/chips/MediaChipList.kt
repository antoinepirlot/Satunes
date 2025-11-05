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

package io.github.antoinepirlot.satunes.ui.components.chips

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.jetpack_libs.components.texts.NormalText
import io.github.antoinepirlot.jetpack_libs.models.JetpackLibsIcons
import io.github.antoinepirlot.satunes.data.allSearchChips
import io.github.antoinepirlot.satunes.data.viewmodels.SearchViewModel
import io.github.antoinepirlot.satunes.models.SearchChips

/**
 * @author Antoine Pirlot on 28/06/2024
 */

@Composable
internal fun MediaChipList(
    modifier: Modifier = Modifier,
    searchViewModel: SearchViewModel = viewModel(),
) {
    val scrollState: ScrollState = rememberScrollState()
    val searchChipsList: List<SearchChips> = allSearchChips
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(state = scrollState),
    ) {
        searchChipsList.forEach { searchChip: SearchChips ->
            val selected: Boolean = searchViewModel.selectedSearchChips.contains(searchChip)
            val filterChipModifier: Modifier = when (searchChip) {
                searchChipsList.first() -> {
                    Modifier.padding(start = 16.dp, end = 4.dp)
                }

                searchChipsList.last() -> {
                    Modifier.padding(start = 4.dp, end = 16.dp)
                }

                else -> {
                    Modifier.padding(horizontal = 4.dp)
                }
            }

            FilterChip(
                modifier = filterChipModifier,
                selected = selected,
                onClick = {
                    if (selected) {
                        searchViewModel.unselect(searchChip = searchChip)
                    } else {
                        searchViewModel.select(searchChip = searchChip)
                    }
                },
                leadingIcon = {
                    val jetpackLibsIcons: JetpackLibsIcons =
                        if (selected) JetpackLibsIcons.CHIP_SELECTED
                        else JetpackLibsIcons.ADD

                    Icon(
                        imageVector = jetpackLibsIcons.imageVector,
                        contentDescription = jetpackLibsIcons.description
                    )
                },
                label = { NormalText(text = stringResource(id = searchChip.stringId)) }
            )
        }
    }
}

@Preview
@Composable
private fun MediaChipListPreview() {
    MediaChipList()
}