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

package io.github.antoinepirlot.satunes.internet

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi
import io.github.antoinepirlot.satunes.internet.updates.UpdateAvailableStatus
import io.github.antoinepirlot.satunes.internet.updates.UpdateCheckManager.updateAvailableStatus
import io.github.antoinepirlot.satunes.utils.logger.SatunesLogger
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

/**
 * @author Antoine Pirlot on 11/04/2024
 */
@RequiresApi(Build.VERSION_CODES.M)
internal class InternetManager(context: Context) : Application() {

    companion object {
        private val _logger: SatunesLogger? = SatunesLogger.getLogger()

        /**
         * Create a httpclient and get the response matching url.
         *
         * @param context the context :p
         * @param url the url to get the response
         */
        internal fun getUrlResponse(context: Context, url: String): Response? {
            return try {
                val internetManager = InternetManager(context = context)
                if (!internetManager.isConnected()) {
                    UpdateAvailableStatus.CANNOT_CHECK.updateLink = null
                    updateAvailableStatus.value = UpdateAvailableStatus.CANNOT_CHECK
                    return null
                }
                val httpClient = OkHttpClient()
                val req: Request = Request.Builder()
                    .url(url)
                    .build()
                httpClient.newCall(req).execute()
            } catch (e: Throwable) {
                _logger?.warning(e.message)
                null
            }
        }
    }

    private val connectivityManager: ConnectivityManager =
        context.getSystemService(ConnectivityManager::class.java)

    private val currentNetwork: Network? = connectivityManager.activeNetwork

    internal fun isConnected(): Boolean {
        return if (currentNetwork != null) {
            isConnected(capabilities = connectivityManager.getNetworkCapabilities(currentNetwork))
        } else {
            _logger?.warning("Current network is null")
            false
        }
    }

    private fun isConnected(capabilities: NetworkCapabilities?): Boolean {
        return capabilities != null && (
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                        || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                )

    }
}