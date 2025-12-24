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

import io.github.antoinepirlot.satunes.database.models.internet.ApiResponse
import io.github.antoinepirlot.satunes.internet.subsonic.models.responses.media.Album
import io.github.antoinepirlot.satunes.internet.subsonic.models.responses.media.Artist
import io.github.antoinepirlot.satunes.internet.subsonic.models.responses.media.Song
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @author Antoine Pirlot 27/09/2025
 */
@Serializable
@SerialName("subsonic-response")
internal data class SubsonicResponse(
    @SerialName(value = "status") val status: String,
    @SerialName(value = "version") val version: String,
    @SerialName(value = "type") val type: String,
    val serverVersion: String? = null,
    val openSubsonic: Boolean = false,
    @SerialName(value = "error") val error: Error? = null,
    @SerialName(value = "randomSongs") val randomSongs: RandomSongs? = null,
    @SerialName(value = "searchResult3") val search3: Search3? = null,
    @SerialName(value = "artist") val artist: Artist? = null,
    @SerialName(value = "album") val album: Album? = null,
    @SerialName(value = "song") val song: Song? = null,
) : ApiResponse {

    companion object {
        private const val OK_STATUS = "ok"
        private const val FAILED_STATUS = "failed"
    }
    fun isError(): Boolean = this.status == FAILED_STATUS
}