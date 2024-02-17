package earth.mp3.router

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.media3.common.MediaItem
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import earth.mp3.models.Artist
import earth.mp3.models.Folder
import earth.mp3.models.Media
import earth.mp3.models.Music
import earth.mp3.services.PlaybackController
import earth.mp3.ui.utils.getMusicListFromFolder
import earth.mp3.ui.utils.startMusic
import earth.mp3.ui.views.MediaListView
import earth.mp3.ui.views.PlayBackView

@Composable
fun Router(
    modifier: Modifier = Modifier,
    startDestination: String,
    rootFolderList: List<Folder>,
    artistListToShow: MutableList<Artist>,
    musicMapToShow: MutableMap<Long, Music>,
    folderMap: Map<Long, Folder>,
    mediaItemList: SnapshotStateList<MediaItem>,
) {
    val navController = rememberNavController()
    val listToShow: MutableList<Media> = remember { mutableListOf() }


    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Destination.FOLDERS.link) {
            // /!\ This route prevent back gesture to exit the app
            MediaListView(
                mediaList = rootFolderList,
                openMedia = { clickedMedia: Media ->
                    openMediaFromFolder(navController, clickedMedia)
                },
                shuffleMusicAction = { /* TODO */ },
                onFABClick = { openCurrentMusic(navController) }
            )
        }

        composable("${Destination.FOLDERS.link}/{id}") {
            val folderId = it.arguments!!.getString("id")!!.toLong()
            var folder: Folder = folderMap[Music.FIRST_FOLDER_INDEX]!!
            listToShow.clear()

            if (folderId >= Music.FIRST_FOLDER_INDEX && folderId <= folderMap.size) {
                folder = folderMap[folderId]!!
                val listToAdd = folder.getSubFolderListAsMedia()
                listToShow.addAll(listToAdd)
            } else {
                listToShow.addAll(rootFolderList)
            }
            if (folder.musicList.isNotEmpty()) {
                listToShow.addAll(folder.musicList.toMutableList())
            }

            MediaListView(
                mediaList = listToShow,
                openMedia = { clickedMedia: Media ->
                    openMediaFromFolder(navController, clickedMedia)
                },
                shuffleMusicAction = { /* TODO */ },
                onFABClick = { openCurrentMusic(navController) }
            )
        }

        composable(Destination.ARTISTS.link) {
            MediaListView(
                mediaList = artistListToShow,
                openMedia = { clickedMedia: Media ->
                    openMedia(
                        navController,
                        clickedMedia,
                        musicMapToShow.values.toList()
                    )
                },
                shuffleMusicAction = { /* TODO */ },
                onFABClick = { openCurrentMusic(navController) }
            )
        }

        composable("${Destination.ARTISTS.link}/{id}") {
            // TODO show artist
        }

        composable(Destination.MUSICS.link) {
            val musicList = musicMapToShow.values.toList()
            MediaListView(
                mediaList = musicMapToShow.values.toList(),
                openMedia = { clickedMedia: Media ->
                    openMedia(
                        navController,
                        clickedMedia,
                        musicList
                    )
                },
                shuffleMusicAction = {
                    openMedia(
                        navController,
                        musicList[0],
                        musicList,
                        shuffleMode = true
                    )
                },
                onFABClick = { openCurrentMusic(navController) }
            )
        }

        composable(Destination.PLAYBACK.link) {
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
 * If the shuffle mode param is true then shuffle the music and start the selected media
 *
 * @param navController the nav controller to redirect to the good path
 * @param media the media to open
 * @param musicList the music list to load in exoplayer (if clickedMedia is a music)
 * @param shuffleMode by default false, if true, it starts in shuffle mode
 */
private fun openMedia(
    navController: NavHostController,
    media: Media,
    musicList: List<Music>,
    shuffleMode: Boolean = false,
) {
    navController.navigate(getDestinationOf(media))
    if (media is Music) {
        startMusic(musicList, media, shuffleMode)
    }
}

private fun openMediaFromFolder(
    navController: NavHostController,
    media: Media
) {
    when (media) {
        is Music -> {
            openMedia(navController, media, getMusicListFromFolder(media.folder!!))
        }

        is Folder -> {
            navController.navigate(getDestinationOf(media))
        }
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
fun getDestinationOf(media: Media): String {
    return when (media) {
        is Folder -> {
            "${Destination.FOLDERS.link}/${media.id}"
        }

        is Artist -> {
            "${Destination.ARTISTS.link}/${media.id}"
        }

        else -> {
            Destination.PLAYBACK.link
        }
    }
}

/**
 * Open the current playing music
 *
 * @throws IllegalStateException if there's no music playing
 */
fun openCurrentMusic(navController: NavHostController) {
    val musicPlaying = PlaybackController.musicPlaying.value
        ?: throw IllegalStateException("No music is currently playing, this button can be accessible")

    navController.navigate(getDestinationOf(musicPlaying))
}