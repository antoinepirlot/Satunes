package earth.mp3.ui.components.music.bars

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import earth.mp3.services.PlaybackController


@Composable
fun MusicPositionBar(
    modifier: Modifier = Modifier
) {
    val playbackController = PlaybackController.getInstance()
    val progression by rememberSaveable { playbackController.currentPositionProgression }

    Column {
        LinearProgressIndicator(
            progress = progression,
            modifier = modifier.fillMaxWidth()
        )
    }

}

@Composable
@Preview
fun MusicPositionBarPreview() {
    MusicPositionBar()
}