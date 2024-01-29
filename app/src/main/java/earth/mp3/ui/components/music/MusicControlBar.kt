package earth.mp3.ui.components.music

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import earth.mp3.models.ExoPlayerManager

@Composable
fun MusicControlBar(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.HorizontalOrVertical = Arrangement.Center,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically
) {
    val exoPlayerManager = ExoPlayerManager.getInstance(null)

    val isPlaying = rememberSaveable { exoPlayerManager.isPlaying }
    val hasPrevious = rememberSaveable { exoPlayerManager.hasPrevious }
    val hasNext = rememberSaveable { exoPlayerManager.hasNext }

    val spaceBetweenButtons = 20.dp
    val playPauseButtonSize = 80.dp
    val optionButtonSize = 30.dp

    Row(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment
    ) {
        if (hasPrevious.value) {
            PreviousMusicButton()
            Spacer(modifier = Modifier.width(spaceBetweenButtons))
        }
        IconButton(
            modifier = Modifier.size(playPauseButtonSize),
            onClick = { exoPlayerManager.playPause() }
        ) {
            Icon(
                modifier = Modifier.size(playPauseButtonSize),
                imageVector = if (isPlaying.value) Icons.Filled.PauseCircle else Icons.Filled.PlayCircle,
                contentDescription = if (isPlaying.value) "Pause" else "Play",
            )
        }
        if (hasNext.value) {
            Spacer(modifier = Modifier.width(spaceBetweenButtons))
            NextMusicButton()
        }
        Spacer(modifier = Modifier.width(spaceBetweenButtons))
        RepeatMusicButton(Modifier.size(optionButtonSize))
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
@Preview
fun MediaControlBarPreview() {
    MusicControlBar()
}