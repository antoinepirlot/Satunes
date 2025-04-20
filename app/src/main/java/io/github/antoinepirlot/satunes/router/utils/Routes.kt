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

package io.github.antoinepirlot.satunes.router.utils

import io.github.antoinepirlot.satunes.database.models.NavBarSection
import io.github.antoinepirlot.satunes.models.Destination

/**
 * @author Antoine Pirlot on 30/08/2024
 */

private val navBarSectionDestinationMap: Map<NavBarSection, Destination> = mapOf(
    Pair(first = NavBarSection.FOLDERS, second = Destination.FOLDERS),
    Pair(first = NavBarSection.ARTISTS, second = Destination.ARTISTS),
    Pair(first = NavBarSection.ALBUMS, second = Destination.ALBUMS),
    Pair(first = NavBarSection.MUSICS, second = Destination.MUSICS),
    Pair(first = NavBarSection.GENRES, second = Destination.GENRES),
    Pair(first = NavBarSection.PLAYLISTS, second = Destination.PLAYLISTS),
)

internal fun getNavBarSectionDestination(navBarSection: NavBarSection): Destination {
    return navBarSectionDestinationMap[navBarSection]!!
}