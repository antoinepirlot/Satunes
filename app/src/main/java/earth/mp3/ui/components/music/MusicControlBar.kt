package earth.mp3.ui.components.music

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import earth.mp3.models.MediaPlayerManager

@Composable
fun MusicControlBar(
    modifier: Modifier = Modifier,
) {
    val isPlaying = rememberSaveable { mutableStateOf(MediaPlayerManager.isPlaying()) }
    val hasPrevious = rememberSaveable { mutableStateOf(MediaPlayerManager.hasPrevious()) }
    val hasNext = rememberSaveable { mutableStateOf(MediaPlayerManager.hasNext()) }

    Row(
        modifier = modifier.fillMaxWidth(),
    ) {
        if (hasPrevious.value) {
            IconButton(onClick = { /*TODO*/ }) {
                // TODO previous music
            }
        }
        IconButton(
            modifier = modifier,
            onClick = { playPause(isPlaying) }
        ) {
            if (isPlaying.value) {
                Icon(imageVector = Icons.Filled.Close, contentDescription = "Close")
            } else {
                Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = "Play Icon")
            }
        }
        if (hasNext.value) {
            NextMusicButton(
                hasNext = hasNext,
                hasPrevious = hasPrevious
            )
        }
    }
}

@Composable
@Preview
fun MediaControlBarPreview() {
    MusicControlBar()
}

/**
 * Play or pause the music using media player manager and update the state of isPlaying
 *
 * @param isPlaying the boolean that indicates if the music is playing
 */
private fun playPause(isPlaying: MutableState<Boolean>) {
    MediaPlayerManager.playPause()
    isPlaying.value = MediaPlayerManager.isPlaying()
}