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

package io.github.antoinepirlot.satunes.internet.subsonic.models.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @author Antoine Pirlot 11/12/2025
 */
@Serializable
data class Song constructor(
    @SerialName(value = "id") val id: Long,
    @SerialName(value = "title") val title: String,
    @SerialName(value = "album") val album: String,
    @SerialName(value = "artist") val artist: String,
    @SerialName(value = "track") val track: Int,
    @SerialName(value = "discNumber") val discNumber: Int,
    @SerialName(value = "duration") val durationSeconds: Long,
    @SerialName(value = "albumId") val albumId: Long,
    @SerialName(value = "artistId") val artistId: Long,
    @SerialName(value = "type") val type: String,
    @SerialName(value = "year") val year: Int,
)