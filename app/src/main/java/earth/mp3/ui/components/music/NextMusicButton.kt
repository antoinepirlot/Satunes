package earth.mp3.ui.components.music

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import earth.mp3.models.ExoPlayerManager
import earth.mp3.models.Music

@Composable
fun NextMusicButton(
    modifier: Modifier = Modifier,
    hasNext: MutableState<Boolean>,
    hasPrevious: MutableState<Boolean>,
    musicPlaying: MutableState<Music>,
) {
    IconButton(
        modifier = modifier.size(45.dp),
        onClick = { nextMusic(hasNext, hasPrevious, musicPlaying) }
    ) {
        Icon(
            modifier = modifier.size(45.dp),
            imageVector = Icons.Filled.SkipNext,
            contentDescription = "Skip Next"
        )
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
@Preview
fun NextMusicButtonPreview() {
    NextMusicButton(
        hasNext = mutableStateOf(true),
        hasPrevious = mutableStateOf(true),
        musicPlaying = mutableStateOf(Music(0, "", 0, 0, Uri.EMPTY, ""))
    )
}

/**
 * Play the next music
 */
private fun nextMusic(
    hasNext: MutableState<Boolean>,
    hasPrevious: MutableState<Boolean>,
    musicPlaying: MutableState<Music>
) {
    val exoPlayerManager = ExoPlayerManager.getInstance(null)
    exoPlayerManager.next()
    hasNext.value = exoPlayerManager.hasNext()
    hasPrevious.value = exoPlayerManager.hasPrevious()
    musicPlaying.value = exoPlayerManager.getMusicPlaying()!!
}