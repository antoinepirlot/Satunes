/*
 * This file is part of Satunes.
 * Satunes is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *  Satunes is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License along with Satunes.
 *  If not, see <https://www.gnu.org/licenses/>.
 *
 * ** INFORMATION ABOUT THE AUTHOR *****
 * The author of this file is Antoine Pirlot, the owner of this project.
 * You find this original project on Codeberg.
 *
 * My Codeberg link is: https://codeberg.org/antoinepirlot
 * This current project's link is: https://codeberg.org/antoinepirlot/Satunes
 */

package io.github.antoinepirlot.satunes.ui.components.settings.library.folders

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.jetpack_libs.components.texts.NormalText
import io.github.antoinepirlot.satunes.data.allFoldersSelections
import io.github.antoinepirlot.satunes.data.states.FolderSelectionUiState
import io.github.antoinepirlot.satunes.data.viewmodels.FolderSelectionViewModel
import io.github.antoinepirlot.satunes.database.models.FoldersSelection

/**
 * @author Antoine Pirlot on 09/08/2024
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FoldersRowSelection(
    modifier: Modifier = Modifier,
    folderSelectionViewModel: FolderSelectionViewModel = viewModel()
) {
    val folderSelectionUiState: FolderSelectionUiState by folderSelectionViewModel.uiState.collectAsState()
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        SingleChoiceSegmentedButtonRow {
            for (i: Int in allFoldersSelections.indices) {
                val foldersSelection: FoldersSelection = allFoldersSelections[i]
                SegmentedButton(
                    selected = foldersSelection == folderSelectionUiState.folderSelectionSelected,
                    shape = SegmentedButtonDefaults.itemShape(
                        index = i,
                        count = allFoldersSelections.size
                    ),
                    icon = {}, //remove selected icon as it's not relevant for using both sections
                    onClick = { folderSelectionViewModel.switchFolderSection() },
                    label = { NormalText(text = stringResource(id = foldersSelection.stringId)) }
                )
            }
        }
    }
}

@Preview
@Composable
private fun FoldersRowSelectionPreview() {
    FoldersRowSelection()
}