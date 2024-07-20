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

import androidx.navigation.NavHostController
import io.github.antoinepirlot.satunes.database.models.Album
import io.github.antoinepirlot.satunes.database.models.Artist
import io.github.antoinepirlot.satunes.database.models.Folder
import io.github.antoinepirlot.satunes.database.models.Genre
import io.github.antoinepirlot.satunes.database.models.MediaImpl
import io.github.antoinepirlot.satunes.database.models.Music
import io.github.antoinepirlot.satunes.database.models.Playlist
import io.github.antoinepirlot.satunes.models.Destination
import io.github.antoinepirlot.satunes.ui.utils.startMusic
import io.github.antoinepirlot.satunes.ui.viewmodels.PlaybackViewModel
import io.github.antoinepirlot.satunes.utils.logger.SatunesLogger

/**
 * @author Antoine Pirlot on 01/04/2024
 */

/**
 * Open the mediaImpl, when it is:
 *      Music: navigate to the media's destination and start music with exoplayer
 *
 *      Folder: navigate to the media's destination
 *
 *      Artist: navigate to the media's destination
 *
 * @param media the mediaImpl to open
 */
internal fun openMedia(
    playbackViewModel: PlaybackViewModel,
    media: MediaImpl? = null,
    navigate: Boolean = true,
    navController: NavHostController?,
) {
    if (media == null || media is Music) {
        startMusic(playbackViewModel = playbackViewModel, mediaToPlay = media)
    }
    if (navigate) {
        if (navController == null) {
            val message = "navController can't be null if you navigate"
            val logger = SatunesLogger(name = null)
            logger.severe(message)
            throw IllegalArgumentException(message)
        }
        navController.navigate(getDestinationOf(media))
    }
}

/**
 * Open mediaImpl from folders' views if the mediaImpl is:
 *      Music: It loads folder's musics data to playback and open the mediaImpl that is music.
 *
 *      Folder: navigate to the folder's view
 */
internal fun openMediaFromFolder(
    media: MediaImpl,
    playbackViewModel: PlaybackViewModel,
    navController: NavHostController
) {
    when (media) {
        is Music -> {
            playbackViewModel.loadMusicFromMedia(
                media = media.folder,
                musicToPlay = media
            )
            openMedia(
                playbackViewModel = playbackViewModel,
                media = media,
                navController = navController
            )
        }

        is Folder -> navController.navigate(getDestinationOf(media))
    }

}

/**
 * Return the destination link of mediaImpl (folder, artists or music) with its id.
 * For example if mediaImpl is folder, it returns: /folders/5
 *
 * @param media the mediaImpl to get the destination link
 *
 * @return the mediaImpl destination link with the mediaImpl's id
 */
private fun getDestinationOf(media: MediaImpl?): String {
    return when (media) {
        is Folder -> "${Destination.FOLDERS.link}/${media.id}"

        is Artist -> "${Destination.ARTISTS.link}/${media.id}"

        is Album -> "${Destination.ALBUMS.link}/${media.id}"

        is Genre -> "${Destination.GENRES.link}/${media.id}"

        is Playlist -> "${Destination.PLAYLISTS.link}/${media.id}"

        else -> Destination.PLAYBACK.link
    }
}

/**
 * Open the current playing music
 *
 * @throws IllegalStateException if there's no music playing
 */
internal fun openCurrentMusic(
    playbackViewModel: PlaybackViewModel,
    navController: NavHostController
) {
    val musicPlaying: Music? = playbackViewModel.musicPlaying
    if (musicPlaying == null) {
        val message = "No music is currently playing, this button can be accessible"
        val logger = SatunesLogger(name = null)
        logger.severe(message)
        throw IllegalStateException(message)
    }

    navController.navigate(getDestinationOf(musicPlaying))
}