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
 *  You should have received a copy of the GNU General Public License along with Satunes.
 *
 * If not, see <https://www.gnu.org/licenses/>.
 *
 * **** INFORMATION ABOUT THE AUTHOR *****
 * The author of this file is Antoine Pirlot, the owner of this project.
 * You find this original project on Codeberg.
 *
 * My Codeberg link is: https://codeberg.org/antoinepirlot
 * This current project's link is: https://codeberg.org/antoinepirlot/Satunes
 */

package io.github.antoinepirlot.satunes.internet.subsonic

import io.github.antoinepirlot.satunes.database.models.User
import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicAlbum
import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicArtist
import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicMedia
import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicMusic
import io.github.antoinepirlot.satunes.database.services.settings.SettingsManager
import io.github.antoinepirlot.satunes.internet.SubsonicCall
import io.github.antoinepirlot.satunes.internet.subsonic.models.ApiType
import io.github.antoinepirlot.satunes.internet.subsonic.models.callbacks.GetAlbumCallback
import io.github.antoinepirlot.satunes.internet.subsonic.models.callbacks.GetArtistCallback
import io.github.antoinepirlot.satunes.internet.subsonic.models.callbacks.GetRandomMusicCallback
import io.github.antoinepirlot.satunes.internet.subsonic.models.callbacks.PingCallback
import io.github.antoinepirlot.satunes.internet.subsonic.models.callbacks.Search3Callback
import io.github.antoinepirlot.satunes.internet.subsonic.models.callbacks.SubsonicCallback
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * @author Antoine Pirlot 03/09/2025
 */
class SubsonicApiRequester() {
    companion object {
        const val SONG_MEDIA_TYPE = "song"
        private const val CLIENT_NAME = "Satunes"
        private const val JSON_FORMAT = "json"
        private var version: String? = null
        internal var status: String? = null
        internal var type: String? = null
        internal var serverVersion: String? = null
        internal var openSubsonic: Boolean? = null
    }

    private val _user: User = User(
        url = SettingsManager.subsonicUrl,
        username = SettingsManager.subsonicUsername,
        password = SettingsManager.subsonicPassword,
        salt = SettingsManager.subsonicSalt
    )

    val inUrlMandatoryParams: String
        get() = "u=${_user.username}&t=${_user.getMd5Password()}&s=${_user.salt}&c=$CLIENT_NAME&v=$version&f=$JSON_FORMAT"

    /**
     * Returns the url as https://example.org/rest/[command]?[inUrlMandatoryParams]&[parameters]
     *
     * @param command the command as [String]. For example getSong
     * @param parameters the parameters of command. For example: id=59feo8
     */
    internal fun getCommandUrl(
        apiType: ApiType = ApiType.SUBSONIC,
        command: String,
        vararg parameters: String
    ): String {
        return when (apiType) {
            ApiType.SUBSONIC -> this.getSubsonicCommandUrl(
                command = command,
                parameters = parameters
            )

            else -> throw UnsupportedOperationException("Only Subsonic is supported at this time.")
        }
    }

    private fun getSubsonicCommandUrl(command: String, vararg parameters: String): String {
        var toReturn = "${_user.url}/rest/$command?$inUrlMandatoryParams"
        for (parameter: String in parameters)
            toReturn += "&$parameter"
        return toReturn
    }

    private fun getFunkwhaleCommandUrl(command: String, vararg parameters: String): String {
        return "${_user.url}/api/v1/$command"
    }

    private fun getBody(vararg parameters: String) {
        var body: String = "{"
        for (i: Int in parameters.indices)
            if (i == 0) body += parameters[i]
            else body += ",${parameters[i]}"
    }

    internal fun updateVersion(version: String) {
        if (version == Companion.version) return
        Companion.version = version
    }


    /**
     * Create and start a request
     *
     * @param url the request's url
     * @param resCallback the callBack object matching the request.
     */
    private fun get(
        url: String,
        resCallback: SubsonicCallback<*>,
    ) {
        val client = OkHttpClient()
        val req: Request = Request.Builder()
            .get()
            .url(url)
            .build()
        val call: Call = client.newCall(req)
        SubsonicCall.Builder()
            .setCall(call = call)
            .setCallBack(callBack = resCallback)
            .build()
            .enqueue()
    }

    /**
     * Ping API
     */
    fun ping(onSucceed: (() -> Unit)? = null, onError: (() -> Unit)? = null) {
        this.get(
            url = this.getCommandUrl(command = "ping", parameters = arrayOf()),
            resCallback = PingCallback(
                subsonicApiRequester = this,
                onSucceed = onSucceed,
                onError = { onError?.invoke() }
            ),
        )
    }

    /**
     * Get randomly [size] musics.
     *
     * @param size the number of music to get (default 10, max 500).
     * @param onDataRetrieved the [Collection] function to run when got data from API.
     */
    suspend fun getRandomSongs(
        size: Int = 10,
        onDataRetrieved: (Collection<SubsonicMusic>) -> Unit
    ) {
        if (size !in 1..500)
            throw IllegalArgumentException("Can't get $size musics")
        get(
            url = getCommandUrl(
                command = "getRandomSongs",
                parameters = arrayOf("size=$size")
            ),
            resCallback = GetRandomMusicCallback(
                subsonicApiRequester = this@SubsonicApiRequester,
                onDataRetrieved = onDataRetrieved
            )
        )
    }

    /**
     * Query subsonic to get matching media to the [query].
     * If the [query] is blank, nothing is done.
     *
     * @param query the [String] to send to api to find matching media.
     * @param onDataRetrieved the function to run when got data from API.
     */
    suspend fun search(query: String, onFinished: () -> Unit, onDataRetrieved: (Collection<SubsonicMedia>) -> Unit) {
        if (query.isBlank()) return
        get(
            url = getCommandUrl(
                command = "search3",
                parameters = arrayOf("query=$query")
            ),
            resCallback = Search3Callback(
                subsonicApiRequester = this,
                onFinished = onFinished,
                onDataRetrieved = onDataRetrieved
            )
        )
    }

    /**
     * Get artist from Subsonic
     *
     * @param artistId the id of artist located on the server
     * @param onDataRetrieved the function to invoke when data has been sent by the server
     */
    fun getArtist(
        artistId: Long,
        onFinished: (() -> Unit)? = null,
        onDataRetrieved: (SubsonicArtist) -> Unit
    ) {
        if (artistId < 1) throw IllegalArgumentException("Artist with id doesn't exist.")
        get(
            url = getCommandUrl(
                command = "getArtist",
                parameters = arrayOf("id=$artistId")
            ),
            resCallback = GetArtistCallback(
                subsonicApiRequester = this,
                onFinished = onFinished,
                onDataRetrieved = onDataRetrieved
            )
        )
    }

    suspend fun getAlbum(
        albumId: Long,
        onDataRetrieved: (SubsonicAlbum) -> Unit,
        onFinished: (() -> Unit)?
    ) {
        if (albumId < 1) throw IllegalArgumentException("Album with id doesn't exist.")
        get(
            url = getCommandUrl(
                command = "getAlbum",
                parameters = arrayOf("id=$albumId")
            ),
            resCallback = GetAlbumCallback(
                subsonicApiRequester = this,
                onFinished = onFinished,
                onDataRetrieved = onDataRetrieved
            )
        )
    }
}