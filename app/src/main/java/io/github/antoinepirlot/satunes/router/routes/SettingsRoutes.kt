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

import android.os.Build
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.github.antoinepirlot.satunes.data.viewmodels.SatunesViewModel
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

internal fun NavGraphBuilder.settingsRoutes(
    satunesViewModel: SatunesViewModel, // Pass it as param to fix no recomposition when permission granted
    onStart: AnimatedContentScope.(NavBackStackEntry) -> Unit
) {
    composable(Destination.SETTINGS.link) {
        LaunchedEffect(key1 = Unit) {
            onStart(it)
        }
        SettingsView()
    }

    composable(Destination.ANDROID_AUTO_SETTINGS.link) {
        LaunchedEffect(key1 = Unit) {
            onStart(it)
        }
        AndroidAutoSettingsView(modifier = Modifier.padding(horizontal = 16.dp))
    }

    composable(Destination.DESIGN_SETTINGS.link) {
        LaunchedEffect(key1 = Unit) {
            onStart(it)
        }
        DesignSettingsView(modifier = Modifier.padding(horizontal = 16.dp))
    }

    composable(Destination.PLAYBACK_SETTINGS.link) {
        LaunchedEffect(key1 = Unit) {
            onStart(it)
        }
        PlaybackSettingsView(modifier = Modifier.padding(horizontal = 16.dp))
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        composable(Destination.UPDATES_SETTINGS.link) {
            LaunchedEffect(key1 = Unit) {
                onStart(it)
            }
            UpdatesSettingView(modifier = Modifier.padding(horizontal = 16.dp))
        }
    }

    composable(Destination.LIBRARY_SETTINGS.link) {
        LaunchedEffect(key1 = Unit) {
            onStart(it)
        }
        LibrarySettingsView() //No padding here as it cuts the row of playlists settings
    }

    composable(route = Destination.SUBSONIC_SETTINGS.link) {
        LaunchedEffect(key1 = Unit) {
            onStart(it)
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            throw IllegalStateException("Wrong version to access Subsonic Settings")
        SubsonicSettingView()
    }

    composable(Destination.PERMISSIONS_SETTINGS.link) {
        LaunchedEffect(key1 = Unit) {
            onStart(it)
        }
        // Pass it as param to fix no recomposition when permission granted
        PermissionsSettingsView(modifier = Modifier.padding(horizontal = 16.dp))
    }

    composable(Destination.BATTERY_SETTINGS.link) {
        LaunchedEffect(key1 = Unit) {
            onStart(it)
        }
        BatterySettingsView(modifier = Modifier.padding(horizontal = 16.dp))
    }

    composable(Destination.SEARCH_SETTINGS.link) {
        LaunchedEffect(key1 = Unit) {
            onStart(it)
        }
        SearchSettingsView(modifier = Modifier.padding(horizontal = 16.dp))
    }

    composable(Destination.LOGS_SETTINGS.link) {
        LaunchedEffect(key1 = Unit) {
            onStart(it)
        }
        LogsSettingsView(modifier = Modifier.padding(horizontal = 16.dp))
    }

    composable(Destination.RESET_SETTINGS.link) {
        LaunchedEffect(key1 = Unit) {
            onStart(it)
        }
        ResetSettingsView(modifier = Modifier.padding(horizontal = 16.dp))
    }
}