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

package io.github.antoinepirlot.satunes.ui.components.forms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Button
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.jetpack_libs.components.texts.NormalText
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.data.local.LocalMainScope
import io.github.antoinepirlot.satunes.data.local.LocalSnackBarHostState
import io.github.antoinepirlot.satunes.data.states.PlaybackUiState
import io.github.antoinepirlot.satunes.data.viewmodels.PlaybackViewModel
import io.github.antoinepirlot.satunes.ui.components.settings.playback.timer.TimerRemainingTime
import kotlinx.coroutines.CoroutineScope

/**
 * @author Antoine Pirlot on 14/10/2024
 */

//max 8 hours
private const val MAX_HOURS: Int = 8
private const val MAX_MINUTES: Int = MAX_HOURS * 60
private const val MAX_SECONDS: Int = MAX_MINUTES * 60

@Composable
internal fun CreateTimerForm(
    modifier: Modifier = Modifier,
    playbackViewModel: PlaybackViewModel = viewModel(),
    onFinished: (() -> Unit)? = null,
) {
    val playbackUiState: PlaybackUiState by playbackViewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .selectableGroup()
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val scope: CoroutineScope = LocalMainScope.current
        val snackBarHostState: SnackbarHostState = LocalSnackBarHostState.current

        val secondsIntField: MutableIntState = rememberSaveable { mutableIntStateOf(0) }
        val minutesIntField: MutableIntState = rememberSaveable { mutableIntStateOf(0) }
        val hoursIntField: MutableIntState = rememberSaveable { mutableIntStateOf(0) }

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                OutlinedNumberField(
                    modifier = Modifier.fillMaxWidth(fraction = 0.30f),
                    value = hoursIntField,
                    label = stringResource(R.string.hours_text_field_label),
                    maxValue = MAX_HOURS
                )
                Spacer(modifier = Modifier.size(16.dp))
                OutlinedNumberField(
                    modifier = Modifier.fillMaxWidth(fraction = 0.49f),
                    value = minutesIntField,
                    label = stringResource(R.string.minutes_text_field_label),
                    maxValue = MAX_MINUTES
                )
                Spacer(modifier = Modifier.size(16.dp))
                OutlinedNumberField(
                    modifier = Modifier.fillMaxWidth(),
                    value = secondsIntField,
                    label = stringResource(R.string.seconds_text_field_label),
                    maxValue = MAX_SECONDS
                )
            }
        }
        TimerRemainingTime()
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            if (playbackUiState.timer != null) {
                Button(
                    onClick = {
                        playbackViewModel.cancelTimer(
                            scope = scope,
                            snackBarHostState = snackBarHostState,
                        )
                        secondsIntField.intValue = 0
                        minutesIntField.intValue = 0
                        hoursIntField.intValue = 0
                        onFinished?.invoke()
                    }
                ) {
                    NormalText(text = stringResource(R.string.cancel))
                }

                Spacer(modifier = Modifier.size(5.dp))
            }
            Button(
                onClick = {
                    computeTime(
                        secondsIntField = secondsIntField,
                        minutesIntField = minutesIntField,
                        hoursIntField = hoursIntField
                    )
                    playbackViewModel.setTimer(
                        scope = scope,
                        snackBarHostState = snackBarHostState,
                        hours = hoursIntField.intValue,
                        minutes = minutesIntField.intValue,
                        seconds = secondsIntField.intValue
                    )
                    onFinished?.invoke()
                }
            ) {
                NormalText(text = stringResource(R.string.start_timer_button_content))
            }
        }
    }
}

private fun computeTime(
    secondsIntField: MutableIntState,
    minutesIntField: MutableIntState,
    hoursIntField: MutableIntState
) {
    val exceedMinutes = secondsIntField.intValue / 60
    minutesIntField.intValue += exceedMinutes
    val exceedHours = minutesIntField.intValue / 60
    hoursIntField.intValue += exceedHours
    if (hoursIntField.intValue >= MAX_HOURS) {
        hoursIntField.intValue = MAX_HOURS
        minutesIntField.intValue = 0
        secondsIntField.intValue = 0
    } else {
        secondsIntField.intValue -= exceedMinutes * 60
        minutesIntField.intValue -= exceedHours * 60
    }
}

@Preview
@Composable
private fun CreateTimerFormPreview() {
    CreateTimerForm()
}