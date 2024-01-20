package earth.mp3.models

class Folder {
    private var name: String = "Music"
    private var parentFolder: Folder? = null
    private val subFolderList: MutableList<Folder> = mutableListOf()
    private val musicList: MutableList<Music> = mutableListOf()

    constructor() {

    }

    constructor(name: String) {
        this.setName(name)
    }

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
    fun createSubFolders(subFolderNameList: List<String>) {
        //TODO A folder have multiple sub-folders, check to use one instance per folder
        //TODO check if it works
        var parentFolder = this
        subFolderNameList.forEach { folderName ->
            try {
                val subFolder = Folder(folderName)
                parentFolder.addSubFolder(subFolder)
                parentFolder = subFolder
            } catch (_: IllegalArgumentException) {

            }
        }
    }

    /**
     * Find the right sub-folder and return when it's found, otherwise null
     * @param splitedPath the list of all sub-folders name
     * /!\ The list reference won't change
     *
     * @return the right Folder matching the last subFolderName of the list
     */
    fun getSubFolder(splitedPath: MutableList<String>): Folder? {
        if (splitedPath.isEmpty()) {
            return null
        }
        if (splitedPath.size == 1 && this.name == splitedPath.get(0)) {
            return this
        }
        splitedPath.remove(splitedPath[0])
        return getSubFolder(splitedPath)
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