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
 *  You should have received a copy of the GNU General Public License along with Satunes.
 *
 * If not, see <https://www.gnu.org/licenses/>.
 *
 * **** INFORMATION ABOUT THE AUTHOR *****
 * The author of this file is Antoine Pirlot, the owner of this project.
 * You find this original project on Codeberg.
 *
 * My Codeberg link is: https://codeberg.org/antoinepirlot
 * This current project's link is: https://codeberg.org/antoinepirlot/Satunes
 */

package io.github.antoinepirlot.satunes.ui.components.settings.subsonic

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.jetpack_libs.components.texts.NormalText
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.data.states.SubsonicUiState
import io.github.antoinepirlot.satunes.data.viewmodels.SubsonicViewModel
import io.github.antoinepirlot.satunes.internet.SubsonicCall
import io.github.antoinepirlot.satunes.ui.components.settings.SubSettings

/**
 * @author Antoine Pirlot 23/09/2025
 */
@Composable
fun SubsonicStateSetting(
    modifier: Modifier = Modifier,
    subsonicViewModel: SubsonicViewModel = viewModel()
) {
    val subsonicUiState: SubsonicUiState by subsonicViewModel.uiState.collectAsState()

    Column(modifier = modifier) {
        SubSettings(title = stringResource(R.string.subsonic_state_title)) {
            NormalText(text = stringResource(R.string.subsonic_state_content) + subsonicUiState.subsonicState)
            NormalText(text = "Requests: ${SubsonicCall.nbRequests}") //TODO remove
        }
    }
}

@Preview
@Composable
private fun SubsonicStateSettingPreview() {
    SubsonicStateSetting()
}