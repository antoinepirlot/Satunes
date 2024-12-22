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

package io.github.antoinepirlot.satunes.data.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import io.github.antoinepirlot.satunes.data.defaultSortingOptions
import io.github.antoinepirlot.satunes.models.radio_buttons.SortOptions

/**
 * @author Antoine Pirlot on 30/11/2024
 */
class SortListViewModel : ViewModel() {

    /**
     * Used to remember the radio button before user change in list.
     */
    private val _currentSortOption: MutableState<SortOptions> =
        mutableStateOf(defaultSortingOptions)

    /**
     * The current selected radio button.
     */
    private val _selectedSortOption: MutableState<SortOptions> =
        mutableStateOf(_currentSortOption.value)

    /**
     * The current selected radio button.
     */
    var selectedSortOption: SortOptions by _selectedSortOption
        private set

    /**
     * The current confirmed selected radio button (used to refresh the list)
     */
    var currentSortOption: SortOptions by _currentSortOption
        private set

    fun selectSortOption(sortRadioButton: SortOptions) {
        this.selectedSortOption = sortRadioButton
    }

    fun applySorting() {
        this.currentSortOption = this.selectedSortOption
    }

    fun cancelSorting() {
        this.selectedSortOption = this.currentSortOption
    }
}