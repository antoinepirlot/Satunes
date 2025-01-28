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

package io.github.antoinepirlot.satunes.ui.components.bars

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import io.github.antoinepirlot.jetpack_libs.components.texts.NormalText
import io.github.antoinepirlot.satunes.data.local.LocalNavController
import io.github.antoinepirlot.satunes.data.playbackViews
import io.github.antoinepirlot.satunes.data.settingsDestinations
import io.github.antoinepirlot.satunes.data.states.SatunesUiState
import io.github.antoinepirlot.satunes.data.viewmodels.SatunesViewModel
import io.github.antoinepirlot.satunes.database.models.NavBarSection
import io.github.antoinepirlot.satunes.models.Destination
import io.github.antoinepirlot.satunes.ui.utils.getRightIconAndDescription

/**
 * @author Antoine Pirlot on 21/07/2024
 */

@Composable
internal fun RowScope.MediaNavBarSelection(
    modifier: Modifier = Modifier,
    satunesViewModel: SatunesViewModel = viewModel(),
    navBarSection: NavBarSection,
) {
    val satunesUiState: SatunesUiState by satunesViewModel.uiState.collectAsState()
    val navController: NavHostController = LocalNavController.current

    val selectedNavBarSection: NavBarSection = satunesUiState.selectedNavBarSection
    val currentDestination: Destination = satunesUiState.currentDestination

    val selectedCanBeShown: Boolean = currentDestination !in settingsDestinations
            && currentDestination !in playbackViews
            && currentDestination != Destination.SEARCH

    NavigationBarItem(
        modifier = modifier,
        label = {
            NormalText(text = stringResource(id = navBarSection.stringId))
        },
        selected = selectedCanBeShown && selectedNavBarSection == navBarSection,
        onClick = {
            satunesViewModel.selectNavBarSection(navBarSection = navBarSection)
            val rootRoute: String = when (navBarSection) {
                NavBarSection.FOLDERS -> Destination.FOLDERS.link
                NavBarSection.ARTISTS -> Destination.ARTISTS.link
                NavBarSection.ALBUMS -> Destination.ALBUMS.link
                NavBarSection.GENRES -> Destination.GENRES.link
                NavBarSection.PLAYLISTS -> Destination.PLAYLISTS.link
                NavBarSection.MUSICS -> Destination.MUSICS.link

            }
            backToRoot(rootRoute = rootRoute, navController = navController)
            println("Current dest is: ${satunesUiState.currentDestination}")
        },
        icon = {
            val pair = getRightIconAndDescription(navBarSection = navBarSection)

            Icon(
                imageVector = pair.first,
                contentDescription = pair.second
            )
        }
    )
}

/**
 * Redirect controller to the state where the user is in a bottom button's view.
 * For example, if the user click on Album button and he is in settings, then it redirects to albums.
 *
 * @param rootRoute the root route to go
 */
internal fun backToRoot(
    rootRoute: String,
    navController: NavHostController
) {
    var currentRoute: String? = navController.currentBackStackEntry?.destination?.route
    while (currentRoute != null) {
        navController.popBackStack()
        currentRoute = navController.currentBackStackEntry?.destination?.route
    }
    navController.navigate(rootRoute)
}

@Preview
@Composable
private fun RowScope.MediaNavBarSelectionPreview() {
    MediaNavBarSelection(navBarSection = NavBarSection.MUSICS)
}