/*
 *  This file is part of MP3 Player.
 *
 *  MP3 Player is free software: you can redistribute it and/or modify it under
 *  the terms of the GNU General Public License as published by the Free Software Foundation,
 *  either version 3 of the License, or (at your option) any later version.
 *
 *  MP3 Player is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *   without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with MP3 Player.
 *  If not, see <https://www.gnu.org/licenses/>.
 *
 *  ***** INFORMATIONS ABOUT THE AUTHOR *****
 *  The author of this file is Antoine Pirlot, the owner of this project.
 *  You find this original project on github.
 *
 *  My github link is: https://github.com/antoinepirlot
 *  This current project's link is: https://github.com/antoinepirlot/MP3-Player
 *
 *  You can contact me via my email: pirlot.antoine@outlook.com
 *  PS: I don't answer quickly.
 */

package earth.mp3player.database.models

import androidx.compose.ui.graphics.ImageBitmap
import androidx.media3.common.MediaItem
import java.util.SortedMap

/**
 * @author Antoine Pirlot on 27/03/2024
 */

data class Album(
    override val id: Long = nextId,
    override var title: String,
    var artist: Artist? = null,
    override val musicMediaItemSortedMap: SortedMap<Music, MediaItem> = sortedMapOf(),
) : Media {
    val musicSortedMap: SortedMap<Long, Music> = sortedMapOf()
    override var artwork: ImageBitmap? = null

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
        this.musicMediaItemSortedMap[music] = music.mediaItem
        this.musicSortedMap[music.id] = music
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


}