package earth.mp3player.services

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.compose.runtime.MutableLongState
import androidx.compose.runtime.mutableLongStateOf
import androidx.media3.common.MediaItem
import earth.mp3player.models.Artist
import earth.mp3player.models.Folder
import earth.mp3player.models.Music
import java.util.SortedMap

object DataLoader {
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

    private lateinit var musicMediaItemSortedMap: SortedMap<Music, MediaItem>
    private lateinit var rootFolderMap: SortedMap<Long, Folder>
    private lateinit var folderMap: SortedMap<Long, Folder>
    private lateinit var artistMap: SortedMap<String, Artist>

    private var folderId: MutableLongState = mutableLongStateOf(FIRST_FOLDER_INDEX)


    fun loadAllData(
        context: Context,
        musicMediaItemSortedMap: SortedMap<Music, MediaItem>,
        rootFolderMap: SortedMap<Long, Folder>,
        folderMap: SortedMap<Long, Folder>,
        artistMap: SortedMap<String, Artist>,
    ) {
        this.musicMediaItemSortedMap = musicMediaItemSortedMap
        this.rootFolderMap = rootFolderMap
        this.folderMap = folderMap
        this.artistMap = artistMap

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.RELATIVE_PATH,
            MediaStore.Audio.Artists._ID,
            MediaStore.Audio.Artists.ARTIST,
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
            } catch (_: IllegalArgumentException) {

            }

            while (it.moveToNext()) {
                val music: Music = loadMusic(cursor = it)
                musicMediaItemSortedMap[music] = music.mediaItem

                loadFolders(music = music)

                if (
                    artistIdColumn != null
                    && artistNameColumn != null
                ) {
                    var artist = loadArtist(cursor = it)
                    artistMap.putIfAbsent(artist.name, artist)
                    artist = artistMap[artist.name]!! //The id is not the same for all same artists
                    artist.musicList.add(music)
                    music.artist = artist
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
        val duration = cursor.getLong(musicDurationColumn!!)
        val size = cursor.getInt(musicSizeColumn!!)
        val relativePath = cursor.getString(relativePathColumn!!)

        val fileUri = Uri.Builder().appendPath("${URI.path}/${name}").build()

        return Music(id, name, duration, size, fileUri, relativePath)
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

    private fun loadArtist(cursor: Cursor): Artist {
        // Get values of columns for a given artist.
        val id = cursor.getLong(artistIdColumn!!)
        val name = cursor.getString(artistNameColumn!!)
//            val nbOfTracks = cursor.getInt(artistNbOfTracksColumn!!)
//            val nbOfAlbums = cursor.getInt(artistNbOfAlbumsColumn!!)
        return Artist(id, name)
    }
}