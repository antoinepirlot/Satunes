/*
 * This file is part of MP3 Player.
 *
 * MP3 Player is free software: you can redistribute it and/or modify it under
 *  the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * MP3 Player is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with MP3 Player.
 * If not, see <https://www.gnu.org/licenses/>.
 *
 * **** INFORMATIONS ABOUT THE AUTHOR *****
 * The author of this file is Antoine Pirlot, the owner of this project.
 * You find this original project on github.
 *
 * My github link is: https://github.com/antoinepirlot
 * This current project's link is: https://github.com/antoinepirlot/MP3-Player
 *
 * You can contact me via my email: pirlot.antoine@outlook.com
 * PS: I don't answer quickly.
 */

package earth.mp3player.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import earth.mp3player.database.daos.AlbumDAO
import earth.mp3player.database.daos.ArtistDAO
import earth.mp3player.database.daos.FolderDAO
import earth.mp3player.database.daos.GenreDAO
import earth.mp3player.database.daos.MusicDAO
import earth.mp3player.database.daos.PlaylistDAO
import earth.mp3player.database.models.tables.Album
import earth.mp3player.database.models.tables.Artist
import earth.mp3player.database.models.tables.Folder
import earth.mp3player.database.models.tables.Genre
import earth.mp3player.database.models.tables.Music
import earth.mp3player.database.models.tables.MusicsPlaylistsRel
import earth.mp3player.database.models.tables.Playlist

/**
 * @author Antoine Pirlot on 27/03/2024
 */

@Database(
    entities = [
        Album::class,
        Artist::class,
        Folder::class,
        Genre::class,
        Music::class,
        MusicsPlaylistsRel::class,
        Playlist::class
    ],
    version = 1
)
abstract class MP3PlayerDatabase: RoomDatabase() {
    companion object {
        private lateinit var database: MP3PlayerDatabase

        fun getDatabase(context: Context): MP3PlayerDatabase {
            if (!Companion::database::isInitialized.get()) {
                database = Room.databaseBuilder(
                    context = context,
                    MP3PlayerDatabase::class.java,
                    "MP3Player-database"
                ).build()
            }
            return database
        }
    }

    abstract fun albumDao(): AlbumDAO
    abstract fun artistDao(): ArtistDAO
    abstract fun folderDao(): FolderDAO
    abstract fun genreDao(): GenreDAO
    abstract fun musicDao(): MusicDAO
    abstract fun playlistDao(): PlaylistDAO
}