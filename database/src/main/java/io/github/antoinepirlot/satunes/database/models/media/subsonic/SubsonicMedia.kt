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

package io.github.antoinepirlot.satunes.database.models.media.subsonic

import android.content.Context
import android.os.Environment
import androidx.compose.ui.graphics.ImageBitmap
import io.github.antoinepirlot.satunes.database.models.DownloadStatus
import io.github.antoinepirlot.satunes.database.models.media.Media

/**
 * @author Antoine Pirlot 13/12/2025
 */
interface SubsonicMedia : Media {

    companion object {

        fun getDownloadStorage(context: Context): String =
            context.getExternalFilesDir(Environment.DIRECTORY_MUSIC).toString()
    }

    val subsonicId: Long

    fun loadArtwork(context: Context, onDataRetrieved: (artwork: ImageBitmap?) -> Unit) {
        throw UnsupportedOperationException()
    }

    fun canBeDownloaded(): Boolean =
        this.downloadStatus == DownloadStatus.NOT_DOWNLOADED || this.downloadStatus == DownloadStatus.ERROR

    fun canDownloadBeRemoved(): Boolean = this.downloadStatus == DownloadStatus.DOWNLOADED

    /**
     * Stores [SubsonicMusic]s into Satunes' storage for offline usage.
     * If it is already stored, do nothing
     *
     * @param context the [Context] to get the parent folder.
     *
     * @throws IllegalStateException if this media is not [SubsonicMedia]
     */
    fun download(context: Context)

    /**
     * Removes the downloaded file matching the musics.
     */
    fun removeDownload()

    override fun isSubsonic(): Boolean = true
}