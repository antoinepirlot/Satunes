package earth.mp3.models

import android.net.Uri


data class Music(
    val id: Long,
    val name: String,
    val duration: Int,
    val size: Int,
    val uri: Uri?,
    val relativePath: String
)

fun getFolderList(musicList: List<Music>): MutableMap<String, Folder> {
    val folderMap = mutableMapOf<String, Folder>()
    musicList.forEach { music: Music ->
        if (folderMap.containsKey(music.relativePath)) {
            //Add music to the right folder
            folderMap[music.relativePath]!!.musicList.add(music)
        } else {
            val newFolder = Folder(
                relativePath = music.relativePath,
                musicList = mutableListOf(music)
            )
            folderMap[music.relativePath] = newFolder
        }

    }
    return folderMap
}
