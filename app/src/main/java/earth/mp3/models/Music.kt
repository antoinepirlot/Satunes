package earth.mp3.models

import android.content.Context
import android.net.Uri
import android.provider.MediaStore


data class Music(
    val id: Long,
    val name: String,
    val duration: Int,
    val size: Int,
    val uri: Uri?,
    val relativePath: String
)

fun loadMusics(
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

            val splitedPath = relativePath.split("/").toMutableList()
            var rootFolder: Folder? = null
            rootFolderList.forEach { folder: Folder ->
                if (folder.getName() == splitedPath[0]) {
                    rootFolder = folder
                }
            }
            if (rootFolder == null) {
                rootFolder = Folder(splitedPath[0])
            }
            splitedPath.removeAt(0)
            rootFolder!!.createSubFolders(splitedPath.toMutableList())
            rootFolder!!.getSubFolder(splitedPath.toMutableList())!!.addMusic(music)
            rootFolderList.add(rootFolder!!)
            musicList.add(music)
        }
    }
}
