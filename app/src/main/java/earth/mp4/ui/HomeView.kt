package earth.mp4.ui

import android.annotation.SuppressLint
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.media3.common.util.UnstableApi
import earth.mp4.data.Music
import earth.mp4.ui.appBars.MP3TopAppBar

@SuppressLint("Range")
@OptIn(UnstableApi::class)
@Composable
fun HomeView(
    modifier: Modifier,
    musicList: List<Music>
) {
    MP3TopAppBar(modifier = modifier)
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