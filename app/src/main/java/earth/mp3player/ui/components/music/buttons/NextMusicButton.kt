package earth.mp3player.ui.components.music.buttons

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import earth.mp3player.services.PlaybackController

@Composable
fun NextMusicButton(
    modifier: Modifier = Modifier
) {
    IconButton(
        modifier = modifier.size(45.dp),
        onClick = { PlaybackController.getInstance().playNext() }
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
    NextMusicButton()
}