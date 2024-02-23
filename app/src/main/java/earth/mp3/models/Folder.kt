package earth.mp3.models

import androidx.compose.runtime.MutableLongState
import androidx.media3.common.MediaItem
import java.util.SortedMap

class Folder(
    override val id: Long,
    override var name: String,
    var parentFolder: Folder? = null,
    private val subFolderList: SortedMap<Long, Folder> = sortedMapOf(),
    val musicMediaItemSortedMap: SortedMap<Music, MediaItem> = sortedMapOf()
) : Media {


    /**
     * Get the list of subfolder
     *
     * @return a list of subfolder and each subfolder is a Folder object
     */
    fun getSubFolderList(): SortedMap<Long, Folder> {
        return this.subFolderList
    }

    /**
     * Get the list of subfolders as media
     *
     * @return a list of subfolder and each subfolder is cast to Media object
     */
    fun getSubFolderListAsMedia(): SortedMap<Long, Media> {
        @Suppress("UNCHECKED_CAST")
        return this.subFolderList as SortedMap<Long, Media>
    }

    fun addMusic(musicData: Music) {
        musicData.folder = this
        this.musicMediaItemSortedMap[musicData] = musicData.mediaItem
    }

    /**
     * Add sub folders in chain to this folder. For example the list is; "Android", "music", "favorites"
     * The rresult will be Android folder contains music folder and music folder contains favorite folder.
     * /!\ It's NOT this folder that contains Android, music and favorite folder side by side.
     * @param subFolderNameChainList : the relative path that contains the sub folders names
     *                                 It's a path not all the subfolder of this folder
     * @param folderId : the next folder id
     *
     */
    fun createSubFolders(
        subFolderNameChainList: MutableList<String>,
        folderId: MutableLongState,
        folderMap: SortedMap<Long, Folder>
    ) {
        //TODO A folder have multiple sub-folders, check to use one instance per folder
        //TODO check if it works
        var parentFolder = this
        subFolderNameChainList.forEach { folderName: String ->
            var subFolder: Folder? = null
            for (folder in parentFolder.subFolderList.values) {
                if (folder.name == folderName) {
                    subFolder = folder
                }
            }
            if (subFolder == null) {
                subFolder = Folder(folderId.longValue, folderName)
                folderMap[folderId.longValue] = subFolder
                folderId.longValue++
                parentFolder.subFolderList[subFolder.id] = subFolder
            }
            parentFolder = subFolder
        }
    }

    /**
     * Find the right sub-folder and return when it's found, otherwise null
     * @param splitPath the list of all sub-folders name
     * /!\ The list reference won't change
     *
     * @return the right Folder matching the last subFolderName of the list
     */
    fun getSubFolder(splitPath: MutableList<String>): Folder? {
        if (splitPath.isEmpty()
            || (splitPath.size == 1 && this.name == splitPath[0])
        ) {
            return this
        }
        this.subFolderList.values.forEach { subFolder: Folder ->
            if (subFolder.name == splitPath[0]) {
                splitPath.remove(splitPath[0])
                return subFolder.getSubFolder(splitPath)
            }
        }
        return null
    }

    fun getAllMusic(): SortedMap<Music, MediaItem> {
        val musicMediaSortedMap: SortedMap<Music, MediaItem> = sortedMapOf()
        musicMediaSortedMap.putAll(this.musicMediaItemSortedMap)
        if (this.subFolderList.isNotEmpty()) {
            this.subFolderList.forEach { (_, folder: Folder) ->
                musicMediaSortedMap.putAll(folder.getAllMusic())
            }
        }
        return musicMediaSortedMap
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Folder

        if (name != other.name) return false
        if (parentFolder != other.parentFolder) return false
        if (subFolderList != other.subFolderList) return false
        return musicMediaItemSortedMap == other.musicMediaItemSortedMap
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + (parentFolder?.hashCode() ?: 0)
        result = 31 * result + subFolderList.hashCode()
        result = 31 * result + musicMediaItemSortedMap.hashCode()
        return result
    }

    override fun toString(): String {
        return this.name
    }
}