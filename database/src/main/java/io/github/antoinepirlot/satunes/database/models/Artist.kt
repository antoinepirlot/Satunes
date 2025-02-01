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

package io.github.antoinepirlot.satunes.database.models

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import java.util.SortedSet

/**
 * @author Antoine Pirlot on 27/03/2024
 */

class Artist(
    title: String,
) : MediaImpl(id = nextId, title = title) {
    private val albumSortedSet: SortedSet<Album> = sortedSetOf()

    val albumSortedSetUpdate: MutableState<Boolean> = mutableStateOf(false)

    companion object {
        var nextId: Long = 1
    }

    init {
        nextId++
    }

    fun addAlbum(album: Album): Boolean {
        if (!contains(album = album)) {
            albumSortedSet.add(element = album)
            return true
        }
        return false
    }

    fun getAlbumSet(): Set<Album> {
        return this.albumSortedSet
    }

    fun contains(album: Album): Boolean {
        return this.albumSortedSet.contains(album)
    }

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