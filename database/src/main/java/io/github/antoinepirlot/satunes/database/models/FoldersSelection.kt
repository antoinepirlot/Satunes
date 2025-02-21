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

package io.github.antoinepirlot.satunes.database.models

import io.github.antoinepirlot.satunes.database.R

/**
 * @author Antoine Pirlot on 09/08/2024
 */
enum class FoldersSelection(
    internal val id: Int,
    val stringId: Int,
    val likeQueryAttribute: String,
    val andOrQueryAttribute: String
) {
    INCLUDE(
        id = 1,
        stringId = R.string.include,
        likeQueryAttribute = "LIKE",
        andOrQueryAttribute = "OR"
    ),
    EXCLUDE(
        id = 2,
        stringId = R.string.exclude,
        likeQueryAttribute = "NOT LIKE",
        andOrQueryAttribute = "AND"
    )
}