package io.github.antoinepirlot.satunes.database.models.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import io.github.antoinepirlot.satunes.database.daos.MusicDAO
import io.github.antoinepirlot.satunes.database.daos.MusicsPlaylistsRelDAO
import io.github.antoinepirlot.satunes.database.daos.PlaylistDAO
import io.github.antoinepirlot.satunes.database.migrations.MigrationFrom1To2
import io.github.antoinepirlot.satunes.database.migrations.MigrationFrom2To3
import io.github.antoinepirlot.satunes.database.migrations.MigrationFrom3To4
import io.github.antoinepirlot.satunes.database.migrations.MigrationFrom4To5
import io.github.antoinepirlot.satunes.database.models.database.tables.MusicDB
import io.github.antoinepirlot.satunes.database.models.database.tables.MusicsPlaylistsRel
import io.github.antoinepirlot.satunes.database.models.database.tables.PlaylistDB

/**
 * @author Antoine Pirlot on 27/03/2024
 */

@Database(
    entities = [
        MusicDB::class,
        MusicsPlaylistsRel::class,
        PlaylistDB::class
    ],
    version = 5,
    exportSchema = true,
)
internal abstract class SatunesDatabase : RoomDatabase() {
    companion object {
        private var database: SatunesDatabase? = null

        fun getDatabase(context: Context): SatunesDatabase {
            if (database == null) {
                database = Room.databaseBuilder(
                    context = context,
                    SatunesDatabase::class.java,
                    "Satunes-database"
                )
                    .addMigrations(
                        MigrationFrom1To2,
                        MigrationFrom2To3,
                        MigrationFrom3To4,
                        MigrationFrom4To5
                    )
                    .build()
            }
            return database!!
        }
    }

    abstract fun musicDao(): MusicDAO
    abstract fun playlistDao(): PlaylistDAO
    abstract fun musicsPlaylistsRelDao(): MusicsPlaylistsRelDAO
}