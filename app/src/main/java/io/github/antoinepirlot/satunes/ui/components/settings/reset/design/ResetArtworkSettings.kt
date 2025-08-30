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
 * ** INFORMATION ABOUT THE AUTHOR *****
 * The author of this file is Antoine Pirlot, the owner of this project.
 * You find this original project on Codeberg.
 *
 * My Codeberg link is: https://codeberg.org/antoinepirlot
 * This current project's link is: https://codeberg.org/antoinepirlot/Satunes
 */

package io.github.antoinepirlot.satunes.ui.components.settings.reset.design

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.data.local.LocalMainScope
import io.github.antoinepirlot.satunes.data.local.LocalSnackBarHostState
import io.github.antoinepirlot.satunes.data.viewmodels.DataViewModel
import io.github.antoinepirlot.satunes.ui.components.settings.reset.ResetSettings
import kotlinx.coroutines.CoroutineScope

/**
 * @author Antoine Pirlot 19/04/2025
 */
@Composable
fun ResetArtworkSettings(
    modifier: Modifier = Modifier,
    dataViewModel: DataViewModel = viewModel()
) {
    val scope: CoroutineScope = LocalMainScope.current
    val snackBarHostState: SnackbarHostState = LocalSnackBarHostState.current

    ResetSettings(
        modifier = modifier,
        text = stringResource(id = R.string.artwork_sub_settings_title),
        onClick = {
            dataViewModel.resetArtworkSettings(
                scope = scope,
                snackBarHostState = snackBarHostState,
            )
        }
    )
}

@Preview
@Composable
private fun ResetArtworkSettingsPreview() {
    ResetArtworkSettings()
}