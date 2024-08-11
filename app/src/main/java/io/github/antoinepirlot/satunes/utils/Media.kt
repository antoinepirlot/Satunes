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

fun getMediaTitle(mediaImpl: MediaImpl) {
    val title: String = when (mediaImpl) {
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