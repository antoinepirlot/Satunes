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
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import io.github.antoinepirlot.satunes.database.models.relations.PlaylistWithMusics
import io.github.antoinepirlot.satunes.database.models.tables.Playlist

/**
 * @author Antoine Pirlot on 27/03/2024
 */

const val LIKES_PLAYLIST_TITLE: String = "Liked Musics"

@Dao
internal interface PlaylistDAO {

    @Transaction
    @Query("SELECT * FROM playlists WHERE playlist_id = :playlistId")
    fun getPlaylistWithMusics(playlistId: Long): PlaylistWithMusics?

    @Transaction
    @Query("SELECT * FROM playlists WHERE title = :title")
    fun getLikesPlaylist(title: String = LIKES_PLAYLIST_TITLE): PlaylistWithMusics?

    @Query("SELECT playlist_id FROM playlists WHERE lower(title) = lower(:title)")
    fun playlistExist(title: String): Boolean

    @Transaction
    @Query("SELECT * FROM  playlists")
    fun getPlaylistsWithMusics(): List<PlaylistWithMusics>

    @Insert
    fun insertAll(vararg playlists: Playlist)

    @Insert
    fun insertOne(playlist: Playlist): Long

    @Delete
    fun remove(playlist: Playlist)
}