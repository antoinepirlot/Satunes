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
 *
 *  ***** INFORMATIONS ABOUT THE AUTHOR *****
 *  The author of this file is Antoine Pirlot, the owner of this project.
 *  You find this original project on github.
 *
 *  My github link is: https://github.com/antoinepirlot
 *  This current project's link is: https://github.com/antoinepirlot/MP3-Player
 *
 *  You can contact me via my email: pirlot.antoine@outlook.com
 *  PS: I don't answer quickly.
 */

package earth.mp3player.router.media.utils

import androidx.navigation.NavHostController
import earth.mp3player.database.models.Album
import earth.mp3player.database.models.Artist
import earth.mp3player.database.models.Folder
import earth.mp3player.database.models.Genre
import earth.mp3player.database.models.Media
import earth.mp3player.database.models.Music
import earth.mp3player.database.models.relations.PlaylistWithMusics
import earth.mp3player.playback.services.playback.PlaybackController
import earth.mp3player.router.media.MediaDestination
import earth.mp3player.services.PlaylistSelectionManager
import earth.mp3player.ui.utils.getMusicListFromFolder
import earth.mp3player.ui.utils.startMusic

/**
 * @author Antoine Pirlot on 01/04/2024
 */

/**
 * Open the media, when it is:
 *      Music: navigate to the media's destination and start music with exoplayer
 *
 *      Folder: navigate to the media's destination
 *
 *      Artist: navigate to the media's destination
 *
 * @param navController the nav controller to redirect to the good path
 * @param media the media to open
 */
fun openMedia(
    navController: NavHostController,
    media: Media? = null
) {
    PlaylistSelectionManager.openedPlaylist = null
    if (media == null || media is Music) {
        startMusic(media)
    }
    navController.navigate(getDestinationOf(media))
}

fun openMediaFromFolder(
    navController: NavHostController,
    media: Media
) {
    when (media) {
        is Music -> {
            val playbackController = PlaybackController.getInstance()
            playbackController.loadMusic(musicMediaItemSortedMap = getMusicListFromFolder(media.folder!!))
            openMedia(navController, media)
        }

        is Folder -> navController.navigate(getDestinationOf(media))
    }

}

/**
 * Return the destination link of media (folder, artists or music) with its id.
 * For example if media is folder, it returns: /folders/5
 *
 * @param media the media to get the destination link
 *
 * @return the media destination link with the media's id
 */
private fun getDestinationOf(media: Media?): String {
    return when (media) {
        is Folder -> "${MediaDestination.FOLDERS.link}/${media.id}"

        is Artist -> "${MediaDestination.ARTISTS.link}/${media.title}"

        is Album -> "${MediaDestination.ALBUMS.link}/${media.id}"

        is Genre -> "${MediaDestination.GENRES.link}/${media.title}"

        is PlaylistWithMusics -> "${MediaDestination.PLAYLISTS.link}/${media.playlist.id}"

        else -> MediaDestination.PLAYBACK.link
    }
}

/**
 * Open the current playing music
 *
 * @throws IllegalStateException if there's no music playing
 */
fun openCurrentMusic(navController: NavHostController) {
    val playbackController: PlaybackController = PlaybackController.getInstance()
    val musicPlaying = playbackController.musicPlaying.value
        ?: throw IllegalStateException("No music is currently playing, this button can be accessible")

    navController.navigate(getDestinationOf(musicPlaying))
}

fun resetOpenedPlaylist() {
    PlaylistSelectionManager.openedPlaylist = null
}