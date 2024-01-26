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
    mediaPlayerManager: MediaPlayerManager
) {
    val isPlaying = rememberSaveable { mutableStateOf(mediaPlayerManager.isPlaying()) }
    Row(
        modifier = modifier.fillMaxWidth(),
    ) {
        IconButton(onClick = { /*TODO*/ }) {
            // TODO previous music
        }
        IconButton(
            modifier = modifier,
            onClick = { playPause(mediaPlayerManager, isPlaying) }
        ) {
            // TODO I used the inverse otherwise when is playing it is false
            //  I think it's a issue with coroutine or something like this
            if (!isPlaying.value) {
                Icon(imageVector = Icons.Filled.Close, contentDescription = "Close")
            } else {
                Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = "Play Icon")
            }
        }

        NextMusicButton(mediaPlayerManager = mediaPlayerManager)
    }
}

@Composable
@Preview
fun MediaControlBarPreview() {
    MusicControlBar(mediaPlayerManager = MediaPlayerManager)
}

/**
 * Play or pause the music using media player manager and update the state of isPlaying
 *
 * @param mediaPlayerManager the media player manager
 * @param isPlaying the boolean that indicates if the music is playing
 */
private fun playPause(mediaPlayerManager: MediaPlayerManager, isPlaying: MutableState<Boolean>) {
    mediaPlayerManager.playPause()
    isPlaying.value = mediaPlayerManager.isPlaying()
}