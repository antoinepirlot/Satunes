package earth.mp3.ui.components.music

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOn
import androidx.compose.material.icons.filled.RepeatOneOn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.media3.common.Player.REPEAT_MODE_ALL
import androidx.media3.common.Player.REPEAT_MODE_ONE
import earth.mp3.services.ExoPlayerManager

@Composable
fun RepeatMusicButton(
    modifier: Modifier = Modifier
) {
    IconButton(
        modifier = modifier,
        onClick = { ExoPlayerManager.getInstance(null).switchRepeatMode() }
    ) {
        Icon(
            modifier = modifier,
            imageVector = getImageVector(),
            contentDescription = "Repeat"
        )
    }


}

@Composable
@Preview
fun RepeatMusicButtonPreview() {
    RepeatMusicButton()
}

private fun getImageVector(): ImageVector {
    return when (ExoPlayerManager.getInstance(null).repeatMode.value) {
        REPEAT_MODE_ONE -> {
            Icons.Filled.RepeatOneOn
        }

        REPEAT_MODE_ALL -> {
            Icons.Filled.RepeatOn
        }

        else -> {
            Icons.Filled.Repeat
        }
    }
}