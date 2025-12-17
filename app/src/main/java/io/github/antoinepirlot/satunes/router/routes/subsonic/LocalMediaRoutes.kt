/*
 * This file is part of Satunes.
 *
 * Satunes is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * Satunes is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with Satunes.
 * If not, see <https://www.gnu.org/licenses/>.
 *
 * *** INFORMATION ABOUT THE AUTHOR *****
 * The author of this file is Antoine Pirlot, the owner of this project.
 * You find this original project on Codeberg.
 *
 * My Codeberg link is: https://codeberg.org/antoinepirlot
 * This current project's link is: https://codeberg.org/antoinepirlot/Satunes
 */

package io.github.antoinepirlot.satunes.router.routes.subsonic

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.data.states.DataUiState
import io.github.antoinepirlot.satunes.data.states.NavigationUiState
import io.github.antoinepirlot.satunes.data.viewmodels.DataViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.NavigationViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.SatunesViewModel
import io.github.antoinepirlot.satunes.database.models.media.Album
import io.github.antoinepirlot.satunes.database.models.media.Artist
import io.github.antoinepirlot.satunes.database.models.media.Folder
import io.github.antoinepirlot.satunes.database.models.media.Genre
import io.github.antoinepirlot.satunes.database.models.media.Media
import io.github.antoinepirlot.satunes.database.models.media.Playlist
import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicArtist
import io.github.antoinepirlot.satunes.database.services.data.DataManager
import io.github.antoinepirlot.satunes.models.Destination
import io.github.antoinepirlot.satunes.ui.components.EmptyView
import io.github.antoinepirlot.satunes.ui.views.LoadingView
import io.github.antoinepirlot.satunes.ui.views.media.NoDataFoundView
import io.github.antoinepirlot.satunes.ui.views.media.album.AlbumView
import io.github.antoinepirlot.satunes.ui.views.media.album.AllAlbumsListView
import io.github.antoinepirlot.satunes.ui.views.media.artist.AllArtistsListView
import io.github.antoinepirlot.satunes.ui.views.media.artist.ArtistView
import io.github.antoinepirlot.satunes.ui.views.media.folder.FolderView
import io.github.antoinepirlot.satunes.ui.views.media.genre.AllGenresListView
import io.github.antoinepirlot.satunes.ui.views.media.genre.GenreView
import io.github.antoinepirlot.satunes.ui.views.media.music.AllMusicsListView
import io.github.antoinepirlot.satunes.ui.views.media.playlist.PlaylistListView
import io.github.antoinepirlot.satunes.ui.views.media.playlist.PlaylistView

/**
 * @author Antoine Pirlot on 15/07/2024
 */

internal fun NavGraphBuilder.localMediaRoutes(
    satunesViewModel: SatunesViewModel,
    dataViewModel: DataViewModel,
    navigationViewModel: NavigationViewModel,
    onStart: (NavBackStackEntry) -> Unit,
    onMediaOpen: (media: Media) -> Unit
) {
    composable(Destination.FOLDERS.link) {
        // /!\ This route prevent back gesture to exit the app
        LaunchedEffect(key1 = Unit) {
            onStart(it)
            val folder: Folder = DataManager.getRootFolder()

            onMediaOpen(folder)
        }

        if (satunesViewModel.isLoadingData || !satunesViewModel.isDataLoaded) {
            LoadingView()
        } else {
            FolderView(folder = dataViewModel.getRootFolder())
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
            LaunchedEffect(key1 = Unit) {
                onMediaOpen(folder)
            }
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
        val dataUiState: DataUiState by dataViewModel.uiState.collectAsState()

        if (satunesViewModel.isLoadingData || !satunesViewModel.isDataLoaded) {
            LoadingView()
        } else {
            val navigationUiState: NavigationUiState by navigationViewModel.uiState.collectAsState()
            val artist: Media? = navigationUiState.currentMediaImpl
            if (artist?.isArtist() == false) return@composable //In case of animation to another media view
            artist as Artist?
            val artistId: Long = it.arguments!!.getString("id")!!.toLong()

            LaunchedEffect(key1 = Unit) {
                dataViewModel.getArtist(
                    id = artistId,
                    onFetched = { fetchedArtist: SubsonicArtist -> onMediaOpen(fetchedArtist) }
                )
            }

            if (dataUiState.isFetching)
                LoadingView()
            else if (artist == null)
                EmptyView(text = stringResource(R.string.error_while_fetching_text))
            else {
                LaunchedEffect(key1 = Unit) {
                    onMediaOpen(artist)
                }
                ArtistView(artist = artist)
            }
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
            val album: Album = dataViewModel.getAlbum(id = albumId)

            LaunchedEffect(key1 = Unit) {
                onMediaOpen(album)
            }

            AlbumView(album = album)
            NoDataFoundView()
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
            LaunchedEffect(key1 = Unit) {
                onMediaOpen(genre)
            }
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
            val playlist: Playlist = dataViewModel.getPlaylist(id = playlistId)!!
            LaunchedEffect(key1 = Unit) {
                onMediaOpen(playlist)
            }
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