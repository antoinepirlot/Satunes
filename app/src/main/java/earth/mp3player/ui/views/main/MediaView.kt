/*
 * This file is part of MP3 Player.
 *
 * MP3 Player is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * MP3 Player is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with MP3 Player.
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

package earth.mp3player.ui.views.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import earth.mp3player.database.models.Artist
import earth.mp3player.database.models.Media

/**
 * @author Antoine Pirlot on 11/04/2024
 */

@Composable
fun MediaView(
    modifier: Modifier = Modifier,
    media: Media
) {
    Column(modifier = modifier) {
        Box(modifier = Modifier.padding(60.dp)) {
            //TODO show a pannel with the media icon and title
        }
        //Here if it is an artist show albums as grid
        if (media is Artist) {

        }
        //Here it is the list of music
    }
}

@Preview
@Composable
fun MediaViewPreview() {
    MediaView(media = Artist(id = 0, title = "Artist"))
}