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
import androidx.compose.runtime.mutableStateOf
import androidx.media3.common.MediaItem
import java.util.SortedMap

/**
 * @author Antoine Pirlot on 27/03/2024
 */

data class Album(
    override val id: Long = nextId,
    override var title: String,
    var artist: Artist? = null,
    override val musicMediaItemMap: SortedMap<Music, MediaItem> = sortedMapOf(),
) : Media {
    override val liked: MutableState<Boolean>? = null // Not used
    override val musicMediaItemMapUpdate: MutableState<Boolean> = mutableStateOf(false)
    override var artwork: Bitmap? = null

    companion object {
        var nextId: Long = 1
    }

    init {
        nextId++
    }

    /**
     * Add music to this album by adding music in musicMediaItemSortedMap
     * and in musicSortedMap.
     *
     * @param music the music to add
     */
    fun addMusic(music: Music) {
        this.musicMediaItemMap[music] = music.mediaItem
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
        result = 31 * result + (artist?.hashCode() ?: 0)
        return result
    }

    override fun compareTo(other: Media): Int {
        val titleCompared: Int = super.compareTo(other)
        if (artist == null || other is Album && other.artist == null) {
            return titleCompared
        }
        if (titleCompared == 0 && other is Album) {
            return artist!!.compareTo(other.artist!!)
        }
        return titleCompared
    }
}