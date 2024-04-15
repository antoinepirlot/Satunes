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

import android.app.Activity
import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import earth.mp3player.internet.Versions.ALPHA
import earth.mp3player.internet.Versions.ALPHA_REGEX
import earth.mp3player.internet.Versions.BETA
import earth.mp3player.internet.Versions.BETA_REGEX
import earth.mp3player.internet.Versions.PREVIEW
import earth.mp3player.internet.Versions.PREVIEW_REGEX
import earth.mp3player.internet.Versions.RELEASES_URL
import earth.mp3player.internet.Versions.RELEASE_REGEX
import earth.mp3player.internet.Versions.versionType
import earth.mp3player.internet.utils.showToastOnUiThread
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response


/**
 * @author Antoine Pirlot on 11/04/2024
 */
object UpdateCheckManager {
    val updateAvailableStatus: MutableState<UpdateAvailableStatus> =
        mutableStateOf(UpdateAvailableStatus.UNDEFINED)
    val isCheckingUpdate: MutableState<Boolean> = mutableStateOf(false)
    val latestVersion: MutableState<String?> = mutableStateOf(null)
    val downloadStatus: MutableState<APKDownloadStatus> =
        mutableStateOf(APKDownloadStatus.NOT_STARTED)

    /**
     * Create a httpclient and get the response matching url.
     *
     * @param context the context :p
     * @param url the url to get the response
     */
    fun getUrlResponse(context: Context, url: String): Response? {
        val internetManager = InternetManager(context = context)
        if (!internetManager.isConnected()) {
            UpdateAvailableStatus.CANNOT_CHECK.updateLink = null
            updateAvailableStatus.value = UpdateAvailableStatus.CANNOT_CHECK
            isCheckingUpdate.value = false
            return null
        }
        val httpClient = OkHttpClient()
        val req: Request = Request.Builder()
            .url(url)
            .build()
        return httpClient.newCall(req).execute()
    }

    /**
     * Checks if an update is available if there's an internet connection
     * and update the available update link.
     */
    fun checkUpdate(context: Context) {
        //Check update
        val activity = Activity()
        CoroutineScope(Dispatchers.IO).launch {
            isCheckingUpdate.value = true
            showToastOnUiThread(
                context = context,
                activity = activity,
                message = context.getString(R.string.checking_update)
            )
            try {
                //Get all versions
                val res: Response = getUrlResponse(context = context, url = RELEASES_URL)!!
                if (!res.isSuccessful) {
                    res.close()
                    UpdateAvailableStatus.CANNOT_CHECK.updateLink = null
                    updateAvailableStatus.value = UpdateAvailableStatus.CANNOT_CHECK
                    isCheckingUpdate.value = false
                    return@launch
                }
                val page: String = res.body!!.string()
                res.close()

                val currentVersion: String =
                    'v' + getCurrentVersion(context = context)
                val updateUrl: String? = getUpdateUrl(page = page, currentVersion = currentVersion)
                UpdateAvailableStatus.AVAILABLE.updateLink = updateUrl
                if (updateUrl == null) {
                    updateAvailableStatus.value = UpdateAvailableStatus.UP_TO_DATE
                    showToastOnUiThread(
                        context = context,
                        activity = activity,
                        message = context.getString(R.string.no_update)
                    )
                } else {
                    updateAvailableStatus.value = UpdateAvailableStatus.AVAILABLE
                    showToastOnUiThread(
                        context = context,
                        activity = activity,
                        message = context.getString(R.string.update_available)
                    )
                }
            } catch (_: Exception) {
                //Don't crash the app if an error occurred internet connection
                //Don't care of internet
                updateAvailableStatus.value = UpdateAvailableStatus.CANNOT_CHECK
                showToastOnUiThread(
                    context = context,
                    activity = activity,
                    message = context.getString(R.string.cannot_check_update)
                )
            } finally {
                isCheckingUpdate.value = false
            }
        }
    }

    /**
     * Generate the update URL and return it.
     *
     * @param page the html page of github releases
     * @param currentVersion the version of the installed app (e.g. vx.y.z[-[versionType]])
     *
     * @return the generated update url from page or null if the app is up to date.
     */
    private fun getUpdateUrl(page: String, currentVersion: String): String? {
        val currentVersionType: String = currentVersion.split("-").last()
        val regex: Regex = when (currentVersionType) {
            ALPHA -> ALPHA_REGEX
            BETA -> BETA_REGEX
            PREVIEW -> PREVIEW_REGEX
            else -> RELEASE_REGEX
        }
        val latestVersion: String? =
            regex.find(
                page,
                0
            )?.value?.split("/")?.last()?.split("\"")?.first()
        return if (latestVersion != null && latestVersion != currentVersion) {
            this.latestVersion.value = latestVersion
            "$RELEASES_URL/tag/$latestVersion"
            //                UpdateAvailableStatus.AVAILABLE
        } else {
            null
        }
    }

    fun getCurrentVersion(context: Context): String {
        val versionName: String =
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        versionType = versionName.split("-").last()
        return versionName
    }
}