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

package earth.galacticmusic.ui.utils

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.media3.common.MediaItem
import earth.galacticmusic.database.models.Folder
import earth.galacticmusic.database.models.Media
import earth.galacticmusic.database.models.Music
import earth.galacticmusic.playback.models.MenuTitle
import earth.galacticmusic.playback.services.playback.PlaybackController
import earth.galacticmusic.ui.views.MP3PlayerIcons.ALBUM
import earth.galacticmusic.ui.views.MP3PlayerIcons.ARTIST
import earth.galacticmusic.ui.views.MP3PlayerIcons.FOLDER
import earth.galacticmusic.ui.views.MP3PlayerIcons.GENRES
import earth.galacticmusic.ui.views.MP3PlayerIcons.MUSIC
import earth.galacticmusic.ui.views.MP3PlayerIcons.PLAYLIST
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

fun getRightIconAndDescription(menuTitle: MenuTitle): Pair<ImageVector, String> {
    return when (menuTitle) {
        MenuTitle.FOLDERS -> FOLDER.imageVector to FOLDER.description

        MenuTitle.ARTISTS -> ARTIST.imageVector to ARTIST.description

        MenuTitle.ALBUMS -> ALBUM.imageVector to ALBUM.description

        MenuTitle.GENRES -> GENRES.imageVector to GENRES.description

        MenuTitle.PLAYLISTS -> PLAYLIST.imageVector to PLAYLIST.description

        else -> MUSIC.imageVector to MUSIC.description
    }
}