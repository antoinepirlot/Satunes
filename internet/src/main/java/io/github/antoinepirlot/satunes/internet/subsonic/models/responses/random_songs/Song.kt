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

package io.github.antoinepirlot.satunes.internet.subsonic.models.responses.random_songs

import androidx.core.net.toUri
import io.github.antoinepirlot.satunes.database.models.media.Album
import io.github.antoinepirlot.satunes.database.models.media.Artist
import io.github.antoinepirlot.satunes.database.models.media.Folder
import io.github.antoinepirlot.satunes.database.models.media.Genre
import io.github.antoinepirlot.satunes.database.models.media.SubsonicMusic
import io.github.antoinepirlot.satunes.database.services.data.DataManager
import io.github.antoinepirlot.satunes.internet.subsonic.SubsonicApiRequester
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @author Antoine Pirlot 11/12/2025
 */
@Serializable
internal class Song constructor(
    @SerialName(value = "id") val id: Long,
    @SerialName(value = "title") val title: String,
    @SerialName(value = "path") val path: String,
    @SerialName(value = "album") val albumTitle: String,
    @SerialName(value = "artist") val artistTitle: String,
    @SerialName(value = "track") val track: Int,
    @SerialName(value = "discNumber") val discNumber: Int,
    @SerialName(value = "duration") val durationSeconds: Long,
    @SerialName(value = "albumId") val albumId: Long,
    @SerialName(value = "artistId") val artistId: Long,
    @SerialName(value = "type") val type: String,
    @SerialName(value = "created") val addedDate: String, //TODO 2025-11-27T12:43:55.000Z like that
    @SerialName(value = "size") val size: Int,
    @SerialName(value = "year") val year: Int,
) {
    /**
     * Convert this object [Song] to [SubsonicMusic] object
     */
    fun toMusic(subsonicApiRequester: SubsonicApiRequester): SubsonicMusic {
        val url: String = subsonicApiRequester.getCommandUrl(
            command = "stream",
            parameters = arrayOf("id=$id")
        )
        return DataManager.getSubsonicMusic(id = id) ?: SubsonicMusic(
            id = this.id,
            subsonicId = this.id,
            title = this.title,
            displayName = this.path.split("/").last(),
            absolutePath = this.path,
            durationMs = this.durationSeconds * 1000,
            size = this.size,
            cdTrackNumber = this.track,
            addedDateMs = 0,//TODO,
            folder = this.getOrCreateFolder(),
            artist = this.getOrCreateArtist(),
            album = this.getOrCreateAlbum(), //Must be after artist, otherwise album is not added in artist
            genre = this.getGenre(),
            uri = url.toUri(),
        )
    }

    /**
     * Get the artist matching the [artistId] or create a new one if it doesn't exist.
     */
    fun getOrCreateArtist(): Artist = DataManager.getArtist(id = artistId) ?: this.createArtist()

    /**
     * Create a new artist matching data
     */
    private fun createArtist(): Artist {
        return Artist(
            id = this.artistId,
            title = this.artistTitle
        )
    }

    fun getOrCreateFolder(): Folder = DataManager.getSubsonicRootFolder()!! //TODO

    /**
     * Get the subsonic album matching the album or creates a new one and returns it if it is not known
     */
    fun getOrCreateAlbum(): Album = DataManager.getAlbum(id = albumId) ?: this.createAlbum()

    /**
     * Create a new Album and returns it.
     */
    private fun createAlbum(): Album {
        return Album(
            id = albumId,
            title = albumTitle,
            artist = this.getOrCreateArtist(),
//            isCompilation = false,
//            year = null
        )
    }

    fun getGenre(): Genre = Genre(title = "UNKNOWN CLOUD GENRE") //TODO

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