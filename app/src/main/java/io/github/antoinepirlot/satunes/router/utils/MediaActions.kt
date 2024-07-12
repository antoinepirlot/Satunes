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

package io.github.antoinepirlot.satunes.router.utils

import android.net.Uri.encode
import androidx.navigation.NavHostController
import io.github.antoinepirlot.satunes.database.models.Album
import io.github.antoinepirlot.satunes.database.models.Artist
import io.github.antoinepirlot.satunes.database.models.Folder
import io.github.antoinepirlot.satunes.database.models.Genre
import io.github.antoinepirlot.satunes.database.models.MediaImpl
import io.github.antoinepirlot.satunes.database.models.Music
import io.github.antoinepirlot.satunes.database.models.Playlist
import io.github.antoinepirlot.satunes.playback.services.PlaybackController
import io.github.antoinepirlot.satunes.router.Destination
import io.github.antoinepirlot.satunes.ui.utils.getMusicListFromFolder
import io.github.antoinepirlot.satunes.ui.utils.startMusic

/**
 * @author Antoine Pirlot on 01/04/2024
 */

/**
 * Open the mediaImpl, when it is:
 *      Music: navigate to the mediaImpl's destination and start music with exoplayer
 *
 *      Folder: navigate to the mediaImpl's destination
 *
 *      Artist: navigate to the mediaImpl's destination
 *
 * @param mediaImpl the mediaImpl to open
 */
internal fun openMedia(
    mediaImpl: MediaImpl? = null,
    navigate: Boolean = true,
    navController: NavHostController?,
) {
    if (mediaImpl == null || mediaImpl is Music) {
        startMusic(mediaImpl)
    }
    if (navigate) {
        if (navController == null) {
            throw IllegalArgumentException("navController can't be null if you navigate")
        }
        navController.navigate(getDestinationOf(mediaImpl))
    }
}

/**
 * Open mediaImpl from folders' views if the mediaImpl is:
 *      Music: It loads folder's musics data to playback and open the mediaImpl that is music.
 *
 *      Folder: navigate to the folder's view
 */
internal fun openMediaFromFolder(
    mediaImpl: MediaImpl,
    navController: NavHostController
) {
    when (mediaImpl) {
        is Music -> {
            val playbackController = PlaybackController.getInstance()
            playbackController.loadMusic(
                musicMediaItemSortedMap = getMusicListFromFolder(mediaImpl.folder),
                musicToPlay = mediaImpl
            )
            openMedia(mediaImpl, navController = navController)
        }

        is Folder -> navController.navigate(getDestinationOf(mediaImpl))
    }

}

/**
 * Return the destination link of mediaImpl (folder, artists or music) with its id.
 * For example if mediaImpl is folder, it returns: /folders/5
 *
 * @param mediaImpl the mediaImpl to get the destination link
 *
 * @return the mediaImpl destination link with the mediaImpl's id
 */
private fun getDestinationOf(mediaImpl: MediaImpl?): String {
    return when (mediaImpl) {
        is Folder -> "${Destination.FOLDERS.link}/${mediaImpl.id}"

        is Artist -> "${Destination.ARTISTS.link}/${encode(mediaImpl.title)}"

        is Album -> "${Destination.ALBUMS.link}/${mediaImpl.id}"

        is Genre -> "${Destination.GENRES.link}/${encode(mediaImpl.title)}"

        is Playlist -> "${Destination.PLAYLISTS.link}/${mediaImpl.id}"

        else -> Destination.PLAYBACK.link
    }
}

/**
 * Open the current playing music
 *
 * @throws IllegalStateException if there's no music playing
 */
internal fun openCurrentMusic(navController: NavHostController) {
    val playbackController: PlaybackController = PlaybackController.getInstance()
    val musicPlaying = playbackController.musicPlaying.value
        ?: throw IllegalStateException("No music is currently playing, this button can be accessible")

    navController.navigate(getDestinationOf(musicPlaying))
}