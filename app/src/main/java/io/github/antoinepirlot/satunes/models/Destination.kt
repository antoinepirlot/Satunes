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

package io.github.antoinepirlot.satunes.models

import android.os.Build
import androidx.annotation.RequiresApi
import io.github.antoinepirlot.satunes.database.models.NavBarSection
import io.github.antoinepirlot.satunes.database.services.settings.SettingsManager
import io.github.antoinepirlot.satunes.router.utils.getNavBarSectionDestination

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
    RESET_SETTINGS(link = "/reset_settings"),
    SEARCH(link = "/search"),
    SEARCH_SETTINGS("/search_settings"),
    SETTINGS(link = "/settings"),

    @RequiresApi(Build.VERSION_CODES.M)
    UPDATES_SETTINGS(link = "/updates");

    companion object {
        private val destinationsMap: MutableMap<String, Destination> = mutableMapOf(
            Pair(first = ALBUMS.link, second = ALBUMS),
            Pair(first = ANDROID_AUTO_SETTINGS.link, second = ANDROID_AUTO_SETTINGS),
            Pair(first = ARTISTS.link, second = ARTISTS),
            Pair(first = BATTERY_SETTINGS.link, second = BATTERY_SETTINGS),
            Pair(first = BOTTOM_BAR_SETTINGS.link, second = BOTTOM_BAR_SETTINGS),
            Pair(first = LIBRARY_SETTINGS.link, second = LIBRARY_SETTINGS),
            Pair(first = FOLDERS.link, second = FOLDERS),
            Pair(first = GENRES.link, second = GENRES),
            Pair(first = LOGS_SETTINGS.link, second = LOGS_SETTINGS),
            Pair(first = MUSICS.link, second = MUSICS),
            Pair(first = PERMISSIONS_SETTINGS.link, second = PERMISSIONS_SETTINGS),
            Pair(first = PLAYBACK.link, second = PLAYBACK),
            Pair(first = PLAYBACK_QUEUE.link, second = PLAYBACK_QUEUE),
            Pair(first = PLAYBACK_SETTINGS.link, second = PLAYBACK_SETTINGS),
            Pair(first = PLAYLISTS.link, second = PLAYLISTS),
            Pair(first = RESET_SETTINGS.link, second = RESET_SETTINGS),
            Pair(first = SEARCH.link, second = SEARCH),
            Pair(first = SEARCH_SETTINGS.link, second = SEARCH_SETTINGS),
            Pair(first = SETTINGS.link, second = SETTINGS),
        )

        val mediaMainRoutesMap: Map<Destination, NavBarSection> = mapOf(
            Pair(first = FOLDERS, second = NavBarSection.FOLDERS),
            Pair(first = ARTISTS, second = NavBarSection.ARTISTS),
            Pair(first = ALBUMS, second = NavBarSection.ALBUMS),
            Pair(first = GENRES, second = NavBarSection.GENRES),
            Pair(first = MUSICS, second = NavBarSection.MUSICS),
            Pair(first = PLAYLISTS, second = NavBarSection.PLAYLISTS)
        )

        init {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                this.destinationsMap[UPDATES_SETTINGS.link] = UPDATES_SETTINGS
            }
        }

        fun getDestination(destination: String): Destination {
            return this.destinationsMap["/${destination.split("/")[1]}"] // don't care of id
                ?: getNavBarSectionDestination(navBarSection = SettingsManager.defaultNavBarSection)
        }
    }
}