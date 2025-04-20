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

package io.github.antoinepirlot.satunes.ui.components.bars.playback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.jetpack_libs.components.texts.NormalText
import io.github.antoinepirlot.satunes.data.viewmodels.PlaybackViewModel
import io.github.antoinepirlot.satunes.database.models.Music
import io.github.antoinepirlot.satunes.models.ProgressBarLifecycleCallbacks
import io.github.antoinepirlot.satunes.ui.utils.getMillisToTimeText

/**
 * @author Antoine Pirlot on 23/02/24
 */

@Composable
internal fun MusicPositionBar(
    modifier: Modifier = Modifier,
    playbackViewModel: PlaybackViewModel = viewModel(),
) {
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
    var newPositionPercentage: Float by remember { mutableFloatStateOf(0f) }
    var isUpdating: Boolean by remember { mutableStateOf(false) }
    val musicPlaying: Music? = playbackViewModel.musicPlaying
    val isPlaying: Boolean = playbackViewModel.isPlaying
    val currentPositionPercentage: Float = playbackViewModel.currentPositionProgression

    LaunchedEffect(key1 = Unit) {
        lifecycleOwner.lifecycle.addObserver(ProgressBarLifecycleCallbacks)
        ProgressBarLifecycleCallbacks.updateCurrentPosition()
    }

    if (isPlaying && !ProgressBarLifecycleCallbacks.isUpdatingPosition)
        ProgressBarLifecycleCallbacks.startUpdatingCurrentPosition()

    Column(modifier = modifier) {
        Slider(
            value = if (isUpdating) newPositionPercentage else if (!currentPositionPercentage.isNaN()) currentPositionPercentage else 0f,
            onValueChange = {
                isUpdating = true
                newPositionPercentage = it
            },
            onValueChangeFinished = {
                isUpdating = false
                playbackViewModel.seekTo(positionPercentage = newPositionPercentage)
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

            NormalText(text = currentPositionTimeText)
            NormalText(text = getMillisToTimeText(maxDuration))
        }
    }
}

@Composable
@Preview
private fun MusicPositionBarPreview() {
    MusicPositionBar()
}