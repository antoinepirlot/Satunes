package earth.mp3.router

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import earth.mp3.models.Artist
import earth.mp3.models.Folder
import earth.mp3.models.Music
import earth.mp3.ui.views.HomeView

@Composable
fun Router(
    modifier: Modifier = Modifier,
    musicList: List<Music>,
    folderList: List<Folder>,
    artistList: List<Artist>,
) {
    HomeView(
        modifier = modifier,
        musicList = musicList,
        folderList = folderList,
        artistList = artistList
    )
}