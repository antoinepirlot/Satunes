/*
 * This file is part of Satunes.
 *
 *  Satunes is free software: you can redistribute it and/or modify it under
 *  the terms of the GNU General Public License as published by the Free Software Foundation,
 *  either version 3 of the License, or (at your option) any later version.
 *
 *  Satunes is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with Satunes.
 *  If not, see <https://www.gnu.org/licenses/>.
 *
 *  **** INFORMATIONS ABOUT THE AUTHOR *****
 *  The author of this file is Antoine Pirlot, the owner of this project.
 *  You find this original project on github.
 *
 *  My github link is: https://github.com/antoinepirlot
 *  This current project's link is: https://github.com/antoinepirlot/Satunes
 *
 *  You can contact me via my email: pirlot.antoine@outlook.com
 *  PS: I don't answer quickly.
 */

package io.github.antoinepirlot.satunes.database.models

import android.graphics.Bitmap
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.media3.common.MediaItem
import io.github.antoinepirlot.satunes.database.services.DataManager
import java.util.SortedMap

/**
 * @author Antoine Pirlot on 27/03/2024
 */

data class Folder(
    override var id: Long = nextId,
    override var title: String,
    var parentFolder: Folder? = null,
) : Media {
    override var liked: Boolean = false
    override var artwork: Bitmap? = null

    val musicMediaItemSortedMapUpdate: MutableState<Boolean> = mutableStateOf(false)
    override val musicMediaItemSortedMap: SortedMap<Music, MediaItem> = sortedMapOf()

    val subFolderMap: SortedMap<String, Folder> = sortedMapOf()


    val absolutePath: String = if (parentFolder == null) {
        "/$title"
    } else {
        parentFolder!!.absolutePath + "/$title"
    }

    companion object {
        var nextId: Long = 1
    }

    init {
        nextId++
    }

    /**
     * Get the list of subfolder
     *
     * @return a list of subfolder and each subfolder is a Folder object
     */
    fun getSubFolderList(): SortedMap<String, Folder> {
        return this.subFolderMap
    }

    /**
     * Get the list of sub-folders as media
     *
     * @return a list of subfolder and each subfolder is cast to Media object
     */
    fun getSubFolderMapAsMedia(): SortedMap<Long, Media> {
        @Suppress("UNCHECKED_CAST")
        return this.subFolderMap as SortedMap<Long, Media>
    }

    fun addMusic(music: Music) {
        music.folder = this
        this.musicMediaItemSortedMap[music] = music.mediaItem
    }

    /**
     * Add sub folders in chain to this folder. For example the list is; "Android", "music", "favorites"
     * The result will be Android folder contains music folder and music folder contains favorite folder.
     * /!\ It's NOT this folder that contains Android, music and favorite folder side by side.
     * @param subFolderNameChainList : the relative path that contains the sub folders names
     *                                 It's a path not all the subfolder of this folder
     *
     */
    fun createSubFolders(
        subFolderNameChainList: MutableList<String>,
    ) {
        var parentFolder = this
        subFolderNameChainList.forEach { folderName: String ->
            var subFolder: Folder? = null
            for (folder in parentFolder.subFolderMap.values) {
                if (folder.title == folderName) {
                    subFolder = folder
                }
            }
            if (subFolder == null) {
                subFolder = Folder(title = folderName, parentFolder = parentFolder)
                DataManager.addFolder(folder = subFolder)
                parentFolder.subFolderMap[subFolder.title] = subFolder
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
            || (splitPath.size == 1 && this.title == splitPath[0])
        ) {
            return this
        }
        this.subFolderMap.values.forEach { subFolder: Folder ->
            if (subFolder.title == splitPath[0]) {
                splitPath.remove(splitPath[0])
                return subFolder.getSubFolder(splitPath)
            }
        }

        return null
    }

    fun getAllMusic(): SortedMap<Music, MediaItem> {
        val musicMediaSortedMap: SortedMap<Music, MediaItem> = sortedMapOf()

        musicMediaSortedMap.putAll(this.musicMediaItemSortedMap)

        if (this.subFolderMap.isNotEmpty()) {
            this.subFolderMap.forEach { (_, folder: Folder) ->
                musicMediaSortedMap.putAll(folder.getAllMusic())
            }
        }

        return musicMediaSortedMap
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Folder

        if (title != other.title) return false
        if (parentFolder != other.parentFolder) return false
        if (subFolderMap != other.subFolderMap) return false
        return musicMediaItemSortedMap == other.musicMediaItemSortedMap
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + (parentFolder?.hashCode() ?: 0)
        result = 31 * result + subFolderMap.hashCode()
        result = 31 * result + musicMediaItemSortedMap.hashCode()
        return result
    }

    override fun toString(): String {
        return this.title
    }
}