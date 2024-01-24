package earth.mp3.models

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.compose.runtime.MutableLongState
import androidx.compose.runtime.mutableLongStateOf

class Music(
    override val id: Long,
    override var name: String,
    val duration: Int,
    val size: Int,
    val uri: Uri?,
    val relativePath: String
) : Media {


    companion object {
        const val FIRST_FOLDER_INDEX: Long = 1
        fun loadData(
            context: Context,
            musicList: MutableList<Music>,
            rootFolderList: MutableList<Folder>,
            folderMap: MutableMap<Long, Folder>,
            artistList: MutableList<Artist>,
        ) {
            val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            val projection = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.RELATIVE_PATH
            )

            context.contentResolver.query(
                uri, projection, null, null
            )?.use { cursor ->
                loadData(cursor, musicList, rootFolderList, folderMap, artistList, uri)
            }
        }

        /**
         * Load all data from the cursor
         */
        private fun loadData(
            cursor: Cursor,
            musicList: MutableList<Music>,
            rootFolderList: MutableList<Folder>,
            folderMap: MutableMap<Long, Folder>,
            artistList: MutableList<Artist>,
            uri: Uri
        ) {
            // Cache music columns indices.
            val musicIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val musicNameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
            val musicDurationColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val musicSizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
            val relativePathColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.RELATIVE_PATH)

            // Cache artist columns indices.
            var artistIdColumn: Int? = null
            var artistNameColumn: Int? = null
            var artistNbOfTracksColumn: Int? = null
            var artistNbOfAlbumsColumn: Int? = null
            try {
                artistIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists._ID)
                artistNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.ARTIST)
                artistNbOfTracksColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_TRACKS)
                artistNbOfAlbumsColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS)
            } catch (_: IllegalArgumentException) {

            }

            //Id for folders
            val folderId: MutableLongState = mutableLongStateOf(FIRST_FOLDER_INDEX)

            while (cursor.moveToNext()) {
                val music: Music = loadMusic(
                    cursor,
                    musicList,
                    musicIdColumn,
                    musicNameColumn,
                    musicDurationColumn,
                    musicSizeColumn,
                    relativePathColumn,
                    uri
                )

                loadFolders(music, rootFolderList, folderMap, folderId)

                if (
                    artistIdColumn != null
                    && artistNameColumn != null
                    && artistNbOfTracksColumn != null
                    && artistNbOfAlbumsColumn != null
                ) {
                    loadArtists(
                        cursor,
                        artistList,
                        artistIdColumn,
                        artistNameColumn,
                        artistNbOfTracksColumn,
                        artistNbOfAlbumsColumn
                    )
                }
            }
        }

        /**
         * Create a music object from the cursor and add it to the music list
         *
         * @param cursor the cursor where music's data is stored
         * @param idColumn the id column in cursor
         * @param nameColumn the name column in cursor
         * @param durationColumn the duration column in cursor
         * @param sizeColumn the size column in cursor
         * @param relativePathColumn the relative path column in cursor
         * @param uri the music's uri
         *
         * @return the created music
         */
        private fun loadMusic(
            cursor: Cursor,
            musicList: MutableList<Music>,
            idColumn: Int,
            nameColumn: Int,
            durationColumn: Int,
            sizeColumn: Int,
            relativePathColumn: Int,
            uri: Uri
        ): Music {
            // Get values of columns for a given music.
            val id = cursor.getLong(idColumn)
            val name = cursor.getString(nameColumn)
            val duration = cursor.getInt(durationColumn)
            val size = cursor.getInt(sizeColumn)
            val relativePath = cursor.getString(relativePathColumn)

            // Stores column values and the contentUri in a local object
            // that represents the media file.
            val fileUri = Uri.Builder().appendPath("${uri.path}/${name}").build()
            val music = Music(id, name, duration, size, fileUri, relativePath)
            musicList.add(music)
            return music
        }

        /**
         * Load folders and subfolders (creaate them if not exists) where the music is present
         *
         * @param music the music to add to the folder //todo check if it works i think no
         * @param rootFolderList the list of root folers where to add folders
         */
        private fun loadFolders(
            music: Music,
            rootFolderList: MutableList<Folder>,
            folderMap: MutableMap<Long, Folder>,
            folderId: MutableLongState,
        ) {
            val splitedPath = music.relativePath.split("/").toMutableList()
            if (splitedPath.last().isBlank()) {
                //remove the blank folder
                splitedPath.removeAt(splitedPath.lastIndex)
            }

            var rootFolder: Folder? = null
            rootFolderList.forEach { folder: Folder ->
                if (folder.name == splitedPath[0]) {
                    rootFolder = folder
                    return@forEach
                }
            }
            if (rootFolder == null) {
                // No root folders in the list
                rootFolder = Folder(folderId.longValue, splitedPath[0])
                folderMap[folderId.longValue] = rootFolder!!
                folderId.longValue++
                rootFolderList.add(rootFolder!!)
            }

            splitedPath.removeAt(0)
            rootFolder!!.createSubFolders(splitedPath.toMutableList(), folderId, folderMap)
            rootFolder!!.getSubFolder(splitedPath.toMutableList())!!.addMusic(music)
        }

        private fun loadArtists(
            cursor: Cursor,
            artistList: MutableList<Artist>,
            idColumn: Int,
            nameColumn: Int,
            numberOfTracksColumn: Int,
            numberOfAlbumsColumn: Int
        ): Artist {
            // Get values of columns for a given artist.
            val id = cursor.getLong(idColumn)
            val name = cursor.getString(nameColumn)
            val nbOfTracksColumn = cursor.getInt(numberOfTracksColumn)
            val nbOfAlbumsColumn = cursor.getInt(numberOfAlbumsColumn)
            val artist = Artist(id, name, nbOfTracksColumn, nbOfAlbumsColumn)
            artistList.add(artist)
            return artist
        }
    }

    override fun toString(): String {
        return this.name
    }
}