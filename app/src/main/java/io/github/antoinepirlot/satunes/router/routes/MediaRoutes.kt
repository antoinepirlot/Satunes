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
 * You should have received a copy of the GNU General Public License along with Satunes.
 *
 * If not, see <https://www.gnu.org/licenses/>.
 *
 * *** INFORMATION ABOUT THE AUTHOR *****
 * The author of this file is Antoine Pirlot, the owner of this project.
 * You find this original project on Codeberg.
 *
 * My Codeberg link is: https://codeberg.org/antoinepirlot
 * This current project's link is: https://codeberg.org/antoinepirlot/Satunes
 */

package io.github.antoinepirlot.satunes.router.routes

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import io.github.antoinepirlot.satunes.data.viewmodels.DataViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.NavigationViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.SatunesViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.SubsonicViewModel
import io.github.antoinepirlot.satunes.database.models.media.Media
import io.github.antoinepirlot.satunes.router.routes.subsonic.subsonicMediaRoutes

/**
 * @author Antoine Pirlot 17/12/2025
 */
internal fun NavGraphBuilder.mediaRoutes(
    satunesViewModel: SatunesViewModel,
    dataViewModel: DataViewModel,
    subsonicViewModel: SubsonicViewModel,
    navigationViewModel: NavigationViewModel,
    onStart: (NavBackStackEntry) -> Unit,
    onMediaOpen: (media: Media) -> Unit
) {
    localMediaRoutes(
        satunesViewModel = satunesViewModel,
        dataViewModel = dataViewModel,
        onStart = onStart,
        onMediaOpen = onMediaOpen,
    )

    subsonicMediaRoutes(
        subsonicViewModel = subsonicViewModel,
        navigationViewModel = navigationViewModel,
        dataViewModel = dataViewModel,
        onStart = onStart,
        onMediaOpen = onMediaOpen,
    )
}