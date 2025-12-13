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

package io.github.antoinepirlot.satunes.ui.components.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.data.local.LocalNavController
import io.github.antoinepirlot.satunes.data.states.NavigationUiState
import io.github.antoinepirlot.satunes.data.viewmodels.NavigationViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.PlaybackViewModel
import io.github.antoinepirlot.satunes.database.models.media.Media
import io.github.antoinepirlot.satunes.ui.views.media.MediaListView

/**
 * @author Antoine Pirlot 13/12/2025
 */
@Composable
fun SubsonicSearchSection(
    modifier: Modifier = Modifier,
    playbackViewModel: PlaybackViewModel = viewModel(),
    navigationViewModel: NavigationViewModel = viewModel(),
) {
    val navigationUiState: NavigationUiState by navigationViewModel.uiState.collectAsState()
    val navController: NavHostController = LocalNavController.current

    MediaListView(
        canBeSorted = false,
        emptyViewText = stringResource(id = R.string.no_result),
        onMediaClick = { mediaImpl: Media ->
            if (mediaImpl.isMusic())
            //TODO start streaming radio
                TODO("Not yet implemented")
            navigationViewModel.openMedia(
                playbackViewModel = playbackViewModel,
                media = mediaImpl,
                navController = navController
            )
        }
    )
}