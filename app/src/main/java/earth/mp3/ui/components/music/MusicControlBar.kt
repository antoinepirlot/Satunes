package earth.mp3.ui.components.music

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import earth.mp3.models.ExoPlayerManager
import earth.mp3.models.Music

@Composable
fun MusicControlBar(
    modifier: Modifier = Modifier,
    musicPlaying: MutableState<Music>,
) {
    val exoPlayerManager = ExoPlayerManager.getInstance(null)

    val isPlaying = rememberSaveable { mutableStateOf(exoPlayerManager.isPlaying()) }
    val hasPrevious = rememberSaveable { mutableStateOf(exoPlayerManager.hasPrevious()) }
    val hasNext = rememberSaveable { mutableStateOf(exoPlayerManager.hasNext()) }

    val spaceBetweenButtons = 20.dp
    val playPauseButtonSize = 80.dp

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (hasPrevious.value) {
            PreviousMusicButton(
                musicPlaying = musicPlaying,
                hasPrevious = hasPrevious,
                hasNext = hasNext
            )
            Spacer(modifier = modifier.width(spaceBetweenButtons))
        }
        IconButton(
            modifier = modifier.size(playPauseButtonSize),
            onClick = { playPause(isPlaying) }
        ) {
            Icon(
                modifier = modifier.size(playPauseButtonSize),
                imageVector = if (isPlaying.value) Icons.Filled.PauseCircle else Icons.Filled.PlayCircle,
                contentDescription = if (isPlaying.value) "Pause" else "Play",
            )
        }
        if (hasNext.value) {
            Spacer(modifier = modifier.width(spaceBetweenButtons))
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