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

package io.github.antoinepirlot.satunes.ui.components.bars.playback

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.satunes.data.viewmodels.PlaybackViewModel
import io.github.antoinepirlot.satunes.database.models.Music
import io.github.antoinepirlot.satunes.ui.components.buttons.playback.custom_actions.AddToPlaylistCustomAction
import io.github.antoinepirlot.satunes.ui.components.buttons.playback.custom_actions.FavoriteCustomAction
import io.github.antoinepirlot.satunes.ui.components.buttons.playback.custom_actions.ForwardXSeconds
import io.github.antoinepirlot.satunes.ui.components.buttons.playback.custom_actions.RewindXSeconds
import io.github.antoinepirlot.satunes.ui.components.buttons.playback.custom_actions.ShareCustomAction
import io.github.antoinepirlot.satunes.ui.components.buttons.playback.custom_actions.TimerCustomAction

/**
 * @author Antoine Pirlot on 01/06/2024
 */

@Composable
internal fun PlaybackCustomActionsBar(
    modifier: Modifier = Modifier,
    playbackViewModel: PlaybackViewModel = viewModel(),
) {
    val musicPlaying: Music = playbackViewModel.musicPlaying!!
    val scrollState = rememberScrollState()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(state = scrollState),
    ) {
        val spacerSize: Dp = 8.dp

        Spacer(modifier = Modifier.size(spacerSize))
        FavoriteCustomAction(music = musicPlaying)
        Spacer(modifier = Modifier.size(spacerSize))

        ForwardXSeconds(seconds = 5) // TODO add setting to choose seconds
        Spacer(modifier = Modifier.size(spacerSize))

        RewindXSeconds(seconds = 5) // TODO add setting to choose seconds
        Spacer(modifier = Modifier.size(spacerSize))

        AddToPlaylistCustomAction(music = musicPlaying)
        Spacer(modifier = Modifier.size(spacerSize))

        ShareCustomAction(music = musicPlaying)
        Spacer(modifier = Modifier.size(spacerSize))

        TimerCustomAction()
        Spacer(modifier = Modifier.size(spacerSize))
    }
}

@Preview
@Composable
private fun PlaybackButtonsRowPreview() {
    PlaybackCustomActionsBar()
}