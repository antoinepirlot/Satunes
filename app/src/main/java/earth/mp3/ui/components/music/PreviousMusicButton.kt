package earth.mp3.ui.components.music

import android.annotation.SuppressLint
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import earth.mp3.models.MediaPlayerManager

@Composable
fun PreviousMusicButton(
    modifier: Modifier = Modifier,
    hasPrevious: MutableState<Boolean>,
    hasNext: MutableState<Boolean>,
) {
    IconButton(onClick = { previousMusic(hasPrevious, hasNext) }) {
        Icon(imageVector = Icons.Filled.ArrowForward, contentDescription = "ArrowForward")
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
@Preview
fun PreviousMusicButtonPreview() {
    PreviousMusicButton(
        hasNext = mutableStateOf(true),
        hasPrevious = mutableStateOf(true)
    )
}

private fun previousMusic(hasPrevious: MutableState<Boolean>, hasNext: MutableState<Boolean>) {
    MediaPlayerManager.previous()
    hasPrevious.value = MediaPlayerManager.hasPrevious()
    hasNext.value = MediaPlayerManager.hasNext()
}