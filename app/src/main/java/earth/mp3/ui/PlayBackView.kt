package earth.mp3.ui

import android.annotation.SuppressLint
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import earth.mp3.models.Music
import earth.mp3.ui.components.music.MusicControlBar

@Composable
fun PlayBackView(
    modifier: Modifier = Modifier,
    mediaPlayer: MediaPlayer,
    music: Music
) {
    startMusic(music, mediaPlayer)
    Column(
        modifier = modifier
    ) {
        MusicControlBar(mediaPlayer = mediaPlayer)
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
@Preview
fun PlayBackViewPreview() {
    PlayBackView(mediaPlayer = MediaPlayer(), music = Music(0, "", 0, 0, Uri.EMPTY, ""))
}

fun startMusic(music: Music, mediaPlayer: MediaPlayer) {
    val path = "/sdcard/${music.relativePath}/${music.name}"
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