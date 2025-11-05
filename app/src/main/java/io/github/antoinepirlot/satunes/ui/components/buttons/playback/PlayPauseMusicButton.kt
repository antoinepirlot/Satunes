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

package io.github.antoinepirlot.satunes.ui.components.buttons.playback

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.jetpack_libs.components.JetpackLibsIcons
import io.github.antoinepirlot.satunes.data.viewmodels.PlaybackViewModel

/**
 * @author Antoine Pirlot on 01/06/2024
 */

@Composable
internal fun PlayPauseMusicButton(
    modifier: Modifier = Modifier,
    playbackViewModel: PlaybackViewModel = viewModel(),
) {
    val isPlaying: Boolean = playbackViewModel.isPlaying

    IconButton(
        modifier = modifier,
        onClick = { playbackViewModel.playPause() }
    ) {
        val jetpackLibsIcons: JetpackLibsIcons =
            getPlayPauseIconWithDescription(isPlaying = isPlaying)

        Icon(
            modifier = modifier,
            imageVector = jetpackLibsIcons.imageVector,
            contentDescription = jetpackLibsIcons.description,
        )
    }
}

private fun getPlayPauseIconWithDescription(isPlaying: Boolean): JetpackLibsIcons {
    return if (isPlaying) {
        JetpackLibsIcons.PAUSE
    } else {
        JetpackLibsIcons.PLAY
    }
}

@Preview
@Composable
private fun PlayPauseMusicButtonPreview() {
    PlayPauseMusicButton()
}