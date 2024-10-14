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

package io.github.antoinepirlot.satunes.ui.components.buttons.playback.custom_actions

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import io.github.antoinepirlot.satunes.data.states.PlaybackUiState
import io.github.antoinepirlot.satunes.data.viewmodels.PlaybackViewModel
import io.github.antoinepirlot.satunes.icons.SatunesIcons
import io.github.antoinepirlot.satunes.models.Timer
import io.github.antoinepirlot.satunes.ui.components.dialog.Dialog
import io.github.antoinepirlot.satunes.ui.components.forms.CreateTimerForm
import io.github.antoinepirlot.satunes.ui.utils.getMillisToTimeText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * @author Antoine Pirlot on 14/10/2024
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TimerCustomAction(
    modifier: Modifier = Modifier,
    playbackViewModel: PlaybackViewModel = viewModel(),
) {
    val playbackUiState: PlaybackUiState by playbackViewModel.uiState.collectAsState()
    val timer: Timer? = playbackUiState.timer
    var remainingTime: String by rememberSaveable { mutableStateOf("") }
    var showDialog: Boolean by rememberSaveable { mutableStateOf(false) }

    if (timer != null) {
        var job: Job? = null
        LaunchedEffect(remainingTime) {
            job?.cancel()
            remainingTime =
                getMillisToTimeText(milliseconds = timer.getRemainingTime())
            job = CoroutineScope(Dispatchers.IO).launch {
                delay(1000) // Wait one second to be refreshed
                remainingTime =
                    getMillisToTimeText(milliseconds = timer.getRemainingTime())
            }
        }
    } else {
        remainingTime = getMillisToTimeText(0)
    }

    CustomActionButton(
        modifier = modifier,
        icon = SatunesIcons.TIMER,
        text = remainingTime,
        onClick = { showDialog = true }
    )

    if (showDialog) {
        Dialog(
            icon = SatunesIcons.TIMER,
            title = stringResource(R.string.timer_settings_title),
            onDismissRequest = { showDialog = false },
            onConfirmRequest = {},
            confirmText = null,
            dismissText = null,
        ) {
            CreateTimerForm(onFinished = { showDialog = false })
        }
    }
}

@Preview
@Composable
private fun TimerCustomActionPreview() {
    TimerCustomAction()
}