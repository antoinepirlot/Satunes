package earth.mp3.router

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import earth.mp3.models.Artist
import earth.mp3.models.ExoPlayerManager
import earth.mp3.models.Folder
import earth.mp3.models.Media
import earth.mp3.models.Music
import earth.mp3.ui.PlayBackView
import earth.mp3.ui.components.cards.MediaCardList
import earth.mp3.ui.utils.startMusic

@Composable
fun Router(
    modifier: Modifier = Modifier,
    startDestination: String,
    rootFolderList: List<Folder>,
    artistListToShow: MutableList<Artist>,
    musicMapToShow: MutableMap<Long, Music>,
    folderMap: Map<Long, Folder>
) {
    val exoPlayerManager = ExoPlayerManager.getInstance(LocalContext.current)

    val listToShow: MutableList<Media> = remember { mutableListOf() }

    val navController = rememberNavController()
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Destination.FOLDERS.link) {
            // /!\ This route prevent back gesture to exit the app
            MediaCardList(
                mediaList = rootFolderList,
                openMedia = { clickedMedia: Media ->
                    openMedia(
                        navController,
                        clickedMedia,
                        musicMapToShow.values.toList()
                    )
                }
            )
        }

        composable("${Destination.FOLDERS.link}/{id}") {
            val folderId = it.arguments!!.getString("id")!!.toLong()
            var folder: Folder = folderMap[Music.FIRST_FOLDER_INDEX]!!
            listToShow.clear()

            if (folderId >= Music.FIRST_FOLDER_INDEX && folderId <= folderMap.size) {
                folder = folderMap[folderId]!!
                val listToAdd = folder.getSubFolderList()
                listToShow.addAll(listToAdd)
            } else {
                listToShow.addAll(rootFolderList)
            }
            if (folder.musicList.isNotEmpty()) {
                listToShow.addAll(folder.musicList.toMutableList())
            }

            MediaCardList(
                mediaList = listToShow,
                openMedia = { clickedMedia: Media ->
                    openMedia(
                        navController,
                        clickedMedia,
                        musicMapToShow.values.toList()
                    )
                }
            )
        }

        composable(Destination.ARTISTS.link) {
            MediaCardList(
                mediaList = artistListToShow,
                openMedia = { clickedMedia: Media ->
                    openMedia(
                        navController,
                        clickedMedia,
                        musicMapToShow.values.toList()
                    )
                }
            )
        }

        composable("${Destination.ARTISTS.link}/{id}") {
            // TODO show artist
        }

        composable(Destination.MUSICS.link) {
            MediaCardList(
                mediaList = musicMapToShow.values.toList(),
                openMedia = { clickedMedia: Media ->
                    openMedia(
                        navController,
                        clickedMedia,
                        musicMapToShow.values.toList()
                    )
                }
            )
        }

        composable("${Destination.PLAYBACK.link}/{mediaId}") {
            //TODO play music
            val music = musicMapToShow[it.arguments!!.getString("mediaId")!!.toLong()]!!
            PlayBackView()
        }
    }
}

/**
 * Open the media, when it is:
 *      Music: navigate to the media's destination and start music with exoplayer
 *
 *      Folder: navigate to the media's destination
 *
 *      Artist: navigate to the media's destination
 *
 * @param navController the nav controller to redirect to the good path
 * @param media the media to open
 * @param musicList the music list to load in exoplayer (if clickedMedia is a music)
 */
private fun openMedia(
    navController: NavHostController,
    media: Media,
    musicList: List<Music>?
) {
    navController.navigate(getDestinationOf(media))
    if (media is Music && musicList != null) {
        startMusic(musicList)
    }
}

/**
 * Return the destination link of media (folder, artists or music) with its id.
 * For example if media is folder, it returns: /folders/5
 *
 * @param media the media to get the destination link
 *
 * @return the media destination link with the media's id
 */
private fun getDestinationOf(media: Media): String {
    return when (media) {
        is Folder -> {
            "${Destination.FOLDERS.link}/${media.id}"
        }

        is Artist -> {
            "${Destination.ARTISTS.link}/${media.id}"
        }

        else -> {
            "${Destination.PLAYBACK.link}/${media.id}"
        }
    }
}