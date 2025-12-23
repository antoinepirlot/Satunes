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
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import io.github.antoinepirlot.android.utils.logger.Logger
import io.github.antoinepirlot.android.utils.utils.runIOThread
import io.github.antoinepirlot.satunes.database.models.DownloadStatus
import io.github.antoinepirlot.satunes.database.models.internet.ApiRequester
import io.github.antoinepirlot.satunes.database.models.media.Album
import io.github.antoinepirlot.satunes.database.models.media.Artist
import io.github.antoinepirlot.satunes.database.models.media.Folder
import io.github.antoinepirlot.satunes.database.models.media.Genre
import io.github.antoinepirlot.satunes.database.models.media.Music
import io.github.antoinepirlot.satunes.database.utils.createFile
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

/**
 * @author Antoine Pirlot 11/12/2025
 */
class SubsonicMusic(
    /**
     * [id] can be different from [subsonicId] because [id] is considered as local id if the music is both online or on device.
     * Device will be used instead of subsonic.
     */
    override var subsonicId: Long,
    title: String,
    displayName: String,
    absolutePath: String,
    override var durationMs: Long = 0,
    override var size: Int = 0,
    cdTrackNumber: Int? = null,
    addedDateMs: Long,
    folder: Folder,
    artist: Artist,
    album: Album,
    genre: Genre,
    uri: Uri? = null,
    private val apiRequester: ApiRequester
) : SubsonicMedia, Music(
    id = subsonicId,
    title = title,
    displayName = displayName,
    absolutePath = absolutePath,
    durationMs = durationMs,
    size = size,
    cdTrackNumber = cdTrackNumber,
    addedDateMs = addedDateMs,
    folder = folder,
    artist = artist,
    album = album,
    genre = genre,
    uri = uri,
) {
    private val _logger: Logger? = Logger.getLogger()

    override fun switchLike() {
        TODO("Switch like is not yet implemented")
    }

    /**
     * Update this [SubsonicMusic] with the new [SubsonicMusic] if both [id] are identical
     * If [id] != [subsonicId] it means it is the local music. No update made and no error thrown.
     *
     * @param new the most updated [SubsonicMusic] matching this one.
     */
    fun update(new: SubsonicMusic) {
        if (this.id != this.subsonicId) return //It is the local music matching the server one. No update
        if (new.id != this.id)
            throw IllegalArgumentException("These musics doesn't have the same id. Old id is: ${this.id} and the new id is: $id")
        this.title = new.title
        this.absolutePath = new.absolutePath
        this.durationMs = new.durationMs
        this.size = new.size
        this.cdTrackNumber = new.cdTrackNumber
        this.addedDate = new.addedDate
        this.folder = new.folder
        this.artist = new.artist
        this.album = new.album
        this.genre = new.genre
        this.uri = new.uri
    }

    override fun getAlbumArtwork(context: Context): Bitmap {
        return this.artwork?.applyShape() ?: this.album.getEmptyAlbumArtwork(context = context)
            .applyShape()
    }

    /**
     * Fetch artwork from network and stores it into [artwork]
     */
    override fun loadArtwork(context: Context, onDataRetrieved: (artwork: ImageBitmap?) -> Unit) {
        (this.album as SubsonicAlbum).loadArtwork(
            context = context,
            onDataRetrieved = onDataRetrieved
        )
    }

    override fun download(context: Context) {
        if (this.isStoredLocally()) return
        this.updateDownloadStatus(downloadStatus = DownloadStatus.DOWNLOADING)
        runIOThread {
            apiRequester.download(
                musicId = this.subsonicId,
                onError = {
                    //Do not directly change downloadStatus here as it will throw an error.
                    //Using this function doesn't crash
                    this.updateDownloadStatus(downloadStatus = DownloadStatus.ERROR)
                },
                onDataRetrieved = {
                    if (this.store(context = context, data = it))
                        this.updateDownloadStatus(downloadStatus = DownloadStatus.DOWNLOADED)
                    else
                        this.updateDownloadStatus(downloadStatus = DownloadStatus.ERROR)
                }
            )
        }
    }

    override fun removeDownload() {
        if (!this.isStoredLocally()) return
        val file = File(this.absolutePath)
        try {
            file.delete()
            downloadStatus = DownloadStatus.NOT_DOWNLOADED
        } catch (e: Exception) {
            _logger?.warning(e.message)
        }
    }

    private fun updateDownloadStatus(downloadStatus: DownloadStatus) {
        this.downloadStatus = downloadStatus
    }

    private fun store(context: Context, data: InputStream): Boolean {
        val relativePath: String = "/downloads/" + this.relativePath
        val absolutePath: String = context.filesDir.path + relativePath
        try {
            val file: File = createFile(path = absolutePath)
            val output = FileOutputStream(file)
            try {
                val buffer = ByteArray(size = 2024)
                var bytesRead: Int = data.read(buffer)
                while (bytesRead != -1) {
                    output.write(buffer, 0, bytesRead)
                    bytesRead = data.read(buffer)
                }
                this.absolutePath = absolutePath
                return true
            } catch (e: Exception) {
                _logger?.warning(e.message)
            } finally {
                output.close()
            }
        } catch (e: Exception) {
            _logger?.warning(e.message)
        } finally {
            data.close()
        }
        return false
    }

    override fun equals(other: Any?): Boolean {
        return if (this.javaClass == other?.javaClass) this.subsonicId == (other as SubsonicMusic).subsonicId
        else super.equals(other)
    }

    override fun hashCode(): Int {
        return 31 * super.hashCode() + this.subsonicId.hashCode()
    }
}