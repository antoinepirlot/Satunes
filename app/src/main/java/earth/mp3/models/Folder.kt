package earth.mp3.models

class Folder {
    private var name: String = "Music"
    private var parentFolder: Folder? = null
    private val subFolderList: MutableList<Folder> = mutableListOf()
    private val musicList: MutableList<Music> = mutableListOf()

    fun setName(name: String) {
        if (name.isBlank()) {
            return
        }
        this.name = name
    }

    fun getName(): String {
        return this.name
    }


    fun getParentFolder(): Folder? {
        return this.parentFolder
    }

    fun setParentFolder(parentFolder: Folder) {
        this.parentFolder = parentFolder
    }

    fun getSubFolderList(): List<Folder>? {
        if (this.subFolderList == null) {
            return null
        }
        return this.subFolderList.toList()
    }

    fun addSubFolder(subFolder: Folder) {
        this.subFolderList.add(subFolder)
    }

    fun removeSubFolder(subFolder: Folder) {
        this.subFolderList.remove(subFolder)
    }

    fun addMusic(music: Music) {
        this.musicList.add(music)
    }

    fun removeMusic(music: Music) {
        this.musicList.remove(music)
    }

    /**
     * Add sub-folders to folder if the relative path is not empty
     * @param relativePath : the relative path that contains the relative path to the directory
     *                       where is the music stored
     * @param folder : the folder to add sub-folders
     *
     */
    fun createSubFolders(relativePath: String, folder: Folder) {
        //TODO A folder have multiple sub-folders, check to use one instance per folder
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
                    subFolder.setName(folderName)
                    parentFolder.addSubFolder(subFolder)
                    parentFolder = subFolder
                } catch (_: IllegalArgumentException) {

                }
            }
        }

    }
}
//if (relativePath.isBlank()) {
//    return
//}
//val splitedPath = relativePath.split("/")
//val folderName = splitedPath[0]
//val reducedPath = relativePath.removePrefix("/$folderName")
//folder.name = folderName
//if (reducedPath.isNotBlank()) {
//    folder.subFolder = Folder()
//    createSubFolders(reducedPath, folder.subFolder!!)
//}