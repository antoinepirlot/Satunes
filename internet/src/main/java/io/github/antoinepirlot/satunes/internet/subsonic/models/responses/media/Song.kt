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

package io.github.antoinepirlot.satunes.internet.subsonic.models.responses.media

import androidx.core.net.toUri
import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicMedia
import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicMusic
import io.github.antoinepirlot.satunes.database.services.data.DataManager
import io.github.antoinepirlot.satunes.internet.subsonic.SubsonicApiRequester
import io.github.antoinepirlot.satunes.internet.subsonic.models.responses.media.utils.getGenre
import io.github.antoinepirlot.satunes.internet.subsonic.models.responses.media.utils.getOrCreateAlbum
import io.github.antoinepirlot.satunes.internet.subsonic.models.responses.media.utils.getOrCreateArtist
import io.github.antoinepirlot.satunes.internet.subsonic.models.responses.media.utils.getOrCreateFolder
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @author Antoine Pirlot 11/12/2025
 */
@Serializable
internal data class Song constructor(
    @SerialName(value = "id") val id: Long,
    @SerialName(value = "isDir") val idDir: Boolean = false,
    @SerialName(value = "title") val title: String,
    @SerialName(value = "album") val albumTitle: String,
    @SerialName(value = "artist") val artistTitle: String,
    @SerialName(value = "track") val track: Int? = null,
    @SerialName(value = "discNumber") val discNumber: Int? = null,
    @SerialName(value = "contentType") val contentTypeMime: String,
    @SerialName(value = "suffix") val fileExtension: String? = null,
    @SerialName(value = "path") val path: String,
    @SerialName(value = "duration") val durationSeconds: Long,
    @SerialName(value = "created") val addedDate: String, //TODO 2025-11-27T12:43:55.000Z like that
    @SerialName(value = "albumId") val albumId: Long,
    @SerialName(value = "artistId") val artistId: Long,
    @SerialName(value = "type") val type: String,
    @SerialName(value = "coverArt") val coverArt: String? = null,
    @SerialName(value = "bitrate") val bitrate: Int,
    @SerialName(value = "size") val size: Int,
    @SerialName(value = "year") val year: Int? = null,
) : SubsonicData {
    /**
     * Convert this object [Song] to [SubsonicMusic] object
     */
    override fun toSubsonicMedia(subsonicApiRequester: SubsonicApiRequester): SubsonicMedia {
        val url: String = subsonicApiRequester.getCommandUrl(
            command = "stream",
            parameters = arrayOf("id=$id")
        )
        return DataManager.getSubsonicMusic(id = id) ?: SubsonicMusic(
            subsonicId = this.id,
            title = this.title,
            displayName = this.path.split("/").last(),
            absolutePath = this.path,
            durationMs = this.durationSeconds * 1000,
            size = this.size,
            cdTrackNumber = this.track,
            addedDateMs = 0,//TODO,
            folder = getOrCreateFolder(),
            artist = getOrCreateArtist(id = this.artistId, title = this.artistTitle),
            album = getOrCreateAlbum(
                id = this.albumId,
                title = this.albumTitle,
                artistId = this.artistId,
                artistTitle = this.artistTitle
            ), //Must be after artist, otherwise album is not added in artist
            genre = getGenre(),
            uri = url.toUri(),
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Song

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}