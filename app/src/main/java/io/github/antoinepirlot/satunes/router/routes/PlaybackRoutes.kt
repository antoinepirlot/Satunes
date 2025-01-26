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
 * You find this original project on github.
 *
 * My github link is: https://github.com/antoinepirlot
 * This current project's link is: https://github.com/antoinepirlot/Satunes
 *
 * PS: I don't answer quickly.
 */

package io.github.antoinepirlot.satunes.router.routes

import androidx.compose.animation.AnimatedContentScope
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import io.github.antoinepirlot.satunes.data.local.LocalNavController
import io.github.antoinepirlot.satunes.data.viewmodels.PlaybackViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.SatunesViewModel
import io.github.antoinepirlot.satunes.database.models.Album
import io.github.antoinepirlot.satunes.database.models.Artist
import io.github.antoinepirlot.satunes.models.Destination
import io.github.antoinepirlot.satunes.router.utils.openMedia
import io.github.antoinepirlot.satunes.ui.views.LoadingView
import io.github.antoinepirlot.satunes.ui.views.playback.PlaybackView
import io.github.antoinepirlot.satunes.ui.views.playback.common.PlaybackQueueView

/**
 * @author Antoine Pirlot on 15/07/2024
 */

internal fun NavGraphBuilder.playbackRoutes(
    satunesViewModel: SatunesViewModel,
    playbackViewModel: PlaybackViewModel,
    onStart: AnimatedContentScope.(NavBackStackEntry) -> Unit,
) {
    composable(Destination.PLAYBACK.link) {
        onStart(it)

        if (satunesViewModel.isLoadingData || !satunesViewModel.isDataLoaded) {
            LoadingView()
        } else {
            val navController: NavHostController = LocalNavController.current
            PlaybackView(
                onAlbumClick = { album: Album? ->
                    if (album != null) {
                        openMedia(
                            playbackViewModel = playbackViewModel,
                            media = album,
                            navController = navController
                        )
                    }
                },
                onArtistClick = { artist: Artist ->
                    openMedia(
                        playbackViewModel = playbackViewModel,
                        media = artist,
                        navController = navController
                    )
                }
            )
        }
    }

    composable(Destination.PLAYBACK_QUEUE.link) {
        onStart(it)
        PlaybackQueueView()
    }
}