package earth.mp3.ui.components.music

import android.media.MediaPlayer
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun MusicControlBar(
    modifier: Modifier = Modifier,
    mediaPlayer: MediaPlayer
) {
    Row(modifier = modifier) {
        val checked = rememberSaveable { mutableStateOf(true) }
        Switch(checked = checked.value, onCheckedChange = {
            checked.value = it
            playPause(mediaPlayer)
        })
//        IconButton(onClick = { playPause(mediaPlayer) }) {
//
////            if (mediaPlayer.isPlaying) {
////                Icon(imageVector = Icons.Filled.Close, contentDescription = "Close")
////            } else {
////                Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = "Play Icon")
////            }
//        }
    }
}

@Composable
@Preview
fun MediaControlBarPreview() {
    MusicControlBar(mediaPlayer = MediaPlayer())
}

fun playPause(mediaPlayer: MediaPlayer) {
    if (mediaPlayer.isPlaying) {
        mediaPlayer.pause()
    } else {
        mediaPlayer.start()
    }
}