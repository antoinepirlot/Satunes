/*
 * This file is part of Satunes.
 *
 *  Satunes is free software: you can redistribute it and/or modify it under
 *  the terms of the GNU General Public License as published by the Free Software Foundation,
 *  either version 3 of the License, or (at your option) any later version.
 *
 *  Satunes is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with Satunes.
 *  If not, see <https://www.gnu.org/licenses/>.
 *
 *  **** INFORMATIONS ABOUT THE AUTHOR *****
 *  The author of this file is Antoine Pirlot, the owner of this project.
 *  You find this original project on github.
 *
 *  My github link is: https://github.com/antoinepirlot
 *  This current project's link is: https://github.com/antoinepirlot/Satunes
 *
 *  You can contact me via my email: pirlot.antoine@outlook.com
 *  PS: I don't answer quickly.
 */

package io.github.antoinepirlot.satunes.utils

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import io.github.antoinepirlot.satunes.MainActivity
import io.github.antoinepirlot.satunes.data.viewmodels.SatunesViewModel
import io.github.antoinepirlot.satunes.database.services.data.DataCleanerManager
import io.github.antoinepirlot.satunes.database.services.data.DataLoader
import io.github.antoinepirlot.satunes.database.services.settings.SettingsManager
import io.github.antoinepirlot.satunes.playback.services.PlaybackManager
import io.github.antoinepirlot.satunes.playback.services.PlaybackService
import io.github.antoinepirlot.satunes.utils.logger.SatunesLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * @author Antoine Pirlot on 29/08/2024
 */

internal fun initSatunes(
    context: Context,
    satunesViewModel: SatunesViewModel?,
) {
    loadSatunesData(context = context, satunesViewModel = satunesViewModel)
    PlaybackManager.checkPlaybackController(context = context, loadAllMusic = false)
    setNotificationOnClick(context = context)
    removeSatunesDownloadedApkFiles(context = context)
}

internal fun loadSatunesData(
    context: Context,
    satunesViewModel: SatunesViewModel?
) {
    if (satunesViewModel == null) {
        runBlocking {
            SettingsManager.loadSettings(context = context)
            DataLoader.loadAllData(context = context)
        }
    } else {
        satunesViewModel.loadSettings()
        satunesViewModel.loadAllData()
    }
}

internal fun removeSatunesDownloadedApkFiles(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        DataCleanerManager.removeApkFiles(context = context)
    } else {
        SatunesLogger.getLogger()
            .warning("Can't remove apk files with API: ${Build.VERSION.SDK_INT}")
    }
}

/**
 * When the user click on playing notification, the app is opened.
 */
@OptIn(UnstableApi::class)
internal fun setNotificationOnClick(context: Context) {
    val intent = Intent(context.applicationContext, MainActivity::class.java)
    CoroutineScope(Dispatchers.IO).launch {
        while (PlaybackService.mediaSession == null) {
            // The init has to be completed
        }
        val pendingIntent = PendingIntent.getActivity(
            context.applicationContext, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        PlaybackService.mediaSession!!.setSessionActivity(pendingIntent)
    }
}