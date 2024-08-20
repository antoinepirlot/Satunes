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

package io.github.antoinepirlot.satunes.internet.updates

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import io.github.antoinepirlot.satunes.internet.R
import io.github.antoinepirlot.satunes.utils.utils.showToastOnUiThread

/**
 * @author Antoine Pirlot on 14/04/2024
 */

@RequiresApi(Build.VERSION_CODES.M)
internal object DownloadReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
            UpdateCheckManager.downloadStatus.value = APKDownloadStatus.DOWNLOADED
            showToastOnUiThread(context = context, context.getString(R.string.downloaded))
        }
    }
}