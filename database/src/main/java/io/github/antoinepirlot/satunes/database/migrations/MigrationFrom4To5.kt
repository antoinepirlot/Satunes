package io.github.antoinepirlot.satunes.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import io.github.antoinepirlot.android.utils.logger.Logger

/**
 * @author Antoine Pirlot 23/12/2025
 */
internal object MigrationFrom4To5 : Migration(startVersion = 4, endVersion = 5) {
    private val _logger: Logger? = Logger.getLogger()

    override fun migrate(db: SupportSQLiteDatabase) {
        try {
            db.execSQL("ALTER TABLE musics ADD COLUMN subsonic_id INTEGER NULL DEFAULT NULL;")
        } catch (e: Throwable) {
            _logger?.severe(e.message)
            throw e
        }
    }
}