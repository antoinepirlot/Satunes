package earth.mp3player.ui.components.music

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import earth.mp3player.services.PlaybackController

@Composable
fun AlbumArtwork(
    modifier: Modifier = Modifier
) {
    val playbackController: PlaybackController = PlaybackController.getInstance()
}

@Composable
@Preview
fun AlbumArtworkPreview() {

}