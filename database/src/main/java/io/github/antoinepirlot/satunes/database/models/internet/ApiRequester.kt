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
 */

package io.github.antoinepirlot.satunes.database.models.internet

import android.graphics.Bitmap
import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicAlbum
import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicArtist
import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicMedia
import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicMusic
import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicPlaylist
import java.io.InputStream

/**
 * @author Antoine Pirlot 20/12/2025
 */
interface ApiRequester {
    /**
     * Ping API
     */
    suspend fun ping(
        onSucceed: (() -> Unit)? = null,
        onError: (() -> Unit)? = null,
        onFinished: (() -> Unit)? = null
    )

    /**
     * Get randomly [size] musics.
     *
     * @param size the number of music to get (default 10, max 500).
     * @param onDataRetrieved the [Collection] function to run when got data from API.
     */
    suspend fun getRandomSongs(
        size: Int = 10,
        onDataRetrieved: (Collection<SubsonicMusic>) -> Unit
    )

    /**
     * Query subsonic to get matching media to the [query].
     * If the [query] is blank, nothing is done.
     *
     * @param query the [String] to send to api to find matching media.
     * @param onDataRetrieved the function to run when got data from API.
     */
    suspend fun search(
        query: String,
        onFinished: () -> Unit,
        onDataRetrieved: (Collection<SubsonicMedia>) -> Unit
    )

    /**
     * Get artist from Subsonic
     *
     * @param artistId the id of artist located on the server
     * @param onDataRetrieved the function to invoke when data has been sent by the server
     */
    suspend fun getArtist(
        artistId: String,
        onFinished: (() -> Unit)? = null,
        onDataRetrieved: (SubsonicArtist) -> Unit,
        onError: ((ApiError?) -> Unit)? = null
    )

    suspend fun getAlbum(
        albumId: String,
        onDataRetrieved: (SubsonicAlbum) -> Unit,
        onFinished: (() -> Unit)? = null,
        onError: ((ApiError?) -> Unit)? = null
    )

    /**
     * Get cover art from API
     */
    suspend fun getCoverArt(coverArtId: String, onDataRetrieved: (Bitmap?) -> Unit)

    suspend fun download(
        musicId: String,
        onDataRetrieved: (InputStream) -> Unit,
        onError: (() -> Unit)? = null,
        onFinished: (() -> Unit)? = null,
    )

    /**
     * Get song from API based on its id.
     *
     * @param musicId the song's id
     * @param onDataRetrieved the function to invoke when [SubsonicMusic] has been retrieved.
     * @param onError the function to invoke when an error occurred
     * @param onFinished the function to invoke when the process is finished
     * @param onSucceed the function to invoke when the process is a success
     */
    suspend fun getSong(
        musicId: String,
        onDataRetrieved: (SubsonicMusic?) -> Unit,
        onError: (() -> Unit)? = null,
        onFinished: (() -> Unit)? = null,
        onSucceed: (() -> Unit)? = null,
    )

    /**
     * Create a new playlist..
     *
     * @param title the playlist's name.
     * @param onDataRetrieved the function to invoke when [SubsonicPlaylist] has been created.
     * @param onError the function to invoke when an error occurred
     * @param onFinished the function to invoke when the process is finished
     * @param onSucceed the function to invoke when the process is a success
     */
    suspend fun createPlaylist(
        title: String,
        onDataRetrieved: (SubsonicPlaylist) -> Unit,
        onError: (() -> Unit)? = null,
        onFinished: (() -> Unit)? = null,
        onSucceed: (() -> Unit)? = null,
    )

    /**
     * Get all playlist
     *
     * @param onDataRetrieved the function to invoke when data received.
     * @param onError the function to invoke when an error occurred
     * @param onFinished the function to invoke when the process is finished
     * @param onSucceed the function to invoke when the process is a success
     */
    fun getPlaylists(
        onDataRetrieved: (Collection<SubsonicPlaylist>) -> Unit,
        onError: (() -> Unit)? = null,
        onFinished: (() -> Unit)? = null,
        onSucceed: (() -> Unit)? = null
    )

    /**
     * Get single playlist
     *
     * @param id the playlist's id
     * @param onDataRetrieved the function to invoke when data received.
     * @param onError the function to invoke when an error occurred
     * @param onFinished the function to invoke when the process is finished
     * @param onSucceed the function to invoke when the process is a success
     */
    fun getPlaylist(
        id: String,
        onDataRetrieved: (Collection<SubsonicMusic>) -> Unit,
        onError: (() -> Unit)? = null,
        onFinished: (() -> Unit)? = null,
        onSucceed: (() -> Unit)? = null
    )

    fun updatePlaylist(
        playlistId: String,
        name: String? = null,
        musicsToAdd: Collection<SubsonicMusic>? = null,
        musicsIndexToRemove: Collection<Int>? = null,
        onError: (() -> Unit)? = null,
        onFinished: (() -> Unit)? = null,
        onSucceed: (() -> Unit)? = null
    )

    fun deletePlaylist(
        playlistId: String,
        onError: (() -> Unit)? = null,
        onFinished: (() -> Unit)? = null,
        onSucceed: (() -> Unit)? = null
    )
}