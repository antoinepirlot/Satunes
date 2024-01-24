package earth.mp3.router

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import earth.mp3.models.Artist
import earth.mp3.models.Folder
import earth.mp3.models.Media
import earth.mp3.models.Music
import earth.mp3.ui.components.cards.MediaCardList

@Composable
fun Router(
    modifier: Modifier = Modifier,
    startDestination: String,
    rootFolderList: List<Folder>,
    artistListToShow: MutableList<Artist>,
    musicListToShow: MutableList<Music>,
    folderMap: Map<Long, Folder>
) {
    val navController = rememberNavController()
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Destination.FOLDERS.link) {
            // /!\ This line prevent back gesture to exit the app
            MediaCardList(
                mediaList = rootFolderList,
                imageVector = Icons.Filled.ArrowForward,
                contentDescription = "Arrow Forward",
                onClick = { clickedFolder: Media ->
                    navController.navigate("${Destination.FOLDERS.link}/${clickedFolder.id}")
                }
            )
        }

        composable("${Destination.FOLDERS.link}/{id}") {
            val folderId = it.arguments!!.getString("id")!!.toLong()
            var folder: Folder = folderMap[Music.FIRST_FOLDER_INDEX]!!
            var folderListToShow: List<Folder> = rootFolderList
            if (folderId >= Music.FIRST_FOLDER_INDEX && folderId <= folderMap.size) {
                folder = folderMap[folderId]!!
                folderListToShow = folder.subFolderList
            }
            MediaCardList(
                mediaList = folderListToShow,
                parentMedia = folder,
                imageVector = Icons.Filled.ArrowForward,
                contentDescription = "Arrow Forward",
                onClick = { clickedFolder: Media ->
                    navController.navigate("${Destination.FOLDERS.link}/${clickedFolder.id}")
                }
            )
        }

        composable(Destination.ARTISTS.link) {
            MediaCardList(
                mediaList = artistListToShow,
                imageVector = Icons.Filled.AccountCircle,
                contentDescription = "Account Circle",
                onClick = { /*TODO show artist albums list*/ }
            )
        }

        composable(Destination.MUSICS.link) {
            MediaCardList(
                mediaList = musicListToShow,
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = "Play Arrow",
                onClick = { /*TODO play music*/ }
            )
        }
    }
}