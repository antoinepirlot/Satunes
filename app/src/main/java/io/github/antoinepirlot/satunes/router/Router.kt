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

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import io.github.antoinepirlot.satunes.models.DEFAULT_DESTINATION
import io.github.antoinepirlot.satunes.models.Destination
import io.github.antoinepirlot.satunes.router.routes.mediaRoutes
import io.github.antoinepirlot.satunes.router.routes.playbackRoutes
import io.github.antoinepirlot.satunes.router.routes.searchRoutes
import io.github.antoinepirlot.satunes.router.routes.settingsRoutes
import io.github.antoinepirlot.satunes.ui.viewmodels.DataViewModel
import io.github.antoinepirlot.satunes.ui.viewmodels.PlaybackViewModel
import io.github.antoinepirlot.satunes.ui.viewmodels.SatunesViewModel

/**
 * @author Antoine Pirlot on 23-01-24
 */

@Composable
internal fun Router(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    satunesViewModel: SatunesViewModel = viewModel(),
    dataViewModel: DataViewModel = viewModel(),
    playbackViewModel: PlaybackViewModel = viewModel(),
) {
    val isAudioAllowed: Boolean = satunesViewModel.isAudioAllowed

    if (isAudioAllowed) {
        LaunchedEffect(key1 = Unit) {
            satunesViewModel.loadAllData()
        }
    }

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = DEFAULT_DESTINATION,
        enterTransition = { fadeIn(animationSpec = tween(500)) },
        exitTransition = { fadeOut(animationSpec = tween(0)) },
    ) {
        mediaRoutes(
            navController = navController,
            satunesViewModel = satunesViewModel,
            dataViewModel = dataViewModel,
            onStart = {
                checkIfAllowed(isAudioAllowed = isAudioAllowed, navController = navController)
                satunesViewModel.setCurrentDestination(destination = it.destination.route!!)
            }
        )
        searchRoutes(
            navController = navController,
            satunesViewModel = satunesViewModel,
            onStart = {
                checkIfAllowed(isAudioAllowed = isAudioAllowed, navController = navController)
                satunesViewModel.setCurrentDestination(destination = it.destination.route!!)
            }
        )
        playbackRoutes(
            navController = navController,
            satunesViewModel = satunesViewModel,
            playbackViewModel = playbackViewModel,
            onStart = {
                checkIfAllowed(isAudioAllowed = isAudioAllowed, navController = navController)
                satunesViewModel.setCurrentDestination(destination = it.destination.route!!)
            }
        )
        settingsRoutes(
            navController = navController,
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
 */
private fun checkIfAllowed(isAudioAllowed: Boolean, navController: NavHostController) {
    if (!isAudioAllowed) {
        navController.popBackStack()
        navController.navigate(Destination.PERMISSIONS_SETTINGS.link)
    }
}