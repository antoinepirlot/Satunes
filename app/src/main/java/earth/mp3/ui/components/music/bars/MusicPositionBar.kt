package earth.mp3.ui.components.music.bars

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import earth.mp3.services.PlaybackController


@Composable
fun MusicPositionBar(
    modifier: Modifier = Modifier
) {
    val playbackController = PlaybackController.getInstance()
    var newPosition by rememberSaveable { mutableFloatStateOf(playbackController.currentPositionProgression.floatValue) }
    val currentPosition by rememberSaveable { playbackController.currentPositionProgression }
    var isUpdating by rememberSaveable { mutableStateOf(false) }

    Column {
        Slider(
            //modifier = modifier.fillMaxWidth(),
            value = if (isUpdating) newPosition else currentPosition,
            onValueChange = {
                isUpdating = true
                newPosition = it
            },
            onValueChangeFinished = {
                playbackController.seekTo(positionPercentage = newPosition)
                isUpdating = false
            },
        )
    }

}

@Composable
@Preview
fun MusicPositionBarPreview() {
    MusicPositionBar()
}