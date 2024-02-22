package earth.mp3.models

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.compose.runtime.MutableLongState
import androidx.compose.runtime.mutableLongStateOf
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import earth.mp3.services.PlaybackController
import java.util.SortedMap

class Music(
    override val id: Long,
    override val name: String,
    val duration: Int,
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