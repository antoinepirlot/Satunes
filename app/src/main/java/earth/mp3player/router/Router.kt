package earth.mp3player.router

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.media3.common.MediaItem
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import earth.mp3player.models.Artist
import earth.mp3player.models.Folder
import earth.mp3player.models.Media
import earth.mp3player.models.Music
import earth.mp3player.services.PlaybackController
import earth.mp3player.ui.utils.getMusicListFromFolder
import earth.mp3player.ui.utils.startMusic
import earth.mp3player.ui.views.MediaListView
import earth.mp3player.ui.views.PlayBackView
import java.util.SortedMap

@Composable
fun Router(
    modifier: Modifier = Modifier,
    startDestination: String,
    rootFolderMap: SortedMap<Long, Folder>,
    allArtistSortedMap: SortedMap<String, Artist>,
    allMusicMediaItemsMap: SortedMap<Music, MediaItem>,
    folderMap: Map<Long, Folder>,
) {
    val navController = rememberNavController()
    val mapToShow: SortedMap<Long, Media> = remember { sortedMapOf() }
    val playbackController: PlaybackController = PlaybackController.getInstance()


    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Destination.FOLDERS.link) {
            // /!\ This route prevent back gesture to exit the app
            @Suppress("UNCHECKED_CAST")
            MediaListView(
                mediaMap = rootFolderMap as SortedMap<Long, Media>,
                openMedia = { clickedMedia: Media ->
                    openMediaFromFolder(navController, clickedMedia)
                },
                shuffleMusicAction = {
                    val musicMediaItemSortedMap: SortedMap<Music, MediaItem> = sortedMapOf()
                    rootFolderMap.forEach { _, folder: Folder ->
                        musicMediaItemSortedMap.putAll(folder.getAllMusic())
                    }
                    playbackController.loadMusic(
                        musicMediaItemSortedMap = musicMediaItemSortedMap,
                        shuffleMode = true
                    )
                    openMedia(navController = navController)
                },
                onFABClick = { openCurrentMusic(navController) }
            )
        }

        composable("${Destination.FOLDERS.link}/{id}") {
            val folderId = it.arguments!!.getString("id")!!.toLong()
            val folder: Folder = folderMap[folderId]!!
            mapToShow.clear()
            //Load sub-folders
            mapToShow.putAll(folder.getSubFolderListAsMedia())

            //Load sub-folder's musics
            val folderMusicMediaItemSortedMap: SortedMap<Music, MediaItem> =
                folder.musicMediaItemSortedMap
            folderMusicMediaItemSortedMap.forEach { (music: Music, _) ->
                mapToShow[music.id] = music
            }

            MediaListView(
                mediaMap = mapToShow,
                openMedia = { clickedMedia: Media ->
                    openMediaFromFolder(navController, clickedMedia)
                },
                shuffleMusicAction = {
                    playbackController.loadMusic(
                        musicMediaItemSortedMap = folder.getAllMusic(),
                        shuffleMode = true
                    )
                    openMedia(navController = navController)
                },
                onFABClick = { openCurrentMusic(navController) }
            )
        }

        composable(Destination.ARTISTS.link) {
            @Suppress("UNCHECKED_CAST")
            MediaListView(
                mediaMap = allArtistSortedMap as SortedMap<Long, Media>,
                openMedia = { clickedMedia: Media ->
                    openMedia(
                        navController,
                        clickedMedia
                    )
                },
                shuffleMusicAction = {
                    playbackController.loadMusic(
                        musicMediaItemSortedMap = allMusicMediaItemsMap,
                        shuffleMode = true
                    )
                    openMedia(navController = navController)
                },
                onFABClick = { openCurrentMusic(navController) }
            )
        }

        composable("${Destination.ARTISTS.link}/{name}") {
            val artistName: String = it.arguments!!.getString("name")!!
            val artist: Artist = allArtistSortedMap[artistName]!!
            val musicMap: SortedMap<Long, Media> = sortedMapOf()
            artist.musicList.forEach { music: Music ->
                musicMap[music.id] = music
            }
            MediaListView(
                mediaMap = musicMap,
                openMedia = { clickedMedia: Media ->
                    openMediaFromFolder(navController, clickedMedia)
                },
                shuffleMusicAction = {
                    val musicMediaItemMap: SortedMap<Music, MediaItem> = sortedMapOf()
                    artist.musicList.forEach { music: Music ->
                        musicMediaItemMap[music] = music.mediaItem
                    }
                    playbackController.loadMusic(
                        musicMediaItemSortedMap = musicMediaItemMap,
                        shuffleMode = true
                    )
                    openMedia(navController = navController)
                },
                onFABClick = { openCurrentMusic(navController) }
            )
        }

        composable(Destination.MUSICS.link) {
            val mediaMap: SortedMap<Long, Media> = sortedMapOf()
            allMusicMediaItemsMap.keys.forEach { music: Music ->
                mediaMap[music.id] = music
            }
            MediaListView(
                mediaMap = mediaMap,
                openMedia = { clickedMedia: Media ->
                    playbackController.loadMusic(musicMediaItemSortedMap = allMusicMediaItemsMap)
                    openMedia(
                        navController,
                        clickedMedia
                    )
                },
                shuffleMusicAction = {
                    playbackController.loadMusic(
                        musicMediaItemSortedMap = allMusicMediaItemsMap,
                        shuffleMode = true
                    )
                    openMedia(navController = navController)
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
 * @param navController the nav controller to redirect to the good path
 * @param media the media to open
 */
private fun openMedia(
    navController: NavHostController,
    media: Media? = null
) {
    if (media == null || media is Music) {
        startMusic(media)
    }
    navController.navigate(getDestinationOf(media))
}


private fun openMediaFromFolder(
    navController: NavHostController,
    media: Media
) {
    when (media) {
        is Music -> {
            val playbackController = PlaybackController.getInstance()
            playbackController.loadMusic(musicMediaItemSortedMap = getMusicListFromFolder(media.folder!!))
            openMedia(navController, media)
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
fun getDestinationOf(media: Media?): String {
    return when (media) {
        is Folder -> {
            "${Destination.FOLDERS.link}/${media.id}"
        }

        is Artist -> {
            "${Destination.ARTISTS.link}/${media.name}"
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
    val playbackController: PlaybackController = PlaybackController.getInstance()
    val musicPlaying = playbackController.musicPlaying.value
        ?: throw IllegalStateException("No music is currently playing, this button can be accessible")

    navController.navigate(getDestinationOf(musicPlaying))
}