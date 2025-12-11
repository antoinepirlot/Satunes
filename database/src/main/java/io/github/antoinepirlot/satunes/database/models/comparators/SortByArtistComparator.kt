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
import io.github.antoinepirlot.satunes.database.models.media.Album
import io.github.antoinepirlot.satunes.database.models.media.MediaImpl
import io.github.antoinepirlot.satunes.database.models.media.Music

/**
 * @author Antoine Pirlot on 22/12/2024
 */
object SortByArtistComparator : MediaComparator<MediaImpl>() {
    override fun compare(mediaImpl1: MediaImpl, mediaImpl2: MediaImpl): Int {
        val cmp: Int = if (mediaImpl1.isMusic()) {
            mediaImpl1 as Music
            val cmp: Int = if ((mediaImpl2.isMusic()))
                mediaImpl1.artist.compareTo((mediaImpl2 as Music).artist)
            else if (mediaImpl2.isAlbum())
                mediaImpl1.artist.compareTo((mediaImpl2 as Album).artist)
            else 1 // mediaImpl2 is not a music or album, so the mediaImpl2 goes to the end

            if (cmp == 0) SortByTitleComparator.compare(mediaImpl1, mediaImpl2)
            else cmp
        } else if (mediaImpl1.isAlbum()) {
            mediaImpl1 as Album
            val cmp: Int =
                if (mediaImpl2.isMusic())
                    mediaImpl1.artist.compareTo((mediaImpl2 as Music).artist)
                else if (mediaImpl2.isAlbum())
                    mediaImpl1.artist.compareTo((mediaImpl2 as Music).artist)
                else 1 // mediaImpl2 is not a music or album, so the mediaImpl2 goes to the end

            if (cmp == 0) SortByTitleComparator.compare(mediaImpl1, mediaImpl2)
            else cmp
        } else {
            if (mediaImpl2.isMusic() || mediaImpl2.isAlbum()) -1
            else throw NotSupportedException("Can't sort ${mediaImpl1.javaClass.name} and ${mediaImpl2.javaClass.name} by artist.")
        }
        return this.getFinalCmp(cmp = cmp)
    }

}