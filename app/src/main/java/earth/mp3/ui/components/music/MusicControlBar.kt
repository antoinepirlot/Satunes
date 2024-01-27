package earth.mp3.ui.components.music

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import earth.mp3.models.ExoPlayerManager
import earth.mp3.models.Music

@Composable
fun MusicControlBar(
    modifier: Modifier = Modifier,
    exoPlayerManager: ExoPlayerManager,
    musicPlaying: MutableState<Music>,
) {
    val isPlaying = rememberSaveable { mutableStateOf(exoPlayerManager.isPlaying()) }
    val hasPrevious = rememberSaveable { mutableStateOf(exoPlayerManager.hasPrevious()) }
    val hasNext = rememberSaveable { mutableStateOf(exoPlayerManager.hasNext()) }

    Row(
        modifier = modifier,
    ) {
        if (hasPrevious.value) {
            PreviousMusicButton(
                musicPlaying = musicPlaying,
                hasPrevious = hasPrevious,
                hasNext = hasNext
            )
        }
        IconButton(
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
                musicPlaying = musicPlaying,
                hasNext = hasNext,
                hasPrevious = hasPrevious
            )
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
@Preview
fun MediaControlBarPreview() {
    MusicControlBar(
        exoPlayerManager = ExoPlayerManager.getInstance(LocalContext.current),
        musicPlaying = mutableStateOf(Music(0, "", 0, 0, Uri.EMPTY, ""))
    )
}

/**
 * Play or pause the music using media player manager and update the state of isPlaying
 *
 * @param isPlaying the boolean that indicates if the music is playing
 */
private fun playPause(isPlaying: MutableState<Boolean>) {
    val exoPlayerManager = ExoPlayerManager.getInstance(null)
    exoPlayerManager.playPause()
    isPlaying.value = exoPlayerManager.isPlaying()
}