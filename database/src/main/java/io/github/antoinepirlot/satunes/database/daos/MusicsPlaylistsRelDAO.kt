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

package io.github.antoinepirlot.satunes.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import io.github.antoinepirlot.satunes.database.models.database.tables.MusicsPlaylistsRel

/**
 * @author Antoine Pirlot on 30/03/2024
 */

@Dao
internal interface MusicsPlaylistsRelDAO {

    @Query("SELECT music_id FROM musics_playlists_rel GROUP BY music_id")
    fun getAllMusicIds(): List<Long>

    @Insert
    fun insert(musicsPlaylistsRel: MusicsPlaylistsRel)

    @Query("DELETE FROM musics_playlists_rel WHERE music_id = :musicId AND playlist_id = :playlistId")
    fun delete(musicId: Long, playlistId: Long)

    @Query("DELETE FROM musics_playlists_rel WHERE music_id = :musicId")
    fun removeAll(musicId: Long)

    @Query("SELECT music_id FROM musics_playlists_rel WHERE music_id = :musicId")
    fun isMusicInPlaylist(musicId: Long): Boolean
}