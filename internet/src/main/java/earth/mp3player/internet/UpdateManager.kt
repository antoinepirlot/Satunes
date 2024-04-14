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

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.getSystemService
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
    private const val ALPHA: String = "alpha"
    private const val BETA: String = "beta"
    private const val PREVIEW: String = "preview"

    private val ALPHA_REGEX: Regex =
        Regex("\"/antoinepirlot/MP3-Player/releases/tag/v[0-9]+\\.[0-9]+\\.[0-9]+.*\"")
    private val ALPHA_APK_REGEX: Regex = Regex(">.*v[0-9]+\\.[0-9]+\\.[0-9]+.*\\.apk<")

    private val BETA_REGEX: Regex =
        Regex("\"/antoinepirlot/MP3-Player/releases/tag/v[0-9]+\\.[0-9]+\\.[0-9]+(?!-$ALPHA).*\"")
    private val BETA_APK_REGEX: Regex = Regex(">.*v[0-9]+\\.[0-9]+\\.[0-9]+(?!-$ALPHA).*\\.apk<")

    private val PREVIEW_REGEX: Regex =
        Regex("\"/antoinepirlot/MP3-Player/releases/tag/v[0-9]+\\.[0-9]+\\.[0-9]+(?!-$ALPHA)(?!-$BETA)\"")
    private val PREVIEW_APK_REGEX: Regex =
        Regex(">.*v[0-9]+\\.[0-9]+\\.[0-9]+(?!-$ALPHA)(?!-$BETA)\\.apk<")

    private val RELEASE_REGEX: Regex =
        Regex("\"/antoinepirlot/MP3-Player/releases/tag/v[0-9]+\\.[0-9]+\\.[0-9]+\"")
    private val RELEASE_APK_REGEX: Regex = Regex(">.*v[0-9]+\\.[0-9]+\\.[0-9]+.apk\"<")

    private const val RELEASES_URL = "https://github.com/antoinepirlot/MP3-Player/releases"

    private const val MIME_TYPE = "application/vnd.android.package-archive"

    private var versionType: String = "" //Alpha, Beta, Preview or "" for Stable version
    private var downloadId: Long = -1

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
    private fun getUrlResponse(context: Context, url: String): Response? {
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
        isCheckingUpdate.value = true
        CoroutineScope(Dispatchers.IO).launch {
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

                //TODO remove hard coded old version
                val currentVersion: String =
                    'v' + "0.7.0-beta"//getCurrentVersion(context = context)
                val updateUrl: String? = getUpdateUrl(page = page, currentVersion = currentVersion)
                UpdateAvailableStatus.AVAILABLE.updateLink = updateUrl
                if (updateUrl == null) {
                    updateAvailableStatus.value = UpdateAvailableStatus.UP_TO_DATE
                } else {
                    updateAvailableStatus.value = UpdateAvailableStatus.AVAILABLE
                }
            } catch (_: Exception) {
                //Don't crash the app if an error occurred internet connection
                //Don't care of internet
                updateAvailableStatus.value = UpdateAvailableStatus.CANNOT_CHECK
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

    //TODO add button to run it
    fun downloadUpdateApk(context: Context) {
        downloadStatus.value = APKDownloadStatus.CHECKING
        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (updateAvailableStatus.value != UpdateAvailableStatus.AVAILABLE) {
                    //Can't be downloaded
                    downloadStatus.value = APKDownloadStatus.NOT_FOUND
                    return@launch
                }
                val downloadUrl: String = getDownloadUrl(context = context) ?: return@launch
                val appName: String = downloadUrl.split("/").last().split("_").first()
                val downloadManager: DownloadManager = context.getSystemService()!!
                val downloadUri: Uri = Uri.parse(downloadUrl)
                val req: DownloadManager.Request = DownloadManager.Request(downloadUri)
                req.setMimeType(MIME_TYPE)
                val destination =
                    "file://" + context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                        .toString() + "/$appName"
                val destinationUri: Uri = Uri.parse(destination)
                req.setDestinationUri(destinationUri)

                req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                setDownloadReceiver(context = context)
                downloadId = downloadManager.enqueue(req)
                downloadStatus.value = APKDownloadStatus.DOWNLOADING
            } catch (_: Exception) {
                downloadStatus.value = APKDownloadStatus.FAILED
            }
        }
    }

    /**
     * Get the download url for the latest version. It looks like:
     * "https://github.com/antoinepirlot/MP3-Player/releases/download/vx.y.z-beta/[name]_vx.y.z[-versionType].apk"
     *
     * @return the download url or null if not found
     */
    private fun getDownloadUrl(context: Context): String? {
        val regex: Regex =
            when (versionType) {
                ALPHA -> ALPHA_APK_REGEX
                BETA -> BETA_APK_REGEX
                PREVIEW -> PREVIEW_APK_REGEX
                else -> RELEASE_APK_REGEX
            }
        val res: Response = getUrlResponse(
            context = context,
            url = "$RELEASES_URL/expanded_assets/${latestVersion.value}"
        )!!
        if (!res.isSuccessful) {
            res.close()
            downloadStatus.value = APKDownloadStatus.NOT_FOUND
            return null
        }
        val page: String = res.body!!.string()
        res.close()
        var apkFileName: String? = regex.find(
            page,
            0
        )?.value
        if (apkFileName == null) {
            downloadStatus.value = APKDownloadStatus.NOT_FOUND
            return null
        }

        apkFileName = apkFileName.drop(1).dropLast(1) //Avoid > and <
        return "$RELEASES_URL/download/${latestVersion.value}/$apkFileName"
    }


    //TODO make it available for Android R and later
    private fun setDownloadReceiver(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(
                DownloadReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
                Context.RECEIVER_EXPORTED
            )
        }
    }

    /**
     * Launch the installation procedure by request the user to install the app.
     */
    fun installUpdate(context: Context) {
        val downloadManager: DownloadManager =
            context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val contentUri: Uri = downloadManager.getUriForDownloadedFile(downloadId)
        val install = Intent(Intent.ACTION_VIEW)
        install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        install.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        install.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true)
        install.data = contentUri
        context.startActivity(install)
    }
}