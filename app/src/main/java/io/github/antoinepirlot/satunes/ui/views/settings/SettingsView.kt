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

package io.github.antoinepirlot.satunes.ui.views.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.router.Destination
import io.github.antoinepirlot.satunes.ui.components.buttons.ClickableListItem
import io.github.antoinepirlot.satunes.ui.components.texts.Title
import io.github.antoinepirlot.satunes.database.R as RDb

/**
 * @author Antoine Pirlot on 02/03/24
 */

@Composable
fun SettingsView(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    val scrollState = rememberScrollState()
    Column(modifier = modifier.verticalScroll(state = scrollState)) {
        Title(text = stringResource(id = R.string.settings))
        HorizontalDivider()
        Column {
            ClickableListItem(
                text = "Android Auto",
                onClick = {
                    navController.navigate(Destination.ANDROID_AUTO_SETTINGS.link)
                }
            )
            HorizontalDivider()
            ClickableListItem(
                text = stringResource(id = R.string.bottom_bar),
                onClick = {
                    navController.navigate(Destination.BOTTOM_BAR_SETTINGS.link)
                }
            )
            HorizontalDivider()
            ClickableListItem(
                text = stringResource(id = R.string.playback_settings),
                onClick = {
                    navController.navigate(Destination.PLAYBACK_SETTINGS.link)
                }
            )
            HorizontalDivider()
            ClickableListItem(
                text = stringResource(id = R.string.exclusion_setting),
                onClick = {
                    navController.navigate(Destination.EXCLUSION_SETTINGS.link)
                }
            )
            HorizontalDivider()
            ClickableListItem(
                text = stringResource(id = RDb.string.playlists) + " (Beta)",
                onClick = {
                    navController.navigate(Destination.PLAYLISTS_SETTINGS.link)
                }
            )
            HorizontalDivider()
            ClickableListItem(
                text = stringResource(id = R.string.permissions),
                onClick = {
                    navController.navigate(Destination.PERMISSIONS_SETTINGS.link)
                }
            )
            HorizontalDivider()
            ClickableListItem(
                text = stringResource(id = R.string.version),
                onClick = {
                    navController.navigate(Destination.UPDATES_SETTINGS.link)
                }
            )
            HorizontalDivider()
            AboutView()
        }
    }
}

@Composable
@Preview
fun SettingsViewPreview() {
    SettingsView(navController = rememberNavController())
}