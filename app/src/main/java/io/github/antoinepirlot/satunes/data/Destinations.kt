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

package io.github.antoinepirlot.satunes.data

import android.os.Build
import io.github.antoinepirlot.satunes.models.Destination

/**
 * @author Antoine Pirlot on 26/07/2024
 */

internal val settingsDestinations: List<Destination> = getSettingsDestinationsDynamically()

private fun getSettingsDestinationsDynamically(): List<Destination> {
    //It's executed once at launch
    val list: MutableList<Destination> = mutableListOf(
        Destination.SETTINGS,
        Destination.BOTTOM_BAR_SETTINGS,
        Destination.ANDROID_AUTO_SETTINGS,
        Destination.PLAYBACK_SETTINGS,
        Destination.LIBRARY_SETTINGS,
        Destination.PERMISSIONS_SETTINGS,
        Destination.SEARCH_SETTINGS,
        Destination.LOGS_SETTINGS,
        Destination.BATTERY_SETTINGS,
    )
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        list.add(element = Destination.UPDATES_SETTINGS)
    return list.toList()
}

internal val playbackViews: List<Destination> = listOf(
    Destination.PLAYBACK,
    Destination.PLAYBACK_QUEUE
)

internal val mediaListViews: List<Destination> = listOf(
    Destination.MUSICS,
    Destination.ALBUMS,
    Destination.FOLDERS,
    Destination.ARTISTS,
    Destination.GENRES,
    Destination.PLAYLISTS
)