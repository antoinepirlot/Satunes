package earth.mp3.router

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import java.util.SortedMap

@Composable
fun Router(
    modifier: Modifier = Modifier,
    startDestination: String,
    rootFolderMap: SortedMap<Long, Folder>,
    artistMapToShow: SortedMap<Long, Artist>,
    musicMapToShow: SortedMap<Long, Music>,
    folderMap: Map<Long, Folder>,
    mediaItemMap: SortedMap<Music, MediaItem>,
) {
    val navController = rememberNavController()
    val mapToShow: SortedMap<Long, Media> = remember { sortedMapOf() }


    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Destination.FOLDERS.link) {
            // /!\ This route prevent back gesture to exit the app
            MediaListView(
                mediaMap = rootFolderMap as SortedMap<Long, Media>,
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
            mapToShow.clear()

            if (folderId >= Music.FIRST_FOLDER_INDEX && folderId <= folderMap.size) {
                folder = folderMap[folderId]!!
                val mapToAdd = folder.getSubFolderListAsMedia()
                mapToShow.putAll(mapToAdd)
            } else {
                mapToShow.putAll(rootFolderMap)
            }
            if (folder.musicMap.isNotEmpty()) {
                mapToShow.putAll(folder.musicMap)
            }

            MediaListView(
                mediaMap = mapToShow,
                openMedia = { clickedMedia: Media ->
                    openMediaFromFolder(navController, clickedMedia)
                },
                shuffleMusicAction = { /* TODO */ },
                onFABClick = { openCurrentMusic(navController) }
            )
        }

        composable(Destination.ARTISTS.link) {
            MediaListView(
                mediaMap = artistMapToShow as SortedMap<Long, Media>,
                openMedia = { clickedMedia: Media ->
                    openMedia(
                        navController,
                        clickedMedia,
                        musicMapToShow
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
            MediaListView(
                mediaMap = musicMapToShow as SortedMap<Long, Media>,
                openMedia = { clickedMedia: Media ->
                    openMedia(
                        navController,
                        clickedMedia,
                        musicMapToShow
                    )
                },
                shuffleMusicAction = {
                    openMedia(
                        navController,
                        musicMapToShow.values.first(),
                        musicMapToShow,
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
 * @param musicMap the music map to load in exoplayer (if clickedMedia is a music)
 * @param shuffleMode by default false, if true, it starts in shuffle mode
 */
private fun openMedia(
    navController: NavHostController,
    media: Media,
    musicMap: SortedMap<Long, Music>,
    shuffleMode: Boolean = false,
) {
    navController.navigate(getDestinationOf(media))
    if (media is Music) {
        startMusic(musicMap, media, shuffleMode)
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