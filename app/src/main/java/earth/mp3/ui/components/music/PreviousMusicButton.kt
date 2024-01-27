package earth.mp3.ui.components.music

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import earth.mp3.models.ExoPlayerManager
import earth.mp3.models.Music

@Composable
fun PreviousMusicButton(
    modifier: Modifier = Modifier,
    exoPlayerManager: ExoPlayerManager,
    hasPrevious: MutableState<Boolean>,
    hasNext: MutableState<Boolean>,
    musicPlaying: MutableState<Music>,
) {
    IconButton(onClick = { previousMusic(exoPlayerManager, hasPrevious, hasNext, musicPlaying) }) {
        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Arrow Back")
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
@Preview
fun PreviousMusicButtonPreview() {
    PreviousMusicButton(
        exoPlayerManager = ExoPlayerManager(LocalContext.current),
        hasPrevious = mutableStateOf(true),
        hasNext = mutableStateOf(true),
        musicPlaying = mutableStateOf(Music(0, "", 0, 0, Uri.EMPTY, "")),
    )
}

private fun previousMusic(
    exoPlayerManager: ExoPlayerManager,
    hasPrevious: MutableState<Boolean>,
    hasNext: MutableState<Boolean>,
    musicPlaying: MutableState<Music>
) {
    exoPlayerManager.previous()
    hasPrevious.value = exoPlayerManager.hasPrevious()
    hasNext.value = exoPlayerManager.hasNext()
    musicPlaying.value = exoPlayerManager.getMusicPlaying()!!
}