package earth.mp3.router

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import earth.mp3.models.Artist
import earth.mp3.models.Folder
import earth.mp3.models.Music
import earth.mp3.ui.views.HomeView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Router(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    musicList: List<Music>,
    folderList: List<Folder>,
    artistList: List<Artist>,
) {
    NavHost(navController = navController, startDestination = Destination.HOME.link) {
        composable(Destination.HOME.link) {
            HomeView(
                modifier = modifier,
                musicList = musicList,
                folderList = folderList,
                artistList = artistList
            )
        }
    }
}