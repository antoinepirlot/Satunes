/*
 * This file is part of Satunes.
 * Satunes is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *  Satunes is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License along with Satunes.
 *  If not, see <https://www.gnu.org/licenses/>.
 *
 * **** INFORMATION ABOUT THE AUTHOR *****
 * The author of this file is Antoine Pirlot, the owner of this project.
 * You find this original project on Codeberg.
 *
 * My Codeberg link is: https://codeberg.org/antoinepirlot
 * This current project's link is: https://codeberg.org/antoinepirlot/Satunes
 */

package io.github.antoinepirlot.satunes.ui.components.buttons.fab

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.satunes.data.local.LocalSnackBarHostState
import io.github.antoinepirlot.satunes.data.states.SatunesUiState
import io.github.antoinepirlot.satunes.data.viewmodels.DataViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.PlaybackViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.SatunesViewModel
import io.github.antoinepirlot.satunes.models.DestinationCategory
import io.github.antoinepirlot.satunes.ui.components.buttons.bars.bottom.ShowCurrentMusicButton

/**
 * @author Antoine Pirlot on 23/12/2024
 */
@Composable
internal fun SatunesFAB(
    modifier: Modifier = Modifier,
    satunesViewModel: SatunesViewModel = viewModel(),
    playbackViewModel: PlaybackViewModel = viewModel(),
    dataViewModel: DataViewModel = viewModel()
) {
    val satunesUiState: SatunesUiState by satunesViewModel.uiState.collectAsState()
    val snackBarHostState: SnackbarHostState = LocalSnackBarHostState.current

    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomEnd,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!dataViewModel.isLoaded) return

            val isInMediaListViews: Boolean =
                satunesUiState.currentDestination.category == DestinationCategory.MEDIA
            if (isInMediaListViews) {
                satunesUiState.extraButtons?.invoke()
                if (playbackViewModel.musicPlaying != null)
                    ShowCurrentMusicButton()
            }
        }
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            SnackbarHost(
                //Mandatory as for Google a snackbar is on top of FAB icons and does not overlay it.
                // So the UI is ugly without that modifier
                modifier = Modifier.padding(start = 30.dp),
                hostState = snackBarHostState
            )
        }
    }
}

@Preview
@Composable
private fun SatunesFABPreview() {
    SatunesFAB()
}