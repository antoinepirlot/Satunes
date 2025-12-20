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

package io.github.antoinepirlot.satunes.router.routes

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.github.antoinepirlot.satunes.data.viewmodels.NavigationViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.PlaybackViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.SatunesViewModel
import io.github.antoinepirlot.satunes.models.Destination
import io.github.antoinepirlot.satunes.ui.views.LoadingView
import io.github.antoinepirlot.satunes.ui.views.playback.PlaybackView
import io.github.antoinepirlot.satunes.ui.views.playback.common.PlaybackQueueView

/**
 * @author Antoine Pirlot on 15/07/2024
 */

internal fun NavGraphBuilder.playbackRoutes(
    satunesViewModel: SatunesViewModel,
    playbackViewModel: PlaybackViewModel,
    navigationViewModel: NavigationViewModel,
    onStart: AnimatedContentScope.(NavBackStackEntry) -> Unit,
) {
    composable(Destination.PLAYBACK.link) {
        LaunchedEffect(key1 = Unit) {
            onStart(it)
            navigationViewModel.setCurrentMediaImpl(media = playbackViewModel.musicPlaying!!)
        }

        if (satunesViewModel.isLoadingData || !satunesViewModel.isDataLoaded) LoadingView()
        else PlaybackView()
    }

    composable(Destination.PLAYBACK_QUEUE.link) {
        LaunchedEffect(key1 = Unit) {
            onStart(it)
        }
        PlaybackQueueView()
    }
}