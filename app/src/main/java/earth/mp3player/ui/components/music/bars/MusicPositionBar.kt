/*
 *  This file is part of MP3 Player.
 *
 *  MP3 Player is free software: you can redistribute it and/or modify it under
 *  the terms of the GNU General Public License as published by the Free Software Foundation,
 *  either version 3 of the License, or (at your option) any later version.
 *
 *  MP3 Player is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *   without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with MP3 Player.
 *  If not, see <https://www.gnu.org/licenses/>.

 *  ***** INFORMATIONS ABOUT THE AUTHOR *****
 *  The author of this file is Antoine Pirlot, the owner of this project.
 *  You find this original project on github.
 *
 *  My github link is: https://github.com/antoinepirlot
 *  This current project's link is: https://github.com/antoinepirlot/MP3-Player
 *
 *  You can contact me via my email: pirlot.antoine@outlook.com
 * PS: I don't answer quickly.
 */

package earth.mp3player.ui.components.music.bars

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import earth.mp3player.services.PlaybackController
import earth.mp3player.ui.utils.getMillisToTimeText

/**
 * @author Antoine Pirlot on 23/02/24
 */

@Composable
fun MusicPositionBar(
    modifier: Modifier = Modifier
) {
    val playbackController = PlaybackController.getInstance()
    val musicPlaying by remember { playbackController.musicPlaying }
    var newPositionPercentage by rememberSaveable { mutableFloatStateOf(playbackController.currentPositionProgression.floatValue) }
    val currentPositionPercentage by rememberSaveable { playbackController.currentPositionProgression }
    var isUpdating by rememberSaveable { mutableStateOf(false) }

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

}

@Composable
@Preview
fun MusicPositionBarPreview() {
    MusicPositionBar()
}