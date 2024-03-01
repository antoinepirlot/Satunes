package earth.mp3player.ui.components.music

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
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
    val musicPlaying by remember { PlaybackController.getInstance().musicPlaying }

    if (musicPlaying!!.album == null || musicPlaying!!.album!!.albumArtWorkUri == Uri.EMPTY) {
        Box(
            modifier = modifier,
        ) {
            Image(
                modifier = modifier,
                painter = painterResource(id = R.mipmap.empty_album_artwork_foreground),
                contentDescription = "Default Album Artwork"
            )
        }
    }
}

@Composable
@Preview
fun AlbumArtworkPreview() {

}