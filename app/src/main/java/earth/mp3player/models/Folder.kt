/*
 *  This file is part of MP3 Player.
 *
 *  MP3 Player is free software: you can redistribute it and/or modify it under
 *  the terms of the GNU General Public License as published by the Free Software Foundation,
 *  either version 3 of the License, or (at your option) any later version.
 *
 *  MP3 Player is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *   without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with MP3 Player.
 *  If not, see <https://www.gnu.org/licenses/>.

 *  ***** INFORMATIONS ABOUT THE AUTHOR *****
 *  The author of this file is Antoine Pirlot, the owner of this project.
 *  You find this original project on github.
 *
 *  My github link is: https://github.com/antoinepirlot
 *  This current project's link is: https://github.com/antoinepirlot/MP3-Player
 *
 *  You can contact me via my email: pirlot.antoine@outlook.com
 * PS: I don't answer quickly.
 */

package earth.mp3player.models

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