package io.github.antoinepirlot.satunes.router.routes.subsonic

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.github.antoinepirlot.satunes.data.states.NavigationUiState
import io.github.antoinepirlot.satunes.data.viewmodels.NavigationViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.SatunesViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.SubsonicViewModel
import io.github.antoinepirlot.satunes.database.models.media.Media
import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicAlbum
import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicArtist
import io.github.antoinepirlot.satunes.models.Destination
import io.github.antoinepirlot.satunes.ui.views.LoadingView
import io.github.antoinepirlot.satunes.ui.views.SubsonicErrorView
import io.github.antoinepirlot.satunes.ui.views.media.NoDataFoundView
import io.github.antoinepirlot.satunes.ui.views.media.album.AlbumView
import io.github.antoinepirlot.satunes.ui.views.media.artist.ArtistView

/**
 * @author Antoine Pirlot 17/12/2025
 */
internal fun NavGraphBuilder.subsonicMediaRoutes(
    satunesViewModel: SatunesViewModel,
    subsonicViewModel: SubsonicViewModel,
    navigationViewModel: NavigationViewModel,
    onStart: (NavBackStackEntry) -> Unit,
    onMediaOpen: (media: Media) -> Unit
) {
    composable(route = Destination.SUBSONIC_ALBUM.link) {
        LaunchedEffect(key1 = Unit) {
            onStart(it)
        }

        if (satunesViewModel.isLoadingData || !satunesViewModel.isDataLoaded) {
            LoadingView()
        } else {
            val navigationUiState: NavigationUiState by navigationViewModel.uiState.collectAsState()
            val albumId: Long = it.arguments!!.getString("id")!!.toLong()
            val album: Media? = navigationUiState.currentMediaImpl
            if (album != null && !album.isAlbum()) return@composable
            album as SubsonicAlbum?

            LaunchedEffect(key1 = Unit) {
                subsonicViewModel.getAlbum(albumId = albumId, onDataRetrieved = onMediaOpen)
            }

            if (album != null) {
                LaunchedEffect(key1 = Unit) {
                    onMediaOpen(album)
                }

                AlbumView(album = album)
            } else if (subsonicViewModel.error != null)
                SubsonicErrorView(error = subsonicViewModel.error!!)
            else
                NoDataFoundView()
        }
    }

    composable(route = Destination.SUBSONIC_ARTIST.link) {
        LaunchedEffect(key1 = Unit) {
            onStart(it)
        }
        if (satunesViewModel.isLoadingData || !satunesViewModel.isDataLoaded) {
            LoadingView()
        } else {
            val navigationUiState: NavigationUiState by navigationViewModel.uiState.collectAsState()
            val artistId: Long = it.arguments!!.getString("id")!!.toLong()
            val artist: Media? = navigationUiState.currentMediaImpl
            if (artist != null && !artist.isArtist()) return@composable
            artist as SubsonicArtist?

            LaunchedEffect(key1 = Unit) {
                subsonicViewModel.getArtistWithMusics(
                    artistId = artistId,
                    onDataRetrieved = { artist: SubsonicArtist ->
                        onMediaOpen(artist)
                    }
                )
            }

            if (artist != null) {
                ArtistView(artist = artist)
            } else if (subsonicViewModel.error != null)
                SubsonicErrorView(error = subsonicViewModel.error!!)
            else
                NoDataFoundView()
        }
    }
}