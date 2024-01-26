package earth.mp3.ui.components.music

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import earth.mp3.models.MediaPlayerManager

@Composable
fun MusicControlBar(
    modifier: Modifier = Modifier,
    mediaPlayerManager: MediaPlayerManager
) {
    Row(modifier = modifier) {
        val checked = rememberSaveable { mutableStateOf(true) }
        Switch(checked = checked.value, onCheckedChange = {
            mediaPlayerManager.playPause()
            checked.value = mediaPlayerManager.isPlaying()
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
    MusicControlBar(mediaPlayerManager = MediaPlayerManager(LocalContext.current))
}