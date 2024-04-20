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

package earth.satunes.ui.components.cards.albums

import android.net.Uri.decode
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import earth.satunes.database.models.Album
import earth.satunes.ui.components.AlbumArtwork
import earth.satunes.ui.components.texts.NormalText
import earth.satunes.ui.components.texts.Title

/**
 * @author Antoine Pirlot on 11/04/2024
 */

@Composable
fun AlbumGridCard(
    modifier: Modifier = Modifier,
    album: Album,
    onClick: (album: Album?) -> Unit,
) {
    Box(
        modifier = modifier.size(250.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            AlbumArtwork(
                modifier
                    .size(225.dp)
                    .align(Alignment.CenterHorizontally),
                media = album,
                onClick = onClick
            )
            NormalText(
                modifier = Modifier.fillMaxWidth(),
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                text = decode(album.title)
            )
        }
    }
}

@Preview
@Composable
fun AlbumGridCardPreview() {
    AlbumGridCard(album = Album(id = 1, title = "Album #1"), onClick = {})
}