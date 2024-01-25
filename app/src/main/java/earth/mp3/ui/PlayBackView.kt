package earth.mp3.ui

import android.annotation.SuppressLint
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import earth.mp3.models.Music
import earth.mp3.ui.components.music.MusicControlBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun PlayBackView(
    modifier: Modifier = Modifier,
    musicList: List<Music>
) {
    val context = LocalContext.current
    val mediaPlayer = remember { mutableStateOf(MediaPlayer(context)) }

    LaunchedEffect(key1 = musicList) {
        CoroutineScope(Dispatchers.Main).launch {
            startMusic(musicList, mediaPlayer.value)
        }
    }
    Column(
        modifier = modifier
    ) {
        MusicControlBar(mediaPlayer = mediaPlayer.value)
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
@Preview
fun PlayBackViewPreview() {
    PlayBackView(musicList = listOf(Music(0, "", 0, 0, Uri.EMPTY, "")))
}

private fun startMusic(musicList: List<Music>, mediaPlayer: MediaPlayer) {
    val path = "/sdcard/${musicList[0].relativePath}/${musicList[0].name}"
    // if if media player is playing is not checked, recomposition will crash the app
    if (!mediaPlayer.isPlaying) {
        mediaPlayer.apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setDataSource(path)
            prepare()
            start()
        }
    }
}