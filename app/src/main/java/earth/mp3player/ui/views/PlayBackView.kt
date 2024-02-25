package earth.mp3player.ui.views

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import earth.mp3player.services.PlaybackController
import earth.mp3player.ui.components.music.bars.MusicControlBar

@Composable
fun PlayBackView(
    modifier: Modifier = Modifier,
) {
    val musicPlaying = remember { PlaybackController.getInstance().musicPlaying }
    Column(
        modifier = modifier
    ) {
        Text(text = musicPlaying.value!!.name)
        MusicControlBar(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
        )
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
@Preview
fun PlayBackViewPreview() {
    PlayBackView()
}