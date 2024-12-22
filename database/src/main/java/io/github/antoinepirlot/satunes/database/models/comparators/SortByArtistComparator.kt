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

package io.github.antoinepirlot.satunes.database.models.comparators

import com.mpatric.mp3agic.NotSupportedException
import io.github.antoinepirlot.satunes.database.models.Album
import io.github.antoinepirlot.satunes.database.models.MediaImpl
import io.github.antoinepirlot.satunes.database.models.Music

/**
 * @author Antoine Pirlot on 22/12/2024
 */
object SortByArtistComparator : Comparator<MediaImpl> {
    override fun compare(mediaImpl1: MediaImpl, mediaImpl2: MediaImpl): Int {
        return when (mediaImpl1) {
            is Music -> {
                val cmp: Int =
                when (mediaImpl2) {
                    is Music -> mediaImpl1.artist.compareTo(mediaImpl2.artist)
                    is Album -> mediaImpl1.artist.compareTo(mediaImpl2.artist)
                    else -> 1 // mediaImpl2 is not a music or album, so the mediaImpl2 goes to the end
                }
                if (cmp == 0) SortByTitleComparator.compare(mediaImpl1, mediaImpl2)
                else cmp
            }

            else ->
                when (mediaImpl2) {
                    is Music, is Album -> -1 // mediaImpl1 is not a music or album, so the mediaImpl1 goes to the end
                    else -> throw NotSupportedException("Can't sort ${mediaImpl1.javaClass.name} and ${mediaImpl2.javaClass.name} by artist.")
                }
        }
    }

}