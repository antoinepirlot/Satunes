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

package io.github.antoinepirlot.satunes.database.models

import io.github.antoinepirlot.satunes.database.models.comparators.MusicInAlbumComparator
import java.util.SortedSet

/**
 * @author Antoine Pirlot on 27/03/2024
 */

class Album(
    title: String,
    var artist: Artist,
    var isCompilation: Boolean = false,
    val numTracks: Int? = null,
    year: Int? = null
) : MediaImpl(id = nextId, title = title) {

    companion object {
        var nextId: Long = 1
    }

    override val musicSortedSet: SortedSet<Music> = sortedSetOf(MusicInAlbumComparator)
    val year: Int? = if (year != null && year < 1) null else year
    init {
        nextId++
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Album

        if (title != other.title) return false
        if (artist != other.artist) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + (artist.hashCode())
        return result
    }

    override fun compareTo(other: MediaImpl): Int {
        var compared: Int = super.compareTo(other)
        if (compared == 0 && other is Album) {
            compared = this.artist.compareTo(other.artist)
        }
        return compared
    }

    override fun toString(): String {
        return "$title - $artist"
    }
}