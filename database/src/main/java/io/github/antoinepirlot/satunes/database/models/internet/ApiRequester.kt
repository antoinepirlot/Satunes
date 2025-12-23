package io.github.antoinepirlot.satunes.database.models.internet

import android.graphics.Bitmap
import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicAlbum
import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicArtist
import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicMedia
import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicMusic
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
        artistId: Long,
        onFinished: (() -> Unit)? = null,
        onDataRetrieved: (SubsonicArtist) -> Unit,
        onError: ((ApiError?) -> Unit)? = null
    )

    suspend fun getAlbum(
        albumId: Long,
        onDataRetrieved: (SubsonicAlbum) -> Unit,
        onFinished: (() -> Unit)? = null,
        onError: ((ApiError?) -> Unit)? = null
    )

    /**
     * Get cover art from API
     */
    suspend fun getCoverArt(coverArtId: String, onDataRetrieved: (Bitmap?) -> Unit)

    suspend fun download(
        musicId: Long,
        onDataRetrieved: (InputStream) -> Unit,
        onError: (() -> Unit)? = null,
        onFinished: (() -> Unit)? = null,
    )
}