package io.github.antoinepirlot.satunes.router.routes.subsonic

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.github.antoinepirlot.satunes.data.states.SubsonicUiState
import io.github.antoinepirlot.satunes.data.viewmodels.SatunesViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.SubsonicViewModel
import io.github.antoinepirlot.satunes.database.models.media.Media
import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicAlbum
import io.github.antoinepirlot.satunes.models.Destination
import io.github.antoinepirlot.satunes.ui.views.LoadingView
import io.github.antoinepirlot.satunes.ui.views.media.NoDataFoundView
import io.github.antoinepirlot.satunes.ui.views.media.album.AlbumView

/**
 * @author Antoine Pirlot 17/12/2025
 */
internal fun NavGraphBuilder.subsonicMediaRoutes(
    satunesViewModel: SatunesViewModel,
    subsonicViewModel: SubsonicViewModel,
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
            val subsonicUiState: SubsonicUiState by subsonicViewModel.uiState.collectAsState()
            val albumId: Long = it.arguments!!.getString("id")!!.toLong()
            val album: SubsonicAlbum? = subsonicUiState.mediaRetrieved as SubsonicAlbum?

            LaunchedEffect(key1 = album) {
                if (album == null)
                    subsonicViewModel.loadAlbum(albumId = albumId)
                else
                    onMediaOpen(album)
            }
            if (album != null)
                AlbumView(album = album)
            else
                NoDataFoundView()
        }
    }
}