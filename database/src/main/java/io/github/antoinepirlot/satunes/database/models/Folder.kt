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

import io.github.antoinepirlot.satunes.database.services.data.DataManager
import java.util.SortedSet

/**
 * @author Antoine Pirlot on 27/03/2024
 */

class Folder(
    title: String,
    var parentFolder: Folder? = null,
) : MediaImpl(id = nextId, title = title) {
    private val subFolderSortedSet: SortedSet<Folder> = sortedSetOf()

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

    override fun isEmpty(): Boolean {
        return super.isEmpty() || try {
            this.subFolderSortedSet.first { it.isNotEmpty() }
            false
        } catch (_: NoSuchElementException) {
            true
        }
    }

    override fun isNotEmpty(): Boolean {
        return super.isNotEmpty() || try {
            this.subFolderSortedSet.first { it.isNotEmpty() }
            true
        } catch (_: NoSuchElementException) {
            false
        }
    }

    /**
     * Get the list of subfolder
     *
     * @return a list of subfolder and each subfolder is a Folder object
     */
    fun getSubFolderMap(): Set<Folder> {
        return this.subFolderSortedSet
    }

    /**
     * Create a list containing sub folders then this folder musics.
     * This list starts with all sub folders sorted by title, then this folder's musics sorted by title.
     *
     * @return a list of this subfolders and then this musics
     */
    fun getSubFolderListWithMusics(): List<MediaImpl> {
        val list: MutableList<MediaImpl> = mutableListOf()
        list.addAll(this.subFolderSortedSet)
        list.addAll(this.musicSortedSet)
        return list
    }

    /**
     * Add sub folders in chain to this folder. For example the list is; "Android", "music", "favorites"
     * The result will be Android folder contains music folder and music folder contains favorite folder.
     * /!\ It's NOT this folder that contains Android, music and favorite folder side by side.
     * @param subFolderNameChainList : the relative path that contains the sub folders names
     *                                 It's a path not all the subfolder of this folder
     *
     */
    fun createSubFolders(subFolderNameChainList: MutableList<String>) {
        var parentFolder = this
        subFolderNameChainList.forEach { folderName: String ->
            var subFolder: Folder? = null
            for (folder in parentFolder.subFolderSortedSet) {
                if (folder.title == folderName) {
                    subFolder = folder
                }
            }
            if (subFolder == null) {
                // No subfolder matching folder name, create new one
                subFolder = Folder(title = folderName, parentFolder = parentFolder)
                DataManager.addFolder(folder = subFolder)
                parentFolder.subFolderSortedSet.add(element = subFolder)
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
        if (splitPath.isEmpty() || splitPath.size == 1 && this.title == splitPath[0]) {
            return this
        }
        this.subFolderSortedSet.forEach { subFolder: Folder ->
            if (subFolder.title == splitPath[0]) {
                splitPath.remove(splitPath[0])
                return subFolder.getSubFolder(splitPath)
            }
        }

        return null
    }

    /**
     * Create a mutable map that contains all folder's music and subfolders' musics in this order:
     * #1 musics from this current folder sorted by name
     * #2 musics from each subfolder sorted by name and by folder
     */
    fun getAllMusic(): Set<Music> {
        val musicSet: MutableSet<Music> = mutableSetOf()

        musicSet.addAll(elements = this.musicSortedSet)

        if (this.subFolderSortedSet.isNotEmpty()) {
            this.subFolderSortedSet.forEach { folder: Folder ->
                musicSet.addAll(elements = folder.getAllMusic())
            }
        }

        return musicSet
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Folder

        return absolutePath == other.absolutePath
    }

    override fun hashCode(): Int {
        return absolutePath.hashCode()
    }

    override fun compareTo(other: MediaImpl): Int {
        var compared: Int = super.compareTo(other)
        if (compared == 0 && this != other) {
            compared = 1
        }
        return compared
    }
}