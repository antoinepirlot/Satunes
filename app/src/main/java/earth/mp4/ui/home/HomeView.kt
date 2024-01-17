package earth.mp4.ui.home

import android.annotation.SuppressLint
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerControlView
import earth.mp4.data.Music

@SuppressLint("Range")
@OptIn(UnstableApi::class)
@Composable
fun HomeView(
    modifier: Modifier,
    musicList: List<Music>
) {
        val player = ExoPlayer.Builder(LocalContext.current).build()
        val playerControlView = PlayerControlView(LocalContext.current)
        playerControlView.player = player

}