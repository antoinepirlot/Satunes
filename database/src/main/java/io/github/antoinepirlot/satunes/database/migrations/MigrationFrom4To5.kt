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
            this.renameOldTables(db = db)
            this.createNewTables(db = db)
            this.migrateData(db = db)
            this.dropOldTables(db = db)
        } catch (e: Throwable) {
            _logger?.severe(e.message)
            throw e
        }
    }

    private fun renameOldTables(db: SupportSQLiteDatabase) {
        db.execSQL(sql = "ALTER TABLE musics RENAME TO old_musics;")
        db.execSQL(sql = "ALTER TABLE musics_playlists_rel RENAME TO old_musics_playlists_rel;")
    }

    private fun createNewTables(db: SupportSQLiteDatabase) {
        db.execSQL(
            sql =
                "CREATE TABLE musics (" +
                        "music_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                        "local_id INTEGER NULL UNIQUE DEFAULT NULL," +
                        "subsonic_id TEXT NULL UNIQUE DEFAULT NULL," +
                        "absolute_path TEXT NOT NULL," +
                        "liked INTEGER NOT NULL" +
                        ");"
        )
        db.execSQL(sql = "CREATE UNIQUE INDEX IF NOT EXISTS index_musics_local_id_subsonic_id ON musics(local_id ASC, subsonic_id ASC);")

        db.execSQL(
            sql =
                "CREATE TABLE musics_playlists_rel (" +
                        "music_id INTEGER NOT NULL," +
                        "playlist_id INTEGER NOT NULL," +
                        "added_date_ms INTEGER NOT NULL," +
                        "PRIMARY KEY(music_id, playlist_id)," +
                        "FOREIGN KEY (playlist_id) REFERENCES playlists(playlist_id) ON DELETE NO ACTION ON UPDATE NO ACTION," +
                        "FOREIGN KEY (music_id) REFERENCES musics(music_id) ON DELETE NO ACTION ON UPDATE NO ACTION" +
                        ");"
        )
    }

    private fun migrateData(db: SupportSQLiteDatabase) {
        db.execSQL(
            sql =
                "INSERT INTO musics (local_id, absolute_path, liked) " +
                        "SELECT music_id, absolute_path, liked " +
                        "FROM old_musics;"
        )
        db.execSQL(
            sql =
                "INSERT INTO 'musics_playlists_rel' (music_id, playlist_id, added_date_ms) " +
                        "SELECT (SELECT m.music_id FROM musics AS m WHERE m.local_id = o.music_id), o.playlist_id, o.added_date_ms " +
                        "FROM old_musics_playlists_rel AS o;"
        )
    }

    private fun dropOldTables(db: SupportSQLiteDatabase) {
        db.execSQL(sql = "DROP TABLE old_musics;")
        db.execSQL(sql = "DROP TABLE old_musics_playlists_rel;")
    }
}