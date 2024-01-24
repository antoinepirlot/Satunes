package earth.mp3.router

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import earth.mp3.models.Artist
import earth.mp3.models.Folder
import earth.mp3.models.Music
import earth.mp3.models.utils.loadObjectsTo
import earth.mp3.ui.appBars.SectionSelection
import earth.mp3.ui.components.cards.CardList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Router(
    modifier: Modifier = Modifier,
    navController: NavHostController,
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

    Column {
        SectionSelection(
            modifier = modifier,
            startDestination = startDestination,
            folderList = folderListToShow
        )

        NavHost(navController = navController, startDestination = startDestination.value) {
            composable(Destination.FOLDERS.link) {
                CardList(
                    objectList = folderListToShow,
                    imageVector = Icons.Filled.ArrowForward,
                    contentDescription = "Arrow Forward",
                    onClick = { loadObjectsTo(folderListToShow, it.getSubFolderList()) }
                )
            }
            composable(Destination.ARTISTS.link) {
                CardList(
                    objectList = artistListToShow,
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = "Account Circle",
                    onClick = { /*TODO show artist albums list*/ }
                )
            }

            composable(Destination.MUSICS.link) {
                CardList(
                    objectList = musicListToShow,
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "Play Arrow",
                    onClick = { /*TODO play music*/ }
                )
            }
        }
    }
}