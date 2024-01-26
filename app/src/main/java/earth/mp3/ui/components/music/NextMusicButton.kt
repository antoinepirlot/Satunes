package earth.mp3.ui.components.music

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import earth.mp3.models.MediaPlayerManager

@Composable
fun NextMusicButton(
    modifier: Modifier = Modifier,
    mediaPlayerManager: MediaPlayerManager,
) {
    val hasNextMusic = rememberSaveable { mutableStateOf(true) }
    if (hasNextMusic.value) {
        IconButton(onClick = { nextMusic(mediaPlayerManager, hasNextMusic) }) {
            Icon(imageVector = Icons.Filled.ArrowForward, contentDescription = "ArrowForward")
        }
    }
}

/**
 * Play the next music
 */
private fun nextMusic(mediaPlayerManager: MediaPlayerManager, hasNextMusic: MutableState<Boolean>) {
    mediaPlayerManager.next()
    hasNextMusic.value = mediaPlayerManager.hasNext()
}