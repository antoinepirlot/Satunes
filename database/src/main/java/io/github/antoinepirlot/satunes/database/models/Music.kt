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

package io.github.antoinepirlot.satunes.database.models

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.net.Uri.encode
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import io.github.antoinepirlot.satunes.database.services.DataManager
import io.github.antoinepirlot.satunes.icons.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * @author Antoine Pirlot on 27/03/2024
 */

@Serializable
class Music private constructor(
    override val id: Long,
    override var title: String,
    @Transient
    private var displayName: String = "displayName", //defined for transient
    @Transient
    val absolutePath: String = "absolutePath", //defined for transient
    @Transient
    val duration: Long = 0,
    @Transient
    val size: Int = 0,
    @Transient
    var folder: Folder? = null, // Set null otherwise transient throws an error. Folder is never null
    var artist: Artist,
    var album: Album,
    var genre: Genre,
) : Media {
    @Transient
    var uri: Uri = Uri.parse(encode(absolutePath)) // Must be init before media item

    @Transient
    val mediaItem: MediaItem = getMediaMetadata()

    @Transient
    override var artwork: Bitmap? = null

    constructor(
        id: Long,
        title: String,
        displayName: String,
        absolutePath: String,
        duration: Long = 0,
        size: Int = 0,
        folder: Folder,
        artist: Artist,
        album: Album,
        genre: Genre,
        context: Context,
    ) : this(id, title, displayName, absolutePath, duration, size, folder, artist, album, genre) {
        loadAlbumArtwork(context = context)
    }

    init {
        DataManager.addMusic(music = this)
        album.addMusic(music = this@Music)
        artist.addMusic(music = this@Music)
        genre.addMusic(music = this@Music)
        folder!!.addMusic(music = this@Music)
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
        //Put it in Dispatchers.IO make the app not freezing while starting
        CoroutineScope(Dispatchers.IO).launch {
            try {
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Music

        if (displayName != other.displayName) return false
        if (artist != other.artist) return false
        if (album != other.album) return false

        return true
    }

    override fun hashCode(): Int {
        var result = displayName.hashCode()
        result = 31 * result + (artist.hashCode())
        result = 31 * result + (album.hashCode())
        return result
    }
}