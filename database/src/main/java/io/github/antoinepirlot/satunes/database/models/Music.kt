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
 * You find this original project on github.
 *
 * My github link is: https://github.com/antoinepirlot
 * This current project's link is: https://github.com/antoinepirlot/Satunes
 *
 * PS: I don't answer quickly.
 */

package io.github.antoinepirlot.satunes.database.models

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.net.Uri.encode
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import io.github.antoinepirlot.satunes.database.services.data.DataManager
import io.github.antoinepirlot.satunes.database.services.database.DatabaseManager
import io.github.antoinepirlot.satunes.database.services.settings.SettingsManager
import io.github.antoinepirlot.satunes.icons.R
import io.github.antoinepirlot.satunes.utils.logger.SatunesLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
    var folder: Folder,
    val artist: Artist,
    val album: Album,
    val genre: Genre,
    val uri: Uri? = Uri.parse(encode(absolutePath)) // Must be init before media item
) : MediaImpl(id = id, title = title.ifBlank { displayName }) {
    private val _logger: SatunesLogger? = SatunesLogger.getLogger()
    private var displayName: String = displayName
        set(displayName) {
            if (displayName.isBlank()) {
                val message = "Display name must not be blank"
                _logger?.warning(message)
                throw IllegalArgumentException(message)
            }
            field = displayName
        }
    val cdTrackNumber: Int?
    var liked: MutableState<Boolean> = mutableStateOf(false)
        private set

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
        artist.addMusic(music = this)
        genre.addMusic(music = this)
        folder.addMusic(music = this)
    }

    //TODO remove context param as it is unused
    fun switchLike(context: Context) {
        this.liked.value = !this.liked.value
        val db = DatabaseManager.getInstance()
        if (this.liked.value) {
            db.like(music = this)
        } else {
            db.unlike(music = this)
        }
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

    /**
     * Load the artwork from a media meta data retriever.
     * Decode the byte array to set music's artwork as ImageBitmap
     * If there's an artwork add it to music as ImageBitmap.
     *
     * @param context the context
     */
    private fun loadAlbumArtwork(context: Context) {
        // Set Dispatchers.Default instead of Dispatchers.IO unblock IO of too long loading
        // Indirect impact is that it is faster to load settings
        CoroutineScope(Dispatchers.Default).launch {
            try {
                //TODO ask the user to show music's album or album's artwork for all music
                // It could cause visual issues as a music has not the same artwork and the user won't know it
//                if (album.artwork != null) {
//                    this@Music.artwork = album.artwork
//                    return@launch
//                }
                val mediaMetadataRetriever = MediaMetadataRetriever()

                mediaMetadataRetriever.setDataSource(context, uri)

                val artwork: ByteArray? = mediaMetadataRetriever.embeddedPicture
                mediaMetadataRetriever.release()
                if (artwork == null) {
                    this@Music.artwork = ResourcesCompat.getDrawable(
                        context.resources,
                        R.mipmap.empty_album_artwork_foreground,
                        null
                    )?.toBitmap()
                } else {
                    val bitmap: Bitmap = BitmapFactory.decodeByteArray(artwork, 0, artwork.size)
                    this@Music.artwork = bitmap
                }
                if (this@Music.album.artwork == null) {
                    this@Music.album.artwork = this@Music.artwork
                }
            } catch (_: Exception) {
                /* No artwork found*/
                artwork = null
            }
        }
    }

    fun getAlbumArtwork(context: Context): Bitmap? {
        return try {
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
        var compared: Int = super.compareTo(other)
        if (compared == 0 && this != other) return 1
        return compared
    }
}