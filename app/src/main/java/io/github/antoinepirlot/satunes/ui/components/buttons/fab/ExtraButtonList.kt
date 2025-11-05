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

package io.github.antoinepirlot.satunes.ui.components.buttons.fab

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import io.github.antoinepirlot.jetpack_libs.components.JetpackLibsIcons
import io.github.antoinepirlot.satunes.data.local.LocalNavController
import io.github.antoinepirlot.satunes.data.states.DataUiState
import io.github.antoinepirlot.satunes.data.states.SatunesUiState
import io.github.antoinepirlot.satunes.data.viewmodels.DataViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.PlaybackViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.SatunesViewModel
import io.github.antoinepirlot.satunes.database.models.MediaImpl
import io.github.antoinepirlot.satunes.router.utils.openMedia

/**
 * Extra Button list to show on scaffold.
 *
 * On click, it will load the playback with the media list loaded.
 *
 * @param modifier the [Modifier].
 * @param playbackViewModel the [PlaybackViewModel] initialized by default.
 *
 * @author Antoine Pirlot on 11/12/2024
 */
@Composable
internal fun ExtraButtonList(
    modifier: Modifier = Modifier,
    satunesViewModel: SatunesViewModel = viewModel(),
    playbackViewModel: PlaybackViewModel = viewModel(),
    dataViewModel: DataViewModel = viewModel()
) {
    val satunesUiState: SatunesUiState by satunesViewModel.uiState.collectAsState()
    val dataUiState: DataUiState by dataViewModel.uiState.collectAsState()
    val navController: NavHostController = LocalNavController.current
    val mediaImplCollection: Collection<MediaImpl> = dataUiState.mediaImplListOnScreen

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ExtraButton(
            jetpackLibsIcons = JetpackLibsIcons.PLAY,
            onClick = {
                playbackViewModel.loadMusicFromMedias(
                    medias = mediaImplCollection,
                    currentDestination = satunesUiState.currentDestination
                )
                openMedia(
                    playbackViewModel = playbackViewModel,
                    navController = navController
                )
            }
        )

        if (!satunesUiState.shuffleMode) {
            //The shuffle mode is always activated by default and don't need to be shown
            ExtraButton(
                jetpackLibsIcons = JetpackLibsIcons.SHUFFLE,
                onClick = {
                    playbackViewModel.loadMusicFromMedias(
                        medias = mediaImplCollection,
                        currentDestination = satunesUiState.currentDestination,
                        shuffleMode = true
                    )
                    openMedia(
                        playbackViewModel = playbackViewModel,
                        navController = navController
                    )
                }
            )
        }
    }
}