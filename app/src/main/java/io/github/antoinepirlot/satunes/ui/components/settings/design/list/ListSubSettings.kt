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

package io.github.antoinepirlot.satunes.ui.components.settings.design.list

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.data.local.LocalMainScope
import io.github.antoinepirlot.satunes.data.local.LocalSnackBarHostState
import io.github.antoinepirlot.satunes.data.states.DataUiState
import io.github.antoinepirlot.satunes.data.viewmodels.DataViewModel
import io.github.antoinepirlot.satunes.models.SwitchSettings
import io.github.antoinepirlot.satunes.ui.components.settings.SubSettings
import io.github.antoinepirlot.satunes.ui.components.settings.SwitchSetting
import kotlinx.coroutines.CoroutineScope

/**
 * @author Antoine Pirlot 06/02/2025
 */
@Composable
fun ListSubSettings(
    modifier: Modifier = Modifier,
    dataViewModel: DataViewModel = viewModel()
) {
    val dataUiState: DataUiState by dataViewModel.uiState.collectAsState()
    val scope: CoroutineScope = LocalMainScope.current
    val snackBarHostState: SnackbarHostState = LocalSnackBarHostState.current

    SubSettings(
        modifier = modifier,
        title = stringResource(R.string.list_sub_settings_title)
    ) {
        SwitchSetting(
            setting = SwitchSettings.SHOW_FIRST_LETTER,
            checked = dataUiState.showFirstLetter,
            onCheckedChange = {
                dataViewModel.switchShowFirstLetter(
                    scope = scope,
                    snackBarHostState = snackBarHostState
                )
            }
        )
    }
}

@Preview
@Composable
private fun ListSubSettingsPreview(modifier: Modifier = Modifier) {
    ListSubSettings()
}