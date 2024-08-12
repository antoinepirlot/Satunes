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

internal val DEFAULT_DESTINATION: String = Destination.FOLDERS.link

internal val settingsDestinations: List<String> =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        listOf(
            Destination.SETTINGS.link, Destination.BOTTOM_BAR_SETTINGS.link,
            Destination.ANDROID_AUTO_SETTINGS.link, Destination.PLAYBACK_SETTINGS.link,
            Destination.FOLDERS_SETTINGS.link, Destination.PLAYLISTS_SETTINGS.link,
            Destination.PERMISSIONS_SETTINGS.link, Destination.UPDATES_SETTINGS.link,
            Destination.SEARCH_SETTINGS.link, Destination.LOGS_SETTINGS.link
        )
    } else {
        // Without UPDATES_SETTINGS
        listOf(
            Destination.SETTINGS.link, Destination.BOTTOM_BAR_SETTINGS.link,
            Destination.ANDROID_AUTO_SETTINGS.link, Destination.PLAYBACK_SETTINGS.link,
            Destination.FOLDERS_SETTINGS.link, Destination.PLAYLISTS_SETTINGS.link,
            Destination.PERMISSIONS_SETTINGS.link, Destination.SEARCH_SETTINGS.link,
            Destination.LOGS_SETTINGS.link
        )
    }

internal val playbackViews: List<String> = listOf(
    Destination.PLAYBACK.link, Destination.PLAYBACK_QUEUE.link
)