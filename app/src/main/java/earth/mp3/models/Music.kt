package earth.mp3.models

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import earth.mp3.services.PlaybackController

class Music(
    override val id: Long,
    override val name: String,
    val duration: Long,
    val size: Int,
    val uri: Uri,
    val relativePath: String,
    var folder: Folder? = null,
    var artist: Artist? = null
) : Media {

    val mediaItem: MediaItem
    val absolutePath: String = "${PlaybackController.ROOT_PATH}/$relativePath/$name"
    val mediaMetadata: MediaMetadata

    init {
        this.mediaItem = MediaItem.Builder()
            .setUri(this.absolutePath)
            .build()

        this.mediaMetadata = this.mediaItem.mediaMetadata
    }
}