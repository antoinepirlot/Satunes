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

package io.github.antoinepirlot.satunes.car.utils

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import androidx.media.utils.MediaConstants
import io.github.antoinepirlot.satunes.car.playback.SatunesCarMusicService
import io.github.antoinepirlot.satunes.database.R
import io.github.antoinepirlot.satunes.database.daos.LIKES_PLAYLIST_TITLE
import io.github.antoinepirlot.satunes.database.models.media.Album
import io.github.antoinepirlot.satunes.database.models.media.Artist
import io.github.antoinepirlot.satunes.database.models.media.Folder
import io.github.antoinepirlot.satunes.database.models.media.Genre
import io.github.antoinepirlot.satunes.database.models.media.MediaImpl
import io.github.antoinepirlot.satunes.database.models.media.Music
import io.github.antoinepirlot.satunes.database.models.media.Playlist

/**
 * @author Antoine Pirlot on 16/03/2024
 */

/**
 * Build a media item
 */
internal fun buildMediaItem(
    id: String,
    description: String,
    title: String,
    subtitle: String? = null,
    uri: Uri? = null,
    icon: Bitmap? = null,
    extras: Bundle? = null,
    flags: Int
): MediaBrowserCompat.MediaItem {
    val mediaDescription: MediaDescriptionCompat = MediaDescriptionCompat.Builder()
        .setMediaId(id)
        .setDescription(description)
        .setTitle(title)
        .setSubtitle(subtitle)
        .setMediaUri(uri)
        .setIconBitmap(icon)
        .setExtras(extras)
        .build()
    return MediaBrowserCompat.MediaItem(
        mediaDescription,
        flags
    )
}

/**
 * Build a mediaImpl item
 */
internal fun buildMediaItem(media: MediaImpl): MediaBrowserCompat.MediaItem {
    val flags: Int =
        if (media.isMusic()) {
            MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
        } else {
            MediaBrowserCompat.MediaItem.FLAG_BROWSABLE
        }
    val description: String =
        if (media.isMusic()) "Music"
        else if (media.isFolder()) "Folder"
        else if (media.isArtist()) "Artist"
        else if (media.isGenre()) "Genre"
        else if (media.isPlaylist()) "Playlist"
        else throw IllegalArgumentException("An issue occurred with Media interface")

    val extras = Bundle()
    extras.putString(
        MediaConstants.DESCRIPTION_EXTRAS_KEY_CONTENT_STYLE_GROUP_TITLE,
        description
    )
    if (media.isMusic()) {
        extras.putInt(
            MediaConstants.DESCRIPTION_EXTRAS_KEY_COMPLETION_STATUS,
            MediaConstants.DESCRIPTION_EXTRAS_VALUE_COMPLETION_STATUS_NOT_PLAYED
        )
        extras.putDouble(MediaConstants.DESCRIPTION_EXTRAS_KEY_COMPLETION_PERCENTAGE, 0.0)
    }
    val title: String = if (media.isPlaylist() && media.title == LIKES_PLAYLIST_TITLE) {
        SatunesCarMusicService.instance.getString(R.string.likes_playlist_title)
    } else {
        media.title
    }
    return buildMediaItem(
        id = media.id.toString(),
        description = description,
        subtitle = if (media.isMusic()) (media as Music).artist.title else null,
        title = title,
        uri = if (media.isMusic()) (media as Music).uri else null,
        icon = media.artwork,
        flags = flags
    )
}