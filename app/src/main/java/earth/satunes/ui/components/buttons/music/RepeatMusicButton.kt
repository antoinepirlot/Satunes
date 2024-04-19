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

package earth.satunes.ui.components.buttons.music

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.media3.common.Player.REPEAT_MODE_ALL
import androidx.media3.common.Player.REPEAT_MODE_ONE
import earth.satunes.playback.services.PlaybackController
import earth.satunes.ui.views.SatunesIcons

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
        val icon: SatunesIcons = getRightRepeatIcon()
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

private fun getRightRepeatIcon(): SatunesIcons {
    return when (PlaybackController.getInstance().repeatMode.value) {
        REPEAT_MODE_ONE -> {
            SatunesIcons.REPEAT_ONE_ON
        }

        REPEAT_MODE_ALL -> {
            SatunesIcons.REPEAT_ON
        }

        else -> {
            SatunesIcons.REPEAT
        }
    }
}