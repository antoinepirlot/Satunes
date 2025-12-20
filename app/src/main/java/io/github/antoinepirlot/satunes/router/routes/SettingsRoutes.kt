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

package io.github.antoinepirlot.satunes.router.routes

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.github.antoinepirlot.satunes.models.Destination
import io.github.antoinepirlot.satunes.ui.views.settings.SettingsView
import io.github.antoinepirlot.satunes.ui.views.settings.android_auto.AndroidAutoSettingsView
import io.github.antoinepirlot.satunes.ui.views.settings.battery.BatterySettingsView
import io.github.antoinepirlot.satunes.ui.views.settings.design.DesignSettingsView
import io.github.antoinepirlot.satunes.ui.views.settings.library.LibrarySettingsView
import io.github.antoinepirlot.satunes.ui.views.settings.logs.LogsSettingsView
import io.github.antoinepirlot.satunes.ui.views.settings.permissions.PermissionsSettingsView
import io.github.antoinepirlot.satunes.ui.views.settings.playback.PlaybackSettingsView
import io.github.antoinepirlot.satunes.ui.views.settings.reset.ResetSettingsView
import io.github.antoinepirlot.satunes.ui.views.settings.search.SearchSettingsView
import io.github.antoinepirlot.satunes.ui.views.settings.subsonic.SubsonicSettingView
import io.github.antoinepirlot.satunes.ui.views.settings.updates.UpdatesSettingView

/**
 * @author Antoine Pirlot on 15/07/2024
 */

internal fun NavGraphBuilder.settingsRoutes() {
    val padding: Dp = 16.dp

    composable(Destination.SETTINGS.link) {
        SettingsView()
    }

    composable(Destination.ANDROID_AUTO_SETTINGS.link) {
        AndroidAutoSettingsView(modifier = Modifier.padding(horizontal = padding))
    }

    composable(Destination.DESIGN_SETTINGS.link) {
        DesignSettingsView(modifier = Modifier.padding(horizontal = padding))
    }

    composable(Destination.PLAYBACK_SETTINGS.link) {
        PlaybackSettingsView(modifier = Modifier.padding(horizontal = padding))
    }


        composable(Destination.UPDATES_SETTINGS.link) {
            UpdatesSettingView(modifier = Modifier.padding(horizontal = padding))
    }

    composable(Destination.LIBRARY_SETTINGS.link) {
        LibrarySettingsView() //No padding here as it cuts the row of playlists settings
    }

    composable(route = Destination.SUBSONIC_SETTINGS.link) {
        SubsonicSettingView(modifier = Modifier.padding(horizontal = padding))
    }

    composable(Destination.PERMISSIONS_SETTINGS.link) {
        // Pass it as param to fix no recomposition when permission granted
        PermissionsSettingsView(modifier = Modifier.padding(horizontal = padding))
    }

    composable(Destination.BATTERY_SETTINGS.link) {
        BatterySettingsView(modifier = Modifier.padding(horizontal = padding))
    }

    composable(Destination.SEARCH_SETTINGS.link) {
        SearchSettingsView(modifier = Modifier.padding(horizontal = padding))
    }

    composable(Destination.LOGS_SETTINGS.link) {
        LogsSettingsView(modifier = Modifier.padding(horizontal = padding))
    }

    composable(Destination.RESET_SETTINGS.link) {
        ResetSettingsView(modifier = Modifier.padding(horizontal = padding))
    }
}