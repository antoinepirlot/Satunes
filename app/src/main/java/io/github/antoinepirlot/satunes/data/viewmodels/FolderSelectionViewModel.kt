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

package io.github.antoinepirlot.satunes.data.viewmodels

import androidx.lifecycle.ViewModel
import io.github.antoinepirlot.satunes.data.states.FolderSelectionUiState
import io.github.antoinepirlot.satunes.database.models.FoldersSelection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * @author Antoine Pirlot 19/04/2025
 */
class FolderSelectionViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<FolderSelectionUiState> =
        MutableStateFlow(FolderSelectionUiState())

    val uiState: StateFlow<FolderSelectionUiState> = _uiState.asStateFlow()

    /**
     * Switch the section selected in Folder sub settings.
     */
    fun switchFolderSection() {
        _uiState.update { currentState: FolderSelectionUiState ->
            if (currentState.folderSelectionSelected == FoldersSelection.INCLUDE)
                currentState.copy(folderSelectionSelected = FoldersSelection.EXCLUDE)
            else
                currentState.copy(folderSelectionSelected = FoldersSelection.INCLUDE)
        }
    }
}