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

package io.github.antoinepirlot.satunes.models.search

import androidx.compose.runtime.Composable
import io.github.antoinepirlot.jetpack_libs.models.JetpackLibsIcons
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.ui.components.search.LocalSearchSection
import io.github.antoinepirlot.satunes.ui.components.search.SubsonicSearchSection

/**
 * Search section to indicate where to search.
 * The order in enum is reflected on screen
 * @author Antoine Pirlot 13/12/2025
 */
enum class SearchSection(
    val stringId: Int,
    val icon: JetpackLibsIcons,
    val composable: @Composable () -> Unit
) {
    LOCAL(
        stringId = R.string.local_section_text,
        icon = JetpackLibsIcons.LOCAL_ICON,
        composable = { LocalSearchSection() }),
    SUBSONIC(
        stringId = R.string.subsonic_section_text,
        icon = JetpackLibsIcons.CLOUD_ON_ICON,
        composable = { SubsonicSearchSection() })
}