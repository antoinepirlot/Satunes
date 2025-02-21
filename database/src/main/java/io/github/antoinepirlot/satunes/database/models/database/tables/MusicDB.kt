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
 * You find this original project on Codeberg.
 *
 * My Codeberg link is: https://codeberg.org/antoinepirlot
 * This current project's link is: https://codeberg.org/antoinepirlot/Satunes
 */

package io.github.antoinepirlot.satunes.database.models.database.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import io.github.antoinepirlot.satunes.database.exceptions.MusicNotFoundException
import io.github.antoinepirlot.satunes.database.models.Media
import io.github.antoinepirlot.satunes.database.models.Music
import io.github.antoinepirlot.satunes.database.services.data.DataManager
import io.github.antoinepirlot.satunes.database.services.database.DatabaseManager
import io.github.antoinepirlot.satunes.utils.logger.SatunesLogger
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * @author Antoine Pirlot on 30/03/2024
 */

@Serializable
@Entity("musics")
internal data class MusicDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("music_id") override val id: Long = 0,
    @ColumnInfo("absolute_path") var absolutePath: String,
) : Media {
    @Transient
    @Ignore
    private val _logger: SatunesLogger? = SatunesLogger.getLogger()

    @Ignore
    @Transient
    override lateinit var title: String

    var liked: Boolean = false

    @Ignore
    @Transient
    var music: Music? =
        try {
            try {
                DataManager.getMusic(absolutePath = absolutePath)
            } catch (_: NullPointerException) {
                //The path has changed
                try {
                    val music: Music = DataManager.getMusic(id = id)
                    DatabaseManager.getInstance().updateMusic(music)
                    music
                } catch (_: MusicNotFoundException) {
                    null
                }
            }
        } catch (e: Throwable) {
            val message = "An error occurred while getting music in musicDB"
            _logger?.severe(message)
            throw e
        }
}