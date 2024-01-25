package earth.mp3.ui

import android.annotation.SuppressLint
import android.media.MediaPlayer
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun PlayBackView(
    modifier: Modifier = Modifier,
    mediaPlayer: MediaPlayer
) {
    Column(
        modifier = modifier
    ) {
        TextButton(onClick = { mediaPlayer.start() }) {
            Text(text = "Click here to play")
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
@Preview
fun PlayBackViewPreview() {
    PlayBackView(mediaPlayer = MediaPlayer())
}