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

package io.github.antoinepirlot.satunes.ui.components.bars.top

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Row
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
import io.github.antoinepirlot.jetpack_libs.components.buttons.IconButton
import io.github.antoinepirlot.jetpack_libs.components.models.ScreenSizes
import io.github.antoinepirlot.jetpack_libs.components.texts.NormalText
import io.github.antoinepirlot.jetpack_libs.models.JetpackLibsIcons
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.data.getSortOptions
import io.github.antoinepirlot.satunes.data.local.LocalNavController
import io.github.antoinepirlot.satunes.data.states.NavigationUiState
import io.github.antoinepirlot.satunes.data.viewmodels.DataViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.NavigationViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.SatunesViewModel
import io.github.antoinepirlot.satunes.models.Destination
import io.github.antoinepirlot.satunes.models.DestinationCategory
import io.github.antoinepirlot.satunes.models.SatunesModes

/**
 * @author Antoine Pirlot on 16/01/24
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TopAppBar(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior,
    satunesViewModel: SatunesViewModel = viewModel(),
    navigationViewModel: NavigationViewModel = viewModel(),
    dataViewModel: DataViewModel = viewModel(),
) {
    val navigationUiState: NavigationUiState by navigationViewModel.uiState.collectAsState()
    val navController: NavHostController = LocalNavController.current
    val screenWidthDp = LocalConfiguration.current.screenWidthDp
    val barModifier: Modifier =
        if (screenWidthDp < ScreenSizes.VERY_VERY_SMALL) modifier.fillMaxHeight(0.11f) else modifier
    val currentDestination: Destination = navigationUiState.currentDestination

    CenterAlignedTopAppBar(
        modifier = barModifier,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        navigationIcon = {
            Row {
                val screenWidth: Int = LocalConfiguration.current.screenWidthDp
                if (
                    currentDestination.category == DestinationCategory.PLAYBACK &&
                    screenWidth < ScreenSizes.LARGE
                ) {
                    IconButton(
                        jetpackLibsIcons = JetpackLibsIcons.PLAYBACK,
                        onClick = {
                            onPlaybackQueueButtonClick(
                                uiState = navigationUiState,
                                navController = navController,
                            navigationViewModel = navigationViewModel
                        )
                    }
                )
            } else if (
                currentDestination.category == DestinationCategory.MEDIA &&
                getSortOptions(destination = navigationUiState.currentDestination).size > 1
                ) {
                    IconButton(
                        jetpackLibsIcons = JetpackLibsIcons.SORT,
                        onClick = { satunesViewModel.showSortDialog() }
                    )
                }
                val mode: SatunesModes = satunesUiState.mode
                IconButton(
                    icon = mode.icon,
                    onClick = { satunesViewModel.switchCloudMode(dataViewModel = dataViewModel) }
                )
            }
        },
        title = {
            NormalText(
                text = "🇺🇦 " + stringResource(id = R.string.app_name) + " 🇪🇺",
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )
        },
        actions = {
            if (currentDestination.category != DestinationCategory.SETTING) {
                // Search Button
                IconButton(
                    jetpackLibsIcons = JetpackLibsIcons.SEARCH,
                    onClick = {
                        onSearchButtonClick(
                            uiState = navigationUiState,
                            navController = navController,
                            navigationViewModel = navigationViewModel
                        )
                    }
                )
            }

            //Setting Button
            IconButton(
                jetpackLibsIcons = JetpackLibsIcons.SETTINGS,
                onClick = {
                    onSettingButtonClick(
                        uiState = navigationUiState,
                        satunesViewModel = satunesViewModel,
                        navController = navController,
                        navigationViewModel = navigationViewModel
                    )
                }
            )
        },
        scrollBehavior = scrollBehavior,
    )
}

private fun onSearchButtonClick(
    uiState: NavigationUiState,
    navController: NavHostController,
    navigationViewModel: NavigationViewModel
) {
    when (uiState.currentDestination) {
        Destination.SEARCH -> navigationViewModel.popBackStack(navController = navController)
        else -> navigationViewModel.navigate(
            navController = navController,
            destination = Destination.SEARCH
        )
    }
}

private fun onPlaybackQueueButtonClick(
    uiState: NavigationUiState,
    navController: NavHostController,
    navigationViewModel: NavigationViewModel
) {
    when (uiState.currentDestination) {
        Destination.PLAYBACK -> navigationViewModel.navigate(
            navController = navController,
            destination = Destination.PLAYBACK_QUEUE
        )
        Destination.PLAYBACK_QUEUE -> navigationViewModel.popBackStack(navController = navController)
        else -> throw UnsupportedOperationException("Not available when current destination is: ${uiState.currentDestination}")
    }
}

/**
 * When currentDestination is the settings list, then return to app only if audio permission has been allowed.
 * Otherwise navigate to settings
 */
private fun onSettingButtonClick(
    uiState: NavigationUiState,
    navController: NavHostController,
    satunesViewModel: SatunesViewModel,
    navigationViewModel: NavigationViewModel
) {
    satunesViewModel.resetUpdatesStatus()
    val currentDestination: Destination = uiState.currentDestination
    if (currentDestination.category == DestinationCategory.SETTING) {
        navigationViewModel.popBackStack(navController = navController)
        if (navController.currentBackStackEntry == null) {
            navigationViewModel.navigate(
                navController = navController,
                destination = Destination.FOLDERS
            )
            navigationViewModel.navigate(
                navController = navController,
                destination = Destination.SETTINGS
            )
        }
    } else {
        navigationViewModel.navigate(
            navController = navController,
            destination = Destination.SETTINGS
        )
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