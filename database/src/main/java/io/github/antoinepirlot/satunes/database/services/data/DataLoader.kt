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
import android.provider.MediaStore
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import io.github.antoinepirlot.satunes.database.R
import io.github.antoinepirlot.satunes.database.models.Album
import io.github.antoinepirlot.satunes.database.models.Artist
import io.github.antoinepirlot.satunes.database.models.Folder
import io.github.antoinepirlot.satunes.database.models.FoldersSelection
import io.github.antoinepirlot.satunes.database.models.Genre
import io.github.antoinepirlot.satunes.database.models.Music
import io.github.antoinepirlot.satunes.database.services.database.DatabaseManager
import io.github.antoinepirlot.satunes.database.services.settings.SettingsManager
import io.github.antoinepirlot.satunes.database.services.widgets.WidgetDatabaseManager
import io.github.antoinepirlot.satunes.utils.logger.SatunesLogger

/**
 * @author Antoine Pirlot on 22/02/24
 */

object DataLoader {
    private val URI: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

    val isLoaded: MutableState<Boolean> = mutableStateOf(false)
    val isLoading: MutableState<Boolean> = mutableStateOf(false)

    // Music variables
    private var musicIdColumn: Int? = null
    private var musicNameColumn: Int? = null
    private var musicTitleColumn: Int? = null
    private var musicDurationColumn: Int? = null
    private var musicSizeColumn: Int? = null
    private var absolutePathColumnId: Int? = null

    // Albums variables
    private var albumNameColumn: Int? = null
    private var albumArtistColumn: Int? = null
    private var albumCompilationColumn: Int? = null
    private var cdTrackNumberColumn: Int? = null
    private var albumYearColumn: Int? = null

    // Artists variables
    private var artistNameColumn: Int? = null

    //Genres variables
    private var genreNameColumn: Int? = null

    private const val UNKNOWN_ARTIST = "<unknown>"
    private const val UNKNOWN_ALBUM = "Unknown Album"
    private const val UNKNOWN_GENRE = "<unknown>"

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
        MediaStore.Audio.Media.ALBUM_ARTIST,
        //TODO test the next fields for old Android versions
        MediaStore.Audio.Media.YEAR,

        //ARTISTS
        MediaStore.Audio.Artists.ARTIST,

        //GENRES is added in init function if SDK >= Android Red Velvet Cake
    )

    private lateinit var selection: String //see loadFoldersPaths function

    private lateinit var selection_args: Array<String> //see loadFoldersPaths function

    private val _logger = SatunesLogger.getLogger()

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            //Albums
            projection += MediaStore.Audio.Media.CD_TRACK_NUMBER
            //Genre
            projection += MediaStore.Audio.Media.GENRE
            projection += MediaStore.Audio.Media.COMPILATION
        } else {
            projection += MediaStore.Audio.Media.TRACK
        }
    }

    /**
     * Load folders path to include or exclude from Data query.
     */
    internal fun loadFoldersPaths() {
        this.selection = ""
        this.selection_args = arrayOf()

        val foldersSelection: FoldersSelection = SettingsManager.foldersSelectionSelected
        for (path: String in SettingsManager.foldersPathsSelectedSet.value) {
            if (path != SettingsManager.foldersPathsSelectedSet.value.first()) {
                this.selection += foldersSelection.andOrQueryAttribute + ' '
            }
            this.selection += "${MediaStore.Audio.Media.DATA} "
            this.selection += "${foldersSelection.likeQueryAttribute} ? "

            if (path.split("/")[1] == "0") {
                this.selection_args += "/storage/emulated$path" // the first '/' is already in the path
            } else {
                this.selection_args += "/storage$path" // the first '/' is already in the path
            }
        }
    }

    fun resetAllData() {
        if (isLoading.value) return
        this.loadFoldersPaths()
        DataManager.resetAllData()
        isLoaded.value = false
    }

    /**
     * Load all Media data from device's storage.
     */
    suspend fun loadAllData(context: Context) {
        if (isLoading.value || (isLoaded.value && DataManager.getMusicSet().isNotEmpty())) return

        isLoading.value = true
        // this allow data to be reloaded if no data loaded
        if (DataManager.getMusicSet().isEmpty()) this.resetAllData()

        WidgetDatabaseManager.refreshWidgets()
        if (!this@DataLoader::selection.isInitialized || !this@DataLoader::selection_args.isInitialized) {
            this@DataLoader.loadFoldersPaths()
        }

        if (
            this@DataLoader.selection_args.isNotEmpty()
            || SettingsManager.foldersSelectionSelected != FoldersSelection.INCLUDE
        ) {
            context.contentResolver.query(
                URI,
                projection,
                this@DataLoader.selection,
                this@DataLoader.selection_args,
                null
            )?.use {
                _logger.info("${it.count} musics to load.")
                loadColumns(cursor = it)
                while (it.moveToNext()) {
                    loadData(cursor = it, context = context)
                }
            }
        }
        DatabaseManager.initInstance(context = context).loadAllPlaylistsWithMusic()
        WidgetDatabaseManager.refreshWidgets()
        isLoaded.value = true
        isLoading.value = false
    }

    /**
     * Cache columns and columns indices for data to load
     */
    private fun loadColumns(cursor: Cursor) {
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
            albumArtistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ARTIST)
            cdTrackNumberColumn =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                    cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.CD_TRACK_NUMBER)
                else cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK)
            albumYearColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                albumCompilationColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.COMPILATION)
            }
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
        val album: Album = loadAlbum(cursor = cursor, context = context)

        //Link album to artist if the album doesn't already have the album
        artist.addAlbum(album = album)

        val absolutePath: String = cursor.getString(absolutePathColumnId!!)

        //Load Genre
        val genre: Genre = loadGenre(context = context, cursor = cursor, album = album)

        //Load Folder
        val folder: Folder = loadFolder(absolutePath = absolutePath)

        //Load music and folder inside load music function
        try {
            loadMusic(
                cursor = cursor,
                album = album,
                artist = artist,
                folder = folder,
                genre = genre,
                absolutePath = absolutePath,
            )
        } catch (e: Throwable) {
            _logger.warning(e.message)

            // No music found
            if (album.isEmpty()) {
                DataManager.removeAlbum(album = album)
            }
            if (artist.isEmpty()) {
                DataManager.removeArtist(artist = artist)
            }
            if (genre.isEmpty()) {
                DataManager.removeGenre(genre = genre)
            }
            if (folder.isEmpty()) {
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
            val message = "Id < 1"
            _logger.severe(message)
            throw IllegalArgumentException(message)
        }
        val size = cursor.getInt(musicSizeColumn!!)
        if (size <= 0) {
            val message = "Size <= 0"
            throw IllegalArgumentException(message)
        }
        val duration: Long = cursor.getLong(musicDurationColumn!!)
        if (duration <= 0) {
            val message = "Duration <= 0"
            throw IllegalArgumentException(message)
        }
        val displayName: String = cursor.getString(musicNameColumn!!)
        val title: String = cursor.getString(musicTitleColumn!!)
        val cdTrackNumber: Int = cursor.getInt(cdTrackNumberColumn!!)

        return Music(
            id = id,
            title = title,
            absolutePath = absolutePath,
            displayName = displayName,
            duration = duration,
            size = size,
            cdTrackNumber = cdTrackNumber,
            album = album,
            artist = artist,
            folder = folder,
            genre = genre,
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

        DataManager.getRootFolderSet().forEach { folder: Folder ->
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
        var name = try {
            cursor.getString(artistNameColumn!!)
        } catch (e: NullPointerException) {
            UNKNOWN_ARTIST
        }


        val isCompilation: Boolean =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                cursor.getInt(albumCompilationColumn!!) == 1
            } else {
                false
            }

        if (name == UNKNOWN_ARTIST) {
            name = if (isCompilation) {
                context.getString(R.string.various_artists)
            } else {
                context.getString(R.string.unknown_artist)
            }
        }

        return DataManager.addArtist(artist = Artist(title = name))
    }

    private fun loadAlbumArtist(context: Context, cursor: Cursor): Artist {
        var name: String = try {
            cursor.getString(albumArtistColumn!!)
        } catch (e: NullPointerException) {
            UNKNOWN_ARTIST
        }

        val isCompilation: Boolean =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                cursor.getInt(albumCompilationColumn!!) == 1
            } else {
                false
            }

        if (name == UNKNOWN_ARTIST) {
            name = if (isCompilation) {
                context.getString(R.string.various_artists)
            } else if (SettingsManager.artistReplacement) {
                //Load music's artist
                return this.loadArtist(context = context, cursor = cursor)
            } else {
                context.getString(R.string.unknown_artist)
            }
        }

        return DataManager.addArtist(artist = Artist(title = name))
    }

    private fun loadAlbum(context: Context, cursor: Cursor): Album {
        var name = try {
            cursor.getString(albumNameColumn!!)
        } catch (e: NullPointerException) {
            UNKNOWN_ALBUM
        }

        val isCompilation: Boolean =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                cursor.getInt(albumCompilationColumn!!) == 1
            } else {
                false
            }

        if (name == UNKNOWN_ALBUM) {
            name = context.getString(R.string.unknown_album)
        }

        val year: Int = cursor.getInt(albumYearColumn!!)

        val artist: Artist = loadAlbumArtist(context = context, cursor = cursor)

        val album: Album = DataManager.addAlbum(
            album = Album(
                title = name,
                artist = artist,
                isCompilation = isCompilation,
                year = year
            )
        )
        artist.addAlbum(album = album)
        return album
    }


    private fun loadGenre(context: Context, cursor: Cursor, album: Album): Genre {
        var name: String = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                cursor.getString(genreNameColumn!!)
            } else {
                getGenreNameForAndroidQAndLess(context = context, cursor = cursor)
            }
        } catch (e: NullPointerException) {
            UNKNOWN_GENRE
        }

        if (name == UNKNOWN_GENRE) {
            name = context.getString(R.string.unknown_genre)
        }
        val genre: Genre = DataManager.addGenre(genre = Genre(title = name))
        genre.addAlbum(album = album)
        return genre
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