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
import io.github.antoinepirlot.satunes.data.states.NavigationUiState
import io.github.antoinepirlot.satunes.data.viewmodels.NavigationViewModel
import io.github.antoinepirlot.satunes.database.models.NavBarSection
import io.github.antoinepirlot.satunes.models.Destination
import io.github.antoinepirlot.satunes.models.DestinationCategory
import io.github.antoinepirlot.satunes.ui.utils.getRightIconAndDescription

/**
 * @author Antoine Pirlot on 21/07/2024
 */

@Composable
internal fun RowScope.MediaNavBarSelection(
    modifier: Modifier = Modifier,
    navBarSection: NavBarSection,
    navigationViewModel: NavigationViewModel = viewModel()
) {
    val navigationUiState: NavigationUiState by navigationViewModel.uiState.collectAsState()
    val navController: NavHostController = LocalNavController.current
    val currentDestination: Destination = navigationUiState.currentDestination
    val selectedCanBeShown: Boolean = currentDestination.category == DestinationCategory.MEDIA

    NavigationBarItem(
        modifier = modifier,
        label = {
            NormalText(text = stringResource(id = navBarSection.stringId))
        },
        selected = selectedCanBeShown && currentDestination.navBarSection == navBarSection,
        onClick = {
            val rootRoute: Destination = Destination.getDestination(navBarSection = navBarSection)
            navigationViewModel.backToRoot(rootRoute = rootRoute, navController = navController)
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

@Preview
@Composable
private fun RowScope.MediaNavBarSelectionPreview() {
    MediaNavBarSelection(navBarSection = NavBarSection.MUSICS)
}