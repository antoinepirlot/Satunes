/*
 * This file is part of Satunes.
 *
 * Satunes is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * Satunes is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License along with Satunes.
 *
 * If not, see <https://www.gnu.org/licenses/>.
 *
 * **** INFORMATION ABOUT THE AUTHOR *****
 * The author of this file is Antoine Pirlot, the owner of this project.
 * You find this original project on Codeberg.
 *
 * My Codeberg link is: https://codeberg.org/antoinepirlot
 * This current project's link is: https://codeberg.org/antoinepirlot/Satunes
 */

package io.github.antoinepirlot.satunes.models

import android.annotation.SuppressLint
import android.os.Build
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.icons.SatunesIcons

/**
 * @author Antoine Pirlot 23/09/2025
 */

@SuppressLint("NewApi")
enum class SettingsViews(
    val stringId: Int,
    val icon: SatunesIcons,
    val destination: Destination,
    val minSdk: Int = -1
) {

    // /!\ WARNING, THE ORDER HERE IS REFLECTED ON THE SCREEN. /!\
    ANDROID_AUTO(
        stringId = R.string.android_auto_title,
        icon = SatunesIcons.ANDROID_AUTO,
        destination = Destination.ANDROID_AUTO_SETTINGS
    ),
    INTERFACE(
        stringId = R.string.design_setting_title,
        icon = SatunesIcons.SETTING_INTERFACE,
        destination = Destination.DESIGN_SETTINGS
    ),
    PLAYBACK(
        stringId = R.string.playback_settings,
        icon = SatunesIcons.PLAYBACK,
        destination = Destination.PLAYBACK
    ),
    SEARCH(
        stringId = R.string.search_setting_title,
        icon = SatunesIcons.SEARCH,
        destination = Destination.SEARCH_SETTINGS
    ),
    BATTERY(
        stringId = R.string.battery_settings,
        icon = SatunesIcons.BATTERY,
        destination = Destination.BATTERY_SETTINGS
    ),
    LIBRARY(
        stringId = R.string.library_settings,
        icon = SatunesIcons.SETTING_LIBRARY,
        destination = Destination.LIBRARY_SETTINGS
    ),
    SUBSONIC(
        stringId = R.string.subsonic_title,
        icon = SatunesIcons.SETTING_SUBSONIC,
        destination = Destination.SUBSONIC_SETTINGS,
        minSdk = Build.VERSION_CODES.M
    ),
    PERMISSIONS(
        stringId = R.string.permissions,
        icon = SatunesIcons.SETTING_PERMISSIONS,
        destination = Destination.PERMISSIONS_SETTINGS
    ),
    UPDATE(
        stringId = R.string.version,
        icon = SatunesIcons.SETTING_UPDATE,
        destination = Destination.UPDATES_SETTINGS,
        minSdk = Build.VERSION_CODES.M
    ),
    RESET(
        stringId = R.string.reset_settings,
        icon = SatunesIcons.RESET_SETTINGS,
        destination = Destination.RESET_SETTINGS
    ),
    LOGS(
        stringId = R.string.logs_settings,
        icon = SatunesIcons.SETTING_LOGS,
        destination = Destination.LOGS_SETTINGS
    )
}