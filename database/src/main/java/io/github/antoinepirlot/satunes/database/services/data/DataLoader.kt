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

package io.github.antoinepirlot.satunes.database.services.data

import android.content.Context
import android.database.Cursor
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import io.github.antoinepirlot.satunes.database.R
import io.github.antoinepirlot.satunes.database.models.media.Album
import io.github.antoinepirlot.satunes.database.models.media.Artist
import io.github.antoinepirlot.satunes.database.models.media.Folder
import io.github.antoinepirlot.satunes.database.models.media.Genre
import io.github.antoinepirlot.satunes.database.models.media.Music
import io.github.antoinepirlot.satunes.database.services.database.DatabaseManager
import io.github.antoinepirlot.satunes.database.services.settings.SettingsManager
import io.github.antoinepirlot.satunes.database.services.widgets.WidgetDatabaseManager
import io.github.antoinepirlot.satunes.utils.logger.Logger

/**
 * @author Antoine Pirlot on 22/02/24
 */

object DataLoader {
    private val URI: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

    val isLoaded: MutableState<Boolean> = mutableStateOf(false)
    val isLoading: MutableState<Boolean> = mutableStateOf(false)

    // Music variables
    private var musicIdColumnId: Int? = null
    private var musicNameColumnId: Int? = null
    private var musicTitleColumnId: Int? = null
    private var musicDurationColumnId: Int? = null
    private var musicSizeColumnId: Int? = null
    private var absolutePathColumnId: Int? = null
    private var dateAddedPathColumnId: Int? = null

    // Albums variables
    private var albumNameColumnId: Int? = null
    private var albumArtistColumnId: Int? = null
    private var albumCompilationColumnId: Int? = null
    private var cdTrackNumberColumnId: Int? = null
    private var albumYearColumnId: Int? = null

    // Artists variables
    private var artistNameColumnId: Int? = null

    //Genres variables
    private var genreNameColumnId: Int? = null

    private const val UNKNOWN_ARTIST = "<unknown>"
    private const val UNKNOWN_ALBUM = "<unknown>"
    private const val UNKNOWN_GENRE = "<unknown>"

    private var projection: Array<String> = arrayOf(
        // AUDIO
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.DISPLAY_NAME,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.SIZE,
        MediaStore.Audio.Media.DATA,
        MediaStore.Audio.Media.DATE_ADDED,

        //ALBUMS
        MediaStore.Audio.Albums.ALBUM,
        MediaStore.Audio.Media.ALBUM_ARTIST,
        MediaStore.Audio.Media.YEAR,

        //ARTISTS
        MediaStore.Audio.Artists.ARTIST,

        //GENRES is added in init function if SDK >= Android Red Velvet Cake
    )

    private lateinit var selection: String //see loadFoldersPaths function

    private lateinit var selection_args: Array<String> //see loadFoldersPaths function

    private val _logger: Logger? = Logger.getLogger()

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

        val hasExcluding: Boolean = SettingsManager.foldersPathsExcludingCollection.isNotEmpty()
        if (SettingsManager.foldersPathsIncludingCollection.isNotEmpty()) {
            this.addPaths(SettingsManager.foldersPathsIncludingCollection, or = true, like = true)
            if (hasExcluding) this.selection += "AND "
        }
        if (hasExcluding)
            this.addPaths(SettingsManager.foldersPathsExcludingCollection, or = false, like = false)
    }


    /**
     * Add paths to selection
     * @param list the list of [String] references paths
     * @param or a [Boolean], true if the comparator keyword is OR (true) or AND (false).
     * @param like a [Boolean], true if the like comparator is LIKE (true) or NOT LIKE (false)
     */
    private fun addPaths(list: Collection<String>, or: Boolean, like: Boolean) {
        if (or) this.selection += '(' //Because the last or condition must not be associated by other AND
        for (path: String in list) {
            if (path != list.first()) this.selection += if (or) "OR " else "AND "

            this.selection += "${MediaStore.Audio.Media.DATA} "
            this.selection += if (like) "LIKE ? " else "NOT LIKE ? "

            if (path.split("/")[1] == "0")
                this.selection_args += "/storage/emulated$path" // the first '/' is already in the path
            else
                this.selection_args += "/storage$path" // the first '/' is already in the path
        }
        if (or) this.selection += ')' //Because the last or condition must not be associated by other AND
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
    fun loadAllData(context: Context) {
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
        ) {
            context.contentResolver.query(
                URI,
                projection,
                this@DataLoader.selection,
                this@DataLoader.selection_args,
                null
            )?.use {
                _logger?.info("${it.count} musics to load.")
                loadColumns(cursor = it)
                while (it.moveToNext()) loadData(cursor = it, context = context)
            }
        }
        DatabaseManager.initInstance(context = context).loadAllPlaylistsWithMusic()

        WidgetDatabaseManager.refreshWidgets()
        isLoaded.value = true
        isLoading.value = false
    }

    /**
     * Load single music with its path (used when opening from file explorer).
     */
    fun load(context: Context, uri: Uri): Music? {
        var music: Music? = null
        context.contentResolver.query(
            uri,
            projection,
            null,
            null,
            null
        )?.use {
            _logger?.info("${it.count} music found (if everything is okay, it should be one")
            loadColumns(cursor = it)
            while (it.moveToNext()) music = loadData(cursor = it, context = context, uri = uri)
        }
        return music
    }

    /**
     * Cache columns and columns indices for data to load
     */
    private fun loadColumns(cursor: Cursor) {
        // Cache music columns indices.
        musicIdColumnId = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
        musicNameColumnId =
            cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
        musicTitleColumnId = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
        musicDurationColumnId =
            cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
        musicSizeColumnId = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
        absolutePathColumnId = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
        dateAddedPathColumnId = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)

        //Cache album columns indices
        try {
            albumNameColumnId = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM)
            albumArtistColumnId = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ARTIST)
            cdTrackNumberColumnId =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                    cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.CD_TRACK_NUMBER)
                else cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK)
            albumYearColumnId = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                albumCompilationColumnId =
                    cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.COMPILATION)
            }
        } catch (_: IllegalArgumentException) {
            // No album
        }

        // Cache artist columns indices.
        try {
            artistNameColumnId = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.ARTIST)
        } catch (_: IllegalArgumentException) {
            // No artist
        }

        // Cache Genre columns indices.
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                genreNameColumnId = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.GENRE)
            }
        } catch (_: IllegalArgumentException) {
            // No genre
        }
    }

    /**
     * Load data from cursor
     *
     * @param uri is the [Uri] used for temporary music
     */
    private fun loadData(cursor: Cursor, context: Context, uri: Uri? = null): Music? {
        val absolutePath: String? = cursor.getString(absolutePathColumnId!!) ?: uri?.path!!
        // /!\ Do not check if File exist here as it will slower the loading, it is check in Music constructor
        //Load Artist
        val artist: Artist = loadArtist(context = context, cursor = cursor, uri = uri)

        //Load album
        val album: Album = loadAlbum(cursor = cursor, context = context, uri = uri)

        //Link album to artist if the album doesn't already have the album
        artist.addAlbum(album = album)


        //Load Genre
        val genre: Genre = loadGenre(context = context, cursor = cursor, album = album, uri = uri)

        //Load Folder
        val folder: Folder = loadFolder(context = context, absolutePath = absolutePath!!)

        //Load music and folder inside load music function
        return try {
            loadMusic(
                context = context,
                cursor = cursor,
                album = album,
                artist = artist,
                folder = folder,
                genre = genre,
                absolutePath = absolutePath,
                uri = uri
            )
        } catch (e: Throwable) {
            _logger?.warning(e.message)

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
            null
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
        uri: Uri?
    ): Music {
        // Get values of columns for a given music.
        var mmr: MediaMetadataRetriever? = null
        if (uri != null) {
            mmr = MediaMetadataRetriever()
            mmr.setDataSource(context, uri)
        }

        val id: Long = cursor.getLong(musicIdColumnId!!)
        if (id < 0L) {
            val message = "Id < 0"
            _logger?.severe(message)
            throw IllegalArgumentException(message)
        }

        val size = cursor.getInt(musicSizeColumnId!!)
        if (size < 0) {
            val message = "Size <= 0"
            throw IllegalArgumentException(message)
        }
        var duration: Long = cursor.getLong(musicDurationColumnId!!)
        if (duration < 0L) {
            val message = "Duration <= 0"
            throw IllegalArgumentException(message)
        } else if (duration == 0L) {
            duration =
                mmr?.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0L
        }
        val displayName: String = cursor.getString(musicNameColumnId!!)
        val title: String = cursor.getString(musicTitleColumnId!!)
            ?: mmr?.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
            ?: ""
        val cdTrackNumber: Int = cursor.getInt(cdTrackNumberColumnId!!)
        val dateAdded: Long = cursor.getInt(dateAddedPathColumnId!!).toLong() * 1000L

        val music = Music(
            id = id,
            title = title,
            absolutePath = absolutePath,
            displayName = displayName,
            duration = duration,
            size = size,
            cdTrackNumber = cdTrackNumber,
            addedDateMs = dateAdded,
            album = album,
            artist = artist,
            folder = folder,
            genre = genre,
            uri = uri
        )
        return music
    }

    /**
     * Load folder (create it if not exists) where the music is present
     *
     * @param absolutePath the absolute path to create folder and sub-folders if not already created
     */
    private fun loadFolder(context: Context, absolutePath: String): Folder {
        val splitPath: Collection<String> =
            this.getPathList(context = context, absolutePath = absolutePath)
        val rootFolder: Folder = DataManager.getRootFolder()
        rootFolder.createSubFolders(splitPath)
        return rootFolder.getSubFolder(splitPath.toMutableList())!!
    }

    /**
     * Returns the path list by removing storage and emulated.
     */
    private fun getPathList(context: Context, absolutePath: String): Collection<String> {
        val splitPathToReturn: MutableCollection<String> = mutableListOf()
        val splitList: List<String> = absolutePath.split("/")
        var canAddPath: Boolean = false
        var storageNameCanBeProcessed: Boolean = false
        for (i: Int in 0..<splitList.lastIndex) { //Do not process the final file which is the music.
            val folderName: String =
                if (storageNameCanBeProcessed) {
                    storageNameCanBeProcessed = false
                    if (splitList[i] == "0") context.getString(R.string.this_device)
                    else splitList[i]
                } else splitList[i]

            if (canAddPath) splitPathToReturn.add(folderName)
            else if (
                folderName == "emulated"
                || folderName == "storage" && splitList[i + 1] != "emulated" //isExternalStorage
            ) {
                canAddPath = true
                storageNameCanBeProcessed = true
            }
        }
        return splitPathToReturn
    }

    private fun loadArtist(context: Context, cursor: Cursor, uri: Uri?): Artist {
        // Get values of columns for a given artist.
        var name: String = try {
            cursor.getString(artistNameColumnId!!)
        } catch (_: NullPointerException) {
            if (uri != null) {
                val mmr = MediaMetadataRetriever()
                mmr.setDataSource(context, uri)
                mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) ?: UNKNOWN_ARTIST
            } else UNKNOWN_ARTIST
        }


        val isCompilation: Boolean =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                cursor.getInt(albumCompilationColumnId!!) == 1
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

    private fun loadAlbumArtist(context: Context, cursor: Cursor, uri: Uri?): Artist {
        var name: String = try {
            cursor.getString(albumArtistColumnId!!)
        } catch (_: NullPointerException) {
            if (uri != null) {
                val mmr = MediaMetadataRetriever()
                mmr.setDataSource(context, uri)
                mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST)
                    ?: UNKNOWN_ARTIST
            } else UNKNOWN_ARTIST
        }

        val isCompilation: Boolean =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                cursor.getInt(albumCompilationColumnId!!) == 1
            } else {
                false
            }

        if (name == UNKNOWN_ARTIST) {
            name = if (isCompilation) {
                context.getString(R.string.various_artists)
            } else if (SettingsManager.artistReplacement) {
                //Load music's artist
                return this.loadArtist(context = context, cursor = cursor, uri = uri)
            } else {
                context.getString(R.string.unknown_artist)
            }
        }

        return DataManager.addArtist(artist = Artist(title = name))
    }

    private fun loadAlbum(context: Context, cursor: Cursor, uri: Uri?): Album {
        var name: String = try {
            cursor.getString(albumNameColumnId!!)
        } catch (_: NullPointerException) {
            if (uri != null) {
                val mmr = MediaMetadataRetriever()
                mmr.setDataSource(context, uri)
                mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM) ?: UNKNOWN_ALBUM
            } else UNKNOWN_ALBUM
        }

        val isCompilation: Boolean =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                cursor.getInt(albumCompilationColumnId!!) == 1
            } else {
                false
            }

        if (name == UNKNOWN_ALBUM) {
            name = context.getString(R.string.unknown_album)
        }

        val year: Int = cursor.getInt(albumYearColumnId!!)

        val artist: Artist = loadAlbumArtist(context = context, cursor = cursor, uri = uri)

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


    private fun loadGenre(context: Context, cursor: Cursor, album: Album, uri: Uri?): Genre {
        var name: String = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                cursor.getString(genreNameColumnId!!)
            } else {
                getGenreNameForAndroidQAndLess(context = context, cursor = cursor)
            }
        } catch (_: NullPointerException) {
            if (uri != null) {
                val mmr = MediaMetadataRetriever()
                mmr.setDataSource(context, uri)
                mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE) ?: UNKNOWN_GENRE
            } else UNKNOWN_GENRE
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
        val musicId: Int = cursor.getInt(musicIdColumnId!!)
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