/*
 * This file is part of Satunes.
 *
 * Satunes is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * Satunes is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License along with Satunes.
 *
 * If not, see <https://www.gnu.org/licenses/>.
 *
 * **** INFORMATION ABOUT THE AUTHOR *****
 * The author of this file is Antoine Pirlot, the owner of this project.
 * You find this original project on Codeberg.
 *
 * My Codeberg link is: https://codeberg.org/antoinepirlot
 * This current project's link is: https://codeberg.org/antoinepirlot/Satunes
 */

package io.github.antoinepirlot.satunes.database.models.media

import io.github.antoinepirlot.satunes.database.services.data.DataManager
import java.util.Date
import java.util.SortedSet

/**
 * @author Antoine Pirlot on 27/03/2024
 */

open class Folder(
    id: Long? = null,
    title: String,
    val parentFolder: Folder? = null,
) : MediaImpl(id = id ?: nextId, title = title) {

    companion object {
        var nextId: Long = 1
    }
    private val subFolderSortedSet: SortedSet<Folder> = sortedSetOf()
    private val _depth: Int = if (this.parentFolder == null) 0 else this.parentFolder.getDepth() + 1

    val absolutePath: String = if (parentFolder == null) {
        "/$title"
    } else {
        parentFolder.absolutePath + "/$title"
    }

    public override var addedDate: Date? = null

    init {
        nextId++
    }

    override fun isEmpty(): Boolean {
        return super.isEmpty() && try {
            this.subFolderSortedSet.first { it.isNotEmpty() }
            false
        } catch (_: NoSuchElementException) {
            true
        }
    }

    override fun isNotEmpty(): Boolean = !isEmpty()

    fun getDepth(): Int = this._depth

    /**
     * Get the list of subfolder
     *
     * @return a list of subfolder and each subfolder is a Folder object
     */
    fun getSubFolderSet(): Set<Folder> = this.subFolderSortedSet

    override fun isFolder(): Boolean = true

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
    fun createSubFolders(subFolderNameChainList: Collection<String>) {
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
     * Get the list of parents in order from the top parent to this.
     * This list respect the order of the absolute path.
     */
    fun getPathAsFolderList(): List<Folder> {
        val folders: MutableList<Folder> = mutableListOf()
        this.getPathAsFolderList(folders = folders)
        return folders
    }

    private fun getPathAsFolderList(folders: MutableCollection<Folder>) {
        if (this == this.getRoot()) return //It's the root folder and don't need to be added.
        this.parentFolder!!.getPathAsFolderList(folders = folders) //Do not switch this line with the next one to preserve the order.
        folders.add(element = this) //Do not switch this line with the previous one to preserve the order.
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

    override fun musicCount(): Int {
        var count = this.musicSortedSet.size
        for (folder: Folder in this.subFolderSortedSet) count += folder.musicCount()
        return count
    }

    fun getRoot(): Folder {
        if (this.isRootFolder()) return this
        return this.parentFolder!!.getRoot()
    }

    /**
     * Add manually a [Folder] in [subFolderSortedSet].
     * (Used in internet module with Subsonic)
     */
    fun addSubfolder(folder: Folder) {
        this.subFolderSortedSet.add(element = folder)
    }
}