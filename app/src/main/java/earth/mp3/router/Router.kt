package earth.mp3.router

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import earth.mp3.models.Artist
import earth.mp3.models.Folder
import earth.mp3.models.Music
import earth.mp3.models.utils.loadObjectsTo
import earth.mp3.ui.components.cards.CardList

@Composable
fun Router(
    modifier: Modifier = Modifier,
    startDestination: String,
    folderListToShow: MutableList<Folder>,
    artistListToShow: MutableList<Artist>,
    musicListToShow: MutableList<Music>,
) {
    val navController = rememberNavController()
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Destination.FOLDERS.link) {
            CardList(
                objectList = folderListToShow,
                imageVector = Icons.Filled.ArrowForward,
                contentDescription = "Arrow Forward",
                onClick = { loadObjectsTo(folderListToShow, it.getSubFolderList()) }
            )
        }

        composable("${Destination.FOLDERS.link}/{id}") {
            val folderId = it.arguments!!.getString("id")!!.toLong()
            Text(text = folderId.toString())
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