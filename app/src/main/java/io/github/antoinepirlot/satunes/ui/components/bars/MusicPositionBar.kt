/*
 * This file is part of Satunes.
 *
 * Satunes is free software: you can redistribute it and/or modify it under
 *  the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * Satunes is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Satunes.
 * If not, see <https://www.gnu.org/licenses/>.
 *
 * **** INFORMATIONS ABOUT THE AUTHOR *****
 * The author of this file is Antoine Pirlot, the owner of this project.
 * You find this original project on github.
 *
 * My github link is: https://github.com/antoinepirlot
 * This current project's link is: https://github.com/antoinepirlot/MP3-Player
 *
 * You can contact me via my email: pirlot.antoine@outlook.com
 * PS: I don't answer quickly.
 */

package io.github.antoinepirlot.satunes.ui.components.bars

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import io.github.antoinepirlot.satunes.database.services.settings.SettingsManager
import io.github.antoinepirlot.satunes.playback.services.PlaybackController
import io.github.antoinepirlot.satunes.ui.utils.getMillisToTimeText
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * @author Antoine Pirlot on 23/02/24
 */

private var isUpdatingCurrentPosition: Boolean = false

@Composable
fun MusicPositionBar(
    modifier: Modifier = Modifier
) {
    val playbackController = PlaybackController.getInstance()
    val musicPlaying by remember { playbackController.musicPlaying }
    var newPositionPercentage by rememberSaveable { mutableFloatStateOf(0f) }
    var isUpdating by rememberSaveable { mutableStateOf(false) }
    val currentPositionPercentage by rememberSaveable { playbackController.currentPositionProgression }

    Column(modifier = modifier) {
        Slider(
            value = if (isUpdating) newPositionPercentage else currentPositionPercentage,
            onValueChange = {
                isUpdating = true
                newPositionPercentage = it
            },
            onValueChangeFinished = {
                playbackController.seekTo(positionPercentage = newPositionPercentage)
                isUpdating = false
            },
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val maxDuration: Long = musicPlaying!!.duration
            val currentPositionTimeText =
                if (isUpdating) getMillisToTimeText((newPositionPercentage * maxDuration).toLong())
                else getMillisToTimeText((currentPositionPercentage * maxDuration).toLong())

            Text(text = currentPositionTimeText)
            Text(text = getMillisToTimeText(maxDuration))
        }
    }

    val isPlaying: Boolean by rememberSaveable { playbackController.isPlaying }
    if (isPlaying) {
        val lifeCycleOwner: LifecycleOwner = LocalLifecycleOwner.current
        LaunchedEffect(lifeCycleOwner.lifecycle.currentState) {
            val state = lifeCycleOwner.lifecycle.currentState
            if (state != Lifecycle.State.DESTROYED && state != Lifecycle.State.CREATED) {
                lifeCycleOwner.lifecycleScope.launch {
                    updateCurrentPosition(lifeCycleOwner)
                }
            }
        }
    }
}

/**
 * Launch a coroutine where the currentPositionProgression is updated every 1 second.
 * If this function is already running, just return by using isUpdatingPosition.
 */
private suspend fun updateCurrentPosition(lifecycleOwner: LifecycleOwner) {
    val playbackController: PlaybackController = PlaybackController.getInstance()
    while (playbackController.isPlaying.value) {
        isUpdatingCurrentPosition = true
        val maxPosition: Long = playbackController.musicPlaying.value!!.duration
        val newPosition: Long = playbackController.getCurrentPosition()

        playbackController.currentPositionProgression.floatValue =
            newPosition.toFloat() / maxPosition.toFloat()
        val timeMillis: Long = (SettingsManager.barSpeed.value * 1000f).toLong()
        isUpdatingCurrentPosition = false
        delay(timeMillis) // todo Wait one second to avoid refreshing all the time
        if (isUpdatingCurrentPosition) {
            // Be sure the play/pause button has not been activated between delay and this
            return
        }
    }
    if (playbackController.isEnded) {
        // It means the music has reached the end of playlist and the music is finished
        playbackController.currentPositionProgression.floatValue = 1f
    }
}

@Composable
@Preview
fun MusicPositionBarPreview() {
    MusicPositionBar()
}