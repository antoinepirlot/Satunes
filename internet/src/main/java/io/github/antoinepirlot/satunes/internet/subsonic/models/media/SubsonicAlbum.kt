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

package io.github.antoinepirlot.satunes.internet.subsonic.models.media

import io.github.antoinepirlot.satunes.database.models.media.Album
import io.github.antoinepirlot.satunes.database.services.data.DataManager
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

/**
 * @author Antoine Pirlot 26/09/2025
 */
@OptIn(ExperimentalSerializationApi::class)
@Serializable
@SerialName("album")
internal data class SubsonicAlbum(
    val id: String,
    val name: String,
    val artistId: String,
    val isCompilation: Boolean,
    @JsonNames("song") val songs: Collection<SubsonicSong> = listOf()
) {
    fun toAlbum(): Album = DataManager.getAlbum(subsonicId = id)?: Album(
            title = name,
            subsonicId = id,
            artist = DataManager.getSubsonicArtist(id = artistId)!!,
            isCompilation = isCompilation
        )
}