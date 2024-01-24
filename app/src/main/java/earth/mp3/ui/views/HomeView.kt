package earth.mp3.ui.views

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.media3.common.util.UnstableApi
import earth.mp3.models.Artist
import earth.mp3.models.Folder
import earth.mp3.models.Music
import earth.mp3.models.utils.loadObjectsTo
import earth.mp3.ui.components.cards.CardList
import earth.mp3.ui.components.cards.menu.CardMenuList

/**
 * Show The Home App Bar and content inside
 */
@OptIn(UnstableApi::class)
@Composable
fun HomeView(
    modifier: Modifier,
    musicList: List<Music>,
    folderList: List<Folder>,
    artistList: List<Artist>
) {

    val folderSelected = rememberSaveable { mutableStateOf(true) } // default section
    val artistsSelected = rememberSaveable { mutableStateOf(false) }
    val tracksSelected = rememberSaveable { mutableStateOf(false) }

    val folderListToShow = remember { mutableStateListOf<Folder>() }
    loadObjectsTo(folderListToShow, folderList)
    val musicListToShow = remember { mutableStateListOf<Music>() }
    loadObjectsTo(musicListToShow, musicList)
    val artistListToShow = remember { mutableStateListOf<Artist>() }
    loadObjectsTo(artistListToShow, artistList)
    Column {
        CardMenuList(
            modifier = modifier,
            folderSelected = folderSelected,
            artistsSelected = artistsSelected,
            tracksSelected = tracksSelected,
            resetFoldersToShow = {
                folderListToShow.clear()
                for (folder in folderList) {
                    folderListToShow.add(folder)
                }
            }
        )
        if (folderSelected.value) {
            CardList(
                objectList = folderListToShow,
                imageVector = Icons.Filled.ArrowForward,
                contentDescription = "Arrow Forward",
                onClick = { loadObjectsTo(folderListToShow, it.getSubFolderList()) }
            )
        } else if (artistsSelected.value) {
            // TODO remove hard data
            artistListToShow.add(Artist(1, "Adèle", 5, 4))
            CardList(
                objectList = artistListToShow,
                imageVector = Icons.Filled.AccountCircle,
                onClick = { /*TODO show artist albums list*/ }
            )
        } else if (tracksSelected.value) {
            CardList(
                objectList = musicListToShow,
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = "Play Arrow",
                onClick = { /*TODO play music*/ }
            )
        } else {
            throw IllegalStateException(
                "No tab selected (folder, artists, tracks), that could not happen"
            )
        }
    }
}

@Composable
@Preview
fun HomeViewPreview() {
    HomeView(
        modifier = Modifier.fillMaxSize(),
        musicList = listOf(),
        folderList = listOf(),
        artistList = listOf(Artist(1, "Adèle", 5, 4))
    )
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