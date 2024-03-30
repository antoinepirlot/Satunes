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

package earth.mp3player.app.ui.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Album
import androidx.compose.material.icons.rounded.Audiotrack
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.media3.common.MediaItem
import earth.mp3player.database.models.tables.Album
import earth.mp3player.database.models.tables.Artist
import earth.mp3player.database.models.tables.Folder
import earth.mp3player.database.models.tables.Genre
import earth.mp3player.database.models.Media
import earth.mp3player.playback.models.MenuTitle
import earth.mp3player.database.models.tables.Music
import earth.mp3player.playback.services.playback.PlaybackController
import java.util.SortedMap

/**
 * @author Antoine Pirlot on 27/01/24
 */

/**
 * Start the music
 *
 * @param mediaToPlay the music to play from the music list
 */

fun startMusic(mediaToPlay: Media? = null) {
    val playbackController = PlaybackController.getInstance()

    when (mediaToPlay) {
        is Music -> {
            playbackController.start(mediaToPlay)
        }

        is Folder -> {
            playbackController.start()
        }

        null -> {
            playbackController.start()
        }
    }
}

/**
 * Create the list of all music in the folder and subfolder
 */
fun getMusicListFromFolder(folder: Folder): SortedMap<Music, MediaItem> {
    val mapOfMusic: SortedMap<Music, MediaItem> = folder.musicMediaItemSortedMap
    for (subfolder in folder.getSubFolderList().values) {
        mapOfMusic.putAll(subfolder.musicMediaItemSortedMap)
    }
    return mapOfMusic
}

fun getRightIconAndDescription(media: Media): Pair<ImageVector, String> {
    return when (media) {
        is Folder -> {
            Icons.Filled.Folder to "Arrow Forward"
        }

        is Artist -> {
            Icons.Filled.AccountCircle to "Account Circle"
        }

        is Album -> {
            Icons.Rounded.Album to "Album Icon"
        }

        is Genre -> {
            Icons.Rounded.Category to "Genres Icon"
        }

        else -> {
            // In that case, media is Music
            Icons.Filled.MusicNote to "Play Arrow"
        }
    }
}

fun getRightIconAndDescription(menuTitle: MenuTitle): Pair<ImageVector, String> {
    return when (menuTitle) {
        MenuTitle.FOLDERS -> Icons.Rounded.Folder to "Folder Icon"

        MenuTitle.ARTISTS -> Icons.Rounded.AccountCircle to "Artist Icon"

        MenuTitle.ALBUMS -> Icons.Rounded.Album to "Album Icon"

        MenuTitle.GENRES -> Icons.Rounded.Category to "Genres Icon"

        else -> Icons.Rounded.Audiotrack to "Music Icon"
    }
}