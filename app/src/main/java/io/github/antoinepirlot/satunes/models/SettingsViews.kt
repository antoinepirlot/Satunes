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
import io.github.antoinepirlot.jetpack_libs.models.JetpackLibsIcons
import io.github.antoinepirlot.satunes.R

/**
 * @author Antoine Pirlot 23/09/2025
 */

@SuppressLint("NewApi")
enum class SettingsViews(
    val stringId: Int,
    val icon: JetpackLibsIcons,
    val destination: Destination,
    val minSdk: Int = -1
) {

    // /!\ WARNING, THE ORDER HERE IS REFLECTED ON THE SCREEN. /!\
    ANDROID_AUTO(
        stringId = R.string.android_auto_title,
        icon = JetpackLibsIcons.ANDROID_AUTO,
        destination = Destination.ANDROID_AUTO_SETTINGS
    ),
    INTERFACE(
        stringId = R.string.design_setting_title,
        icon = JetpackLibsIcons.SETTING_INTERFACE,
        destination = Destination.DESIGN_SETTINGS
    ),
    PLAYBACK(
        stringId = R.string.playback_settings,
        icon = JetpackLibsIcons.PLAYBACK,
        destination = Destination.PLAYBACK
    ),
    SEARCH(
        stringId = R.string.search_setting_title,
        icon = JetpackLibsIcons.SEARCH,
        destination = Destination.SEARCH_SETTINGS
    ),
    BATTERY(
        stringId = R.string.battery_settings,
        icon = JetpackLibsIcons.BATTERY,
        destination = Destination.BATTERY_SETTINGS
    ),
    LIBRARY(
        stringId = R.string.library_settings,
        icon = JetpackLibsIcons.SETTING_LIBRARY,
        destination = Destination.LIBRARY_SETTINGS
    ),
    SUBSONIC(
        stringId = R.string.subsonic_title,
        icon = JetpackLibsIcons.SETTING_CLOUD,
        destination = Destination.SUBSONIC_SETTINGS,
    ),
    PERMISSIONS(
        stringId = R.string.permissions,
        icon = JetpackLibsIcons.SETTING_PERMISSIONS,
        destination = Destination.PERMISSIONS_SETTINGS
    ),
    UPDATE(
        stringId = R.string.version,
        icon = JetpackLibsIcons.SETTING_UPDATE,
        destination = Destination.UPDATES_SETTINGS,
    ),
    RESET(
        stringId = R.string.reset_settings,
        icon = JetpackLibsIcons.RESET_SETTINGS,
        destination = Destination.RESET_SETTINGS
    ),
    LOGS(
        stringId = R.string.logs_settings,
        icon = JetpackLibsIcons.SETTING_LOGS,
        destination = Destination.LOGS_SETTINGS
    )
}