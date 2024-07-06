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

package io.github.antoinepirlot.satunes.ui.components.forms

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.database.models.Album
import io.github.antoinepirlot.satunes.ui.components.images.AlbumArtwork
import io.github.antoinepirlot.satunes.ui.components.texts.NormalText
import io.github.antoinepirlot.satunes.ui.components.texts.Title

/**
 * @author Antoine Pirlot on 06/07/2024
 */

@Composable
internal fun AlbumForm(
    modifier: Modifier = Modifier,
    album: Album,
) {
    Column(
        modifier = modifier,
    ) {
        Title(text = stringResource(id = R.string.album_form_title), fontSize = 30.sp)

        AlbumArtwork(
            modifier = Modifier
                .size(100.dp)
                .align(alignment = Alignment.CenterHorizontally),
            onClick = { /* TODO launch change picture process */ },
            media = album
        )

        Spacer(modifier = Modifier.size(16.dp))

        var albumTitle: String by rememberSaveable { mutableStateOf(album.title) }
        OutlinedTextField(
            value = albumTitle,
            onValueChange = {
                albumTitle = it
                album.title = it
            },
            label = {
                NormalText(text = stringResource(id = R.string.title))
            }
        )
    }
}

@Preview
@Composable
private fun AlbumFormPreview() {
    val album = Album(title = "Album title")
    AlbumForm(album = album)
}