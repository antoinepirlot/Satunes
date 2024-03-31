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

package earth.mp3player.database.models.relations

import androidx.media3.common.MediaItem
import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Junction
import androidx.room.Relation
import earth.mp3player.database.models.Media
import earth.mp3player.database.models.Music
import earth.mp3player.database.models.tables.MusicDB
import earth.mp3player.database.models.tables.MusicsPlaylistsRel
import earth.mp3player.database.models.tables.Playlist
import java.util.SortedMap

/**
 * @author Antoine Pirlot on 27/03/2024
 */
data class PlaylistWithMusics(
    @Embedded val playlist: Playlist,
    @Relation(
        parentColumn = "playlist_id",
        entityColumn = "music_id",
        associateBy = Junction(MusicsPlaylistsRel::class)
    )
    val musics: MutableList<MusicDB>
) : Media {
    @Ignore
    override val id: Long = -1 // Not used

    @Ignore
    override val title: String = "Title is not used for PlaylistWithMusics class." // Not used

    @Ignore
    override val musicMediaItemSortedMap: SortedMap<Music, MediaItem> = sortedMapOf()

    init {
        musics.forEach { musicDB: MusicDB ->
            val music: Music = musicDB.music
            musicMediaItemSortedMap[music] = music.mediaItem
        }
    }
}
