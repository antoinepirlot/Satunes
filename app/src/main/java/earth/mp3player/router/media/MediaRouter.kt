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

package earth.mp3player.router.media

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.media3.common.MediaItem
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import earth.mp3player.database.models.Album
import earth.mp3player.database.models.Artist
import earth.mp3player.database.models.Folder
import earth.mp3player.database.models.Genre
import earth.mp3player.database.models.Media
import earth.mp3player.database.models.Music
import earth.mp3player.database.models.relations.PlaylistWithMusics
import earth.mp3player.database.services.DataManager
import earth.mp3player.playback.services.playback.PlaybackController
import earth.mp3player.services.PlaylistSelectionManager
import earth.mp3player.ui.utils.getMusicListFromFolder
import earth.mp3player.ui.utils.startMusic
import earth.mp3player.ui.views.PlayBackView
import earth.mp3player.ui.views.main.AlbumView
import earth.mp3player.ui.views.main.AllAlbumsListView
import earth.mp3player.ui.views.main.AllArtistsListView
import earth.mp3player.ui.views.main.AllGenresListView
import earth.mp3player.ui.views.main.AllMusicsListView
import earth.mp3player.ui.views.main.ArtistView
import earth.mp3player.ui.views.main.GenreView
import earth.mp3player.ui.views.main.MediaListView
import earth.mp3player.ui.views.main.PlaylistListView
import earth.mp3player.ui.views.main.PlaylistView
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
            resetOpenedPlaylist()
            MediaListView(
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
            )
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

            resetOpenedPlaylist()
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
            AllArtistsListView(navController = navController)
        }

        composable("${MediaDestination.ARTISTS.link}/{name}") {
            val artistName: String = it.arguments!!.getString("name")!!
            val artist: Artist = DataManager.getArtist(artistName)
            ArtistView(navController = navController, artist = artist)
        }

        composable(MediaDestination.ALBUMS.link) {
            AllAlbumsListView(navController = navController)
        }

        composable("${MediaDestination.ALBUMS.link}/{id}") {
            val albumId: Long = it.arguments!!.getString("id")!!.toLong()
            val album: Album = DataManager.getAlbum(albumId)
            AlbumView(navController = navController, album = album)
        }

        composable(MediaDestination.GENRES.link) {
            AllGenresListView(navController = navController)
        }

        composable("${MediaDestination.GENRES.link}/{name}") {
            val genreName: String = it.arguments!!.getString("name")!!
            val genreMap: SortedMap<String, Genre> = remember { DataManager.genreMap }
            val genre = genreMap[genreName]!!
            GenreView(navController = navController, genre = genre)
        }

        composable(MediaDestination.PLAYLISTS.link) {
            PlaylistListView(navController = navController)
        }

        composable("${MediaDestination.PLAYLISTS.link}/{id}") {
            val playlistId: Long = it.arguments!!.getString("id")!!.toLong()
            val playlist: PlaylistWithMusics = DataManager.getPlaylist(playlistId = playlistId)
            PlaylistView(navController = navController, playlist = playlist)
        }

        composable(MediaDestination.MUSICS.link) {
            AllMusicsListView(navController = navController)
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
fun openMedia(
    navController: NavHostController,
    media: Media? = null
) {
    PlaylistSelectionManager.openedPlaylist = null
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

        is PlaylistWithMusics -> "${MediaDestination.PLAYLISTS.link}/${media.playlist.id}"

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

fun resetOpenedPlaylist() {
    PlaylistSelectionManager.openedPlaylist = null
}