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

package io.github.antoinepirlot.satunes.database.models.comparators

import io.github.antoinepirlot.satunes.database.models.media.Folder
import io.github.antoinepirlot.satunes.database.models.media.Media
import io.github.antoinepirlot.satunes.database.models.media.Music
import java.util.Date

/**
 * @author Antoine Pirlot 30/01/2025
 */
object SortByAddedDateComparator : MediaComparator<Media>() {
    override fun compare(o1: Media, o2: Media): Int {
        var cmp: Int =
            if (o1.isMusic()) {
                o1 as Music
                if (o2.isMusic()) this.getCmp(date1 = o1.addedDate, date2 = (o2 as Music).addedDate)
                else if (o2.isFolder()) this.getCmp(
                    date1 = o1.addedDate,
                    date2 = (o2 as Folder).addedDate
                )
                else throw UnsupportedOperationException("Can't sort non music or folder medias by added date.")
            } else if (o1.isFolder()) {
                o1 as Folder
                if (o2.isMusic()) this.getCmp(date1 = o1.addedDate, date2 = (o2 as Music).addedDate)
                else if (o2.isFolder()) -this.getCmp(
                    date1 = o1.addedDate,
                    date2 = (o2 as Folder).addedDate
                )
                else throw UnsupportedOperationException("Can't sort non music or folder medias by added date.")
            } else throw UnsupportedOperationException("Can't sort non music or folder medias by added date.")

        cmp = if (cmp == 0) o1.compareTo(o2) else cmp
        return this.getFinalCmp(cmp = cmp)
    }

    private fun getCmp(date1: Date?, date2: Date?): Int {
        return if (date1 != null && date2 != null)
            -date1.compareTo(date2)
        else if (date1 != null) return 1
        else if (date2 != null) return -1
        else 0
    }
}