package earth.mp3.data

import android.net.Uri


data class Music(
    val id: Long,
    val name: String,
    val duration: Int,
    val size: Int,
    val uri: Uri?
)

