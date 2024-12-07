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

package io.github.antoinepirlot.satunes.ui.components.buttons.settings.playback

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
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
import io.github.antoinepirlot.satunes.data.viewmodels.PlaybackViewModel
import io.github.antoinepirlot.satunes.ui.components.forms.OutlinedNumberField
import kotlinx.coroutines.CoroutineScope

/**
 * @author Antoine Pirlot on 15/11/2024
 */
@SuppressLint("UnrememberedMutableState")
@Composable
internal fun ForwardRewindButtons(
    modifier: Modifier = Modifier,
    playbackViewModel: PlaybackViewModel = viewModel(),
) {
    val scope: CoroutineScope = LocalMainScope.current
    val snackBarHostState: SnackbarHostState = LocalSnackBarHostState.current

    val forwardMs: Long = playbackViewModel.forwardMs
    val forwardSeconds: Int = (forwardMs / 1000).toInt()
    val rewindMs: Long = playbackViewModel.rewindMs
    val rewindSeconds: Int = (rewindMs / 1000).toInt()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        ForwardRewindSection(
            text = stringResource(R.string.forward),
            label = stringResource(R.string.seconds_text_field_label),
            seconds = forwardSeconds,
            onValueChanged = {
                playbackViewModel.updateForward(
                    scope = scope,
                    snackBarHostState = snackBarHostState,
                    seconds = it,
                )
            }
        )
        ForwardRewindSection(
            text = stringResource(R.string.rewind),
            label = stringResource(R.string.seconds_text_field_label),
            seconds = rewindSeconds,
            onValueChanged = {
                playbackViewModel.updateRewind(
                    scope = scope,
                    snackBarHostState = snackBarHostState,
                    seconds = it
                )
            }
        )
    }
}

@Composable
private fun ForwardRewindSection(
    modifier: Modifier = Modifier,
    text: String,
    label: String,
    seconds: Int,
    onValueChanged: (value: Int) -> Unit,
) {
    Box(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val mutableSeconds: MutableIntState = rememberSaveable { mutableIntStateOf(seconds) }
            NormalText(text = "$text: ")
            OutlinedNumberField(
                modifier = Modifier.width(150.dp),
                value = mutableSeconds,
                label = label,
                maxValue = null,
                onValueChanged = {
                    if (it > 0) onValueChanged(it)
                }
            )
        }
    }
}

@Preview
@Composable
private fun ForwardRewindRadioButtonsPreview() {
    ForwardRewindButtons()
}
