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

import android.graphics.Bitmap
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.SortedSet

/**
 * @author Antoine Pirlot on 29/03/2024
 */
abstract class MediaImpl(
    id: Long,
    title: String
) : Media, Comparable<MediaImpl> {
    override var id: Long = id
        internal set
    override var title: String by mutableStateOf(value = title)

    var artwork: Bitmap? by mutableStateOf(null)
        internal set

    protected val musicSortedSet: SortedSet<Music> = sortedSetOf()
    val musicSetUpdated: MutableState<Boolean> = mutableStateOf(false)

    open fun isEmpty(): Boolean {
        return this.musicSortedSet.isEmpty()
    }

    open fun isNotEmpty(): Boolean {
        return this.musicSortedSet.isNotEmpty()
    }

    fun getMusicSet(): Set<Music> {
        return this.musicSortedSet
    }

    fun addMusic(music: Music) {
        if (!this.musicSortedSet.contains(element = music)) {
            this.musicSortedSet.add(element = music)
            this.musicSetUpdated.value = true
        }
    }

    fun removeMusic(music: Music) {
        if (this.musicSortedSet.contains(element = music)) {
            this.musicSortedSet.remove(music)
            this.musicSetUpdated.value = true
        }
    }

    override fun compareTo(other: MediaImpl): Int {
        if (this == other) return 0
        var compared: Int = StringComparator.compare(o1 = this.title, o2 = other.title)
        if (compared == 0 && this.javaClass != other.javaClass) {
            compared = when(this) {
                is Music -> -1
                is Album -> if (other is Music)  1 else -1
                is Artist -> if (other is Music || other is Album) 1 else -1
                is Genre -> if (other is Folder || other is Playlist) -1 else 1
                is Playlist -> if (other !is Playlist && other is Folder) -1 else 1
                else -> 1
            }
        }
        return compared
    }

    fun musicCount(): Int = this.musicSortedSet.size
}