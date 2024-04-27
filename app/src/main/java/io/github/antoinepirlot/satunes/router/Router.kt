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

package io.github.antoinepirlot.satunes.router

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
import io.github.antoinepirlot.satunes.database.models.Album
import io.github.antoinepirlot.satunes.database.models.Artist
import io.github.antoinepirlot.satunes.database.models.Folder
import io.github.antoinepirlot.satunes.database.models.Genre
import io.github.antoinepirlot.satunes.database.models.relations.PlaylistWithMusics
import io.github.antoinepirlot.satunes.database.services.DataLoader
import io.github.antoinepirlot.satunes.database.services.DataManager
import io.github.antoinepirlot.satunes.router.utils.openMedia
import io.github.antoinepirlot.satunes.ui.views.LoadingView
import io.github.antoinepirlot.satunes.ui.views.PlayBackView
import io.github.antoinepirlot.satunes.ui.views.album.AlbumView
import io.github.antoinepirlot.satunes.ui.views.album.AllAlbumsListView
import io.github.antoinepirlot.satunes.ui.views.artist.AllArtistsListView
import io.github.antoinepirlot.satunes.ui.views.artist.ArtistView
import io.github.antoinepirlot.satunes.ui.views.folder.FolderView
import io.github.antoinepirlot.satunes.ui.views.folder.RootFolderView
import io.github.antoinepirlot.satunes.ui.views.genre.AllGenresListView
import io.github.antoinepirlot.satunes.ui.views.genre.GenreView
import io.github.antoinepirlot.satunes.ui.views.music.AllMusicsListView
import io.github.antoinepirlot.satunes.ui.views.playlist.PlaylistListView
import io.github.antoinepirlot.satunes.ui.views.playlist.PlaylistView
import io.github.antoinepirlot.satunes.ui.views.settings.BottomNavigationBarSettingView
import io.github.antoinepirlot.satunes.ui.views.settings.ExclusionSettingView
import io.github.antoinepirlot.satunes.ui.views.settings.PlaybackSettingView
import io.github.antoinepirlot.satunes.ui.views.settings.SettingsView
import io.github.antoinepirlot.satunes.ui.views.settings.UpdatesView

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
                LoadingView()
            } else {
                RootFolderView(navController = navController)
            }
        }

        composable("${Destination.FOLDERS.link}/{id}") {
            if (isLoading.value) {
                LoadingView()
            } else {
                val folderId = it.arguments!!.getString("id")!!.toLong()
                val folder: Folder by remember { mutableStateOf(DataManager.getFolder(folderId = folderId)) }
                FolderView(navController = navController, folder = folder)
            }
        }

        composable(Destination.ARTISTS.link) {
            if (isLoading.value) {
                LoadingView()
            } else {
                AllArtistsListView(navController = navController)
            }
        }

        composable("${Destination.ARTISTS.link}/{name}") {
            if (isLoading.value) {
                LoadingView()
            } else {
                val artistName: String = encode(it.arguments!!.getString("name")!!)
                val artist: Artist by remember { mutableStateOf(DataManager.getArtist(artistName)) }
                ArtistView(navController = navController, artist = artist)
            }
        }

        composable(Destination.ALBUMS.link) {
            if (isLoading.value) {
                LoadingView()
            } else {
                AllAlbumsListView(navController = navController)
            }
        }

        composable("${Destination.ALBUMS.link}/{id}") {
            if (isLoading.value) {
                LoadingView()
            } else {
                val albumId: Long = it.arguments!!.getString("id")!!.toLong()
                val album: Album by remember { mutableStateOf(DataManager.getAlbum(albumId)) }
                AlbumView(navController = navController, album = album)
            }
        }

        composable(Destination.GENRES.link) {
            if (isLoading.value) {
                LoadingView()
            } else {
                AllGenresListView(navController = navController)
            }
        }

        composable("${Destination.GENRES.link}/{name}") {
            if (isLoading.value) {
                LoadingView()
            } else {
                val genreName: String = encode(it.arguments!!.getString("name")!!)
                val genre: Genre by remember { mutableStateOf(DataManager.getGenre(genreName = genreName)) }
                GenreView(navController = navController, genre = genre)
            }
        }

        composable(Destination.PLAYLISTS.link) {
            if (isLoading.value) {
                LoadingView()
            } else {
                PlaylistListView(navController = navController)
            }
        }

        composable("${Destination.PLAYLISTS.link}/{id}") {
            if (isLoading.value) {
                LoadingView()
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
                LoadingView()
            } else {
                AllMusicsListView(navController = navController)
            }
        }

        composable(Destination.PLAYBACK.link) {
            if (isLoading.value) {
                LoadingView()
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
            BottomNavigationBarSettingView()
        }

        composable(Destination.PLAYBACK_SETTINGS.link) {
            PlaybackSettingView()
        }

        composable(Destination.UPDATES.link) {
            UpdatesView()
        }

        composable(Destination.EXCLUSION.link) {
            ExclusionSettingView()
        }
    }
}