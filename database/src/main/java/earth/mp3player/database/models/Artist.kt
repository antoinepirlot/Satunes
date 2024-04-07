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

import android.content.Context
import androidx.media3.common.MediaItem
import earth.mp3player.database.services.utils.computeString
import java.util.SortedMap

/**
 * @author Antoine Pirlot on 27/03/2024
 */

data class Artist(
    override val id: Long,
    override var title: String,
    var musicList: MutableList<Music> = mutableListOf(),
    var albumSortedMap: SortedMap<String, Album> = sortedMapOf(),
    val context: Context,
) : Media {
    override val musicMediaItemSortedMap: SortedMap<Music, MediaItem> = sortedMapOf()

    init {
        title = computeString(context = context, string = title)
    }

    fun addAlbum(album: Album) {
        this.albumSortedMap.putIfAbsent(album.title, album)
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