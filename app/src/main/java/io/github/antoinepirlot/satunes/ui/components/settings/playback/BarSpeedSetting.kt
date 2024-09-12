/*
 * This file is part of Satunes.
 *
 *  Satunes is free software: you can redistribute it and/or modify it under
 *  the terms of the GNU General Public License as published by the Free Software Foundation,
 *  either version 3 of the License, or (at your option) any later version.
 *
 *  Satunes is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with Satunes.
 *  If not, see <https://www.gnu.org/licenses/>.
 *
 *  **** INFORMATIONS ABOUT THE AUTHOR *****
 *  The author of this file is Antoine Pirlot, the owner of this project.
 *  You find this original project on github.
 *
 *  My github link is: https://github.com/antoinepirlot
 *  This current project's link is: https://github.com/antoinepirlot/Satunes
 *
 *  You can contact me via my email: pirlot.antoine@outlook.com
 *  PS: I don't answer quickly.
 */

package io.github.antoinepirlot.satunes.ui.components.settings.playback

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.jetpack_libs.components.texts.NormalText
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.data.availableSpeeds
import io.github.antoinepirlot.satunes.data.local.LocalMainScope
import io.github.antoinepirlot.satunes.data.local.LocalSnackBarHostState
import io.github.antoinepirlot.satunes.data.states.SatunesUiState
import io.github.antoinepirlot.satunes.data.viewmodels.SatunesViewModel
import io.github.antoinepirlot.satunes.database.models.BarSpeed
import kotlinx.coroutines.CoroutineScope

/**
 * @author Antoine Pirlot on 27/04/2024
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BarSpeedSetting(
    modifier: Modifier = Modifier,
    satunesViewModel: SatunesViewModel = viewModel(),
) {
    val satunesUiState: SatunesUiState by satunesViewModel.uiState.collectAsState()
    val scope: CoroutineScope = LocalMainScope.current
    val snackBarHostState: SnackbarHostState = LocalSnackBarHostState.current

    val currentBarSpeed: BarSpeed = satunesUiState.barSpeed

    var isUpdating: Boolean by rememberSaveable { mutableStateOf(false) }
    var newBarSpeed: Float by rememberSaveable { mutableFloatStateOf(currentBarSpeed.speed) }

    Column(modifier = modifier) {
        NormalText(text = stringResource(id = R.string.bar_speed))
        if (isUpdating) {
            NormalText(text = stringResource(id = satunesViewModel.getBarSpeed(speed = newBarSpeed).stringId))
        } else {
            NormalText(text = stringResource(id = currentBarSpeed.stringId))
        }
        Slider(
            value = if (isUpdating) newBarSpeed else availableSpeeds.indexOf(element = currentBarSpeed)
                .toFloat(),
            onValueChange = {
                isUpdating = true
                newBarSpeed = it
            },
            onValueChangeFinished = {
                satunesViewModel.updateBarSpeed(
                    scope = scope,
                    snackBarHostState = snackBarHostState,
                    newSpeedValue = newBarSpeed
                )
                isUpdating = false
            },
            valueRange = 0f..availableSpeeds.lastIndex.toFloat(),
            steps = availableSpeeds.size - 2 // remove the first and last position to have the correct number of level
        )
    }
}

@Preview
@Composable
private fun BarSpeedSettingPreview() {
    BarSpeedSetting()
}