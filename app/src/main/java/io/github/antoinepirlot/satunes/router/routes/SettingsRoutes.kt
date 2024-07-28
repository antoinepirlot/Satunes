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

package io.github.antoinepirlot.satunes.router.routes

import android.os.Build
import androidx.compose.animation.AnimatedContentScope
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import io.github.antoinepirlot.satunes.models.Destination
import io.github.antoinepirlot.satunes.ui.viewmodels.SatunesViewModel
import io.github.antoinepirlot.satunes.ui.views.settings.AndroidAutoSettingsView
import io.github.antoinepirlot.satunes.ui.views.settings.BatterySettingsView
import io.github.antoinepirlot.satunes.ui.views.settings.BottomNavigationBarSettingsView
import io.github.antoinepirlot.satunes.ui.views.settings.ExclusionSettingsView
import io.github.antoinepirlot.satunes.ui.views.settings.PermissionsSettingsView
import io.github.antoinepirlot.satunes.ui.views.settings.PlaybackSettingsView
import io.github.antoinepirlot.satunes.ui.views.settings.PlaylistsSettingsView
import io.github.antoinepirlot.satunes.ui.views.settings.SearchSettingsView
import io.github.antoinepirlot.satunes.ui.views.settings.SettingsView
import io.github.antoinepirlot.satunes.ui.views.settings.UpdatesSettingView
import io.github.antoinepirlot.satunes.ui.views.settings.logs.LogsSettingsView

/**
 * @author Antoine Pirlot on 15/07/2024
 */

internal fun NavGraphBuilder.settingsRoutes(
    navController: NavHostController,
    satunesViewModel: SatunesViewModel, // Pass it as param to fix no recomposition when permission granted
    onStart: AnimatedContentScope.(NavBackStackEntry) -> Unit
) {
    composable(Destination.SETTINGS.link) {
        onStart(it)
        SettingsView(navController = navController)
    }

    composable(Destination.BOTTOM_BAR_SETTINGS.link) {
        onStart(it)
        BottomNavigationBarSettingsView()
    }

    composable(Destination.PLAYBACK_SETTINGS.link) {
        onStart(it)
        PlaybackSettingsView()
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        composable(Destination.UPDATES_SETTINGS.link) {
            onStart(it)
            UpdatesSettingView()
        }
    }

    composable(Destination.EXCLUSION_SETTINGS.link) {
        onStart(it)
        ExclusionSettingsView()
    }

    composable(Destination.PLAYLISTS_SETTINGS.link) {
        onStart(it)
        PlaylistsSettingsView()
    }

    composable(Destination.PERMISSIONS_SETTINGS.link) {
        onStart(it)
        // Pass it as param to fix no recomposition when permission granted
        PermissionsSettingsView(satunesViewModel = satunesViewModel)
    }

    composable(Destination.ANDROID_AUTO_SETTINGS.link) {
        onStart(it)
        AndroidAutoSettingsView()
    }

    composable(Destination.BATTERY_SETTINGS.link) {
        onStart(it)
        BatterySettingsView()
    }

    composable(Destination.SEARCH_SETTINGS.link) {
        onStart(it)
        SearchSettingsView()
    }

    composable(Destination.LOGS_SETTINGS.link) {
        onStart(it)
        LogsSettingsView()
    }
}