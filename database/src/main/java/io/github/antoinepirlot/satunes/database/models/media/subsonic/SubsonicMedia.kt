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
import io.github.antoinepirlot.android.utils.logger.Logger
import io.github.antoinepirlot.satunes.database.models.DownloadStatus
import io.github.antoinepirlot.satunes.database.models.internet.ApiRequester
import io.github.antoinepirlot.satunes.database.models.media.Media
import io.github.antoinepirlot.satunes.database.models.media.Music

/**
 * @author Antoine Pirlot 13/12/2025
 */
interface SubsonicMedia : Media {

    companion object {
        private val _logger: Logger? = Logger.getLogger()

        fun getDownloadStorage(context: Context): String =
            context.getExternalFilesDir(Environment.DIRECTORY_MUSIC).toString()
    }

    val subsonicId: String
    override var downloadStatus: DownloadStatus
    val downloadedMusics: Set<SubsonicMusic>

    val downloadedMusicsCount: Int
        get() = this.downloadedMusics.count()

    fun loadArtwork(context: Context, onDataRetrieved: (artwork: ImageBitmap?) -> Unit) {
        throw UnsupportedOperationException()
    }

    fun canBeDownloaded(): Boolean =
        this.downloadStatus == DownloadStatus.NOT_DOWNLOADED || this.downloadStatus == DownloadStatus.ERROR

    fun canDownloadBeRemoved(): Boolean = this.downloadStatus == DownloadStatus.DOWNLOADED

    private fun updateDownloadStatus(downloadStatus: DownloadStatus) {
        this.downloadStatus = downloadStatus
    }

    fun getApiRequester(): ApiRequester

    /**
     * Stores [SubsonicMusic]s into Satunes' storage for offline usage.
     * If it is already stored, do nothing
     *
     * @param context the [Context] to get the parent folder.
     * @param onDataRetrieved the function to invoke when download has finished
     *
     * @throws IllegalStateException if this media is not [SubsonicMedia]
     */
    suspend fun download(context: Context, onDataRetrieved: () -> Unit, onFinished: (() -> Unit)? = null) {
        if (this.isStoredLocally()) return
        this.updateDownloadStatus(downloadStatus = DownloadStatus.DOWNLOADING)
        if (!this.isMusic()) {
            var downloadStatus: DownloadStatus = DownloadStatus.DOWNLOADED
            var count: Int = 0
            for (music: Music in this.musicCollection) {
                (music as SubsonicMusic).download(
                    context = context,
                    onDataRetrieved = {
                        if (music.downloadStatus != DownloadStatus.DOWNLOADED)
                            downloadStatus = DownloadStatus.ERROR
                    },
                    onFinished = { count++ }
                )
            }

            while (count != this.musicCollection.size);
            onDataRetrieved()
            this.updateDownloadStatus(downloadStatus = downloadStatus)
        }
    }

    /**
     * Removes the downloaded file matching the musics.
     */
    fun removeDownload() {
        val oldList: Collection<SubsonicMusic> = this.downloadedMusics.toList()
        for (music: SubsonicMusic in oldList)
            music.removeDownload()
    }

    fun isDownloaded(): Boolean = this.downloadStatus == DownloadStatus.DOWNLOADED

    override fun isSubsonic(): Boolean = true
    fun addDownloadedMusic(music: SubsonicMusic) {
        if (this.isMusic())
            throw UnsupportedOperationException()
        else
            (this.downloadedMusics as MutableSet<SubsonicMusic>).add(element = music)

        if (this.downloadedMusics.size == this.musicCollection.size)
            this.downloadStatus = DownloadStatus.DOWNLOADED
        else if (this.downloadStatus != DownloadStatus.ERROR)
            this.downloadStatus = DownloadStatus.NOT_DOWNLOADED
    }

    fun removeDownloadedMusic(music: SubsonicMusic) {
        if (this.isMusic())
            throw UnsupportedOperationException()
        else
            (this.downloadedMusics as MutableSet<SubsonicMusic>).remove(element = music)

        if (this.downloadedMusics.isEmpty())
            this.downloadStatus = DownloadStatus.NOT_DOWNLOADED
        //TODO partial
    }
}