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
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.github.antoinepirlot.satunes.data.viewmodels.DataViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.SatunesViewModel
import io.github.antoinepirlot.satunes.database.models.Album
import io.github.antoinepirlot.satunes.database.models.Artist
import io.github.antoinepirlot.satunes.database.models.Folder
import io.github.antoinepirlot.satunes.database.models.Genre
import io.github.antoinepirlot.satunes.database.models.Playlist
import io.github.antoinepirlot.satunes.models.Destination
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
    satunesViewModel: SatunesViewModel,
    dataViewModel: DataViewModel,
    onStart: AnimatedContentScope.(NavBackStackEntry) -> Unit
) {
    composable(Destination.FOLDERS.link) {
        // /!\ This route prevent back gesture to exit the app
        LaunchedEffect(key1 = Unit) {
            onStart(it)
        }

        if (satunesViewModel.isLoadingData || !satunesViewModel.isDataLoaded) {
            LoadingView()
        } else {
            RootFolderView(satunesViewModel = satunesViewModel) //(for all all media list) send view model needed to avoid not showing extra buttons on first launch
        }
    }

    composable(Destination.FOLDER.link) {
        LaunchedEffect(key1 = Unit) {
            onStart(it)
        }

        if (satunesViewModel.isLoadingData || !satunesViewModel.isDataLoaded) {
            LoadingView()
        } else {
            val folderId = it.arguments!!.getString("id")!!.toLong()
            val folder: Folder = dataViewModel.getFolder(id = folderId)
            FolderView(folder = folder)
        }
    }

    composable(Destination.ARTISTS.link) {
        LaunchedEffect(key1 = Unit) {
            onStart(it)
        }

        if (satunesViewModel.isLoadingData || !satunesViewModel.isDataLoaded) {
            LoadingView()
        } else {
            AllArtistsListView(satunesViewModel = satunesViewModel)
        }
    }

    composable(Destination.ARTIST.link) {
        LaunchedEffect(key1 = Unit) {
            onStart(it)
        }

        if (satunesViewModel.isLoadingData || !satunesViewModel.isDataLoaded) {
            LoadingView()
        } else {
            val artistId: Long = it.arguments!!.getString("id")!!.toLong()
            val artist: Artist = dataViewModel.getArtist(id = artistId)
            ArtistView(artist = artist)
        }
    }

    composable(Destination.ALBUMS.link) {
        LaunchedEffect(key1 = Unit) {
            onStart(it)
        }

        if (satunesViewModel.isLoadingData || !satunesViewModel.isDataLoaded) {
            LoadingView()
        } else {
            AllAlbumsListView(satunesViewModel = satunesViewModel)
        }
    }

    composable(Destination.ALBUM.link) {
        LaunchedEffect(key1 = Unit) {
            onStart(it)
        }

        if (satunesViewModel.isLoadingData || !satunesViewModel.isDataLoaded) {
            LoadingView()
        } else {
            val albumId: Long = it.arguments!!.getString("id")!!.toLong()
            val album: Album = dataViewModel.getAlbum(albumId)
            AlbumView(album = album)
        }
    }

    composable(Destination.GENRES.link) {
        LaunchedEffect(key1 = Unit) {
            onStart(it)
        }

        if (satunesViewModel.isLoadingData || !satunesViewModel.isDataLoaded) {
            LoadingView()
        } else {
            AllGenresListView(satunesViewModel = satunesViewModel)
        }
    }

    composable(Destination.GENRE.link) {
        LaunchedEffect(key1 = Unit) {
            onStart(it)
        }

        if (satunesViewModel.isLoadingData || !satunesViewModel.isDataLoaded) {
            LoadingView()
        } else {
            val genreId: Long = it.arguments!!.getString("id")!!.toLong()
            val genre: Genre = dataViewModel.getGenre(id = genreId)
            GenreView(genre = genre)
        }
    }

    composable(Destination.PLAYLISTS.link) {
        LaunchedEffect(key1 = Unit) {
            onStart(it)
        }

        if (satunesViewModel.isLoadingData || !satunesViewModel.isDataLoaded) {
            LoadingView()
        } else {
            PlaylistListView(satunesViewModel = satunesViewModel)
        }
    }

    composable(Destination.PLAYLIST.link) {
        LaunchedEffect(key1 = Unit) {
            onStart(it)
        }

        if (satunesViewModel.isLoadingData || !satunesViewModel.isDataLoaded) {
            LoadingView()
        } else {
            val playlistId: Long = it.arguments!!.getString("id")!!.toLong()
            val playlist: Playlist = dataViewModel.getPlaylist(id = playlistId)
            PlaylistView(playlist = playlist)
        }
    }

    composable(Destination.MUSICS.link) {
        LaunchedEffect(key1 = Unit) {
            onStart(it)
        }

        if (satunesViewModel.isLoadingData || !satunesViewModel.isDataLoaded) {
            LoadingView()
        } else {
            AllMusicsListView(satunesViewModel = satunesViewModel)
        }
    }
}