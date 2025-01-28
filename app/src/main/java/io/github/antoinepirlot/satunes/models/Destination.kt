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

internal enum class Destination(
    val link: String,
    val category: DestinationCategory,
    val navBarSection: NavBarSection? = null
) {
    ALBUMS(
        link = "/albums",
        category = DestinationCategory.MEDIA,
        navBarSection = NavBarSection.ALBUMS
    ),
    ALBUM(
        link = ALBUMS.link + "/{id}",
        category = DestinationCategory.MEDIA,
        navBarSection = NavBarSection.ALBUMS
    ),
    ANDROID_AUTO_SETTINGS(link = "/android_auto_setting", category = DestinationCategory.SETTING),
    ARTISTS(
        link = "/artists",
        category = DestinationCategory.MEDIA,
        navBarSection = NavBarSection.ARTISTS
    ),
    ARTIST(
        link = ARTISTS.link + "/{id}",
        category = DestinationCategory.MEDIA,
        navBarSection = NavBarSection.ARTISTS
    ),
    BATTERY_SETTINGS(link = "/battery_settings", category = DestinationCategory.SETTING),
    BOTTOM_BAR_SETTINGS(link = "/navbar_settings", category = DestinationCategory.SETTING),
    LIBRARY_SETTINGS(link = "/library_settings", category = DestinationCategory.SETTING),
    FOLDERS(
        link = "/folders",
        category = DestinationCategory.MEDIA,
        navBarSection = NavBarSection.FOLDERS
    ),
    FOLDER(
        link = FOLDERS.link + "/{id}",
        category = DestinationCategory.MEDIA,
        navBarSection = NavBarSection.FOLDERS
    ),
    GENRES(
        link = "/genres",
        category = DestinationCategory.MEDIA,
        navBarSection = NavBarSection.GENRES
    ),
    GENRE(
        link = GENRES.link + "/{id}",
        category = DestinationCategory.MEDIA,
        navBarSection = NavBarSection.GENRES
    ),
    LOGS_SETTINGS(link = "/logs_settings", category = DestinationCategory.SETTING),
    MUSICS(
        link = "/musics",
        category = DestinationCategory.MEDIA,
        navBarSection = NavBarSection.MUSICS
    ),
    PERMISSIONS_SETTINGS(link = "/permissions_settings", category = DestinationCategory.SETTING),
    PLAYBACK(link = "/playback", category = DestinationCategory.PLAYBACK),
    PLAYBACK_QUEUE(link = "/playback_queue", category = DestinationCategory.PLAYBACK),
    PLAYBACK_SETTINGS(link = "/playback_settings", category = DestinationCategory.SETTING),
    PLAYLISTS(
        link = "/playlists",
        category = DestinationCategory.MEDIA,
        navBarSection = NavBarSection.PLAYLISTS
    ),
    PLAYLIST(
        link = PLAYLISTS.link + "/{id}",
        category = DestinationCategory.MEDIA,
        navBarSection = NavBarSection.PLAYLISTS
    ),
    RESET_SETTINGS(link = "/reset_settings", category = DestinationCategory.SETTING),
    SEARCH(link = "/search", category = DestinationCategory.MEDIA),
    SEARCH_SETTINGS("/search_settings", category = DestinationCategory.SETTING),
    SETTINGS(link = "/settings", category = DestinationCategory.SETTING),

    @RequiresApi(Build.VERSION_CODES.M)
    UPDATES_SETTINGS(link = "/updates", category = DestinationCategory.SETTING);

    companion object {
        private val destinationsMap: MutableMap<String, Destination> = mutableMapOf(
            Pair(first = ALBUMS.link, second = ALBUMS),
            Pair(first = ALBUM.link, second = ALBUM),
            Pair(first = ANDROID_AUTO_SETTINGS.link, second = ANDROID_AUTO_SETTINGS),
            Pair(first = ARTISTS.link, second = ARTISTS),
            Pair(first = ARTIST.link, second = ARTIST),
            Pair(first = BATTERY_SETTINGS.link, second = BATTERY_SETTINGS),
            Pair(first = BOTTOM_BAR_SETTINGS.link, second = BOTTOM_BAR_SETTINGS),
            Pair(first = LIBRARY_SETTINGS.link, second = LIBRARY_SETTINGS),
            Pair(first = FOLDERS.link, second = FOLDERS),
            Pair(first = FOLDER.link, second = FOLDER),
            Pair(first = GENRES.link, second = GENRES),
            Pair(first = GENRE.link, second = GENRE),
            Pair(first = LOGS_SETTINGS.link, second = LOGS_SETTINGS),
            Pair(first = MUSICS.link, second = MUSICS),
            Pair(first = PERMISSIONS_SETTINGS.link, second = PERMISSIONS_SETTINGS),
            Pair(first = PLAYBACK.link, second = PLAYBACK),
            Pair(first = PLAYBACK_QUEUE.link, second = PLAYBACK_QUEUE),
            Pair(first = PLAYBACK_SETTINGS.link, second = PLAYBACK_SETTINGS),
            Pair(first = PLAYLISTS.link, second = PLAYLISTS),
            Pair(first = PLAYLIST.link, second = PLAYLIST),
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
            return this.destinationsMap[destination]
                ?: getNavBarSectionDestination(navBarSection = SettingsManager.defaultNavBarSection)
        }
    }
}