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

package earth.mp3player.ui.components.music.buttons

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.media3.common.Player.REPEAT_MODE_ALL
import androidx.media3.common.Player.REPEAT_MODE_ONE
import earth.mp3player.playback.services.PlaybackController
import earth.mp3player.ui.views.MP3PlayerIcons

/**
 * @author Antoine Pirlot on 29/01/24
 */

@Composable
fun RepeatMusicButton(
    modifier: Modifier = Modifier
) {
    IconButton(
        modifier = modifier,
        onClick = { PlaybackController.getInstance().switchRepeatMode() }
    ) {
        val icon: MP3PlayerIcons = getRightRepeatIcon()
        Icon(
            modifier = modifier,
            imageVector = icon.imageVector,
            contentDescription = icon.description
        )
    }
}

@Composable
@Preview
fun RepeatMusicButtonPreview() {
    RepeatMusicButton()
}

private fun getRightRepeatIcon(): MP3PlayerIcons {
    return when (PlaybackController.getInstance().repeatMode.value) {
        REPEAT_MODE_ONE -> {
            MP3PlayerIcons.REPEAT_ONE_ON
        }

        REPEAT_MODE_ALL -> {
            MP3PlayerIcons.REPEAT_ON
        }

        else -> {
            MP3PlayerIcons.REPEAT
        }
    }
}