package earth.mp3.ui.views

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import earth.mp3.models.Artist
import earth.mp3.models.Folder
import earth.mp3.models.Music
import earth.mp3.models.utils.loadObjectsTo
import earth.mp3.router.Destination
import earth.mp3.router.Router
import earth.mp3.ui.appBars.SectionSelection

@Composable
fun HomeView(
    modifier: Modifier = Modifier,
    musicList: List<Music>,
    folderList: List<Folder>,
    artistList: List<Artist>,
) {
    val startDestination = rememberSaveable { mutableStateOf(Destination.FOLDERS.link) }

    val folderListToShow = remember { mutableStateListOf<Folder>() }
    loadObjectsTo(folderListToShow, folderList)
    val musicListToShow = remember { mutableStateListOf<Music>() }
    loadObjectsTo(musicListToShow, musicList)
    val artistListToShow = remember { mutableStateListOf<Artist>() }
    loadObjectsTo(artistListToShow, artistList)

    Column(modifier = modifier) {
        SectionSelection(
            startDestination = startDestination,
            folderList = folderListToShow
        )

        Router(
            startDestination = startDestination.value,
            folderListToShow = folderListToShow,
            artistListToShow = artistListToShow,
            musicListToShow = musicListToShow
        )
    }
}

@Composable
@Preview
fun HomeViewPreview() {
    HomeView(musicList = listOf(), folderList = listOf(), artistList = listOf())
}