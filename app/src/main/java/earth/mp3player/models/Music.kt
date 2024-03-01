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

import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import earth.mp3player.services.PlaybackController

class Music(
    override val id: Long,
    override val name: String,
    val duration: Long,
    val size: Int,
    val uri: Uri,
    val relativePath: String,
    var folder: Folder? = null,
    var artist: Artist? = null,
    var album: Album? = null,
) : Media {

    val mediaItem: MediaItem
    val absolutePath: String = "${PlaybackController.ROOT_PATH}/$relativePath/$name"
    var artwork: ByteArray? = null

    init {
        this.mediaItem = MediaItem.Builder()
            .setUri(this.absolutePath)
            .build()
    }
}