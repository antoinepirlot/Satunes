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

import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.icons.SatunesIcons
import io.github.antoinepirlot.satunes.models.Destination
import io.github.antoinepirlot.satunes.ui.components.buttons.settings.SettingButton
import io.github.antoinepirlot.satunes.ui.components.texts.Title
import io.github.antoinepirlot.satunes.database.R as RDb

/**
 * @author Antoine Pirlot on 02/03/24
 */

@Composable
internal fun SettingsView(
    modifier: Modifier = Modifier,
    navController: NavHostController,
) {
    val scrollState = rememberScrollState()
    Column(modifier = modifier.verticalScroll(state = scrollState)) {
        Title(text = stringResource(id = R.string.settings))
        HorizontalDivider()
        Column {
            SettingButton(
                text = "Android Auto",
                icon = SatunesIcons.ANDROID_AUTO,
                onClick = {
                    navController.navigate(Destination.ANDROID_AUTO_SETTINGS.link)
                }
            )
            SettingButton(
                text = stringResource(id = R.string.bottom_bar),
                icon = SatunesIcons.BOTTOM_BAR,
                onClick = {
                    navController.navigate(Destination.BOTTOM_BAR_SETTINGS.link)
                }
            )
            SettingButton(
                text = stringResource(id = R.string.playback_settings),
                icon = SatunesIcons.PLAYBACK,
                onClick = {
                    navController.navigate(Destination.PLAYBACK_SETTINGS.link)
                }
            )
            SettingButton(
                text = stringResource(id = R.string.search_setting_title),
                icon = SatunesIcons.SEARCH,
                onClick = {
                    navController.navigate(Destination.SEARCH_SETTINGS.link)
                }
            )
            SettingButton(
                text = stringResource(id = R.string.battery_settings),
                icon = SatunesIcons.BATTERY,
                onClick = {
                    navController.navigate(Destination.BATTERY_SETTINGS.link)
                }
            )
            SettingButton(
                text = stringResource(id = R.string.exclusion_setting),
                icon = SatunesIcons.SETTING_EXCLUSION,
                onClick = {
                    navController.navigate(Destination.EXCLUSION_SETTINGS.link)
                }
            )
            SettingButton(
                text = stringResource(id = RDb.string.playlists),
                icon = SatunesIcons.PLAYLIST,
                onClick = {
                    navController.navigate(Destination.PLAYLISTS_SETTINGS.link)
                }
            )
            SettingButton(
                text = stringResource(id = R.string.permissions),
                icon = SatunesIcons.SETTING_PERMISSIONS,
                onClick = {
                    navController.navigate(Destination.PERMISSIONS_SETTINGS.link)
                }
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                SettingButton(
                    text = stringResource(id = R.string.version),
                    icon = SatunesIcons.SETTING_UPDATE,
                    onClick = { navController.navigate(Destination.UPDATES_SETTINGS.link) }
                )
            }

            SettingButton(
                text = stringResource(id = R.string.logs_settings),
                icon = SatunesIcons.SETTING_LOGS,
                onClick = { navController.navigate(Destination.LOGS_SETTINGS.link) }
            )

            AboutView(modifier.padding(bottom = 16.dp)) // Bottom padding for a little space
        }
    }
}

@Composable
@Preview
private fun SettingsViewPreview() {
    val navController: NavHostController = rememberNavController()
    SettingsView(navController = navController)
}