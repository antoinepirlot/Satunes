/*
 * This file is part of MP3 Player.
 *
 * MP3 Player is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * MP3 Player is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with MP3 Player.
 * If not, see <https://www.gnu.org/licenses/>.
 *
 * **** INFORMATIONS ABOUT THE AUTHOR *****
 * The author of this file is Antoine Pirlot, the owner of this project.
 * You find this original project on github.
 *
 * My github link is: https://github.com/antoinepirlot
 * This current project's link is: https://github.com/antoinepirlot/MP3-Player
 *
 * You can contact me via my email: pirlot.antoine@outlook.com
 * PS: I don't answer quickly.
 */

package earth.mp3player.internet

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

/**
 * @author Antoine Pirlot on 11/04/2024
 */
object UpdateManager {
    val updateAvailable: MutableState<UpdateAvailableStatus> =
        mutableStateOf(UpdateAvailableStatus.UNDEFINED)
    val isCheckingUpdate: MutableState<Boolean> = mutableStateOf(false)

    private const val URL = "https://github.com/antoinepirlot/MP3-Player/releases"

    /**
     * Checks if an update is available if there's an internet connection.
     */
    fun checkUpdate(context: Context) {
        //Check update
        isCheckingUpdate.value = true
        try {
            val internetManager = InternetManager(context = context)
            if (!internetManager.isConnected()) {
                UpdateAvailableStatus.CANNOT_CHECK.updateLink = null
                updateAvailable.value = UpdateAvailableStatus.CANNOT_CHECK
                isCheckingUpdate.value = false
                return
            }
            CoroutineScope(Dispatchers.IO).launch {
                //Get all versions
                val httpClient = OkHttpClient()
                val req: Request = Request.Builder()
                    .url(URL)
                    .build()
                val res: Response = httpClient.newCall(req).execute()
                if (!res.isSuccessful) {
                    res.close()
                    UpdateAvailableStatus.CANNOT_CHECK.updateLink = null
                    updateAvailable.value = UpdateAvailableStatus.CANNOT_CHECK
                    isCheckingUpdate.value = false
                    return@launch
                }
                val page: String = res.body!!.string()
                res.close()

                val currentVersion: String = 'v' + getCurrentVersion(context = context)
                if (isBeta(currentVersion)) {
                    checkBetaVersion(page = page, currentVersion = currentVersion)
                } else {
                    checkReleaseVersion(page = page, currentVersion = currentVersion)
                }
                isCheckingUpdate.value = false
            }
        } catch (_: Exception) {
            //Don't crash the app if not internet connection
            isCheckingUpdate.value = false
        }
    }

    private fun isBeta(version: String): Boolean = version.split("-").last() == "beta"

    private fun checkBetaVersion(page: String, currentVersion: String) {
        val latestBetaVersion: String? =
            Regex("/antoinepirlot/MP3-Player/releases/tag/v[0-9]+\\.[0-9]+\\.[0-9]+(-beta)?").find(
                page,
                0
            )?.value?.split("/")?.last()
        updateAvailable.value =
            if (latestBetaVersion != null && latestBetaVersion != currentVersion) {
                UpdateAvailableStatus.AVAILABLE.updateLink = "$URL/tag/$latestBetaVersion"
                UpdateAvailableStatus.AVAILABLE
            } else {
                UpdateAvailableStatus.UP_TO_DATE.updateLink = null
                UpdateAvailableStatus.UP_TO_DATE
            }
    }

    private fun checkReleaseVersion(page: String, currentVersion: String) {
        val latestReleaseVersion: String? =
            Regex("/antoinepirlot/MP3-Player/releases/tag/v[0-9]+\\.[0-9]+\\.[0-9]+^(-beta)").find(
                page,
                0
            )?.value?.split("/")?.last()
        updateAvailable.value =
            if (latestReleaseVersion != null && latestReleaseVersion != currentVersion) {
                UpdateAvailableStatus.AVAILABLE.updateLink = "$URL/tag/$latestReleaseVersion"
                UpdateAvailableStatus.AVAILABLE
            } else {
                UpdateAvailableStatus.UP_TO_DATE.updateLink = null
                UpdateAvailableStatus.UP_TO_DATE
            }
    }

    fun getCurrentVersion(context: Context): String {
        return context.packageManager.getPackageInfo(context.packageName, 0).versionName
    }
}