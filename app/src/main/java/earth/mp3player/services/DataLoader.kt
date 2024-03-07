/*
 *  This file is part of MP3 Player.
 *
 *  MP3 Player is free software: you can redistribute it and/or modify it under
 *  the terms of the GNU General Public License as published by the Free Software Foundation,
 *  either version 3 of the License, or (at your option) any later version.
 *
 *  MP3 Player is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *   without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with MP3 Player.
 *  If not, see <https://www.gnu.org/licenses/>.

 *  ***** INFORMATIONS ABOUT THE AUTHOR *****
 *  The author of this file is Antoine Pirlot, the owner of this project.
 *  You find this original project on github.
 *
 *  My github link is: https://github.com/antoinepirlot
 *  This current project's link is: https://github.com/antoinepirlot/MP3-Player
 *
 *  You can contact me via my email: pirlot.antoine@outlook.com
 * PS: I don't answer quickly.
 */

package earth.mp3player.services

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import androidx.compose.runtime.MutableLongState
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.ui.graphics.asImageBitmap
import androidx.media3.common.MediaItem
import earth.mp3player.models.Album
import earth.mp3player.models.Artist
import earth.mp3player.models.Folder
import earth.mp3player.models.Genre
import earth.mp3player.models.Music
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.SortedMap

object DataLoader {
    const val FIRST_FOLDER_INDEX: Long = 1
    private val URI: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

    // Music variables
    private var musicIdColumn: Int? = null
    private var musicNameColumn: Int? = null
    private var musicDurationColumn: Int? = null
    private var musicSizeColumn: Int? = null
    private var relativePathColumn: Int? = null

    // Albums variables
    private var albumIdColumn: Int? = null
    private var albumNameColumn: Int? = null

    // Artists variables
    private var artistIdColumn: Int? = null
    private var artistNameColumn: Int? = null
    private var artistNbOfTracksColumn: Int? = null
    private var artistNbOfAlbumsColumn: Int? = null

    //Genres varibles
    private var genreIdColumn: Int? = null
    private var genreNameColumn: Int? = null

    private lateinit var musicMediaItemSortedMap: SortedMap<Music, MediaItem>
    private lateinit var rootFolderMap: SortedMap<Long, Folder>
    private lateinit var folderMap: SortedMap<Long, Folder>
    private lateinit var artistMap: SortedMap<String, Artist>
    private lateinit var genreMap: SortedMap<String, Genre>

    private var folderId: MutableLongState = mutableLongStateOf(FIRST_FOLDER_INDEX)


    fun loadAllData(
        context: Context,
        musicMediaItemSortedMap: SortedMap<Music, MediaItem>,
        rootFolderMap: SortedMap<Long, Folder>,
        folderMap: SortedMap<Long, Folder>,
        artistMap: SortedMap<String, Artist>,
        albumMap: SortedMap<String, Album>,
        genreMap: SortedMap<String, Genre>
    ) {
        this.musicMediaItemSortedMap = musicMediaItemSortedMap
        this.rootFolderMap = rootFolderMap
        this.folderMap = folderMap
        this.artistMap = artistMap
        this.genreMap = genreMap

        val projection = arrayOf(
            // AUDIO
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.RELATIVE_PATH,
            //ALBUMS
            MediaStore.Audio.Albums.ALBUM_ID,
            MediaStore.Audio.Albums.ALBUM,
            //ARTISTS
            MediaStore.Audio.Artists._ID,
            MediaStore.Audio.Artists.ARTIST,
            //Genre
            MediaStore.Audio.Media.GENRE_ID,
            MediaStore.Audio.Media.GENRE
        )
        context.contentResolver.query(URI, projection, null, null)?.use {
            // Cache music columns indices.
            musicIdColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            musicNameColumn =
                it.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
            musicDurationColumn =
                it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            musicSizeColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
            relativePathColumn =
                it.getColumnIndexOrThrow(MediaStore.Audio.Media.RELATIVE_PATH)

            //Cache album columns indices
            try {
                albumIdColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM_ID)
                albumNameColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM)
            } catch (_: IllegalArgumentException) {
                // No album
            }

            // Cache artist columns indices.
            try {
                artistIdColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Artists._ID)
                artistNameColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Artists.ARTIST)
            } catch (_: IllegalArgumentException) {
                // No artist
            }

            // Cache Genre columns indices.
            try {
                genreIdColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.GENRE_ID)
                genreNameColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.GENRE)
            } catch (_: IllegalArgumentException) {
                // No genre
            }

            while (it.moveToNext()) {
                var artist: Artist? = null
                var album: Album? = null

                if (albumIdColumn != null && albumNameColumn != null) {
                    album = loadAlbum(cursor = it)
                    albumMap[album.name] = album
                }
                var music: Music? = null
                try {
                    music = loadMusic(context = context, cursor = it, album = album)
                    musicMediaItemSortedMap[music] = music.mediaItem
                } catch (_: IllegalAccessError) {
                    // No music found
                    if (album != null && album.musicSortedMap.isEmpty()) {
                        albumMap.remove(album.name)
                    }
                    continue
                }
                loadFolders(music = music)

                try {
                    var genre: Genre = loadGenre(cursor = it)
                    genreMap.putIfAbsent(genre.name, genre)
                    genre = genreMap[genre.name]!! // The id is not the same for all same genre
                    music.genre = genre
                    genre.addMusic(music)
                } catch (_: Exception) {
                    //No Genre
                }

                if (artistIdColumn != null && artistNameColumn != null) {
                    var artist = loadArtist(cursor = it)
                    artistMap.putIfAbsent(artist.name, artist)
                    artist = artistMap[artist.name]!! //The id is not the same for all same artists
                    artist.musicList.add(music)
                    music.artist = artist
                }

                if (artist != null && album != null) {
                    artist.addAlbum(album)
                    album.artist = artist
                }
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
    private fun loadMusic(context: Context, cursor: Cursor, album: Album?): Music {
        // Get values of columns for a given music.
        val id = cursor.getLong(musicIdColumn!!)
        val name = cursor.getString(musicNameColumn!!)
        val duration = cursor.getLong(musicDurationColumn!!)
        val size = cursor.getInt(musicSizeColumn!!)
        val relativePath = cursor.getString(relativePathColumn!!)
        val music: Music = Music(
            context = context,
            id = id,
            name = name,
            duration = duration,
            size = size,
            relativePath = relativePath,
            album = album
        )
        loadAlbumArtwork(context = context, music = music)
        return music
    }

    /**
     * Load the artwork from a media meta data retriever.
     * Decode the byte array to set music's artwork as ImageBitmap
     * If there's an artwork add it to music as ImageBitmap.
     *
     * @param context the context
     * @param music the music to add the artwork
     */
    private fun loadAlbumArtwork(context: Context, music: Music) {
        //Put it in Dispatchers.IO make the app not freezing while starting
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val mediaMetadataRetriever: MediaMetadataRetriever = MediaMetadataRetriever()
                mediaMetadataRetriever.setDataSource(context, music.uri)
                val artwork: ByteArray? = mediaMetadataRetriever.embeddedPicture
                if (artwork != null) {
                    val bitmap: Bitmap = BitmapFactory.decodeByteArray(artwork, 0, artwork.size)
                    music.artwork = bitmap.asImageBitmap()
                }
            } catch (_: Exception) {
                /* No artwork found*/
                music.artwork = null
            }
        }
    }

    /**
     * Load folders and subfolders (creaate them if not exists) where the music is present
     *
     * @param music the music to add to the folder
     */
    private fun loadFolders(
        music: Music,
    ) {
        val splitPath = music.relativePath.split("/").toMutableList()
        if (splitPath.last().isBlank()) {
            //remove the blank folder
            splitPath.removeAt(splitPath.lastIndex)
        }

        var rootFolder: Folder? = null
        rootFolderMap.values.forEach { folder: Folder ->
            if (folder.name == splitPath[0]) {
                rootFolder = folder
                return@forEach
            }
        }

        if (rootFolder == null) {
            // No root folders in the list
            rootFolder = Folder(folderId.longValue, splitPath[0])
            folderMap[folderId.longValue] = rootFolder!!
            folderId.longValue++
            rootFolderMap[rootFolder!!.id] = rootFolder!!
        }

        splitPath.removeAt(0)
        rootFolder!!.createSubFolders(
            splitPath.toMutableList(),
            folderId,
            folderMap
        )
        val subfolder = rootFolder!!.getSubFolder(splitPath.toMutableList())!!
        subfolder.addMusic(music)
    }

    private fun loadAlbum(cursor: Cursor): Album {
        val id: Long = cursor.getLong(albumIdColumn!!)
        val name = cursor.getString(albumNameColumn!!)
        return Album(id = id, name = name)
    }

    private fun loadArtist(cursor: Cursor): Artist {
        // Get values of columns for a given artist.
        val id = cursor.getLong(artistIdColumn!!)
        val name = cursor.getString(artistNameColumn!!)
//            val nbOfTracks = cursor.getInt(artistNbOfTracksColumn!!)
//            val nbOfAlbums = cursor.getInt(artistNbOfAlbumsColumn!!)
        return Artist(id = id, name = name)
    }

    private fun loadGenre(cursor: Cursor): Genre {
        val id = cursor.getLong(genreIdColumn!!)
        val name = cursor.getString(genreNameColumn!!)
        return Genre(id = id, name = name)
    }
}