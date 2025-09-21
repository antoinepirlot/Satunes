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
        internal var version: String? = null
        internal var status: String? = null
        internal var type: String? = null
        internal var serverVersion: String? = null
        internal var openSubsonic: Boolean? = null
    }

    private val url: String = "$url/rest"
    var subsonicState: SubsonicState = DEFAULT_STATE
        internal set(value) {
            field = value
            onSubsonicStateChanged.invoke(field)
        }

    private fun get(url: String, resCallback: Callback, newState: SubsonicState) {
        if (!this.canMakeRequest()) throw AlreadyRequestingException()
        this.subsonicState = newState
        val client = OkHttpClient()
        val req: Request = Request.Builder()
            .get()
            .url(url)
            .build()
        client.newCall(req).enqueue(responseCallback = resCallback)
    }

    private fun checkInternetConnection(context: Context) {
        if (!InternetManager(context = context).isConnected())
            throw NotConnectedException("Internet connection KO")
        ping()
    }

    private fun ping() {
        var url: String = this.url + "/ping?u=$username&c=$CLIENT_NAME&t=$md5Password"
        if (version != null) url += "&v=$version"
        this.get(
            url = url,
            resCallback = PingCallback(subsonicApiRequester = this),
            newState = SubsonicState.PINGING
        )
    }

    fun ping(context: Context) = this.checkInternetConnection(context = context)

    fun disconnect() {
        this.subsonicState = SubsonicState.DISCONNECTED
    }

    private fun canMakeRequest(): Boolean {
        return when (this.subsonicState) {
            SubsonicState.IDLE, SubsonicState.DISCONNECTED -> true
            else -> false
        }
    }
}