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

package io.github.antoinepirlot.satunes.ui.components.settings.reset

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.jetpack_libs.components.texts.Title
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.data.local.LocalMainScope
import io.github.antoinepirlot.satunes.data.local.LocalSnackBarHostState
import io.github.antoinepirlot.satunes.data.viewmodels.DataViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.SatunesViewModel
import io.github.antoinepirlot.satunes.ui.components.buttons.settings.reset.ResetButton
import io.github.antoinepirlot.satunes.ui.components.settings.SubSettings
import io.github.antoinepirlot.satunes.ui.components.settings.reset.battery.ResetBatterySettings
import io.github.antoinepirlot.satunes.ui.components.settings.reset.library.ResetLibrarySubSettings
import io.github.antoinepirlot.satunes.ui.components.settings.reset.navigation_bar.ResetInterfaceSubSettings
import io.github.antoinepirlot.satunes.ui.components.settings.reset.playback.ResetPlaybackSubSettings
import io.github.antoinepirlot.satunes.ui.components.settings.reset.search.ResetSearchSubSettings
import kotlinx.coroutines.CoroutineScope

/**
 * @author Antoine Pirlot on 21/11/2024
 */

@Composable
internal fun AllResetSettings(
    modifier: Modifier = Modifier,
    satunesViewModel: SatunesViewModel = viewModel(),
    dataViewModel: DataViewModel = viewModel(),
) {
    val scope: CoroutineScope = LocalMainScope.current
    val snackBarHostState: SnackbarHostState = LocalSnackBarHostState.current

    val scrollState: ScrollState = rememberScrollState()

    Column(
        modifier = modifier.verticalScroll(state = scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Title(text = stringResource(R.string.reset_settings))
        SubSettings {
            ResetButton(
                text = stringResource(R.string.reset_all_settings),
                onClick = {
                    dataViewModel.resetAllSettings(
                        scope = scope,
                        snackBarHostState = snackBarHostState,
                        satunesViewModel = satunesViewModel
                    )
                }
            )
        }
        ResetInterfaceSubSettings()
        ResetPlaybackSubSettings()
        ResetSearchSubSettings()
        ResetLibrarySubSettings()
        ResetBatterySettings()

    }
}

@Preview
@Composable
private fun AllResetSubSettingsPreview() {
    AllResetSettings()
}