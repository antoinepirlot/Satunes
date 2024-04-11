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

package earth.mp3player.ui.components.cards.albums

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import earth.mp3player.database.R
import earth.mp3player.database.models.Album
import earth.mp3player.ui.components.texts.Title

/**
 * @author Antoine Pirlot on 11/04/2024
 */

@Composable
fun AlbumGrid(
    modifier: Modifier = Modifier,
    mediaList: List<Album>,
    onClick: (album: Album?) -> Unit,
) {
    Box {
        Column {
            Title(
                text = stringResource(id = R.string.albums),
                textAlign = TextAlign.Left,
                fontSize = 20.sp
            )
            val lazyState = rememberLazyListState()
            LazyRow(
                modifier = modifier.fillMaxWidth(),
                state = lazyState
            ) {
                items(
                    items = mediaList,
                    key = { it.id }
                ) { album: Album ->
                    AlbumGridCard(album = album, onClick = onClick)
                    Spacer(modifier = modifier.size(16.dp))
                }
            }
        }
    }
}

@Preview
@Composable
fun AlbumGridPreview() {
    val albumList: MutableList<Album> = mutableListOf()
    for (i: Int in 0..10) {
        albumList.add(Album(id = i.toLong(), title = "Album #$i"))
    }
    AlbumGrid(mediaList = albumList, onClick = {})
}