package earth.mp3.models

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore

class Music(id: Long, name: String, duration: Int, size: Int, uri: Uri?, relativePath: String) {
    private val id: Long = id
    var name: String = name
    var duration: Int = duration
    var size: Int = size
    var uri: Uri? = uri
    var relativePath = relativePath

    companion object {
        fun loadData(
            context: Context,
            musicList: MutableList<Music>,
            rootFolderList: MutableList<Folder>
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
                loadData(cursor, musicList, rootFolderList, uri)
            }
        }

        /**
         * Load all data from the cursor
         */
        private fun loadData(
            cursor: Cursor,
            musicList: MutableList<Music>,
            rootFolderList: MutableList<Folder>,
            uri: Uri
        ) {
// Cache column indices.
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val nameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
            val durationColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
            val relativePathColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.RELATIVE_PATH)
            while (cursor.moveToNext()) {
                val music: Music = loadMusic(
                    cursor,
                    musicList,
                    idColumn,
                    nameColumn,
                    durationColumn,
                    sizeColumn,
                    relativePathColumn,
                    uri
                )
                loadFolders(music, rootFolderList)
            }
        }

        /**
         * Create a music object from the cursor and add it to the music list
         *
         * @param cursor the cursor where music's data is stored
         * @param idColumn the id column in cursor
         * @param name the name column in cursor
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
            // Get values of columns for a given video.
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
            rootFolderList: MutableList<Folder>
        ) {
            val splitedPath = music.relativePath.split("/").toMutableList()
            if (splitedPath.last().isBlank()) {
                //remove the blank folder
                splitedPath.removeAt(splitedPath.lastIndex)
            }
            var rootFolder: Folder? = null
            rootFolderList.forEach { folder: Folder ->
                if (folder.getName() == splitedPath[0]) {
                    rootFolder = folder
                }
            }
            if (rootFolder == null) {
                rootFolder = Folder(splitedPath[0])
                rootFolderList.add(rootFolder!!)
            }
            splitedPath.removeAt(0)
            rootFolder!!.createSubFolders(splitedPath.toMutableList())
            rootFolder!!.getSubFolder(splitedPath.toMutableList())!!.addMusic(music)
        }

        private fun loadArtists(cursor: Cursor) {

        }
    }

    override fun toString(): String {
        return this.name
    }
}