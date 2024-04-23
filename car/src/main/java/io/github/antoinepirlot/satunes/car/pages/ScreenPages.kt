/*
 * This file is part of Satunes.
 *
 * Satunes is free software: you can redistribute it and/or modify it under
 *  the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * Satunes is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Satunes.
 * If not, see <https://www.gnu.org/licenses/>.
 *
 * **** INFORMATIONS ABOUT THE AUTHOR *****
 * The author of this file is Antoine Pirlot, the owner of this project.
 * You find this original project on github.
 *
 * My github link is: https://github.com/antoinepirlot
 * This current project's link is: https://github.com/antoinepirlot/MP3-Player
 *
 * You can contact me via my email: pirlot.antoine@outlook.com
 * PS: I don't answer quickly.
 */

package io.github.antoinepirlot.satunes.car.pages

/**
 * @author Antoine Pirlot on 16/03/2024
 */

enum class ScreenPages(
    val id: String,
    val description: String,
    val title: String,
) {
    ROOT(id = "root", description = "root page", title = "Root"),
    ALL_FOLDERS(id = "folders", description = "List of all folders", title = "FOLDERS"),
    ALL_ARTISTS(id = "artists", description = "List of all artists", title = "ARTISTS"),
    ALL_ALBUMS(id = "albums", description = "List of all albums", title = "ALBUMS"),
    ALL_GENRES(id = "genres", description = "List of all genres", title = "GENRES"),
    ALL_MUSICS(id = "musics", description = "List of all musics", title = "MUSICS"),
    ALL_PLAYLISTS(id = "playlists", description = "List of all playlists", title = "PLAYLISTS")
}

/**
 *  Tabs showed in Android Auto are in the same order as this list, change this list order also
 *  affect the order in Android Auto
 */
internal val pages: List<ScreenPages> = listOf(
    ScreenPages.ALL_PLAYLISTS,
    ScreenPages.ALL_FOLDERS,
    ScreenPages.ALL_MUSICS,
    ScreenPages.ALL_ARTISTS,
    ScreenPages.ALL_ALBUMS,
    ScreenPages.ALL_GENRES,
)