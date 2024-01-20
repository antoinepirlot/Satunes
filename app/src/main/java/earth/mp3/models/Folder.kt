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

fun <T, U> mapToList(map: Map<U, T>): List<T> {
    val listToReturn = mutableListOf<T>()
    map.map { entry ->
        listToReturn.add(entry.value)
    }
    return listToReturn
}

/**
 * Add sub-folders to folder if the relative path is not empty
 * @param relativePath : the relative path that contains the relative path to the directory
 *                       where is the music stored
 * @param folder : the folder to add sub-folders
 *
 */
fun createSubFolders(relativePath: String, folder: Folder) {
    //TODO check if it works
    if (relativePath.isBlank()) {
        return
    }
    val folderNameList = relativePath.split("/")
    var parentFolder = folder
    folderNameList.forEach { folderName ->
        if (folderName != folderNameList[0]) {
            try {
                val subFolder = Folder()
                setFolderName(folderName = folderName, folder = subFolder)
                parentFolder.subFolder = subFolder
                parentFolder = subFolder
            } catch (_: IllegalArgumentException) {

            }
        }
    }


//    if (relativePath.isBlank()) {
//        return
//    }
//    val splitedPath = relativePath.split("/")
//    val folderName = splitedPath[0]
//    val reducedPath = relativePath.removePrefix("/$folderName")
//    folder.name = folderName
//    if (reducedPath.isNotBlank()) {
//        folder.subFolder = Folder()
//        createSubFolders(reducedPath, folder.subFolder!!)
//    }
}

private fun setFolderName(folderName: String, folder: Folder) {
    if (folderName.isBlank()) {
        throw IllegalArgumentException("The folder name is blank")
    }
    folder.name = folderName
}