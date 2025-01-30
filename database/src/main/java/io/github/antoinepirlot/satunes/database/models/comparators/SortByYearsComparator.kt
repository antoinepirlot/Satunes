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
 * You find this original project on github.
 *
 * My github link is: https://github.com/antoinepirlot
 * This current project's link is: https://github.com/antoinepirlot/Satunes
 *
 * PS: I don't answer quickly.
 */

package io.github.antoinepirlot.satunes.database.models.comparators

import io.github.antoinepirlot.satunes.database.models.Album
import io.github.antoinepirlot.satunes.database.models.MediaImpl
import io.github.antoinepirlot.satunes.database.models.Music

/**
 * @author Antoine Pirlot 30/01/2025
 */
object SortByYearsComparator : Comparator<MediaImpl> {

    /**
     * Sort o1 and o2 by dates.
     * o1 and o2 must be Music or Album
     * @throws UnsupportedOperationException if o1 or o2 can't be sorted by year
     *
     * @return < 0 if the year of o1 is before the year of o2.
     *         0 if the year of o1 is the same as the year of o2.
     *         > 0 if the year of o1 is after the year of o2.
     */
    override fun compare(o1: MediaImpl, o2: MediaImpl): Int {
        val year1: Int? = when (o1) {
            is Music -> o1.year
            is Album -> o1.year
            else -> throw UnsupportedOperationException("Only Musics and Albums can be sorted by year")
        }
        val year2: Int? = when (o2) {
            is Music -> o2.year
            is Album -> o2.year
            else -> throw UnsupportedOperationException("Only Musics and Albums can be sorted by year")
        }
        return if (year1 == year2) {
            if (year1 == null) 0
            else o1.compareTo(o2)
        }
        else if (year1 == null)
            1
        else if (year2 == null)
            -1
        else if (year1 > year2) -1
        else 1
    }
}