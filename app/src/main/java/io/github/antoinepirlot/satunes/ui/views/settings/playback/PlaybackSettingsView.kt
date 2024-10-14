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

package io.github.antoinepirlot.satunes.ui.views.settings.playback

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.jetpack_libs.components.texts.Title
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.data.states.SatunesUiState
import io.github.antoinepirlot.satunes.data.viewmodels.SatunesViewModel
import io.github.antoinepirlot.satunes.models.SwitchSettings
import io.github.antoinepirlot.satunes.ui.components.settings.SettingsSwitchList
import io.github.antoinepirlot.satunes.ui.components.settings.SubSettings
import io.github.antoinepirlot.satunes.ui.components.settings.battery.AudioOffloadSetting
import io.github.antoinepirlot.satunes.ui.components.settings.playback.BarSpeedSetting
import io.github.antoinepirlot.satunes.ui.components.settings.playback.PlaybackModesSubSettings
import io.github.antoinepirlot.satunes.ui.components.settings.playback.TimerSubSetting

/**
 *   @author Antoine Pirlot 06/03/2024
 */

@Composable
internal fun PlaybackSettingsView(
    modifier: Modifier = Modifier,
    satunesViewModel: SatunesViewModel = viewModel()
) {
    val satunesUiState: SatunesUiState by satunesViewModel.uiState.collectAsState()

    val playbackSettingsChecked: Map<SwitchSettings, Boolean> = mapOf(
        Pair(
            first = SwitchSettings.PLAYBACK_WHEN_CLOSED,
            second = satunesUiState.playbackWhenClosedChecked
        ),
        Pair(first = SwitchSettings.PAUSE_IF_NOISY, second = satunesUiState.pauseIfNoisyChecked),
        Pair(
            first = SwitchSettings.PAUSE_IF_ANOTHER_PLAYBACK,
            second = satunesUiState.pauseIfAnotherPlayback
        )
    )

    val scrollState: ScrollState = rememberScrollState()
    Column(modifier = modifier.verticalScroll(scrollState)) {
        Title(text = stringResource(id = R.string.playback_settings))
        SubSettings {
            SettingsSwitchList(checkedMap = playbackSettingsChecked) //Contains list item so always padding horizontal 16.dp
            BarSpeedSetting(modifier = Modifier.padding(horizontal = 16.dp))
        }
        PlaybackModesSubSettings() //Contains list item so always padding horizontal 16.dp
        TimerSubSetting(modifier.padding(horizontal = 16.dp))
        SubSettings(
            modifier = Modifier.padding(horizontal = 16.dp),
            title = stringResource(id = R.string.battery_settings)
        ) {
            AudioOffloadSetting() //Contains list item so always padding horizontal 16.dp
        }
    }
}

@Composable
@Preview
private fun PlaybackSettingsViewPreview() {
    PlaybackSettingsView()
}