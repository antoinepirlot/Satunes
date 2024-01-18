package earth.mp4.ui.home

import android.annotation.SuppressLint
import androidx.annotation.OptIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.MediaItem
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

}

//if (musicList.isEmpty()) {
//    Text(text = "The music list is empty, please add music to your phone and restart")
//} else {
//    val player = ExoPlayer.Builder(LocalContext.current).build()
//    val playerControlView = PlayerControlView(LocalContext.current)
//    playerControlView.player = player
//
//    val mediaItem = MediaItem.fromUri(musicList[0].uri)
//    player.setMediaItem(mediaItem)
//    player.prepare()
//    player.play()
//    Text(text = musicList[0].name)
//}