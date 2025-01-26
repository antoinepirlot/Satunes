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

package io.github.antoinepirlot.satunes.router

import android.content.Context
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import io.github.antoinepirlot.satunes.data.local.LocalNavController
import io.github.antoinepirlot.satunes.data.states.SatunesUiState
import io.github.antoinepirlot.satunes.data.viewmodels.DataViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.PlaybackViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.SatunesViewModel
import io.github.antoinepirlot.satunes.models.Destination
import io.github.antoinepirlot.satunes.router.routes.mediaRoutes
import io.github.antoinepirlot.satunes.router.routes.playbackRoutes
import io.github.antoinepirlot.satunes.router.routes.searchRoutes
import io.github.antoinepirlot.satunes.router.routes.settingsRoutes
import io.github.antoinepirlot.satunes.router.utils.getNavBarSectionDestination
import io.github.antoinepirlot.satunes.utils.loadSatunesData
import io.github.antoinepirlot.satunes.utils.logger.SatunesLogger

/**
 * @author Antoine Pirlot on 23-01-24
 */

@Composable
internal fun Router(
    modifier: Modifier = Modifier,
    satunesViewModel: SatunesViewModel = viewModel(),
    dataViewModel: DataViewModel = viewModel(),
    playbackViewModel: PlaybackViewModel = viewModel(),
) {
    SatunesLogger.getLogger().info("Router Composable")

    val context: Context = LocalContext.current
    val satunesUiState: SatunesUiState by satunesViewModel.uiState.collectAsState()
    val navController: NavHostController = LocalNavController.current
    val isAudioAllowed: Boolean = satunesViewModel.isAudioAllowed
    val defaultDestination: Destination =
        getNavBarSectionDestination(navBarSection = satunesUiState.defaultNavBarSection)

    LaunchedEffect(key1 = isAudioAllowed) {
        if (isAudioAllowed) {
            loadSatunesData(context = context, satunesViewModel = satunesViewModel)
        }
    }

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = defaultDestination.link,
        enterTransition = { fadeIn(animationSpec = tween(500)) },
        exitTransition = { fadeOut(animationSpec = tween(0)) },
    ) {
        mediaRoutes(
            satunesViewModel = satunesViewModel,
            dataViewModel = dataViewModel,
            onStart = {
                if (checkIfAllowed(
                        satunesUiState = satunesUiState,
                        isAudioAllowed = isAudioAllowed,
                        navController = navController
                    )
                ) satunesViewModel.setCurrentDestination(destination = it.destination.route!!)
            }
        )
        searchRoutes(
            satunesViewModel = satunesViewModel,
            onStart = {
                if (checkIfAllowed(
                        satunesUiState = satunesUiState,
                        isAudioAllowed = isAudioAllowed,
                        navController = navController
                    )
                ) satunesViewModel.setCurrentDestination(destination = it.destination.route!!)
            }
        )
        playbackRoutes(
            satunesViewModel = satunesViewModel,
            playbackViewModel = playbackViewModel,
            onStart = {
                if (checkIfAllowed(
                        satunesUiState = satunesUiState,
                        isAudioAllowed = isAudioAllowed,
                        navController = navController
                    )
                ) satunesViewModel.setCurrentDestination(destination = it.destination.route!!)
            }
        )
        settingsRoutes(
            satunesViewModel = satunesViewModel, // Pass it as param to fix no recomposition when permission granted
            onStart = {
                satunesViewModel.setCurrentDestination(destination = it.destination.route!!)
            }
        )
    }
}

/**
 * Get the right view depending on is audio allowed.
 *
 * If isAudioAllowed is true, then let the user goes into the app.
 * If isAudioAllowed is false, then force the user to go to permission view
 *
 * @param isAudioAllowed true if the permission has been allowed, otherwise false
 * @param navController the [NavHostController] to navigate
 *
 * @return true if it is allowed, false otherwise
 */
private fun checkIfAllowed(
    satunesUiState: SatunesUiState,
    isAudioAllowed: Boolean,
    navController: NavHostController
): Boolean {
    if (!isAudioAllowed && satunesUiState.currentDestination != Destination.PERMISSIONS_SETTINGS) {
        navController.popBackStack()
        navController.navigate(Destination.PERMISSIONS_SETTINGS.link)
        return false
    }
    return true
}