/*
 * This file is part of Satunes.
 *
 * Satunes is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * Satunes is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License along with Satunes.
 *
 * If not, see <https://www.gnu.org/licenses/>.
 *
 * **** INFORMATION ABOUT THE AUTHOR *****
 * The author of this file is Antoine Pirlot, the owner of this project.
 * You find this original project on Codeberg.
 *
 * My Codeberg link is: https://codeberg.org/antoinepirlot
 * This current project's link is: https://codeberg.org/antoinepirlot/Satunes
 */

package io.github.antoinepirlot.satunes.database.models

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.net.Uri.encode
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import io.github.antoinepirlot.satunes.database.data.DEFAULT_ROOT_FILE_PATH
import io.github.antoinepirlot.satunes.database.services.data.DataManager
import io.github.antoinepirlot.satunes.database.services.database.DatabaseManager
import io.github.antoinepirlot.satunes.database.services.settings.SettingsManager
import io.github.antoinepirlot.satunes.utils.utils.toCircularBitmap
import java.util.Date
import io.github.antoinepirlot.satunes.icons.R as RIcon


/**
 * @author Antoine Pirlot on 27/03/2024
 */

class Music(
    id: Long,
    title: String,
    displayName: String,
    val absolutePath: String,
    val duration: Long = 0,
    val size: Int = 0,
    cdTrackNumber: Int? = null,
    addedDateMs: Long,
    var folder: Folder,
    val artist: Artist,
    val album: Album,
    val genre: Genre,
    uri: Uri? = null,
) : MediaImpl(id = id, title = title.ifBlank { displayName }) {

    /**
     * Remember in which order music has been added to playlists
     */
    private val _playlistsOrderMap: MutableMap<Playlist, Long> = mutableMapOf()

    val relativePath: String = this.absolutePath.replace("$DEFAULT_ROOT_FILE_PATH/", "")

    val cdTrackNumber: Int?

    public override var addedDate: Date? = null

    var liked: MutableState<Boolean> = mutableStateOf(false)
        private set

    val uri: Uri? = uri ?: encode(absolutePath).toUri() // Must be init before media item

    val mediaItem: MediaItem = getMediaMetadata()

    init {
        if (cdTrackNumber != null && cdTrackNumber < 1)
            this.cdTrackNumber = null
        else
            this.cdTrackNumber = cdTrackNumber
        DataManager.addMusic(music = this)
        album.addMusic(music = this)
        if (SettingsManager.compilationMusic) {
            album.artist.addMusic(music = this)
        }
        this.addedDate = Date(addedDateMs)
        artist.addMusic(music = this)
        genre.addMusic(music = this)
        folder.addMusic(music = this)
    }

    fun switchLike() {
        this.liked.value = !this.liked.value
        val db = DatabaseManager.getInstance()
        if (this.liked.value) {
            db.like(music = this)
        } else {
            db.unlike(music = this)
        }
    }

    fun getYear(): Int? {
        return this.album.year
    }

    private fun getMediaMetadata(): MediaItem {
        val mediaMetaData: MediaMetadata = MediaMetadata.Builder()
            .setArtist(artist.title)
            .setTitle(title)
            .setGenre(genre.title)
            .setAlbumTitle(album.title)
            .build()
        return MediaItem.Builder()
            .setMediaId(id.toString())
            .setUri(uri)
            .setMediaMetadata(mediaMetaData)
            .build()
    }

    fun getAlbumArtwork(context: Context): Bitmap {
        var bitmap: Bitmap? = try {
            val mediaMetadataRetriever = MediaMetadataRetriever()
            mediaMetadataRetriever.setDataSource(context, uri)
            val artwork: ByteArray? = mediaMetadataRetriever.embeddedPicture
            mediaMetadataRetriever.release()
            if (artwork == null) null
            else BitmapFactory.decodeByteArray(artwork, 0, artwork.size)
        } catch (e: Throwable) {
            _logger?.warning(e.message)
            null
        }
        if (bitmap == null)
            bitmap = AppCompatResources.getDrawable(
                context,
                RIcon.mipmap.empty_album_artwork_foreground
            )!!.toBitmap()
        return if (SettingsManager.artworkCircleShape.value) bitmap.toCircularBitmap() else bitmap
    }

    /**
     * Link this [Music] to [Playlist] with its order in the [Playlist].
     *
     * @param playlist a [Playlist] where this [Music] has been added.
     * @param order [Long] value indicating the position in the playlist.
     *
     * @throws IllegalArgumentException if the [Playlist] has already been added.
     */
    fun setOrderInPlaylist(playlist: Playlist, order: Long) {
        if (this._playlistsOrderMap.containsKey(key = playlist)) return
        this._playlistsOrderMap[playlist] = order
    }

    /**
     * Remove the link between this [Music] and the [Playlist].
     *
     * @param playlist the [Playlist] to remove the link.
     */
    fun removeOrderInPlaylist(playlist: Playlist) {
        this._playlistsOrderMap.remove(key = playlist)
    }

    /**
     * Returns the order of this [Music] in the [Playlist]
     * or -1 if there's no link between this [Music] and the [Playlist].
     *
     * @param playlist the [Playlist] where this [Music] should be.
     *
     * @return a [Long] value representing the order of this [Music] in the [Playlist] otherwise -1.
     */
    fun getOrder(playlist: Playlist): Long {
        return this._playlistsOrderMap[playlist] ?: -1
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Music

        return this.id == other.id
    }

    override fun hashCode(): Int {
        return this.id.hashCode()
    }

    override fun compareTo(other: MediaImpl): Int {
        val compared: Int = super.compareTo(other)
        if (compared == 0 && this != other) return 1
        return compared
    }
}