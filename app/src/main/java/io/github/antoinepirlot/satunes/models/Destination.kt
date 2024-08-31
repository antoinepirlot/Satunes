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

package io.github.antoinepirlot.satunes.models

import android.os.Build
import androidx.annotation.RequiresApi

/**
 * @author Antoine Pirlot on 24-01-24
 */

internal enum class Destination(val link: String) {
    ALBUMS(link = "/albums"),
    ANDROID_AUTO_SETTINGS(link = "/android_auto_setting"),
    ARTISTS(link = "/artists"),
    BATTERY_SETTINGS(link = "/battery_settings"),
    BOTTOM_BAR_SETTINGS(link = "/navbar_settings"),
    LIBRARY_SETTINGS(link = "/library_settings"),
    FOLDERS(link = "/folders"),
    GENRES(link = "/genres"),
    LOGS_SETTINGS(link = "/logs_settings"),
    MUSICS(link = "/musics"),
    PERMISSIONS_SETTINGS(link = "/permissions_settings"),
    PLAYBACK(link = "/playback"),
    PLAYBACK_QUEUE(link = "/playback_queue"),
    PLAYBACK_SETTINGS(link = "/playback_settings"),
    PLAYLISTS(link = "/playlists"),
    PLAYLISTS_SETTINGS(link = "/playlists_settings"),
    SEARCH(link = "/search"),
    SEARCH_SETTINGS("/search_settings"),
    SETTINGS(link = "/settings"),

    @RequiresApi(Build.VERSION_CODES.M)
    UPDATES_SETTINGS(link = "/updates"),
}