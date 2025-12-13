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

import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicAlbum
import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicMedia
import io.github.antoinepirlot.satunes.database.services.data.DataManager
import io.github.antoinepirlot.satunes.internet.subsonic.SubsonicApiRequester
import io.github.antoinepirlot.satunes.internet.subsonic.models.responses.media.utils.getOrCreateArtist
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @author Antoine Pirlot 13/12/2025
 */
@Serializable
internal data class Album(
    @SerialName(value = "id") val id: Long,
    @SerialName(value = "artistId") val artistId: Long,
    @SerialName(value = "name") val title: String,
    @SerialName(value = "artist") val artistTitle: String,
    @SerialName(value = "created") val createdDate: String,
    @SerialName(value = "duration") val durationSeconds: Int,
    @SerialName(value = "playCount") val playCount: Long,
    @SerialName(value = "coverArt") val coverArt: String,
    @SerialName(value = "genre") val genreTitle: String,
    @SerialName(value = "year") val year: Int,
    @SerialName(value = "songCount") val songCount: Int,
) : SubsonicData {
    override fun toSubsonicMedia(subsonicApiRequester: SubsonicApiRequester): SubsonicMedia {
        return (DataManager.getSubsonicAlbum(id = id) ?: SubsonicAlbum(
            subsonicId = this.id,
            title = this.title,
            artist = getOrCreateArtist(id = this.artistId, title = this.artistTitle),
//            isCompilation = false,
            year = this.year,
        )) as SubsonicMedia //TODO use separated artist list in datamanger
    }

}