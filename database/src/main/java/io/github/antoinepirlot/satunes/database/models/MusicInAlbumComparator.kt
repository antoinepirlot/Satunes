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

import io.github.antoinepirlot.satunes.database.exceptions.NotInAlbumException

/**
 * @author Antoine Pirlot on 18/10/2024
 */
object MusicInAlbumComparator : Comparator<Music> {
    /**
     * Compare 2 music's in the same album.
     * Assume it is used only in Album's music set
     *
     * If o1 album is not the same as o2's throws [NotInAlbumException]
     * If at least one cdTrackNumber is null or the o1's is greater than o2's returns 1
     * If the o1 track number is < of the o2's return -1
     * returns 0 in other cases.
     *
     * @param o1 [Music]
     * @param o2 [Music]
     *
     * @throws [NotInAlbumException] if o1 has not the same album as o2
     * @return [Int]: -1 or 0 or 1 as specified.
     */
    override fun compare(o1: Music, o2: Music): Int {
        if (o1.album != o2.album) throw NotInAlbumException()
        if (o1.cdTrackNumber == null || o2.cdTrackNumber == null || o1.cdTrackNumber > o2.cdTrackNumber) return 1
        if (o1.cdTrackNumber!! < o2.cdTrackNumber!!) return -1
        return 0
    }

}