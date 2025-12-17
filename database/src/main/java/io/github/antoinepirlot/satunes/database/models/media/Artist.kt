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

package io.github.antoinepirlot.satunes.database.models.media

import androidx.compose.runtime.mutableStateListOf
import java.util.SortedSet

/**
 * @author Antoine Pirlot on 27/03/2024
 */

open class Artist(
    id: Long? = null,
    title: String,
) : MediaImpl(
    id = id ?: nextId,
    title = title
) {
    companion object {
        var nextId: Long = 1
    }

    private val _albumSortedSet: SortedSet<Album> = sortedSetOf()

    val albumCollection: Collection<Album> = mutableStateListOf()

    init {
        nextId++
    }

    fun addAlbum(album: Album): Boolean {
        if (!this.contains(album = album)) {
            this.albumCollection as MutableList
            _albumSortedSet.add(element = album)
            this.albumCollection.add(element = album)
            this.albumCollection.sort()
            return true
        }
        return false
    }

    fun contains(album: Album): Boolean {
        return this._albumSortedSet.contains(album)
    }

    fun updateAlbum(album: Album) {
        if (this.contains(album = album)) {
            albumCollection as MutableList
            this._albumSortedSet.remove(element = album) //If it is equals, reference may be different
            this.albumCollection.remove(element = album)
            this._albumSortedSet.add(element = album)
            this.albumCollection.add(element = album)
            this.albumCollection.sort()
        }
    }

    override fun isArtist(): Boolean = true

    override fun toString(): String {
        return this.title
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Artist

        return title == other.title
    }

    override fun hashCode(): Int {
        return title.hashCode()
    }
}