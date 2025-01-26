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
import androidx.room.Update
import io.github.antoinepirlot.satunes.database.models.database.tables.MusicDB

/**
 * @author Antoine Pirlot on 27/03/2024
 */

@Dao
internal interface MusicDAO {

    @Query("SELECT count(music_id) FROM musics")
    fun count(): Long

    @Query("SELECT * FROM musics WHERE music_id == :id")
    fun get(id: Long): MusicDB?

    @Query("SELECT * FROM musics")
    fun getAll(): List<MusicDB>

    @Insert
    fun insert(vararg musics: MusicDB)

    @Update
    fun update(vararg musicDBs: MusicDB)

    @Query("UPDATE musics SET liked = 1 WHERE music_id = :musicId")
    fun like(musicId: Long)

    @Query("UPDATE musics SET liked = 0 WHERE music_id = :musicId")
    fun unlike(musicId: Long)

    @Delete
    fun delete(music: MusicDB)

    @Query("DELETE from musics WHERE music_id = :musicId")
    fun delete(musicId: Long)
}