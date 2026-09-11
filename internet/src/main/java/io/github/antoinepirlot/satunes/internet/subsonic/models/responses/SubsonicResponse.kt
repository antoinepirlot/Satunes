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
import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicAlbum
import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicArtist
import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicGenre
import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicMedia
import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicMusic
import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicPlaylist
import io.github.antoinepirlot.satunes.internet.subsonic.SubsonicApiRequester
import io.github.antoinepirlot.satunes.internet.subsonic.models.responses.media.Album
import io.github.antoinepirlot.satunes.internet.subsonic.models.responses.media.Artist
import io.github.antoinepirlot.satunes.internet.subsonic.models.responses.media.Genre
import io.github.antoinepirlot.satunes.internet.subsonic.models.responses.media.Playlist
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
    @SerialName(value = "randomSongs") val songs: Songs? = null,
    @SerialName(value = "songsByGenre") val genre: Genre? = null,
    @SerialName(value = "searchResult3") val search3: Search3? = null,
    @SerialName(value = "artist") val artist: Artist? = null,
    @SerialName(value = "album") val album: Album? = null,
    @SerialName(value = "albumList2") val albums: AlbumsResponse? = null,
    @SerialName(value = "song") val song: Song? = null,
    @SerialName(value = "playlist") val playlist: Playlist? = null,
    @SerialName(value = "playlists") val playlistsResponse: PlaylistsResponse? = null,
    @SerialName(value = "genres") val genres: GenresResponse? = null
) : ApiResponse {

    companion object {
        private const val OK_STATUS = "ok"
        private const val FAILED_STATUS = "failed"
    }

    fun isError(): Boolean = this.status == FAILED_STATUS

    @Suppress("UNCHECKED_CAST")
    @Throws(IllegalStateException::class)
    fun getSubsonicMedia(subsonicApiRequester: SubsonicApiRequester, clazz: String): SubsonicMedia {
        return when (clazz) {
            SubsonicPlaylist::class.simpleName -> this.playlist!!.toSubsonicMedia(subsonicApiRequester = subsonicApiRequester)
            SubsonicAlbum::class.simpleName -> this.album!!.toSubsonicMedia(subsonicApiRequester = subsonicApiRequester)
            SubsonicArtist::class.simpleName -> this.artist!!.toSubsonicMedia(subsonicApiRequester = subsonicApiRequester)
            SubsonicMusic::class.simpleName -> this.song!!.toSubsonicMedia(subsonicApiRequester = subsonicApiRequester)
            SubsonicGenre::class.simpleName -> this.genre!!.toSubsonicMedia(subsonicApiRequester = subsonicApiRequester)
            else -> throw IllegalStateException("No subsonic media retrieved. Class unsupported: $clazz.")
        }
    }

    inline fun <reified T : Collection<SubsonicMedia>> getSubsonicMediaList(subsonicApiRequester: SubsonicApiRequester): T {
        return if (T::class == SubsonicMusic::class)
            this.songs!!.toSubsonicMusicCollection(subsonicApiRequester = subsonicApiRequester) as T
        else if (T::class == SubsonicPlaylist::class)
            this.playlistsResponse!!.toSubsonicResponse(subsonicApiRequester = subsonicApiRequester) as T
        else if (T::class == SubsonicAlbum::class)
            this.albums!!.toSubsonicResponse(subsonicApiRequester = subsonicApiRequester) as T
        else
            throw IllegalStateException("No subsonic media retrieved. Class unsupported: ${T::class}.")
    }
}