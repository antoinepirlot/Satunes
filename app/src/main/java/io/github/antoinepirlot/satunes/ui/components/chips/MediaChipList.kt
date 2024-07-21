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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.satunes.icons.SatunesIcons
import io.github.antoinepirlot.satunes.models.SearchChips
import io.github.antoinepirlot.satunes.models.allSearchChips
import io.github.antoinepirlot.satunes.ui.components.texts.NormalText
import io.github.antoinepirlot.satunes.ui.states.SatunesUiState
import io.github.antoinepirlot.satunes.ui.viewmodels.SatunesViewModel

/**
 * @author Antoine Pirlot on 28/06/2024
 */

@Composable
internal fun MediaChipList(
    modifier: Modifier = Modifier,
    satunesViewModel: SatunesViewModel = viewModel(),
) {
    val satunesUiState: SatunesUiState by satunesViewModel.uiState.collectAsState()

    val scrollState: ScrollState = rememberScrollState()
    val searchChipsList: List<SearchChips> = allSearchChips
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(state = scrollState),
    ) {
        searchChipsList.forEach { searchChip: SearchChips ->
            val selected: Boolean = satunesViewModel.selectedSearchChips.contains(searchChip)
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
                        satunesViewModel.unselect(searchChip = searchChip)
                    } else {
                        satunesViewModel.select(searchChip = searchChip)
                    }
                },
                leadingIcon = {
                    val icon: SatunesIcons =
                        if (selected) SatunesIcons.CHIP_SELECTED
                        else SatunesIcons.ADD

                    Icon(imageVector = icon.imageVector, contentDescription = icon.description)
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