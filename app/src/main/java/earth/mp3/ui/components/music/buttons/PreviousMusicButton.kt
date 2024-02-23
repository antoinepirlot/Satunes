package earth.mp3.ui.components.music.buttons

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import earth.mp3.services.PlaybackController

@Composable
fun PreviousMusicButton(
    modifier: Modifier = Modifier
) {
    IconButton(
        modifier = modifier.size(45.dp),
        onClick = { PlaybackController.getInstance().playPrevious() }
    ) {
        Icon(
            modifier = modifier.size(45.dp),
            imageVector = Icons.Filled.SkipPrevious,
            contentDescription = "Skip Previous"
        )
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
@Preview
fun PreviousMusicButtonPreview() {
    PreviousMusicButton()
}