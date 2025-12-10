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

package io.github.antoinepirlot.satunes.internet.updates

import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import io.github.antoinepirlot.android.utils.logger.Logger
import io.github.antoinepirlot.satunes.database.models.UpdateChannel
import io.github.antoinepirlot.satunes.database.models.UpdateChannel.ALPHA
import io.github.antoinepirlot.satunes.database.models.UpdateChannel.BETA
import io.github.antoinepirlot.satunes.database.models.UpdateChannel.PREVIEW
import io.github.antoinepirlot.satunes.database.services.settings.SettingsManager
import io.github.antoinepirlot.satunes.internet.InternetManager.Companion.getUrlResponse
import io.github.antoinepirlot.satunes.internet.updates.Versions.ALPHA_REGEX
import io.github.antoinepirlot.satunes.internet.updates.Versions.BETA_REGEX
import io.github.antoinepirlot.satunes.internet.updates.Versions.PREVIEW_REGEX
import io.github.antoinepirlot.satunes.internet.updates.Versions.RELEASES_URL
import io.github.antoinepirlot.satunes.internet.updates.Versions.RELEASE_REGEX
import io.github.antoinepirlot.satunes.internet.updates.Versions.TAG_RELEASE_URL
import io.github.antoinepirlot.android.utils.logger.SatunesLogger
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response


/**
 * @author Antoine Pirlot on 11/04/2024
 */

object UpdateCheckManager {
    private val _logger: Logger? = Logger.getLogger()

    val updateAvailableStatus: MutableState<UpdateAvailableStatus> =
        mutableStateOf(UpdateAvailableStatus.UNDEFINED)
    val latestVersion: MutableState<String?> = mutableStateOf(null)
    val downloadStatus: MutableState<APKDownloadStatus> =
        mutableStateOf(APKDownloadStatus.NOT_STARTED)


    /**
     * Checks if an update is available if there's an internet connection
     * and update the available update link.
     */
    fun checkUpdate(context: Context) {
        //Check update
        try {
            //Get all versions
            val res: Response = getUrlResponse(context = context, url = RELEASES_URL)!!
            if (!res.isSuccessful) {
                res.close()
                UpdateAvailableStatus.CANNOT_CHECK.updateLink = null
                updateAvailableStatus.value = UpdateAvailableStatus.CANNOT_CHECK
                return
            }
            val page: String = res.body!!.string()
            res.close()

            val currentVersion: String =
                'v' + getCurrentVersion(context = context)
            val updateUrl: String? = getUpdateUrl(page = page, currentVersion = currentVersion)
            UpdateAvailableStatus.AVAILABLE.updateLink = updateUrl
            if (updateUrl == null) {
                updateAvailableStatus.value = UpdateAvailableStatus.UP_TO_DATE
            } else {
                updateAvailableStatus.value = UpdateAvailableStatus.AVAILABLE
            }
        } catch (e: Throwable) {
            //Don't crash the app if an error occurred internet connection
            //Don't care of internet
            updateAvailableStatus.value = UpdateAvailableStatus.CANNOT_CHECK
            _logger?.severe(e.message)
            throw e
        }
    }

    /**
     * Generate the update URL and return it.
     *
     * @param page the html page of github releases
     * @param currentVersion the version of the installed app (e.g. vx.y.z[-versionType])
     *
     * @return the generated update url from page or null if the app is up to date.
     */
    private fun getUpdateUrl(page: String, currentVersion: String): String? {
        val regex: Regex = when (SettingsManager.updateChannel.value) {
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
        if (latestVersion == null) {
            val message =
                "No update url found. Latest version is $latestVersion & currentVersion is $currentVersion"
            _logger?.warning(message)
            return null
        }

        return if (this.isUpdateAvailable(
                latestVersion = latestVersion,
                currentVersion = currentVersion
            )
        ) {
            UpdateCheckManager.latestVersion.value = latestVersion
            "$TAG_RELEASE_URL/$latestVersion"
        } else {
            val message =
                "No update url found. Latest version is $latestVersion & currentVersion is $currentVersion"
            _logger?.warning(message)
            null
        }
    }

    /**
     * Check if an update is available based on update channel.
     * The scope of channel is based on [UpdateChannel] order
     *      - Alpha gets all versions
     *      - Beta gets Beta, Preview and Stable releases
     *      - Preview gets Preview and Stable releases
     *      - Stable only gets Stable releases
     */
    private fun isUpdateAvailable(latestVersion: String, currentVersion: String): Boolean {
        val selectedChannel: UpdateChannel = SettingsManager.updateChannel.value
        var split: List<String> = currentVersion.split("-")
        val currentVersionNumber: String = split[0].split("v")[1]
        val currentChannel: UpdateChannel =
            if (split.size == 1) UpdateChannel.STABLE else UpdateChannel.getUpdateChannel(name = split[1])
        val currentChannelVersion: Int = if (split.size == 1) 0 else split[2].toInt()

        split = latestVersion.split("-")
        val latestVersionNumber: String = split[0].split("v")[1]
        val latestChannel: UpdateChannel =
            if (split.size == 1) UpdateChannel.STABLE else UpdateChannel.getUpdateChannel(name = split[1])
        val latestChannelVersion: Int = if (split.size == 1) 0 else split[2].toInt()

        var numberIncreased = false
        val currentVersionToCheck: List<String> = currentVersionNumber.split(".")
        val latestVersionToCheck: List<String> = latestVersionNumber.split(".")
        for (i: Int in 0..<3) { // "Version is like x.y.z (3 numbers)
            val currentNumber: Int = currentVersionToCheck[i].toInt()
            val latestNumber: Int = latestVersionToCheck[i].toInt()
            if (currentNumber > latestNumber) return false
            if (currentNumber < latestNumber) {
                numberIncreased = true
                break
            }
        }

        if (isMoreStableThan(latestChannel, selectedChannel)
            || isMoreStableThan(latestChannel, currentChannel)
        ) {
            return numberIncreased || latestChannel != currentChannel || currentChannelVersion < latestChannelVersion
        }
        return false
    }

    /**
     * Returns true if [channel1].stability >= [channel2].stability. False otherwise
     */
    private fun isMoreStableThan(channel1: UpdateChannel, channel2: UpdateChannel): Boolean {
        return channel1.stability >= channel2.stability
    }

    fun getCurrentVersion(context: Context): String {
        val versionName: String = try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName!!
        } catch (e: PackageManager.NameNotFoundException) {
            _logger?.severe(e.message)
            throw e
        }
        return versionName
    }
}