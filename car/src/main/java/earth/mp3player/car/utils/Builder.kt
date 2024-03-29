/*
 * This file is part of MP3 Player.
 *
 * MP3 Player is free software: you can redistribute it and/or modify it under
 *  the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * MP3 Player is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with MP3 Player.
 * If not, see <https://www.gnu.org/licenses/>.
 *
 * **** INFORMATIONS ABOUT THE AUTHOR *****
 * The author of this file is Antoine Pirlot, the owner of this project.
 * You find this original project on github.
 *
 * My github link is: https://github.com/antoinepirlot
 * This current project's link is: https://github.com/antoinepirlot/MP3-Player
 *
 * You can contact me via my email: pirlot.antoine@outlook.com
 * PS: I don't answer quickly.
 */

package earth.mp3player.car.utils

import android.net.Uri
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import androidx.media.utils.MediaConstants
import earth.mp3player.playback.models.Album
import earth.mp3player.playback.models.Artist
import earth.mp3player.playback.models.Folder
import earth.mp3player.playback.models.Genre
import earth.mp3player.playback.models.Media
import earth.mp3player.playback.models.Music

/**
 * @author Antoine Pirlot on 16/03/2024
 */

/**
 * Build a media item
 */
fun buildMediaItem(
    id: String,
    description: String,
    title: String,
    subtitle: String? = null,
    uri: Uri? = null,
    extras: Bundle? = null,
    flags: Int
): MediaBrowserCompat.MediaItem {
    val mediaDescription: MediaDescriptionCompat = MediaDescriptionCompat.Builder()
        .setMediaId(id)
        .setDescription(description)
        .setTitle(title)
        .setSubtitle(subtitle)
        .setMediaUri(uri)
        .setExtras(extras)
        .build()
    return MediaBrowserCompat.MediaItem(
        mediaDescription,
        flags
    )
}

/**
 * Build a media item
 */
fun buildMediaItem(media: Media): MediaBrowserCompat.MediaItem {
    val flags: Int =
        if (media is Music) {
            MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
        } else {
            MediaBrowserCompat.MediaItem.FLAG_BROWSABLE
        }
    val description: String =
        when (media) {
            is Music -> "Music"
            is Folder -> "Folder"
            is Artist -> "Artist"
            is Album -> "Album"
            is Genre -> "Genre"
            else -> throw IllegalArgumentException("An issue occurred with Media interface")
        }
    val extras = Bundle()
    extras.putString(
        MediaConstants.DESCRIPTION_EXTRAS_KEY_CONTENT_STYLE_GROUP_TITLE,
        description
    )
    if (media is Music) {
        extras.putInt(
            MediaConstants.DESCRIPTION_EXTRAS_KEY_COMPLETION_STATUS,
            MediaConstants.DESCRIPTION_EXTRAS_VALUE_COMPLETION_STATUS_NOT_PLAYED
        )
        extras.putDouble(MediaConstants.DESCRIPTION_EXTRAS_KEY_COMPLETION_PERCENTAGE, 0.0)
    }
    return buildMediaItem(
        id = media.id.toString(),
        description = description,
        subtitle = if (media is Music && media.artist != null) media.artist!!.title else null,
        title = media.title,
        uri = if (media is Music) media.uri else null,
        flags = flags
    )
}