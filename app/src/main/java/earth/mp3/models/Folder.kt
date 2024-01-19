package earth.mp3.models

data class Folder(
    val relativePath: String,
    val subFolders: MutableMap<String, Folder> = mutableMapOf(),
    val musicList: MutableList<Music> = mutableListOf()
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