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
 *
 *  ***** INFORMATIONS ABOUT THE AUTHOR *****
 *  The author of this file is Antoine Pirlot, the owner of this project.
 *  You find this original project on github.
 *
 *  My github link is: https://github.com/antoinepirlot
 *  This current project's link is: https://github.com/antoinepirlot/MP3-Player
 *
 *  You can contact me via my email: pirlot.antoine@outlook.com
 *  PS: I don't answer quickly.
 */

package earth.mp3player.app.ui.components.music

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import earth.mp3player.app.R
import earth.mp3player.playback.services.playback.PlaybackController

/**
 * @author Antoine Pirlot on 29/02/24
 */

@Composable
fun AlbumArtwork(
    modifier: Modifier = Modifier
) {
    val musicPlaying by remember { PlaybackController.getInstance().musicPlaying }

    Box(
        modifier = modifier,
    ) {
        if (musicPlaying!!.artwork != null) {
            Image(
                modifier = modifier,
                bitmap = musicPlaying!!.artwork!!,
                contentDescription = "Music Playing Album Artwork"
            )
        } else {
            Image(
                modifier = modifier,
                painter = painterResource(id = R.mipmap.empty_album_artwork_foreground),
                contentDescription = "Default Album Artwork"
            )
        }
    }
}

@Composable
@Preview
fun AlbumArtworkPreview() {

}