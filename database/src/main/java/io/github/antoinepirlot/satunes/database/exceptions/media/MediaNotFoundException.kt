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

package io.github.antoinepirlot.satunes.database.exceptions.media

import com.mpatric.mp3agic.NotSupportedException

/**
 * @author Antoine Pirlot on 11/07/2024
 */
internal abstract class MediaNotFoundException(
    private val _id: Long?,
    private val _cloudId: String?
) : NullPointerException() {
    abstract fun isLocal(): Boolean
    abstract fun isCloud(): Boolean

    fun getLocalId(): Long {
        if (!this.isLocal())
            throw IllegalStateException("This is not a local media not found exception.")
        return this._id!!
    }

    fun getCloudId(): String {
        if (!this.isCloud())
            throw IllegalStateException("This is not a cloud media not found exception.")
        return this._cloudId!!
    }

    fun getIdAsString(): String {
        return if (this.isLocal()) this._id!!.toString()
        else if (this.isCloud()) this._cloudId!!
        else throw NotSupportedException("Not supported not cloud or local.")
    }
}