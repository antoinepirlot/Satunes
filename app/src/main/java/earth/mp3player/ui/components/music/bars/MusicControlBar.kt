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

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import earth.mp3player.services.PlaybackController
import earth.mp3player.ui.components.music.buttons.NextMusicButton
import earth.mp3player.ui.components.music.buttons.PreviousMusicButton
import earth.mp3player.ui.components.music.buttons.RepeatMusicButton
import earth.mp3player.ui.components.music.buttons.ShuffleMusicButton

/**
 * @author Antoine Pirlot on 25/01/24
 */

@Composable
fun MusicControlBar(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.HorizontalOrVertical = Arrangement.Center,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically
) {
    val playbackController = PlaybackController.getInstance()

    val isPlaying = rememberSaveable { playbackController.isPlaying }

    val spaceBetweenButtons = 20.dp
    val playPauseButtonSize = 80.dp
    val optionButtonSize = 30.dp

    Column {
        MusicPositionBar()
        Row(
            modifier = modifier,
            horizontalArrangement = horizontalArrangement,
            verticalAlignment = verticalAlignment
        ) {
            ShuffleMusicButton(modifier = Modifier.size(optionButtonSize))
            Spacer(modifier = Modifier.width(spaceBetweenButtons))

            PreviousMusicButton()
            Spacer(modifier = Modifier.width(spaceBetweenButtons))

            IconButton(
                modifier = Modifier.size(playPauseButtonSize),
                onClick = { playbackController.playPause() }
            ) {
                val iconWithDescription: Pair<ImageVector, String> =
                    getPlayPauseIconWithDescription(isPlaying = isPlaying.value)

                Icon(
                    modifier = Modifier.size(playPauseButtonSize),
                    imageVector = iconWithDescription.first,
                    contentDescription = iconWithDescription.second,
                )
            }

            Spacer(modifier = Modifier.width(spaceBetweenButtons))
            NextMusicButton()

            Spacer(modifier = Modifier.width(spaceBetweenButtons))
            RepeatMusicButton(modifier = Modifier.size(optionButtonSize))
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
@Preview
fun MediaControlBarPreview() {
    MusicControlBar()
}

private fun getPlayPauseIconWithDescription(isPlaying: Boolean): Pair<ImageVector, String> {
    return if (isPlaying) {
        Icons.Filled.PauseCircle to "Pause"
    } else {
        Icons.Filled.PlayCircle to "Play"
    }
}