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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import io.github.antoinepirlot.satunes.MainActivity
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.icons.SatunesIcons
import io.github.antoinepirlot.satunes.internet.updates.UpdateAvailableStatus
import io.github.antoinepirlot.satunes.internet.updates.UpdateCheckManager
import io.github.antoinepirlot.satunes.navController
import io.github.antoinepirlot.satunes.router.Destination
import io.github.antoinepirlot.satunes.router.settingsDestinations
import io.github.antoinepirlot.satunes.ui.ScreenSizes

/**
 * @author Antoine Pirlot on 16/01/24
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SatunesTopAppBar(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    val screenWidthDp = LocalConfiguration.current.screenWidthDp
    val barModifier: Modifier =
        if (screenWidthDp <= ScreenSizes.VERY_SMALL) modifier.fillMaxHeight(0.11f) else modifier
    CenterAlignedTopAppBar(
        modifier = barModifier,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            Text(
                text = stringResource(id = R.string.app_name),
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )
        },
        actions = {
            IconButton(
                onClick = {
                    if (UpdateCheckManager.updateAvailableStatus.value != UpdateAvailableStatus.AVAILABLE) {
                        UpdateCheckManager.updateAvailableStatus.value =
                            UpdateAvailableStatus.UNDEFINED
                    }

                    val currentDestination: String =
                        navController.currentBackStackEntry!!.destination.route!!
                    when (currentDestination) {
                        in settingsDestinations -> {
                            if (currentDestination == Destination.PERMISSIONS_SETTINGS.link
                                && !MainActivity.instance.isAudioAllowed()
                            ) {
                                return@IconButton
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
            ) {
                val settingsIcon: SatunesIcons = SatunesIcons.SETTINGS
                Icon(
                    imageVector = settingsIcon.imageVector,
                    contentDescription = settingsIcon.description
                )
            }
        },
        scrollBehavior = scrollBehavior,
    )
}

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
private fun SatunesTopAppBarPreview() {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(
        rememberTopAppBarState()
    )
    SatunesTopAppBar(
        modifier = Modifier,
        scrollBehavior = scrollBehavior,
    )
}