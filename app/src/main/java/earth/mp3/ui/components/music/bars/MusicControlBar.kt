package earth.mp3.ui.components.music.bars

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
import earth.mp3.services.PlaybackController
import earth.mp3.ui.components.music.buttons.NextMusicButton
import earth.mp3.ui.components.music.buttons.PreviousMusicButton
import earth.mp3.ui.components.music.buttons.RepeatMusicButton
import earth.mp3.ui.components.music.buttons.ShuffleMusicButton

@Composable
fun MusicControlBar(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.HorizontalOrVertical = Arrangement.Center,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically
) {
    val playbackController = PlaybackController.getInstance()

    val isPlaying = rememberSaveable { playbackController.isPlaying }

    val spaceBetweenButtons = 20.dp
    val playPauseButtonSize = 80.dp
    val optionButtonSize = 30.dp

    Row(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment
    ) {
        ShuffleMusicButton(modifier = Modifier.size(optionButtonSize))
        Spacer(modifier = Modifier.width(spaceBetweenButtons))

        PreviousMusicButton()
        Spacer(modifier = Modifier.width(spaceBetweenButtons))

        IconButton(
            modifier = Modifier.size(playPauseButtonSize),
            onClick = { playbackController.playPause() }
        ) {
            Icon(
                modifier = Modifier.size(playPauseButtonSize),
                imageVector = if (isPlaying.value) Icons.Filled.PauseCircle else Icons.Filled.PlayCircle,
                contentDescription = if (isPlaying.value) "Pause" else "Play",
            )
        }

        Spacer(modifier = Modifier.width(spaceBetweenButtons))
        NextMusicButton()

        Spacer(modifier = Modifier.width(spaceBetweenButtons))
        RepeatMusicButton(modifier = Modifier.size(optionButtonSize))
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
@Preview
fun MediaControlBarPreview() {
    MusicControlBar()
}