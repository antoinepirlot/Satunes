package earth.mp3.models

data class Folder(
    var name: String = "Music",
    var subFolder: Folder? = null,
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
                name = music.relativePath,
                musicList = mutableListOf(music)
            )
            folderMap[music.relativePath] = newFolder
        }

    }
    return folderMap
}

/**
 * Create subfolders into subfolders if exists and remove /Music/ because all have it
 *
 */
private fun createSubfolders(relativePath: String, folder: Folder) {
    //TODO check if it works
    if (relativePath.isBlank()) {
        return
    }
    val splitedPath = relativePath.split("/")
    val folderName = splitedPath[0]
    val reducedPath = relativePath.removePrefix("/$folderName")
    folder.name = folderName
    if (reducedPath.isNotBlank()) {
        folder.subFolder = Folder()
        createSubfolders(reducedPath, folder.subFolder!!)
    }
}