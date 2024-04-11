/*
 *  This file is part of MP3 Player.
 *
 *  MP3 Player is free software: you can redistribute it and/or modify it under
 *  the terms of the GNU General Public License as published by the Free Software Foundation,
 *  either version 3 of the License, or (at your option) any later version.
 *
 *  MP3 Player is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *   without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with MP3 Player.
 *  If not, see <https://www.gnu.org/licenses/>.
 *
 *  ***** INFORMATIONS ABOUT THE AUTHOR *****
 *  The author of this file is Antoine Pirlot, the owner of this project.
 *  You find this original project on github.
 *
 *  My github link is: https://github.com/antoinepirlot
 *  This current project's link is: https://github.com/antoinepirlot/MP3-Player
 *
 *  You can contact me via my email: pirlot.antoine@outlook.com
 *  PS: I don't answer quickly.
 */

package earth.mp3player.database.models

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import earth.mp3player.database.services.DataManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * @author Antoine Pirlot on 27/03/2024
 */

class Music(
    override val id: Long,
    override var title: String,
    private var displayName: String,
    val absolutePath: String,
    val duration: Long = 0,
    val size: Int = 0,
    var folder: Folder?,
    var artist: Artist? = null,
    var album: Album? = null,
    var genre: Genre? = null,
    context: Context,
) : Media {
    var uri: Uri = Uri.parse(absolutePath) // Must be init before media item
    val mediaItem: MediaItem = getMediaMetadata()
    var artwork: ImageBitmap? = null

    init {
        DataManager.musicMediaItemSortedMap[this@Music] = mediaItem
        album?.addMusic(music = this@Music)
        artist?.addMusic(music = this@Music)
        genre?.addMusic(music = this@Music)
        folder?.addMusic(music = this@Music)
        loadAlbumArtwork(context = context)

        if (displayName != title) {
            displayName = Uri.encode(displayName)
        }
        title = Uri.encode(title)
    }


    private fun getMediaMetadata(): MediaItem {
        val mediaMetaData: MediaMetadata = MediaMetadata.Builder()
            .setArtist(if (artist != null) artist!!.title else null)
            .setTitle(title)
            .setGenre(if (genre != null) genre!!.title else null)
            .setAlbumTitle(if (album != null) album!!.title else null)
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

                if (artwork != null) {
                    val bitmap: Bitmap = BitmapFactory.decodeByteArray(artwork, 0, artwork.size)
                    this@Music.artwork = bitmap.asImageBitmap()
                    if (this@Music.album != null && this@Music.album!!.artwork == null) {
                        this@Music.album!!.artwork = this@Music.artwork
                    }
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
        result = 31 * result + (artist?.hashCode() ?: 0)
        result = 31 * result + (album?.hashCode() ?: 0)
        return result
    }
}