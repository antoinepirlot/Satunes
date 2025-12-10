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

package io.github.antoinepirlot.satunes.data.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import io.github.antoinepirlot.satunes.data.defaultSortingOption
import io.github.antoinepirlot.satunes.database.models.comparators.MediaComparator
import io.github.antoinepirlot.satunes.models.radio_buttons.SortOptions

/**
 * @author Antoine Pirlot on 30/11/2024
 */
class SortListViewModel : ViewModel() {

    /**
     * The current selected radio button.
     */
    var selectedSortOption: SortOptions by mutableStateOf(defaultSortingOption)
        private set

    private var previousSelectedSortOption: SortOptions = this.selectedSortOption

    var reverseOrder: Boolean by mutableStateOf(MediaComparator.DEFAULT_REVERSE_ORDER)
        private set

    private var previousReverseOrder: Boolean = this.reverseOrder

    fun selectSortOption(sortRadioButton: SortOptions) {
        this.selectedSortOption = sortRadioButton
    }

    fun switchReverseOrder() {
        this.reverseOrder = !this.reverseOrder
    }

    fun apply(dataViewModel: DataViewModel) {
        this.previousReverseOrder = this.reverseOrder
        this.previousSelectedSortOption = this.selectedSortOption
        dataViewModel.setReverseOrder(reverseOrder = this.reverseOrder)
        dataViewModel.setSorting(sortOption = this.selectedSortOption)
    }

    /**
     * Reset form value to the state before opening dialog.
     */
    fun reset() {
        this.reverseOrder = this.previousReverseOrder
        this.selectedSortOption = this.previousSelectedSortOption
    }
}