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

package io.github.antoinepirlot.satunes.ui.components.settings.battery

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.data.states.SatunesUiState
import io.github.antoinepirlot.satunes.data.viewmodels.SatunesViewModel
import io.github.antoinepirlot.satunes.models.SwitchSettings
import io.github.antoinepirlot.satunes.ui.components.dialog.InformationDialog
import io.github.antoinepirlot.satunes.ui.components.settings.SettingWithSwitch

/**
 * @author Antoine Pirlot on 27/05/2024
 */

@Composable
internal fun AudioOffloadSetting(
    modifier: Modifier = Modifier,
    satunesViewModel: SatunesViewModel = viewModel(),
) {
    val satunesUiState: SatunesUiState by satunesViewModel.uiState.collectAsState()

    val audioOffloadChecked: Boolean = satunesUiState.audioOffloadChecked
    var showAudioOffloadDialog: Boolean by rememberSaveable { mutableStateOf(false) }

    SettingWithSwitch(
        modifier = modifier,
        setting = SwitchSettings.AUDIO_OFFLOAD,
        checked = audioOffloadChecked,
        onCheckedChange = {
            if (audioOffloadChecked) {
                satunesViewModel.switchAudioOffload()
            } else {
                showAudioOffloadDialog = true
            }
        }
    )

    if (showAudioOffloadDialog && !audioOffloadChecked) {
        InformationDialog(
            title = stringResource(id = R.string.audio_offload),
            text = stringResource(id = R.string.audio_offload_info),
            onDismissRequest = { showAudioOffloadDialog = false },
            onConfirm = {
                showAudioOffloadDialog = false
                satunesViewModel.switchAudioOffload()
            }
        )
    }
}

@Preview
@Composable
private fun AudioOffloadSettingPreview() {
    AudioOffloadSetting()
}