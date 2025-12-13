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

package io.github.antoinepirlot.satunes.ui.utils

import androidx.compose.ui.graphics.vector.ImageVector
import io.github.antoinepirlot.jetpack_libs.models.JetpackLibsIcons
import io.github.antoinepirlot.jetpack_libs.models.JetpackLibsIcons.ALBUM
import io.github.antoinepirlot.jetpack_libs.models.JetpackLibsIcons.ARTIST
import io.github.antoinepirlot.jetpack_libs.models.JetpackLibsIcons.FOLDER
import io.github.antoinepirlot.jetpack_libs.models.JetpackLibsIcons.GENRES
import io.github.antoinepirlot.jetpack_libs.models.JetpackLibsIcons.MUSIC
import io.github.antoinepirlot.jetpack_libs.models.JetpackLibsIcons.PLAYLIST
import io.github.antoinepirlot.satunes.data.viewmodels.PlaybackViewModel
import io.github.antoinepirlot.satunes.database.models.NavBarSection
import io.github.antoinepirlot.satunes.database.models.media.Media
import io.github.antoinepirlot.satunes.database.models.media.Music

/**
 * @author Antoine Pirlot on 27/01/2024
 */

/**
 * Start the music
 *
 * @param mediaToPlay the music to play from the music list
 */

internal fun startMusic(
    playbackViewModel: PlaybackViewModel,
    mediaToPlay: Media? = null,
    reset: Boolean = false
) {
    if (mediaToPlay == null) {
        playbackViewModel.start(reset = reset)
    } else if (mediaToPlay.isMusic()) {
        mediaToPlay as Music
        playbackViewModel.start(mediaToPlay = mediaToPlay, reset = reset)
    } else if (mediaToPlay.isFolder()) {
        playbackViewModel.start(reset = reset)
    }
}

fun getRightIconAndDescription(navBarSection: NavBarSection): Pair<ImageVector, String> {
    return when (navBarSection) {
        NavBarSection.FOLDERS -> FOLDER.imageVector to FOLDER.description

        NavBarSection.ARTISTS -> ARTIST.imageVector to ARTIST.description

        NavBarSection.ALBUMS -> ALBUM.imageVector to ALBUM.description

        NavBarSection.GENRES -> GENRES.imageVector to GENRES.description

        NavBarSection.PLAYLISTS -> PLAYLIST.imageVector to PLAYLIST.description

        else -> MUSIC.imageVector to MUSIC.description
    }
}

fun getRightIconAndDescription(media: Media): JetpackLibsIcons {
    return if (media.isFolder())
        FOLDER
    else if (media.isArtist())
        ARTIST
    else if (media.isAlbum())
        ALBUM
    else if (media.isGenre())
        GENRES
    else if (media.isPlaylist())
        PLAYLIST
    else MUSIC // In that case, mediaImpl is Music
}