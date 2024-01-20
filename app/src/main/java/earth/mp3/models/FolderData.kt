package earth.mp3.models

data class FolderData(
    var name: String = "Music",
    var parentFolderData: FolderData? = null,
    var subFolderListData: MutableList<FolderData>? = null,
    val musicList: MutableList<Music> = mutableListOf()
)

fun getFolderList(musicList: List<Music>): MutableMap<String, FolderData> {
    val folderDataMap = mutableMapOf<String, FolderData>()
    musicList.forEach { music: Music ->
        if (folderDataMap.containsKey(music.relativePath)) {
            //Add music to the right folder
            folderDataMap[music.relativePath]!!.musicList.add(music)
        } else {
            val newFolderData = FolderData(
                name = music.relativePath,
                musicList = mutableListOf(music)
            )
            folderDataMap[music.relativePath] = newFolderData
        }

    }
    return folderDataMap
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
 * @param folderData : the folder to add sub-folders
 *
 */
fun createSubFolders(relativePath: String, folderData: FolderData) {
    //TODO A folder have multiple sub-folders, check to use one instance per folder
    //TODO check if it works
    if (relativePath.isBlank()) {
        return
    }
    val folderNameList = relativePath.split("/")
    var parentFolder = folderData
    folderNameList.forEach { folderName ->
        if (folderName != folderNameList[0]) {
            try {
                val subFolderData = FolderData()
                setFolderName(folderName = folderName, folderData = subFolderData)
                if (parentFolder.subFolderListData == null) {
                    parentFolder.subFolderListData = mutableListOf()
                }
                parentFolder!!.subFolderListData!!.add(subFolderData)
                parentFolder = subFolderData
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

private fun setFolderName(folderName: String, folderData: FolderData) {
    if (folderName.isBlank()) {
        throw IllegalArgumentException("The folder name is blank")
    }
    folderData.name = folderName
}