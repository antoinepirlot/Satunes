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
 * This current project's link is: https://github.com/antoinepirlot/Satunes
 *
 * You can contact me via my email: pirlot.antoine@outlook.com
 * PS: I don't answer quickly.
 */

package io.github.antoinepirlot.satunes.ui.components.buttons.playback

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.media3.common.Player.REPEAT_MODE_ONE
import io.github.antoinepirlot.satunes.icons.SatunesIcons
import io.github.antoinepirlot.satunes.ui.utils.getRightIconColors
import io.github.antoinepirlot.satunes.ui.utils.getRightIconTintColor
import io.github.antoinepirlot.satunes.ui.viewmodels.PlaybackViewModel

/**
 * @author Antoine Pirlot on 29/01/24
 */

@Composable
internal fun RepeatMusicButton(
    modifier: Modifier = Modifier,
    playbackViewModel: PlaybackViewModel = PlaybackViewModel(context = LocalContext.current),
) {
    val repeatMode: Int = playbackViewModel.repeatMode
    val isOn: Boolean = repeatMode > 0 // When repeat mode is off, value is 0.

    IconButton(
        modifier = modifier,
        colors = getRightIconColors(isOn = isOn),
        onClick = { playbackViewModel.switchRepeatMode() }
    ) {
        val icon: SatunesIcons = getRightRepeatIcon(repeatMode = repeatMode)
        Icon(
            modifier = modifier,
            imageVector = icon.imageVector,
            contentDescription = icon.description,
            tint = getRightIconTintColor(isOn = isOn)
        )
    }
}

@Composable
@Preview
private fun RepeatMusicButtonPreview() {
    RepeatMusicButton()
}

private fun getRightRepeatIcon(repeatMode: Int): SatunesIcons {
    return when (repeatMode) {
        REPEAT_MODE_ONE -> {
            SatunesIcons.REPEAT_ONE
        }

        else -> {
            SatunesIcons.REPEAT
        }
    }
}