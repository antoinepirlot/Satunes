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

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import io.github.antoinepirlot.satunes.internet.InternetManager
import io.github.antoinepirlot.satunes.internet.exceptions.AlreadyRequestingException
import io.github.antoinepirlot.satunes.internet.exceptions.NotConnectedException
import io.github.antoinepirlot.satunes.internet.subsonic.callbacks.GetRandomMusicCallback
import io.github.antoinepirlot.satunes.internet.subsonic.callbacks.PingCallback
import io.github.antoinepirlot.satunes.internet.subsonic.models.SubsonicState
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * @author Antoine Pirlot 03/09/2025
 */
@RequiresApi(Build.VERSION_CODES.M)
class SubsonicApiRequester(
    url: String,
    private val username: String,
    private val md5Password: String,
    private val onSubsonicStateChanged: (SubsonicState) -> Unit
) {
    companion object {
        val DEFAULT_STATE: SubsonicState = SubsonicState.DISCONNECTED
        private const val CLIENT_NAME = "Satunes"
        private var version: String? = null
        internal var status: String? = null
        internal var type: String? = null
        internal var serverVersion: String? = null
        internal var openSubsonic: Boolean? = null
    }

    private val url: String = "$url/rest"
    private var inUrlCredentials: String = "u=$username&t=$md5Password&c=$CLIENT_NAME&v=$version"

    var subsonicState: SubsonicState = DEFAULT_STATE
        internal set(value) {
            field = value
            onSubsonicStateChanged.invoke(field)
        }

    internal fun updateVersion(version: String) {
        if (version == Companion.version) return
        Companion.version = version
        this.inUrlCredentials = "u=$username&t=$md5Password&c=$CLIENT_NAME&v=${Companion.version}"
    }


    /**
     * Create and start a request
     *
     * @param url the request's url
     * @param resCallback the callBack object matching the request.
     * @param newState the new state the [subsonicState] must have.
     */
    private fun get(
        context: Context,
        url: String,
        resCallback: Callback,
        newState: SubsonicState = SubsonicState.REQUESTING
    ) {
        if (!this.canMakeRequest(context = context)) throw AlreadyRequestingException()
        this.subsonicState = newState
        val client = OkHttpClient()
        val req: Request = Request.Builder()
            .get()
            .url(url)
            .build()
        client.newCall(req).enqueue(responseCallback = resCallback)
    }

    /**
     * Ping API
     */
    fun ping(context: Context, onSucceed: (() -> Unit)? = null) {
        var url: String = this.url + "/ping?${this.inUrlCredentials}"
        this.get(
            context = context,
            url = url,
            resCallback = PingCallback(subsonicApiRequester = this, onSucceed = onSucceed),
            newState = SubsonicState.PINGING
        )
    }

    /**
     * Change [subsonicState] to [SubsonicState.DISCONNECTED] if it can be changed.
     *
     * @return true if the process has been successfully done, otherwise false.
     */
    fun disconnect(context: Context): Boolean {
        if (canMakeRequest(context = context)) {
            this.subsonicState = SubsonicState.DISCONNECTED
            return true
        }
        return false
    }

    /**
     * Checks if the internet connection is valid and if another process is not already requesting.
     *
     * @return true if a new request can be done, otherwise false.
     */
    private fun canMakeRequest(context: Context): Boolean {
        if (!InternetManager(context = context).isConnected())
            throw NotConnectedException("Internet connection KO")
        return when (this.subsonicState) {
            SubsonicState.IDLE, SubsonicState.DISCONNECTED -> true
            else -> false
        }
    }

    /**
     * Get randomly [size] musics.
     *
     * @param size the number of music to get (default 10, max 500).
     *
     * @return musics //TODO
     */
    fun getRandomSongs(context: Context, size: Int = 10, onSucceed: (() -> Unit)? = null) {
        if (size < 1 || size > 500)
            throw IllegalArgumentException("Can't get $size musics")
        this.get(
            context = context,
            url = this.url + "/getRandomSongs?${this.inUrlCredentials}&size=$size",
            resCallback = GetRandomMusicCallback(subsonicApiRequester = this, onSucceed = onSucceed)
        )
    }
}