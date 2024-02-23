package earth.mp3.ui.components.music.bars

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
import earth.mp3.services.PlaybackController
import earth.mp3.ui.utils.getMillisToTimeText


@Composable
fun MusicPositionBar(
    modifier: Modifier = Modifier
) {
    val playbackController = PlaybackController.getInstance()
    val musicPlaying by remember { playbackController.musicPlaying }
    var newPosition by rememberSaveable { mutableFloatStateOf(playbackController.currentPositionProgression.floatValue) }
    val currentPosition by rememberSaveable { playbackController.currentPositionProgression }
    var isUpdating by rememberSaveable { mutableStateOf(false) }

    Column(modifier = modifier) {
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val maxDuration: Long = musicPlaying!!.duration
            Text(
                text = (
                        if (isUpdating) getMillisToTimeText((newPosition * maxDuration).toLong())
                        else getMillisToTimeText((currentPosition * maxDuration).toLong())
                        ),
            )
            Text(
                text = (
                        getMillisToTimeText(maxDuration)
                        ),
            )
//            if (isUpdating) {
//                Text(text = getMillisToTimeText(milliseconds = currentPosition.toLong() * maxDuration))
//            } else {
//                Text(text = getMillisToTimeText(milliseconds = currentPosition.toLong() * maxDuration))
//            }
//            Text(
//                text = getMillisToTimeText(milliseconds = musicPlaying!!.duration),
//                textAlign = TextAlign.Right
//            )
        }
    }

}

@Composable
@Preview
fun MusicPositionBarPreview() {
    MusicPositionBar()
}