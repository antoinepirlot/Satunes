/*
 * This file is part of Satunes.
 *
 * Satunes is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * Satunes is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with Satunes.
 * If not, see <https://www.gnu.org/licenses/>.
 *
 * *** INFORMATION ABOUT THE AUTHOR *****
 * The author of this file is Antoine Pirlot, the owner of this project.
 * You find this original project on Codeberg.
 *
 * My Codeberg link is: https://codeberg.org/antoinepirlot
 * This current project's link is: https://codeberg.org/antoinepirlot/Satunes
 */

package io.github.antoinepirlot.satunes.utils

import io.github.antoinepirlot.satunes.MainActivity
import io.github.antoinepirlot.satunes.database.R
import io.github.antoinepirlot.satunes.database.daos.LIKES_PLAYLIST_TITLE
import io.github.antoinepirlot.satunes.database.models.Folder
import io.github.antoinepirlot.satunes.database.models.MediaImpl
import io.github.antoinepirlot.satunes.database.models.Playlist

/**
 * @author Antoine Pirlot on 11/08/2024
 */

fun getMediaTitle(mediaImpl: MediaImpl): String {
    return when (mediaImpl) {
        is Playlist -> {
            if (mediaImpl.title == LIKES_PLAYLIST_TITLE) {
                MainActivity.instance.getString(R.string.likes_playlist_title)
            } else {
                mediaImpl.title
            }
        }

        is Folder -> {
            if (mediaImpl.title == "0") {
                MainActivity.instance.getString(io.github.antoinepirlot.satunes.R.string.this_device)
            } else {
                mediaImpl.title
            }
        }

        else -> mediaImpl.title
    }
}

/**
 * Returns the first folder containing at least 1 music or 2 folders.
 */
fun getTheFirstVisibleFolder(folder: Folder): Folder {
    val subFolders: Collection<Folder> = folder.getSubFolderSet()
    if (subFolders.size == 1 && folder.getMusicSet().isEmpty()) return getTheFirstVisibleFolder(
        subFolders.first()
    )
    return folder
}