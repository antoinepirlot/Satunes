package earth.mp3player.ui.components.music.bars

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import earth.mp3player.services.PlaybackController
import earth.mp3player.ui.utils.getMillisToTimeText


@Composable
fun MusicPositionBar(
    modifier: Modifier = Modifier
) {
    val playbackController = PlaybackController.getInstance()
    val musicPlaying by remember { playbackController.musicPlaying }
    var newPositionPercentage by rememberSaveable { mutableFloatStateOf(playbackController.currentPositionProgression.floatValue) }
    val currentPositionPercentage by rememberSaveable { playbackController.currentPositionProgression }
    var isUpdating by rememberSaveable { mutableStateOf(false) }

    Column(modifier = modifier) {
        Slider(
            value = if (isUpdating) newPositionPercentage else currentPositionPercentage,
            onValueChange = {
                isUpdating = true
                newPositionPercentage = it
            },
            onValueChangeFinished = {
                playbackController.seekTo(positionPercentage = newPositionPercentage)
                isUpdating = false
            },
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val maxDuration: Long = musicPlaying!!.duration
            Text(
                text = (
                        if (isUpdating) getMillisToTimeText((newPositionPercentage * maxDuration).toLong())
                        else getMillisToTimeText((currentPositionPercentage * maxDuration).toLong())
                        ),
            )
            Text(
                text = (
                        getMillisToTimeText(maxDuration)
                        ),
            )
        }
    }

}

@Composable
@Preview
fun MusicPositionBarPreview() {
    MusicPositionBar()
}