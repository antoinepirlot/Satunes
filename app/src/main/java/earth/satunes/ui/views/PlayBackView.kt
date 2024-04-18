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

package earth.satunes.ui.views

import android.annotation.SuppressLint
import android.net.Uri.decode
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import earth.satunes.database.models.Album
import earth.satunes.playback.services.PlaybackController
import earth.satunes.ui.components.music.MusicPlayingAlbumArtwork
import earth.satunes.ui.components.music.bars.MusicControlBar

/**
 * @author Antoine Pirlot on 25/01/24
 */

@Composable
fun PlayBackView(
    modifier: Modifier = Modifier,
    onClick: (album: Album?) -> Unit,
) {
    val musicPlaying = remember { PlaybackController.getInstance().musicPlaying }
    val albumArtworkSize = 200.dp
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        MusicPlayingAlbumArtwork(modifier = modifier.size(albumArtworkSize), onClick = onClick)

        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = decode(musicPlaying.value!!.title))

            MusicControlBar(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            )
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
@Preview
fun PlayBackViewPreview() {
    PlayBackView(onClick = {})
}