/*
 * This file is part of Satunes.
 *
 * Satunes is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * Satunes is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License along with Satunes.
 *
 * If not, see <https://www.gnu.org/licenses/>.
 *
 * **** INFORMATION ABOUT THE AUTHOR *****
 * The author of this file is Antoine Pirlot, the owner of this project.
 * You find this original project on Codeberg.
 *
 * My Codeberg link is: https://codeberg.org/antoinepirlot
 * This current project's link is: https://codeberg.org/antoinepirlot/Satunes
 */

package io.github.antoinepirlot.satunes.models.listeners

import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.savedstate.SavedState
import io.github.antoinepirlot.satunes.MainActivity
import io.github.antoinepirlot.satunes.data.viewmodels.NavigationViewModel
import io.github.antoinepirlot.satunes.models.Destination

/**
 * @author Antoine Pirlot 20/08/2025
 */
class OnDestinationChangedListener(
    private val navigationViewModel: NavigationViewModel
) : NavController.OnDestinationChangedListener {

    companion object {
        private var _depth: Int = 0

        fun incrementDepth() {
            this._depth++
        }

        fun decrementDepth() {
            //Depth < 0 means leaving app
            this._depth--
        }

        fun resetDepth() {
            this._depth = 0
        }
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: SavedState?
    ) {
        if (navigationViewModel.stackSize() != _depth)
            throw IllegalStateException("You didn't push the destination on stack. Be sure to use NavigationViewModel to navigate.")
    }
}