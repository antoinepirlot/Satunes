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

import io.github.antoinepirlot.satunes.database.models.media.Music
import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicAlbum
import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicMedia
import io.github.antoinepirlot.satunes.internet.subsonic.SubsonicApiRequester
import io.github.antoinepirlot.satunes.internet.subsonic.models.responses.media.utils.getOrCreateSubsonicAlbum
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
    @SerialName(value = "created") val createdDate: String? = null,
    @SerialName(value = "duration") val durationSeconds: Int? = null,
    @SerialName(value = "playCount") val playCount: Long? = null,
    @SerialName(value = "coverArt") val coverArtId: String? = null,
    @SerialName(value = "genre") val genreTitle: String? = null,
    @SerialName(value = "year") val year: Int? = null,
    @SerialName(value = "song") val songs: Collection<Song>? = null,
) : SubsonicData {
    override fun toSubsonicMedia(subsonicApiRequester: SubsonicApiRequester): SubsonicMedia {
        val subsonicAlbum: SubsonicAlbum = getOrCreateSubsonicAlbum(
            id = id,
            title = title,
            coverArtId = this.coverArtId,
            artistId = artistId,
            artistTitle = artistTitle,
        )
        songs?.forEach { song: Song ->
            subsonicAlbum.addMusic(
                music = song.toSubsonicMedia(subsonicApiRequester = subsonicApiRequester) as Music
            )
        }
        return subsonicAlbum
    }
}