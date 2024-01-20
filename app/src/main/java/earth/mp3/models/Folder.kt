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
     * Add sub folders in chain to this folder. For example the list is; "Android", "music", "favorites"
     * The rresult will be Android folder contains music folder and music folder contains favorite folder.
     * /!\ It's NOT this folder that contains Android, music and favorite folder side by side.
     * @param subFolderNameChainList : the relative path that contains the sub folders names
     *                                 It's a path not all the subfolder of this folder
     *
     */
    fun createSubFolders(subFolderNameChainList: MutableList<String>) {
        //TODO A folder have multiple sub-folders, check to use one instance per folder
        //TODO check if it works
        var parentFolder = this
        subFolderNameChainList.forEach { folderName ->
            var subFolder: Folder? = null
            for (folder in parentFolder.subFolderList) {
                if (folder.name == folderName) {
                    subFolder = folder
                }
            }
            if (subFolder == null) {
                subFolder = Folder(folderName)
                parentFolder.addSubFolder(subFolder)
            }
            parentFolder = subFolder
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
        if (splitedPath.last() == "") {
            splitedPath.removeAt(splitedPath.lastIndex)
        }
        if (splitedPath.isEmpty()) {
            return null
        }
        if (splitedPath.size == 1 && this.name == splitedPath.get(0)) {
            return this
        }
        splitedPath.remove(splitedPath[0])
        this.subFolderList.forEach { subFolder: Folder ->
            if (subFolder.name == splitedPath[0]) {
                return subFolder.getSubFolder(splitedPath)
            }
        }
        return null
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Folder

        if (name != other.name) return false
        if (parentFolder != other.parentFolder) return false
        if (subFolderList != other.subFolderList) return false
        return musicList == other.musicList
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + (parentFolder?.hashCode() ?: 0)
        result = 31 * result + subFolderList.hashCode()
        result = 31 * result + musicList.hashCode()
        return result
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