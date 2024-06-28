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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.antoinepirlot.satunes.database.R
import io.github.antoinepirlot.satunes.services.search.ChipSelectionManager
import io.github.antoinepirlot.satunes.ui.components.texts.NormalText

/**
 * @author Antoine Pirlot on 28/06/2024
 */

val chipsList: List<Int> = listOf(
    R.string.musics,
    R.string.albums,
    R.string.artists,
    R.string.genres,
    R.string.folders,
)

@Composable
internal fun MediaChipList(
    modifier: Modifier = Modifier,
) {
    val scrollState: ScrollState = rememberScrollState()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(state = scrollState),
    ) {
        chipsList.forEach { chipName: Int ->
            var selected: Boolean by rememberSaveable {
                mutableStateOf(ChipSelectionManager.selectedChips.contains(chipName))
            }
            FilterChip(
                selected = selected,
                onClick = {
                    selected = !selected
                    if (selected) {
                        ChipSelectionManager.addChip(chipName = chipName)
                    } else {
                        ChipSelectionManager.removeChip(chipName = chipName)
                    }
                },
                label = { NormalText(text = stringResource(id = chipName)) }
            )
            if (chipName != chipsList.last()) {
                Spacer(modifier = Modifier.size(8.dp))
            }
        }
    }
}

@Preview
@Composable
private fun MediaChipListPreview() {
    MediaChipList()
}