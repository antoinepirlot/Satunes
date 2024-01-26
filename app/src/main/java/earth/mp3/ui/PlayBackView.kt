package earth.mp3.ui

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import earth.mp3.models.MediaPlayerManager
import earth.mp3.models.Music
import earth.mp3.ui.components.music.MusicControlBar

@Composable
fun PlayBackView(
    modifier: Modifier = Modifier,
    musicList: List<Music>,
) {
    MediaPlayerManager.startMusic()
    val musicPlaying = remember { mutableStateOf(MediaPlayerManager.getMusicPlaying()!!) }
    Column(
        modifier = modifier
    ) {
        Text(text = musicPlaying.value.name)
        MusicControlBar(musicPlaying = musicPlaying)
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
@Preview
fun PlayBackViewPreview() {
    PlayBackView(
        musicList = listOf(Music(0, "", 0, 0, Uri.EMPTY, ""))
    )
}