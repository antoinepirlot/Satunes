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

package io.github.antoinepirlot.satunes.ui.components.settings.library.loading_logic

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.data.local.LocalMainScope
import io.github.antoinepirlot.satunes.data.local.LocalSnackBarHostState
import io.github.antoinepirlot.satunes.data.states.SatunesUiState
import io.github.antoinepirlot.satunes.data.viewmodels.SatunesViewModel
import io.github.antoinepirlot.satunes.models.SwitchSettings
import io.github.antoinepirlot.satunes.ui.components.settings.SettingWithSwitch
import io.github.antoinepirlot.satunes.ui.components.settings.SubSettings
import kotlinx.coroutines.CoroutineScope

/**
 * @author Antoine Pirlot on 31/08/2024
 */

@Composable
internal fun LoadingLogicSubSettings(
    modifier: Modifier = Modifier,
    satunesViewModel: SatunesViewModel = viewModel(),
) {
    val satunesUiState: SatunesUiState by satunesViewModel.uiState.collectAsState()
    val scope: CoroutineScope = LocalMainScope.current
    val snackBarHostState: SnackbarHostState = LocalSnackBarHostState.current

    SubSettings(
        modifier = modifier,
        title = stringResource(R.string.data_loading_settings_title)
    ) {
        SettingWithSwitch(
            setting = SwitchSettings.COMPILATION_MUSIC,
            checked = satunesUiState.compilationMusic,
            onCheckedChange = { satunesViewModel.switchCompilationMusic() }
        )
        Spacer(modifier = Modifier.size(size = 16.dp))

        SettingWithSwitch(
            setting = SwitchSettings.ARTIST_REPLACEMENT,
            checked = satunesUiState.artistReplacement,
            onCheckedChange = {
                satunesViewModel.switchArtistReplacement(
                    scope = scope,
                    snackBarHostState = snackBarHostState
                )
            }
        )
    }
}

@Preview
@Composable
private fun LoadingSubSettingsPreview() {
    LoadingLogicSubSettings()
}