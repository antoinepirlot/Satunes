package earth.mp3.models

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.compose.runtime.mutableLongStateOf
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import earth.mp3.services.PlaybackController
import java.util.SortedMap

class Music(
    override val id: Long,
    override val name: String,
    val duration: Int,
    val size: Int,
    val uri: Uri,
    val relativePath: String,
    var folder: Folder? = null,
) : Media {

    val mediaItem: MediaItem
    val absolutePath: String = "${PlaybackController.ROOT_PATH}/$relativePath/$name"
    val mediaMetadata: MediaMetadata

    init {
        this.mediaItem = MediaItem.Builder()
            .setUri(this.absolutePath)
            .build()

        this.mediaMetadata = this.mediaItem.mediaMetadata
    }

    companion object {
        const val FIRST_FOLDER_INDEX: Long = 1
        private val URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        private var musicIdColumn: Int? = null
        private var musicNameColumn: Int? = null
        private var musicDurationColumn: Int? = null
        private var musicSizeColumn: Int? = null
        private var relativePathColumn: Int? = null
        private var artistIdColumn: Int? = null
        private var artistNameColumn: Int? = null
        private var artistNbOfTracksColumn: Int? = null
        private var artistNbOfAlbumsColumn: Int? = null

        private lateinit var musicMap: SortedMap<Long, Music>
        private lateinit var rootFolderMap: SortedMap<Long, Folder>
        private lateinit var folderMap: SortedMap<Long, Folder>
        private lateinit var artistMap: SortedMap<Long, Artist>

        private var folderId: Long = Music.FIRST_FOLDER_INDEX


        fun loadAllData(
            context: Context,
            musicMap: SortedMap<Long, Music>,
            rootFolderMap: SortedMap<Long, Folder>,
            folderMap: SortedMap<Long, Folder>,
            artistMap: SortedMap<Long, Artist>,
        ) {
            this.musicMap = musicMap
            this.rootFolderMap = rootFolderMap
            this.folderMap = folderMap
            this.artistMap = artistMap

            val projection = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.RELATIVE_PATH
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

                // Cache artist columns indices.
                try {
                    artistIdColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Artists._ID)
                    artistNameColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Artists.ARTIST)
                    artistNbOfTracksColumn =
                        it.getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_TRACKS)
                    artistNbOfAlbumsColumn =
                        it.getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS)
                } catch (_: IllegalArgumentException) {

                }

                while (it.moveToNext()) {
                    val music: Music = loadMusic(cursor = it)
                    musicMap[music.id] = music

                    loadFolders(music = music)

                    if (
                        artistIdColumn != null
                        && artistNameColumn != null
                        && artistNbOfTracksColumn != null
                        && artistNbOfAlbumsColumn != null
                    ) {
                        val artist = loadArtists(cursor = it)
                        artistMap[artist.id] = artist
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
        private fun loadMusic(cursor: Cursor): Music {
            // Get values of columns for a given music.
            val id = cursor.getLong(musicIdColumn!!)
            val name = cursor.getString(musicNameColumn!!)
            val duration = cursor.getInt(musicDurationColumn!!)
            val size = cursor.getInt(musicSizeColumn!!)
            val relativePath = cursor.getString(relativePathColumn!!)

            val fileUri = Uri.Builder().appendPath("${URI.path}/${name}").build()

            return Music(id, name, duration, size, fileUri, relativePath)
        }

        /**
         * Load folders and subfolders (creaate them if not exists) where the music is present
         *
         * @param music the music to add to the folder
         * @param rootFolderList the list of root folers where to add folders
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
                rootFolder = Folder(folderId, splitPath[0])
                folderMap[folderId] = rootFolder!!
                folderId++
                rootFolderMap[rootFolder!!.id] = rootFolder!!
            }

            splitPath.removeAt(0)
            rootFolder!!.createSubFolders(
                splitPath.toMutableList(),
                mutableLongStateOf(folderId),
                folderMap
            )
            rootFolder!!.getSubFolder(splitPath.toMutableList())!!.addMusic(music)
        }

        private fun loadArtists(cursor: Cursor): Artist {
            // Get values of columns for a given artist.
            val id = cursor.getLong(artistIdColumn!!)
            val name = cursor.getString(artistNameColumn!!)
            val nbOfTracks = cursor.getInt(artistNbOfTracksColumn!!)
            val nbOfAlbums = cursor.getInt(artistNbOfAlbumsColumn!!)
            return Artist(id, name, nbOfTracks, nbOfAlbums)
        }
    }
}