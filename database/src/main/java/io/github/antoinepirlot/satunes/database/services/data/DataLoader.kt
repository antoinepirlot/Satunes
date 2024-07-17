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

package io.github.antoinepirlot.satunes.database.services.data

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import io.github.antoinepirlot.satunes.database.R
import io.github.antoinepirlot.satunes.database.exceptions.DuplicatedAlbumException
import io.github.antoinepirlot.satunes.database.models.Album
import io.github.antoinepirlot.satunes.database.models.Artist
import io.github.antoinepirlot.satunes.database.models.Folder
import io.github.antoinepirlot.satunes.database.models.Genre
import io.github.antoinepirlot.satunes.database.models.Music
import io.github.antoinepirlot.satunes.database.services.database.DatabaseManager
import io.github.antoinepirlot.satunes.database.services.settings.SettingsManager
import io.github.antoinepirlot.satunes.utils.logger.SatunesLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

/**
 * @author Antoine Pirlot on 22/02/24
 */

object DataLoader {
    private val URI: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

    var isLoaded: MutableState<Boolean> = mutableStateOf(false)
    var isLoading: MutableState<Boolean> = mutableStateOf(false)

    // Music variables
    private var musicIdColumn: Int? = null
    private var musicNameColumn: Int? = null
    private var musicTitleColumn: Int? = null
    private var musicDurationColumn: Int? = null
    private var musicSizeColumn: Int? = null
    private var absolutePathColumnId: Int? = null

    // Albums variables
    private var albumNameColumn: Int? = null

    // Artists variables
    private var artistNameColumn: Int? = null

    //Genres variables
    private var genreNameColumn: Int? = null

    private const val UNKNOWN_ARTIST = "<unknown>"
    private const val UNKNOWN_ALBUM = "Unknown Album"
    private const val UNKNOWN_GENRE = "<unknown>"
    private val EXTERNAL_STORAGE_PATH: File = Environment.getExternalStorageDirectory()

    private var projection: Array<String> = arrayOf(
        // AUDIO
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.DISPLAY_NAME,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.SIZE,
        MediaStore.Audio.Media.DATA,

        //ALBUMS
        MediaStore.Audio.Albums.ALBUM,

        //ARTISTS
        MediaStore.Audio.Artists.ARTIST,

        //GENRES is added in init function if SDK >= Android Red Velvet Cake
    )

    private val selection: String = "${MediaStore.Audio.Media.DATA} LIKE ?" +
            if (SettingsManager.includeRingtonesChecked.value) {
                " OR ${MediaStore.Audio.Media.DATA} LIKE ?" +
                        " OR ${MediaStore.Audio.Media.DATA} LIKE ?" +
                        " OR ${MediaStore.Audio.Media.DATA} LIKE ?"
            } else {
                ""
            }

    private var selection_args: Array<String> = arrayOf("$EXTERNAL_STORAGE_PATH/Music/%")

    private val logger = SatunesLogger(name = this::class.java.name)

    init {
        if (SettingsManager.includeRingtonesChecked.value) {
            selection_args += "$EXTERNAL_STORAGE_PATH/Android/%"
            selection_args += "$EXTERNAL_STORAGE_PATH/Ringtones/%"
            selection_args += "$EXTERNAL_STORAGE_PATH/Notifications/%"
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            //Genre
            projection += MediaStore.Audio.Media.GENRE
        }
    }

    /**
     * Load all Media data from device's storage.
     */
    fun loadAllData(context: Context) {
        isLoading.value = true
        CoroutineScope(Dispatchers.IO).launch {
            context.contentResolver.query(
                URI,
                projection,
                selection,
                selection_args,
                null
            )?.use {
                loadColumns(cursor = it)
                while (it.moveToNext()) {
                    loadData(cursor = it, context = context)
                }
            }
            DatabaseManager(context = context).loadAllPlaylistsWithMusic()
            isLoaded.value = true
            isLoading.value = false
        }
    }

    /**
     * Cache columns and columns indices for data to load
     */
    private fun
            loadColumns(cursor: Cursor) {
        // Cache music columns indices.
        musicIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
        musicNameColumn =
            cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
        musicTitleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
        musicDurationColumn =
            cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
        musicSizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)

        absolutePathColumnId = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)


        //Cache album columns indices
        try {
            albumNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM)
        } catch (_: IllegalArgumentException) {
            // No album
        }

        // Cache artist columns indices.
        try {
            artistNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.ARTIST)
        } catch (_: IllegalArgumentException) {
            // No artist
        }

        // Cache Genre columns indices.
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                genreNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.GENRE)
            }
        } catch (_: IllegalArgumentException) {
            // No genre
        }
    }

    /**
     * Load data from cursor
     */
    private fun loadData(cursor: Cursor, context: Context) {
        //Load Artist
        val artist: Artist = loadArtist(context = context, cursor = cursor)

        //Load album
        val album: Album = try {
            loadAlbum(cursor = cursor, artist = artist, context = context)
        } catch (e: DuplicatedAlbumException) {
            // The album already exist
            e.existingAlbum
        }

        //Link album to artist if the album doesn't already have the album
        if (!artist.albumSortedMap.containsValue(value = album)) {
            artist.addAlbum(album = album)
        }

        val absolutePath: String = cursor.getString(absolutePathColumnId!!)

        //Load Genre
        val genre: Genre = loadGenre(context = context, cursor = cursor)

        //Load Folder
        val folder: Folder = loadFolder(absolutePath = absolutePath)

        //Load music and folder inside load music function
        try {
            loadMusic(
                context = context,
                cursor = cursor,
                album = album,
                artist = artist,
                folder = folder,
                genre = genre,
                absolutePath = absolutePath,
            )
        } catch (_: IllegalAccessError) {
            // No music found
            if (album.musicMediaItemMap.isEmpty()) {
                DataManager.removeAlbum(album = album)
            }
            if (artist.musicMediaItemMap.isEmpty()) {
                DataManager.removeArtist(artist = artist)
            }
            if (genre.musicMediaItemMap.isEmpty()) {
                DataManager.removeGenre(genre = genre)
            }
            if (folder.musicMediaItemMap.isEmpty()) {
                DataManager.removeFolder(folder = folder)
            }
        }
    }

    /**
     * Create a music object from the cursor and add it to the music list
     *
     * @param cursor the cursor where music's data is stored
     *
     * @return the created music
     */
    private fun loadMusic(
        context: Context,
        cursor: Cursor,
        album: Album,
        artist: Artist,
        folder: Folder,
        genre: Genre,
        absolutePath: String,
    ): Music {
        // Get values of columns for a given music.
        val id: Long = cursor.getLong(musicIdColumn!!)
        if (id < 1) {
            val message = "The id is less than 1"
            logger.severe(message)
            throw IllegalArgumentException(message)
        }
        val size = cursor.getInt(musicSizeColumn!!)
        if (size < 0) {
            val message = "Size is less than 0"
            logger.severe(message)
            throw IllegalArgumentException(message)
        }
        val duration: Long = cursor.getLong(musicDurationColumn!!)
        if (duration < 0) {
            val message = "Duration is less than 0"
            logger.severe(message)
            throw IllegalArgumentException(message)
        }
        val displayName: String = cursor.getString(musicNameColumn!!)
        val title: String = cursor.getString(musicTitleColumn!!)

        return Music(
            id = id,
            title = title,
            absolutePath = absolutePath,
            displayName = displayName,
            duration = duration,
            size = size,
            album = album,
            artist = artist,
            folder = folder,
            genre = genre,
            context = context
        )
    }

    /**
     * Load folder (create it if not exists) where the music is present
     *
     * @param absolutePath the absolute path to create folder and sub-folders if not already created
     */
    private fun loadFolder(absolutePath: String): Folder {
        val splitPath: MutableList<String> = mutableListOf()
        val splitList: List<String> = absolutePath.split("/")
        for (index: Int in 0..<splitList.lastIndex) {
            //Don't create a folder for the file (no folder called music.mp3)
            //The last name is a file
            val folderName: String = splitList[index]
            if (folderName !in listOf("", "storage", "emulated")) {
                splitPath.add(folderName)
            }
        }

        var rootFolder: Folder? = null

        DataManager.rootFolderSet.forEach { folder: Folder ->
            if (folder.title == splitPath[0]) {
                rootFolder = folder
                return@forEach
            }
        }

        if (rootFolder == null) {
            // No root folders in the list
            rootFolder = Folder(title = splitPath[0])
            DataManager.addFolder(folder = rootFolder!!)
        }

        splitPath.removeAt(0)
        rootFolder!!.createSubFolders(splitPath.toMutableList())
        return rootFolder!!.getSubFolder(splitPath.toMutableList())!!
    }

    private fun loadArtist(context: Context, cursor: Cursor): Artist {
        // Get values of columns for a given artist.
        return try {
            val name = cursor.getString(artistNameColumn!!)
            return if (name == UNKNOWN_ARTIST) {
                // Assign the Unknown Artist
                DataManager.getArtist(context.getString(R.string.unknown_artist))
            } else {
                DataManager.addArtist(artist = Artist(title = name))
            }
        } catch (_: NullPointerException) {
            val newArtist = Artist(title = context.getString(R.string.unknown_artist))
            DataManager.addArtist(artist = newArtist)
        }
    }

    private fun loadAlbum(context: Context, cursor: Cursor, artist: Artist?): Album {
        return try {
            val name = cursor.getString(albumNameColumn!!)
            return if (name == UNKNOWN_ALBUM) {
                // Assign the Unknown Album
                val album: Album =
                    DataManager.getAlbum(context.getString(R.string.unknown_album))
                if (album.artist != artist) {
                    throw NoSuchElementException()
                }
                album
            } else {
                val newAlbum = Album(title = name, artist = artist)
                DataManager.addAlbum(album = newAlbum)
                newAlbum
            }
        } catch (_: NoSuchElementException) {
            val newAlbum = Album(
                title = context.getString(R.string.unknown_album),
                artist = artist
            )
            DataManager.addAlbum(album = newAlbum)
            newAlbum
        }
    }


    private fun loadGenre(context: Context, cursor: Cursor): Genre {
        return try {
            val name: String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                cursor.getString(genreNameColumn!!)
            } else {
                getGenreNameForAndroidQAndLess(context = context, cursor = cursor)
            }
            return if (name == UNKNOWN_GENRE) {
                // Assign the Unknown Genre
                DataManager.getGenre(context.getString(R.string.unknown_genre))

            } else {
                DataManager.addGenre(genre = Genre(title = name))
            }
        } catch (_: NullPointerException) {
            val newGenre = Genre(title = context.getString(R.string.unknown_genre))
            DataManager.addGenre(genre = newGenre)
        }
    }

    private fun getGenreNameForAndroidQAndLess(context: Context, cursor: Cursor): String {
        val genreProj: Array<String> =
            arrayOf(MediaStore.Audio.Genres._ID, MediaStore.Audio.Genres.NAME)
        val musicId: Int = cursor.getInt(musicIdColumn!!)
        val genreUri: Uri = MediaStore.Audio.Genres.getContentUriForAudioId("external", musicId)
        val genreCursor: Cursor? =
            context.contentResolver.query(genreUri, genreProj, null, null, null)
        val genreIndex: Int = genreCursor!!.getColumnIndexOrThrow(MediaStore.Audio.Genres.NAME)
        val genreName: String =
            if (genreCursor.moveToNext()) {
                genreCursor.getString(genreIndex)
            } else {
                UNKNOWN_GENRE
            }
        genreCursor.close()
        return genreName
    }
}