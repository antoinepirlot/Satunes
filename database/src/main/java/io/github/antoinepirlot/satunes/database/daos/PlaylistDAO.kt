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

package io.github.antoinepirlot.satunes.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import io.github.antoinepirlot.satunes.database.models.database.relations.PlaylistWithMusics
import io.github.antoinepirlot.satunes.database.models.database.tables.PlaylistDB

/**
 * @author Antoine Pirlot on 27/03/2024
 */

const val LIKES_PLAYLIST_TITLE: String = "_likes"

@Dao
internal interface PlaylistDAO {

    @Query("SELECT title FROM playlists WHERE playlist_id = :playlistId")
    fun getOriginalPlaylistTitle(playlistId: Long): String

    @Transaction
    @Query("SELECT * FROM playlists WHERE playlist_id = :playlistId")
    fun getPlaylistWithMusics(playlistId: Long): PlaylistWithMusics?

    @Transaction
    @Query("SELECT * FROM playlists WHERE title = :title")
    fun getPlaylistWithMusics(title: String): PlaylistWithMusics?

    @Query("SELECT playlist_id FROM playlists WHERE lower(title) = lower(:title)")
    fun exists(title: String): Boolean

    @Transaction
    @Query("SELECT * FROM  playlists")
    fun getPlaylistsWithMusics(): List<PlaylistWithMusics>

    @Insert
    fun insertAll(vararg playlistDBS: PlaylistDB)

    @Insert
    fun insertOne(playlistDB: PlaylistDB): Long

    @Update
    fun update(playlistDB: PlaylistDB)

    @Delete
    fun remove(playlistDB: PlaylistDB)

    @Query("DELETE FROM playlists WHERE playlist_id = :id")
    fun remove(id: Long)
}