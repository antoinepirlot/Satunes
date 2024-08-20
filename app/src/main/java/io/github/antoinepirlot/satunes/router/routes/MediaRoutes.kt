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

package io.github.antoinepirlot.satunes.router.routes

import androidx.compose.animation.AnimatedContentScope
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import io.github.antoinepirlot.satunes.database.models.Album
import io.github.antoinepirlot.satunes.database.models.Artist
import io.github.antoinepirlot.satunes.database.models.Folder
import io.github.antoinepirlot.satunes.database.models.Genre
import io.github.antoinepirlot.satunes.database.models.Playlist
import io.github.antoinepirlot.satunes.models.Destination
import io.github.antoinepirlot.satunes.data.viewmodels.DataViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.SatunesViewModel
import io.github.antoinepirlot.satunes.ui.views.LoadingView
import io.github.antoinepirlot.satunes.ui.views.media.album.AlbumView
import io.github.antoinepirlot.satunes.ui.views.media.album.AllAlbumsListView
import io.github.antoinepirlot.satunes.ui.views.media.artist.AllArtistsListView
import io.github.antoinepirlot.satunes.ui.views.media.artist.ArtistView
import io.github.antoinepirlot.satunes.ui.views.media.folder.FolderView
import io.github.antoinepirlot.satunes.ui.views.media.folder.RootFolderView
import io.github.antoinepirlot.satunes.ui.views.media.genre.AllGenresListView
import io.github.antoinepirlot.satunes.ui.views.media.genre.GenreView
import io.github.antoinepirlot.satunes.ui.views.media.music.AllMusicsListView
import io.github.antoinepirlot.satunes.ui.views.media.playlist.PlaylistListView
import io.github.antoinepirlot.satunes.ui.views.media.playlist.PlaylistView

/**
 * @author Antoine Pirlot on 15/07/2024
 */

internal fun NavGraphBuilder.mediaRoutes(
    navController: NavHostController,
    satunesViewModel: SatunesViewModel,
    dataViewModel: DataViewModel,
    onStart: AnimatedContentScope.(NavBackStackEntry) -> Unit
) {
    composable(Destination.FOLDERS.link) {
        // /!\ This route prevent back gesture to exit the app
        onStart(it)

        if (satunesViewModel.isLoadingData || !satunesViewModel.isDataLoaded) {
            LoadingView()
        } else {
            RootFolderView(navController = navController)
        }
    }

    composable("${Destination.FOLDERS.link}/{id}") {
        onStart(it)

        if (satunesViewModel.isLoadingData || !satunesViewModel.isDataLoaded) {
            LoadingView()
        } else {
            val folderId = it.arguments!!.getString("id")!!.toLong()
            val folder: Folder = dataViewModel.getFolder(id = folderId)
            FolderView(navController = navController, folder = folder)
        }
    }

    composable(Destination.ARTISTS.link) {
        onStart(it)

        if (satunesViewModel.isLoadingData || !satunesViewModel.isDataLoaded) {
            LoadingView()
        } else {
            AllArtistsListView(navController = navController)
        }
    }

    composable("${Destination.ARTISTS.link}/{id}") {
        onStart(it)

        if (satunesViewModel.isLoadingData || !satunesViewModel.isDataLoaded) {
            LoadingView()
        } else {
            val artistId: Long = it.arguments!!.getString("id")!!.toLong()
            val artist: Artist = dataViewModel.getArtist(id = artistId)
            ArtistView(navController = navController, artist = artist)
        }
    }

    composable(Destination.ALBUMS.link) {
        onStart(it)

        if (satunesViewModel.isLoadingData || !satunesViewModel.isDataLoaded) {
            LoadingView()
        } else {
            AllAlbumsListView(navController = navController)
        }
    }

    composable("${Destination.ALBUMS.link}/{id}") {
        onStart(it)

        if (satunesViewModel.isLoadingData || !satunesViewModel.isDataLoaded) {
            LoadingView()
        } else {
            val albumId: Long = it.arguments!!.getString("id")!!.toLong()
            val album: Album = dataViewModel.getAlbum(albumId)
            AlbumView(navController = navController, album = album)
        }
    }

    composable(Destination.GENRES.link) {
        onStart(it)

        if (satunesViewModel.isLoadingData || !satunesViewModel.isDataLoaded) {
            LoadingView()
        } else {
            AllGenresListView(navController = navController)
        }
    }

    composable("${Destination.GENRES.link}/{id}") {
        onStart(it)

        if (satunesViewModel.isLoadingData || !satunesViewModel.isDataLoaded) {
            LoadingView()
        } else {
            val genreId: Long = it.arguments!!.getString("id")!!.toLong()
            val genre: Genre = dataViewModel.getGenre(id = genreId)
            GenreView(navController = navController, genre = genre)
        }
    }

    composable(Destination.PLAYLISTS.link) {
        onStart(it)

        if (satunesViewModel.isLoadingData || !satunesViewModel.isDataLoaded) {
            LoadingView()
        } else {
            PlaylistListView(navController = navController)
        }
    }

    composable("${Destination.PLAYLISTS.link}/{id}") {
        onStart(it)

        if (satunesViewModel.isLoadingData || !satunesViewModel.isDataLoaded) {
            LoadingView()
        } else {
            val playlistId: Long = it.arguments!!.getString("id")!!.toLong()
            val playlist: Playlist = dataViewModel.getPlaylist(id = playlistId)
            PlaylistView(playlist = playlist, navController = navController)
        }
    }

    composable(Destination.MUSICS.link) {
        onStart(it)

        if (satunesViewModel.isLoadingData || !satunesViewModel.isDataLoaded) {
            LoadingView()
        } else {
            AllMusicsListView(navController = navController)
        }
    }
}