package earth.mp3player.ui.components.music

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import earth.mp3player.R
import earth.mp3player.services.PlaybackController

@Composable
fun AlbumArtwork(
    modifier: Modifier = Modifier
) {
    val playbackController: PlaybackController = PlaybackController.getInstance()
    val musicPlaying by remember { playbackController.musicPlaying }

    if (musicPlaying!!.album == null || musicPlaying!!.album!!.albumArtWorkUri == Uri.EMPTY) {
        Image(
            painter = painterResource(id = R.mipmap.empty_album_artwork_foreground),
            contentDescription = "Default Album Artwork"
        )
    }
}

@Composable
@Preview
fun AlbumArtworkPreview() {

}