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

package io.github.antoinepirlot.satunes.ui.components.bars

import android.annotation.SuppressLint
import android.os.Build
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import io.github.antoinepirlot.jetpack_libs.components.models.ScreenSizes
import io.github.antoinepirlot.jetpack_libs.components.texts.NormalText
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.data.local.LocalNavController
import io.github.antoinepirlot.satunes.data.mediaListViews
import io.github.antoinepirlot.satunes.data.playbackViews
import io.github.antoinepirlot.satunes.data.settingsDestinations
import io.github.antoinepirlot.satunes.data.states.SatunesUiState
import io.github.antoinepirlot.satunes.data.viewmodels.SatunesViewModel
import io.github.antoinepirlot.satunes.icons.SatunesIcons
import io.github.antoinepirlot.satunes.models.Destination
import io.github.antoinepirlot.satunes.ui.components.buttons.IconButton

/**
 * @author Antoine Pirlot on 16/01/24
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TopAppBar(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior,
    satunesViewModel: SatunesViewModel = viewModel(),
) {
    val uiState: SatunesUiState by satunesViewModel.uiState.collectAsState()
    val navController: NavHostController = LocalNavController.current
    val screenWidthDp = LocalConfiguration.current.screenWidthDp
    val barModifier: Modifier =
        if (screenWidthDp < ScreenSizes.VERY_VERY_SMALL) modifier.fillMaxHeight(0.11f) else modifier
    val currentDestination: Destination = uiState.currentDestination

    CenterAlignedTopAppBar(
        modifier = barModifier,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        navigationIcon = {
            val screenWidth: Int = LocalConfiguration.current.screenWidthDp
            if (currentDestination in playbackViews && screenWidth < ScreenSizes.LARGE) {
                IconButton(
                    icon = SatunesIcons.PLAYBACK,
                    onClick = {
                        onPlaybackQueueButtonClick(
                            uiState = uiState,
                            navController = navController
                        )
                    }
                )
            } else if (currentDestination in mediaListViews) {
            }
        },
        title = {
            NormalText(
                text = stringResource(id = R.string.app_name),
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )
        },
        actions = {
            if (currentDestination !in settingsDestinations) {
                // Search Button
                IconButton(
                    icon = SatunesIcons.SEARCH,
                    onClick = {
                        onSearchButtonClick(
                            uiState = uiState,
                            navController = navController
                        )
                    }
                )
            }

            //Setting Button
            IconButton(
                icon = SatunesIcons.SETTINGS,
                onClick = {
                    onSettingButtonClick(
                        uiState = uiState,
                        satunesViewModel = satunesViewModel,
                        navController = navController
                    )
                }
            )
        },
        scrollBehavior = scrollBehavior,
    )
}

private fun onSearchButtonClick(uiState: SatunesUiState, navController: NavHostController) {
    when (uiState.currentDestination) {
        Destination.SEARCH -> navController.popBackStack()
        else -> navController.navigate(Destination.SEARCH.link)
    }
}

private fun onPlaybackQueueButtonClick(uiState: SatunesUiState, navController: NavHostController) {
    when (uiState.currentDestination) {
        Destination.PLAYBACK -> navController.navigate(Destination.PLAYBACK_QUEUE.link)
        Destination.PLAYBACK_QUEUE -> navController.popBackStack()
        else -> throw UnsupportedOperationException("Not available when current destination is: ${uiState.currentDestination}")
    }
}

/**
 * When currentDestination is the settings list, then return to app only if audio permission has been allowed.
 * Otherwise navigate to settings
 */
private fun onSettingButtonClick(
    uiState: SatunesUiState,
    navController: NavHostController,
    satunesViewModel: SatunesViewModel,
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        satunesViewModel.resetUpdatesStatus()
    }

    when (val currentDestination: Destination = uiState.currentDestination) {
        in settingsDestinations -> {
            if (currentDestination == Destination.PERMISSIONS_SETTINGS && !uiState.isAudioAllowed) {
                return
            } else {
                navController.popBackStack()
                if (navController.currentBackStackEntry == null) {
                    navController.navigate(Destination.FOLDERS.link)
                    navController.navigate(Destination.SETTINGS.link)
                }
            }
        }

        else -> navController.navigate(Destination.SETTINGS.link)
    }
}

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
private fun SatunesTopAppBarPreview() {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(
        rememberTopAppBarState()
    )
    TopAppBar(modifier = Modifier, scrollBehavior = scrollBehavior)
}