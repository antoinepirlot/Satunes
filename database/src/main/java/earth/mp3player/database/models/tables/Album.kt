/*
 * This file is part of MP3 Player.
 *
 * MP3 Player is free software: you can redistribute it and/or modify it under
 *  the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * MP3 Player is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with MP3 Player.
 * If not, see <https://www.gnu.org/licenses/>.
 *
 * **** INFORMATIONS ABOUT THE AUTHOR *****
 * The author of this file is Antoine Pirlot, the owner of this project.
 * You find this original project on github.
 *
 * My github link is: https://github.com/antoinepirlot
 * This current project's link is: https://github.com/antoinepirlot/MP3-Player
 *
 * You can contact me via my email: pirlot.antoine@outlook.com
 * PS: I don't answer quickly.
 */

package earth.mp3player.database.models.tables

import androidx.media3.common.MediaItem
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import earth.mp3player.database.models.Media
import java.util.SortedMap

/**
 * @author Antoine Pirlot on 27/03/2024
 */

@Entity(tableName = "albums")
data class Album(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "album_id") override val id: Long,
    @ColumnInfo(name = "title") override val title: String,
) : Media {
    @Ignore
    override var musicMediaItemSortedMap: SortedMap<Music, MediaItem> = sortedMapOf()
        private set

    @Ignore
    val musicSortedMap: SortedMap<Long, Music> = sortedMapOf()

    constructor(id: Long, title: String, musicMediaItemSortedMap: SortedMap<Music, MediaItem>)
            : this(id = id, title = title) {
        this.musicMediaItemSortedMap = musicMediaItemSortedMap
    }

    init {
        musicMediaItemSortedMap.forEach { (music: Music, _: MediaItem) ->
            musicSortedMap[music.id] = music
        }
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
}