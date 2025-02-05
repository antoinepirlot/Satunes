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

package io.github.antoinepirlot.satunes.ui.components.settings.design.navigation_bar

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.database.models.NavBarSection
import io.github.antoinepirlot.satunes.models.SwitchSettings
import io.github.antoinepirlot.satunes.ui.components.settings.SettingsSwitchList
import io.github.antoinepirlot.satunes.ui.components.settings.SubSettings

/**
 * @author Antoine Pirlot 05/02/2025
 */

private val navBarSectionSettingsChecked: Map<SwitchSettings, NavBarSection> = mapOf(
    Pair(first = SwitchSettings.FOLDERS_NAVBAR, second = NavBarSection.FOLDERS),
    Pair(first = SwitchSettings.ARTISTS_NAVBAR, second = NavBarSection.ARTISTS),
    Pair(first = SwitchSettings.ALBUMS_NAVBAR, second = NavBarSection.ALBUMS),
    Pair(first = SwitchSettings.GENRES_NAVBAR, second = NavBarSection.GENRES),
    Pair(first = SwitchSettings.PLAYLISTS_NAVBAR, second = NavBarSection.PLAYLISTS)
)

@Composable
fun NavigationBarSubSettings(modifier: Modifier = Modifier) {
    SubSettings(
        modifier = modifier,
        title = stringResource(id = R.string.navigation_bar_settings_title)
    ) {
        SettingsSwitchList(navbarMap = navBarSectionSettingsChecked)
        DefaultNavBarSectionSetting(modifier = Modifier.padding(horizontal = 16.dp))
    }
}

@Preview
@Composable
private fun NavigationBarSubSettingsPreview(modifier: Modifier = Modifier) {
    NavigationBarSubSettings()
}