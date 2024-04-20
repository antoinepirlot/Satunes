/*
 * This file is part of Satunes.
 *
 *  Satunes is free software: you can redistribute it and/or modify it under
 *  the terms of the GNU General Public License as published by the Free Software Foundation,
 *  either version 3 of the License, or (at your option) any later version.
 *
 *  Satunes is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with Satunes.
 *  If not, see <https://www.gnu.org/licenses/>.
 *
 *  **** INFORMATIONS ABOUT THE AUTHOR *****
 *  The author of this file is Antoine Pirlot, the owner of this project.
 *  You find this original project on github.
 *
 *  My github link is: https://github.com/antoinepirlot
 *  This current project's link is: https://github.com/antoinepirlot/Satunes
 *
 *  You can contact me via my email: pirlot.antoine@outlook.com
 *  PS: I don't answer quickly.
 */

package earth.satunes.router

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
import earth.satunes.database.models.Album
import earth.satunes.database.models.Artist
import earth.satunes.database.models.Folder
import earth.satunes.database.models.Genre
import earth.satunes.database.models.relations.PlaylistWithMusics
import earth.satunes.database.services.DataLoader
import earth.satunes.database.services.DataManager
import earth.satunes.router.utils.openMedia
import earth.satunes.ui.components.LoadingCircle
import earth.satunes.ui.views.PlayBackView
import earth.satunes.ui.views.album.AlbumView
import earth.satunes.ui.views.album.AllAlbumsListView
import earth.satunes.ui.views.artist.AllArtistsListView
import earth.satunes.ui.views.artist.ArtistView
import earth.satunes.ui.views.folder.FolderView
import earth.satunes.ui.views.folder.RootFolderView
import earth.satunes.ui.views.genre.AllGenresListView
import earth.satunes.ui.views.genre.GenreView
import earth.satunes.ui.views.music.AllMusicsListView
import earth.satunes.ui.views.playlist.PlaylistListView
import earth.satunes.ui.views.playlist.PlaylistView
import earth.satunes.ui.views.settings.BottomNavigationBarSettingsView
import earth.satunes.ui.views.settings.PlaybackSettingsView
import earth.satunes.ui.views.settings.SettingsView
import earth.satunes.ui.views.settings.UpdatesView

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
                PlayBackView(
                    onAlbumClick = { album: Album? ->
                        if (album != null) {
                            openMedia(navController = navController, media = album)
                        }
                    },
                    onArtistClick = { artist: Artist ->
                        openMedia(navController = navController, media = artist)
                    }
                )
            }
        }

        composable(Destination.SETTINGS.link) {
            SettingsView(navController = navController)
        }

        composable(Destination.BOTTOM_BAR_SETTING.link) {
            BottomNavigationBarSettingsView()
        }

        composable(Destination.PLAYBACK_SETTINGS.link) {
            PlaybackSettingsView()
        }

        composable(Destination.UPDATES.link) {
            UpdatesView()
        }
    }
}