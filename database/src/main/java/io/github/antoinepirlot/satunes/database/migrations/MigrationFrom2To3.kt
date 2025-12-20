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
import io.github.antoinepirlot.android.utils.logger.Logger
import io.github.antoinepirlot.satunes.database.models.media.Music
import io.github.antoinepirlot.satunes.database.services.data.DataManager

/**
 * @author Antoine Pirlot on 26/07/2024
 */

internal object MigrationFrom2To3 : Migration(2, 3) {
    private val _logger: Logger? = Logger.getLogger()

    override fun migrate(db: SupportSQLiteDatabase) {
        try {
            db.execSQL("ALTER TABLE musics ADD COLUMN absolute_path TEXT NOT NULL DEFAULT '';")
            DataManager.getMusicSet().forEach { music: Music ->
                db.execSQL(
                    "UPDATE musics" +
                            " SET absolute_path = ?" +
                            " WHERE music_id = ?;", arrayOf(music.absolutePath, music.id)
                )
            }
        } catch (e: Throwable) {
            _logger?.severe(e.message)
            throw e
        }
    }
}