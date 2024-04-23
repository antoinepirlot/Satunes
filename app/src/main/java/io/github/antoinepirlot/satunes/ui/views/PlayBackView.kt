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

package io.github.antoinepirlot.satunes.ui.views

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.antoinepirlot.satunes.database.models.Album
import io.github.antoinepirlot.satunes.database.models.Artist
import io.github.antoinepirlot.satunes.playback.services.PlaybackController
import io.github.antoinepirlot.satunes.ui.components.images.MusicPlayingAlbumArtwork
import io.github.antoinepirlot.satunes.ui.components.bars.MusicControlBar
import io.github.antoinepirlot.satunes.ui.components.texts.NormalText
import io.github.antoinepirlot.satunes.ui.components.texts.Subtitle

/**
 * @author Antoine Pirlot on 25/01/24
 */

@Composable
fun PlayBackView(
    modifier: Modifier = Modifier,
    onAlbumClick: (album: Album?) -> Unit,
    onArtistClick: (artist: Artist) -> Unit,
) {
    val musicPlaying = remember { PlaybackController.getInstance().musicPlaying }
    val albumArtworkSize = 350.dp
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MusicPlayingAlbumArtwork(
            modifier = Modifier
                .size(albumArtworkSize)
                .padding(bottom = 40.dp),
            onClick = onAlbumClick
        )
        NormalText(text = musicPlaying.value!!.title)
        Subtitle(
            modifier = Modifier.clickable { onArtistClick(musicPlaying.value!!.artist) },
            text = musicPlaying.value!!.artist.title
        )
        MusicControlBar(
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
@Preview
fun PlayBackViewPreview() {
    PlayBackView(onAlbumClick = {}, onArtistClick = {})
}