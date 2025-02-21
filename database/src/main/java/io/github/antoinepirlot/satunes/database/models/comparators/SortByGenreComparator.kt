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

package io.github.antoinepirlot.satunes.database.models.comparators

import com.mpatric.mp3agic.NotSupportedException
import io.github.antoinepirlot.satunes.database.models.Genre
import io.github.antoinepirlot.satunes.database.models.MediaImpl
import io.github.antoinepirlot.satunes.database.models.Music

/**
 * Compare [MediaImpl] by [Genre].
 * If it is the same [Genre] then compares by [MediaImpl]'s title.
 *
 * @throws NotSupportedException if both element doesn't have a genre.
 *
 *  @author Antoine Pirlot on 22/12/2024
 */
object SortByGenreComparator : Comparator<MediaImpl> {
    override fun compare(mediaImpl1: MediaImpl, mediaImpl2: MediaImpl): Int {
        return when (mediaImpl1) {
            is Music -> {
                when (mediaImpl2) {
                    is Music -> {
                        val cmp: Int =
                            mediaImpl1.genre.compareTo(mediaImpl2.genre)
                        if (cmp == 0) SortByTitleComparator.compare(mediaImpl1, mediaImpl2)
                        else cmp
                    }
                    else -> 1 // mediaImpl2 is not a music, so the mediaImpl2 goes to the end
                }
            }

            else ->
                when (mediaImpl2) {
                    is Music -> -1 // mediaImpl1 is not a music, so the mediaImpl1 goes to the end
                    else -> throw NotSupportedException("Can't sort ${mediaImpl1.javaClass.name} and ${mediaImpl2.javaClass.name} and ${mediaImpl2.javaClass.name} by genre.")
                }
        }
    }
}