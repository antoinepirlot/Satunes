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

package io.github.antoinepirlot.satunes.database.utils

import io.github.antoinepirlot.satunes.database.models.NavBarSection
import io.github.antoinepirlot.satunes.database.services.settings.navigation_bar.NavBarSettings.DEFAULT_DEFAULT_NAV_BAR_SECTION

/**
 * @author Antoine Pirlot 03/02/2025
 */
internal fun getNavBarSection(id: Int?): NavBarSection {
    var navBarSection: NavBarSection = when (id) {
        0 -> NavBarSection.FOLDERS
        1 -> NavBarSection.ARTISTS
        2 -> NavBarSection.MUSICS
        3 -> NavBarSection.ALBUMS
        4 -> NavBarSection.GENRES
        5 -> NavBarSection.PLAYLISTS
        else -> DEFAULT_DEFAULT_NAV_BAR_SECTION
    }
    if (!navBarSection.isEnabled.value) {
        navBarSection = DEFAULT_DEFAULT_NAV_BAR_SECTION
    }
    return navBarSection
}