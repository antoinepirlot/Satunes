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

package earth.satunes.ui.appBars

import android.annotation.SuppressLint
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import earth.satunes.R
import earth.satunes.internet.UpdateAvailableStatus
import earth.satunes.internet.UpdateCheckManager
import earth.satunes.router.Destination
import earth.satunes.ui.views.SatunesIcons

/**
 * @author Antoine Pirlot on 16/01/24
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SatunesTopAppBar(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior,
    navController: NavHostController,
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            Text(
                stringResource(id = R.string.app_name),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        actions = {
            IconButton(
                onClick = {
                    if (UpdateCheckManager.updateAvailableStatus.value != UpdateAvailableStatus.AVAILABLE) {
                        UpdateCheckManager.updateAvailableStatus.value = UpdateAvailableStatus.UNDEFINED
                    }
                    when (navController.currentBackStackEntry!!.destination.route!!) {
                        Destination.SETTINGS.link -> navController.popBackStack()
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
fun SatunesTopAppBarPreview() {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(
        rememberTopAppBarState()
    )
    SatunesTopAppBar(
        modifier = Modifier,
        scrollBehavior = scrollBehavior,
        navController = rememberNavController(),
    )
}