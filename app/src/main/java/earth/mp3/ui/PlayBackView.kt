package earth.mp3.ui

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
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
    Column(
        modifier = modifier
    ) {
        MusicControlBar()
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