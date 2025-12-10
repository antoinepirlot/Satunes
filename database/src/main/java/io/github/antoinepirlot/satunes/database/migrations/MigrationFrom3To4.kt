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

package io.github.antoinepirlot.satunes.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import io.github.antoinepirlot.satunes.database.models.media.Music
import io.github.antoinepirlot.satunes.database.services.data.DataManager
import io.github.antoinepirlot.satunes.utils.logger.Logger

/**
 * @author Antoine Pirlot 31/01/2025
 */
object MigrationFrom3To4 : Migration(3, 4) {
    private val _logger: Logger? = Logger.getLogger()

    override fun migrate(db: SupportSQLiteDatabase) {
        val now: Long = System.currentTimeMillis()
        try {
            db.execSQL("ALTER TABLE musics_playlists_rel ADD COLUMN added_date_ms BIGINT NOT NULL DEFAULT 0;")
            DataManager.getMusicSet().forEach { music: Music ->
                db.execSQL(
                    "UPDATE musics_playlists_rel" +
                            " SET added_date_ms = ?" +
                            " WHERE music_id = ?;", arrayOf(now, music.id)
                )
            }
        } catch (e: Throwable) {
            _logger?.severe(e.message)
            throw e
        }
    }
}