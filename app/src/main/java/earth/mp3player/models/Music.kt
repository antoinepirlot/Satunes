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

 *  ***** INFORMATIONS ABOUT THE AUTHOR *****
 *  The author of this file is Antoine Pirlot, the owner of this project.
 *  You find this original project on github.
 *
 *  My github link is: https://github.com/antoinepirlot
 *  This current project's link is: https://github.com/antoinepirlot/MP3-Player
 *
 *  You can contact me via my email: pirlot.antoine@outlook.com
 * PS: I don't answer quickly.
 */

package earth.mp3player.models

import android.content.Context
import android.net.Uri
import android.os.storage.StorageManager
import android.os.storage.StorageVolume
import androidx.compose.ui.graphics.ImageBitmap
import androidx.core.content.getSystemService
import androidx.media3.common.MediaItem
import earth.mp3player.services.playback.PlaybackController
import java.io.File

/**
 * @author Antoine Pirlot on 19/01/24
 */

class Music(
    override val id: Long,
    val displayName: String,
    override val title: String = displayName,
    val duration: Long,
    val size: Int,
    var relativePath: String,
    var folder: Folder? = null,
    var artist: Artist? = null,
    var album: Album? = null,
    var genre: Genre? = null,
    context: Context
) : Media {

    val mediaItem: MediaItem
    private var absolutePath: String? = "${PlaybackController.ROOT_PATH}/$relativePath/$displayName"
    var uri: Uri = Uri.Builder().appendPath(this.absolutePath).build()
    var artwork: ImageBitmap? = null

    init {
        val storageManager = context.getSystemService<StorageManager>()
        val storageVolumes: List<StorageVolume> = storageManager!!.storageVolumes

        for (volume in storageVolumes) {
            absolutePath = "${volume.directory!!.path}/$relativePath/$displayName"
            if (!File(this.absolutePath!!).exists()) {
                if (storageVolumes.last() == volume) {
                    throw IllegalAccessException("This media doesn't exist")
                }
                continue
            }
            relativePath = "${volume.directory!!.path.split("/").last()}/$relativePath"
            this.uri = Uri.parse(absolutePath)
            break
        }

        this.mediaItem = MediaItem.Builder()
            .setUri(this.uri)
            .build()

        if (this.album != null) {
            this.album!!.addMusic(music = this)
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