/*
 * This file is part of Satunes.
 *
 * Satunes is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * Satunes is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with Satunes.
 *
 * If not, see <https://www.gnu.org/licenses/>.
 *
 * *** INFORMATION ABOUT THE AUTHOR *****
 * The author of this file is Antoine Pirlot, the owner of this project.
 * You find this original project on Codeberg.
 *
 * My Codeberg link is: https://codeberg.org/antoinepirlot
 * This current project's link is: https://codeberg.org/antoinepirlot/Satunes
 *
 */

package io.github.antoinepirlot.satunes.internet.subsonic.models.responses

import io.github.antoinepirlot.satunes.internet.subsonic.models.media.SubsonicAlbum
import io.github.antoinepirlot.satunes.internet.subsonic.models.media.SubsonicArtist
import io.github.antoinepirlot.satunes.internet.subsonic.models.media.SubsonicFolder
import io.github.antoinepirlot.satunes.internet.subsonic.models.media.SubsonicFolders
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

/**
 * @author Antoine Pirlot 27/09/2025
 */
@OptIn(ExperimentalSerializationApi::class)
@Serializable
@SerialName("subsonic-response")
internal data class SubsonicResponse(
    val status: String,
    val version: String,
    val type: String,
    val serverVersion: String,
    val openSubsonic: Boolean,
    val error: Error? = null,
    @JsonNames("indexes") private val indexes: Indexes? = null,
    @JsonNames("musicFolders") private val subsonicFolders: SubsonicFolders? = null,
    @JsonNames("artist") val artist: SubsonicArtist? = null,
    @JsonNames("album") val album: SubsonicAlbum? = null,
) {

    companion object {
        private const val OK_STATUS = "ok"
        private const val FAILED_STATUS = "failed"
    }
    fun isError(): Boolean = this.status == FAILED_STATUS
    fun hasArtist(): Boolean = this.artist != null
    fun hasAlbum(): Boolean = this.album != null
    fun hasFolders(): Boolean = this.subsonicFolders != null
    fun hasIndexes(): Boolean = this.indexes != null

    /**
     * Returns the list of [Index] received.
     *
     * @throws IllegalStateException if it has not been received
     */
    fun getAllIndexes(): Collection<Index> {
        if(!this.hasIndexes()) throw IllegalStateException("No indexes received.")
        return this.indexes?.list?: listOf()
    }

    /**
     * Returns the list of [SubsonicFolder] received.
     *
     * @throws IllegalStateException if it has not been received
     */
    fun getAllMusicFolders(): Collection<SubsonicFolder> {
        if(!this.hasFolders()) throw IllegalStateException("No music folder received.")
        return this.subsonicFolders?.list?: listOf()
    }
}