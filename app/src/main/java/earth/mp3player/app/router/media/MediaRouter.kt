/*
 *  This file is part of MP3 Player.
 *
 *  MP3 Player is free software: you can redistribute it and/or modify it under
 *  the terms of the GNU General Public License as published by the Free Software Foundation,
 *  either version 3 of the License, or (at your option) any later version.
 *
 *  MP3 Player is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *   without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with MP3 Player.
 *  If not, see <https://www.gnu.org/licenses/>.
 *
 *  ***** INFORMATIONS ABOUT THE AUTHOR *****
 *  The author of this file is Antoine Pirlot, the owner of this project.
 *  You find this original project on github.
 *
 *  My github link is: https://github.com/antoinepirlot
 *  This current project's link is: https://github.com/antoinepirlot/MP3-Player
 *
 *  You can contact me via my email: pirlot.antoine@outlook.com
 *  PS: I don't answer quickly.
 */

package earth.mp3player.app.router.media

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.media3.common.MediaItem
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import earth.mp3player.database.models.tables.Album
import earth.mp3player.database.models.tables.Artist
import earth.mp3player.database.models.tables.Folder
import earth.mp3player.database.models.tables.Genre
import earth.mp3player.database.models.Media
import earth.mp3player.database.models.tables.Music
import earth.mp3player.database.services.DataManager
import earth.mp3player.playback.services.playback.PlaybackController
import earth.mp3player.app.ui.utils.getMusicListFromFolder
import earth.mp3player.app.ui.utils.startMusic
import earth.mp3player.app.ui.views.MediaListView
import earth.mp3player.app.ui.views.PlayBackView
import java.util.SortedMap

/**
 * @author Antoine Pirlot on 23-01-24
 */

@Composable
fun MediaRouter(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: String,
) {
    val playbackController: PlaybackController = remember { PlaybackController.getInstance() }

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(MediaDestination.FOLDERS.link) {
            // /!\ This route prevent back gesture to exit the app
            val rootFolderMap: SortedMap<Long, Folder> = remember { DataManager.rootFolderMap }
            @Suppress("UNCHECKED_CAST")
            (MediaListView(
        mediaMap = rootFolderMap as SortedMap<Long, Media>,

        openMedia = { clickedMedia: Media ->
            openMediaFromFolder(navController, clickedMedia)
        },

        shuffleMusicAction = {
            val musicMediaItemSortedMap: SortedMap<Music, MediaItem> = sortedMapOf()
            @Suppress("NAME_SHADOWING")
            rootFolderMap.forEach { (_, folder: Media) ->
                val folder = folder as Folder
                musicMediaItemSortedMap.putAll(folder.getAllMusic())
            }
            playbackController.loadMusic(
                musicMediaItemSortedMap = musicMediaItemSortedMap,
                shuffleMode = true
            )
            openMedia(navController = navController)
        },

        onFABClick = { openCurrentMusic(navController) }
    ))
        }

        composable("${MediaDestination.FOLDERS.link}/{id}") {
            val folderId = it.arguments!!.getString("id")!!.toLong()
            val folderMap: SortedMap<Long, Folder> = remember { DataManager.folderMap }
            val folder: Folder = folderMap[folderId]!!
            val mapToShow: SortedMap<Long, Media> = sortedMapOf()

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

        composable(MediaDestination.ARTISTS.link) {
            val musicMediaItemMap: SortedMap<Music, MediaItem> =
                remember { DataManager.musicMediaItemSortedMap }
            val artistMap: SortedMap<String, Artist> = remember { DataManager.artistMap }
            @Suppress("UNCHECKED_CAST")
            (MediaListView(
        mediaMap = artistMap as SortedMap<Long, Media>,

        openMedia = { clickedMedia: Media ->
            openMedia(
                navController,
                clickedMedia
            )
        },

        shuffleMusicAction = {
            playbackController.loadMusic(
                musicMediaItemSortedMap = musicMediaItemMap,
                shuffleMode = true
            )
            openMedia(navController = navController)
        },

        onFABClick = { openCurrentMusic(navController) }
    ))
        }

        composable("${MediaDestination.ARTISTS.link}/{name}") {
            val artistMap: SortedMap<String, Artist> = remember { DataManager.artistMap }
            val artistName: String = it.arguments!!.getString("name")!!
            val artist: Artist = artistMap[artistName]!!
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

        composable(MediaDestination.ALBUMS.link) {
            val albumMap: SortedMap<String, Album> = remember { DataManager.albumMap }
            val musicMediaItemSortedMap: SortedMap<Music, MediaItem> = sortedMapOf()

            albumMap.forEach { (_: String, album: Album) ->
                musicMediaItemSortedMap.putAll(album.musicMediaItemSortedMap)
            }

            @Suppress("UNCHECKED_CAST")
            (MediaListView(
        mediaMap = albumMap as SortedMap<Long, Media>,

        openMedia = { clickedMedia: Media ->
            openMedia(navController = navController, media = clickedMedia)
        },

        shuffleMusicAction = {
            playbackController.loadMusic(
                musicMediaItemSortedMap = musicMediaItemSortedMap,
                shuffleMode = true
            )
            openMedia(navController = navController)
        },

        onFABClick = { openCurrentMusic(navController = navController) }
    ))
        }

        composable("${MediaDestination.ALBUMS.link}/{id}") {
            val albumId: Long = it.arguments!!.getString("id")!!.toLong()
            var albumName: String? = null
            val albumMap: SortedMap<String, Album> = remember { DataManager.albumMap }

            albumMap.forEach { (name: String, album: Album) ->
                if (album.id == albumId) {
                    albumName = name
                    return@forEach
                }
            }

            val album: Album = albumMap[albumName]!!

            @Suppress("UNCHECKED_CAST")
            (MediaListView(
        mediaMap = album.musicSortedMap as SortedMap<Long, Media>,

        openMedia = { clickedMedia: Media ->
            playbackController.loadMusic(
                musicMediaItemSortedMap = album.musicMediaItemSortedMap
            )
            openMedia(navController = navController, media = clickedMedia)
        },

        shuffleMusicAction = {
            playbackController.loadMusic(
                musicMediaItemSortedMap = album.musicMediaItemSortedMap,
                shuffleMode = true
            )
            openMedia(navController = navController)
        },

        onFABClick = { openCurrentMusic(navController = navController) }
    ))
        }

        composable(MediaDestination.GENRES.link) {
            val musicMediaItemSortedMap: SortedMap<Music, MediaItem> = sortedMapOf()
            val mediaMap: MutableMap<Long, Media> = mutableMapOf()
            val genreMap: SortedMap<String, Genre> = remember { DataManager.genreMap }

            genreMap.forEach { (_: String, genre: Genre) ->
                musicMediaItemSortedMap.putAll(genre.musicMediaItemSortedMap)
                mediaMap.putIfAbsent(genre.id, genre)
            }

            MediaListView(
                mediaMap = mediaMap,

                openMedia = { clickedMedia: Media ->
                    openMedia(navController = navController, media = clickedMedia)
                },

                shuffleMusicAction = {
                    playbackController.loadMusic(
                        musicMediaItemSortedMap = musicMediaItemSortedMap,
                        shuffleMode = true
                    )
                    openMedia(navController = navController)
                },

                onFABClick = { openCurrentMusic(navController = navController) }
            )
        }

        composable("${MediaDestination.GENRES.link}/{name}") {
            val genreName: String = it.arguments!!.getString("name")!!
            val genreMap: SortedMap<String, Genre> = remember { DataManager.genreMap }
            val genre = genreMap[genreName]!!

            MediaListView(
                mediaMap = genre.musicMap,

                openMedia = { clickedMedia: Media ->
                    playbackController.loadMusic(
                        musicMediaItemSortedMap = genre.musicMediaItemSortedMap
                    )
                    openMedia(navController = navController, media = clickedMedia)
                },

                shuffleMusicAction = {
                    playbackController.loadMusic(
                        musicMediaItemSortedMap = genre.musicMediaItemSortedMap,
                        shuffleMode = true
                    )
                    openMedia(navController = navController)
                },

                onFABClick = { openCurrentMusic(navController = navController) }
            )
        }


        composable(MediaDestination.MUSICS.link) {
            //Find a way to do something more aesthetic but it works
            val mediaMap: SortedMap<Music, Media> = sortedMapOf()
            val musicMediaItemMap: SortedMap<Music, MediaItem> =
                remember { DataManager.musicMediaItemSortedMap }

            musicMediaItemMap.keys.forEach { music: Music ->
                mediaMap[music] = music
            }

            MediaListView(
                mediaMap = mediaMap,

                openMedia = { clickedMedia: Media ->
                    playbackController.loadMusic(
                        musicMediaItemSortedMap = musicMediaItemMap
                    )
                    openMedia(
                        navController,
                        clickedMedia
                    )
                },

                shuffleMusicAction = {
                    playbackController.loadMusic(
                        musicMediaItemSortedMap = musicMediaItemMap,
                        shuffleMode = true
                    )
                    openMedia(navController = navController)
                },

                onFABClick = { openCurrentMusic(navController) }
            )
        }

        composable(MediaDestination.PLAYBACK.link) {
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

        is Folder -> navController.navigate(getDestinationOf(media))
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
        is Folder -> "${MediaDestination.FOLDERS.link}/${media.id}"

        is Artist -> "${MediaDestination.ARTISTS.link}/${media.title}"

        is Album -> "${MediaDestination.ALBUMS.link}/${media.id}"

        is Genre -> "${MediaDestination.GENRES.link}/${media.title}"

        else -> MediaDestination.PLAYBACK.link
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