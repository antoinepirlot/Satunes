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

package io.github.antoinepirlot.satunes.ui.views.playback.common

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.jetpack_libs.components.texts.NormalText
import io.github.antoinepirlot.jetpack_libs.components.texts.Title
import io.github.antoinepirlot.satunes.data.viewmodels.PlaybackViewModel
import io.github.antoinepirlot.satunes.database.models.Music

/**
 * @author Antoine Pirlot on 23/10/2024
 */

@Composable
internal fun LyricsView(
    modifier: Modifier = Modifier,
    playbackViewModel: PlaybackViewModel = viewModel(),
) {
    val musicPlaying: Music = playbackViewModel.musicPlaying!!
    val scrollState: ScrollState = rememberScrollState()
    Column(modifier = modifier.verticalScroll(state = scrollState)) {
        Title(text = musicPlaying.title)
        NormalText(text = musicPlaying.lyrics!!)
    }
}

@Preview
@Composable
private fun LyricsViewPreview() {
    LyricsView()
}