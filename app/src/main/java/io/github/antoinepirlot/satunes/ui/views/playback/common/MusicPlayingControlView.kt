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

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.jetpack_libs.components.models.ScreenSizes
import io.github.antoinepirlot.jetpack_libs.components.texts.NormalText
import io.github.antoinepirlot.jetpack_libs.components.texts.Subtitle
import io.github.antoinepirlot.satunes.data.viewmodels.PlaybackViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.SatunesViewModel
import io.github.antoinepirlot.satunes.database.models.Album
import io.github.antoinepirlot.satunes.database.models.Artist
import io.github.antoinepirlot.satunes.database.models.Music
import io.github.antoinepirlot.satunes.ui.components.bars.MusicControlBar
import io.github.antoinepirlot.satunes.ui.components.buttons.playback.custom_actions.PlaybackCustomActionsBar
import io.github.antoinepirlot.satunes.ui.components.dialog.artist.ArtistOptionsDialog
import io.github.antoinepirlot.satunes.ui.components.images.MusicPlayingAlbumArtwork

/**
 * @author Antoine Pirlot on 25/01/24
 */

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun MusicPlayingControlView(
    modifier: Modifier = Modifier,
    satunesViewModel: SatunesViewModel = viewModel(),
    playbackViewModel: PlaybackViewModel = viewModel(),
    onAlbumClick: (album: Album?) -> Unit,
    onArtistClick: (artist: Artist) -> Unit,
) {
    val haptics: HapticFeedback = LocalHapticFeedback.current
    var showArtistOptions: Boolean by rememberSaveable { mutableStateOf(false) }
    val musicPlaying: Music = playbackViewModel.musicPlaying!!
    val padding: Dp = 16.dp

    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        val screenHeightDp: Int = LocalConfiguration.current.screenHeightDp
        if (!(LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE && screenHeightDp < 480)) {
            val screenWidthDp = LocalConfiguration.current.screenWidthDp
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(
                        if (screenWidthDp < ScreenSizes.VERY_VERY_SMALL) 0.35f
                        else if (screenWidthDp < ScreenSizes.VERY_SMALL) 0.4f
                        else 0.55f
                    )
                    .padding(top = padding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                MusicPlayingAlbumArtwork(onClick = onAlbumClick)
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = padding),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            NormalText(
                modifier = Modifier.padding(horizontal = padding),
                text = musicPlaying.title,
                fontSize = 20.sp
            )
            Subtitle(
                modifier = Modifier
                    .padding(horizontal = padding)
                    .combinedClickable(
                        onClick = {
                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onArtistClick(musicPlaying.artist)
                        },
                        onLongClick = {
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            satunesViewModel.mediaOptionsIsOpen()
                            showArtistOptions = true
                        }
                    ),
                text = musicPlaying.artist.title
            )
            PlaybackCustomActionsBar()
            MusicControlBar(modifier = Modifier.padding(horizontal = padding))
        }

        /**
         * Artist Options
         */
        if (showArtistOptions) {
            ArtistOptionsDialog(
                artist = musicPlaying.artist,
                onDismissRequest = {
                    satunesViewModel.mediaOptionsIsClosed()
                    showArtistOptions = false
                }
            )
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
@Preview
private fun MusicPlayingControlViewPreview() {
    MusicPlayingControlView(onAlbumClick = {}, onArtistClick = {})
}