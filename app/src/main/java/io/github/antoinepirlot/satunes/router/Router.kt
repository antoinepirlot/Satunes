/*
 * This file is part of Satunes.
 * Satunes is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *  Satunes is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License along with Satunes.
 *  If not, see <https://www.gnu.org/licenses/>.
 *
 * **** INFORMATION ABOUT THE AUTHOR *****
 * The author of this file is Antoine Pirlot, the owner of this project.
 * You find this original project on Codeberg.
 *
 * My Codeberg link is: https://codeberg.org/antoinepirlot
 * This current project's link is: https://codeberg.org/antoinepirlot/Satunes
 */

package io.github.antoinepirlot.satunes.router

import android.content.Context
import androidx.activity.BackEventCompat
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import io.github.antoinepirlot.satunes.data.local.LocalNavController
import io.github.antoinepirlot.satunes.data.states.NavigationUiState
import io.github.antoinepirlot.satunes.data.states.SatunesUiState
import io.github.antoinepirlot.satunes.data.viewmodels.DataViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.NavigationViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.PlaybackViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.SatunesViewModel
import io.github.antoinepirlot.satunes.database.models.media.Playlist
import io.github.antoinepirlot.satunes.models.Destination
import io.github.antoinepirlot.satunes.models.listeners.OnBackPressedListener
import io.github.antoinepirlot.satunes.router.routes.mediaRoutes
import io.github.antoinepirlot.satunes.router.routes.playbackRoutes
import io.github.antoinepirlot.satunes.router.routes.searchRoutes
import io.github.antoinepirlot.satunes.router.routes.settingsRoutes
import io.github.antoinepirlot.satunes.router.utils.getNavBarSectionDestination
import io.github.antoinepirlot.satunes.utils.checkDefaultPlaylistSetting
import io.github.antoinepirlot.satunes.utils.logger.SatunesLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

/**
 * @author Antoine Pirlot on 23-01-24
 */

@Composable
internal fun Router(
    modifier: Modifier = Modifier,
    satunesViewModel: SatunesViewModel = viewModel(),
    dataViewModel: DataViewModel = viewModel(),
    playbackViewModel: PlaybackViewModel = viewModel(),
    navigationViewModel: NavigationViewModel = viewModel(),
) {
    SatunesLogger.getLogger()?.info("Router Composable")

    val context: Context = LocalContext.current
    val satunesUiState: SatunesUiState by satunesViewModel.uiState.collectAsState()
    val navigationUiState: NavigationUiState by navigationViewModel.uiState.collectAsState()
    val navController: NavHostController = LocalNavController.current
    val isAudioAllowed: Boolean = satunesUiState.isAudioAllowed
    var defaultDestination: Destination? by rememberSaveable { mutableStateOf(null) }
    var isInitialisation: Boolean by rememberSaveable { mutableStateOf(true) }
    LaunchedEffect(key1 = Unit) {
        defaultDestination =
            getNavBarSectionDestination(navBarSection = satunesViewModel.defaultNavBarSection)
        navigationViewModel.init(defaultDestination = defaultDestination!!)
    }

    if (defaultDestination == null) return

    HandleBackButtonPressed()
    HandleSwipeBack()

    LaunchedEffect(key1 = dataViewModel.isLoaded) {
        if (
            defaultDestination == Destination.PLAYLISTS &&
            dataViewModel.isLoaded &&
            isInitialisation
        ) {
            isInitialisation = false
            checkDefaultPlaylistSetting(context = context)
            if (satunesViewModel.defaultPlaylistId >= 0) {
                val playlist: Playlist =
                    dataViewModel.getPlaylist(id = satunesViewModel.defaultPlaylistId)!!
                navigationViewModel.backToRoot(
                    rootRoute = defaultDestination!!,
                    navController = navController
                )
                navigationViewModel.navigate(
                    navController = navController,
                    mediaImpl = playlist
                )
            }
        }
    }

    // Start handle destination change

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = defaultDestination!!.link,
        enterTransition = { fadeIn(animationSpec = tween(500)) },
        exitTransition = { fadeOut(animationSpec = tween(0)) },
    ) {
        mediaRoutes(
            satunesViewModel = satunesViewModel,
            dataViewModel = dataViewModel,
            onStart = {
                checkIfAllowed(
                    isAudioAllowed = isAudioAllowed,
                    navController = navController,
                    navigationViewModel = navigationViewModel,
                    navigationUiState = navigationUiState
                )
            }
        )
        searchRoutes(
            satunesViewModel = satunesViewModel,
            onStart = {
                checkIfAllowed(
                    isAudioAllowed = isAudioAllowed,
                    navController = navController,
                    navigationViewModel = navigationViewModel,
                    navigationUiState = navigationUiState
                )
            }
        )
        playbackRoutes(
            satunesViewModel = satunesViewModel,
            playbackViewModel = playbackViewModel,
            onStart = {
                checkIfAllowed(
                    isAudioAllowed = isAudioAllowed,
                    navController = navController,
                    navigationViewModel = navigationViewModel,
                    navigationUiState = navigationUiState
                )
            }
        )
        settingsRoutes(
            satunesViewModel = satunesViewModel, // Pass it as param to fix no recomposition when permission granted
            onStart = { /* Nothing */ }
        )
    }
}

@Composable
private fun HandleSwipeBack(navigationViewModel: NavigationViewModel = viewModel()) {
    val navController = LocalNavController.current

    PredictiveBackHandler(onBack = { event: @JvmSuppressWildcards Flow<BackEventCompat> ->
        event.collect()
        navigationViewModel.popBackStack(navController = navController)
    })
}

@Composable
private fun HandleBackButtonPressed(navigationViewModel: NavigationViewModel = viewModel()) {
    val navController = LocalNavController.current
    val backPressedDispatcher: OnBackPressedDispatcher? =
        LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    LaunchedEffect(key1 = Unit) {
        backPressedDispatcher?.addCallback(
            onBackPressedCallback = OnBackPressedListener(
                navigationViewModel = navigationViewModel,
                navController = navController
            )
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
    isAudioAllowed: Boolean,
    navController: NavHostController,
    navigationUiState: NavigationUiState,
    navigationViewModel: NavigationViewModel
): Boolean {
    if (!isAudioAllowed && navigationUiState.currentDestination != Destination.PERMISSIONS_SETTINGS) {
        navigationViewModel.backToRoot(
            rootRoute = Destination.PERMISSIONS_SETTINGS,
            navController = navController
        )
        return false
    }
    return true
}