/*
 * This file is part of MP3 Player.
 *
 * MP3 Player is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * MP3 Player is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with MP3 Player.
 * If not, see <https://www.gnu.org/licenses/>.
 *
 * **** INFORMATIONS ABOUT THE AUTHOR *****
 * The author of this file is Antoine Pirlot, the owner of this project.
 * You find this original project on github.
 *
 * My github link is: https://github.com/antoinepirlot
 * This current project's link is: https://github.com/antoinepirlot/MP3-Player
 *
 * You can contact me via my email: pirlot.antoine@outlook.com
 * PS: I don't answer quickly.
 */

package earth.mp3player.router

import android.net.Uri.encode
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import earth.mp3player.database.models.Album
import earth.mp3player.database.models.Artist
import earth.mp3player.database.models.Folder
import earth.mp3player.database.models.Genre
import earth.mp3player.database.models.relations.PlaylistWithMusics
import earth.mp3player.database.services.DataLoader
import earth.mp3player.database.services.DataManager
import earth.mp3player.router.utils.openMedia
import earth.mp3player.ui.components.LoadingCircle
import earth.mp3player.ui.views.PlayBackView
import earth.mp3player.ui.views.main.album.AlbumView
import earth.mp3player.ui.views.main.album.AllAlbumsListView
import earth.mp3player.ui.views.main.artist.AllArtistsListView
import earth.mp3player.ui.views.main.artist.ArtistView
import earth.mp3player.ui.views.main.folder.FolderView
import earth.mp3player.ui.views.main.folder.RootFolderView
import earth.mp3player.ui.views.main.genre.AllGenresListView
import earth.mp3player.ui.views.main.genre.GenreView
import earth.mp3player.ui.views.main.music.AllMusicsListView
import earth.mp3player.ui.views.main.playlist.PlaylistListView
import earth.mp3player.ui.views.main.playlist.PlaylistView
import earth.mp3player.ui.views.settings.SettingsView

/**
 * @author Antoine Pirlot on 23-01-24
 */

@Composable
internal fun Router(
    modifier: Modifier = Modifier,
    navController: NavHostController,
) {
    val isLoading: MutableState<Boolean> = remember { DataLoader.isLoading }

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Destination.FOLDERS.link
    ) {

        composable(Destination.FOLDERS.link) {
            // /!\ This route prevent back gesture to exit the app
            if (isLoading.value) {
                LoadingCircle()
            } else {
                RootFolderView(navController = navController)
            }
        }

        composable("${Destination.FOLDERS.link}/{id}") {
            if (isLoading.value) {
                LoadingCircle()
            } else {
                val folderId = it.arguments!!.getString("id")!!.toLong()
                val folder: Folder by remember { mutableStateOf(DataManager.getFolder(folderId = folderId)) }
                FolderView(navController = navController, folder = folder)
            }
        }

        composable(Destination.ARTISTS.link) {
            if (isLoading.value) {
                LoadingCircle()
            } else {
                AllArtistsListView(navController = navController)
            }
        }

        composable("${Destination.ARTISTS.link}/{name}") {
            if (isLoading.value) {
                LoadingCircle()
            } else {
                val artistName: String = encode(it.arguments!!.getString("name")!!)
                val artist: Artist by remember { mutableStateOf(DataManager.getArtist(artistName)) }
                ArtistView(navController = navController, artist = artist)
            }
        }

        composable(Destination.ALBUMS.link) {
            if (isLoading.value) {
                LoadingCircle()
            } else {
                AllAlbumsListView(navController = navController)
            }
        }

        composable("${Destination.ALBUMS.link}/{id}") {
            if (isLoading.value) {
                LoadingCircle()
            } else {
                val albumId: Long = it.arguments!!.getString("id")!!.toLong()
                val album: Album by remember { mutableStateOf(DataManager.getAlbum(albumId)) }
                AlbumView(navController = navController, album = album)
            }
        }

        composable(Destination.GENRES.link) {
            if (isLoading.value) {
                LoadingCircle()
            } else {
                AllGenresListView(navController = navController)
            }
        }

        composable("${Destination.GENRES.link}/{name}") {
            if (isLoading.value) {
                LoadingCircle()
            } else {
                val genreName: String = encode(it.arguments!!.getString("name")!!)
                val genre: Genre by remember { mutableStateOf(DataManager.getGenre(genreName = genreName)) }
                GenreView(navController = navController, genre = genre)
            }
        }

        composable(Destination.PLAYLISTS.link) {
            if (isLoading.value) {
                LoadingCircle()
            } else {
                PlaylistListView(navController = navController)
            }
        }

        composable("${Destination.PLAYLISTS.link}/{id}") {
            if (isLoading.value) {
                LoadingCircle()
            } else {
                val playlistId: Long = it.arguments!!.getString("id")!!.toLong()
                val playlist: PlaylistWithMusics by remember {
                    mutableStateOf(DataManager.getPlaylist(playlistId = playlistId))
                }
                PlaylistView(navController = navController, playlist = playlist)
            }
        }

        composable(Destination.MUSICS.link) {
            if (isLoading.value) {
                LoadingCircle()
            } else {
                AllMusicsListView(navController = navController)
            }
        }

        composable(Destination.PLAYBACK.link) {
            if (isLoading.value) {
                LoadingCircle()
            } else {
                PlayBackView(onClick = { album: Album? ->
                    if (album != null) {
                        openMedia(navController = navController, media = album)
                    }
                })
            }
        }

        composable(Destination.SETTINGS.link) {
            SettingsView()
        }
    }
}