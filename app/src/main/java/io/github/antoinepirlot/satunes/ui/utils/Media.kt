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
 * You find this original project on github.
 *
 * My github link is: https://github.com/antoinepirlot
 * This current project's link is: https://github.com/antoinepirlot/Satunes
 *
 * PS: I don't answer quickly.
 */

package io.github.antoinepirlot.satunes.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.data.viewmodels.PlaybackViewModel
import io.github.antoinepirlot.satunes.database.models.Album
import io.github.antoinepirlot.satunes.database.models.Artist
import io.github.antoinepirlot.satunes.database.models.Folder
import io.github.antoinepirlot.satunes.database.models.Genre
import io.github.antoinepirlot.satunes.database.models.MediaImpl
import io.github.antoinepirlot.satunes.database.models.Music
import io.github.antoinepirlot.satunes.database.models.NavBarSection
import io.github.antoinepirlot.satunes.database.models.Playlist
import io.github.antoinepirlot.satunes.icons.SatunesIcons
import io.github.antoinepirlot.satunes.icons.SatunesIcons.ALBUM
import io.github.antoinepirlot.satunes.icons.SatunesIcons.ARTIST
import io.github.antoinepirlot.satunes.icons.SatunesIcons.FOLDER
import io.github.antoinepirlot.satunes.icons.SatunesIcons.GENRES
import io.github.antoinepirlot.satunes.icons.SatunesIcons.MUSIC
import io.github.antoinepirlot.satunes.icons.SatunesIcons.PLAYLIST

/**
 * @author Antoine Pirlot on 27/01/24
 */

/**
 * Start the music
 *
 * @param mediaToPlay the music to play from the music list
 */

internal fun startMusic(
    playbackViewModel: PlaybackViewModel,
    mediaToPlay: MediaImpl? = null
) {
    when (mediaToPlay) {
        is Music -> {
            playbackViewModel.start(mediaToPlay)
        }

        is Folder -> {
            playbackViewModel.start()
        }

        null -> {
            playbackViewModel.start()
        }
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

fun getRightIconAndDescription(media: MediaImpl): SatunesIcons {
    return when (media) {
        is Folder -> FOLDER
        is Artist -> ARTIST
        is Album -> ALBUM
        is Genre -> GENRES
        is Playlist -> PLAYLIST
        else -> MUSIC // In that case, mediaImpl is Music
    }
}

/**
 * Return the root folder name: 0 -> This device, else -> External Storage: name
 */
@Composable
fun getRootFolderName(title: String): String {
    return when (title) {
        "0" -> stringResource(id = R.string.this_device)

        else -> "${stringResource(id = R.string.external_storage)}: $title"
    }
}