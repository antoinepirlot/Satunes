package earth.mp3.ui.components.music

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.ShuffleOn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import earth.mp3.services.ExoPlayerManager

@Composable
fun ShuffleMusicButton(
    modifier: Modifier = Modifier
) {
    val exoPlayerManager: ExoPlayerManager = ExoPlayerManager.getInstance(null)

    IconButton(
        modifier = modifier,
        onClick = { exoPlayerManager.switchShuffleMode() }
    ) {
        Icon(
            modifier = modifier,
            imageVector = getImageVector(),
            contentDescription = "Shuffle mode"
        )
    }
}

@Composable
@Preview
fun ShuffleMusicButtonPreview() {
    ShuffleMusicButton()
}

private fun getImageVector(): ImageVector {
    return if (ExoPlayerManager.getInstance(null).shuffleMode.value) {
        Icons.Filled.ShuffleOn
    } else {
        Icons.Filled.Shuffle
    }
}