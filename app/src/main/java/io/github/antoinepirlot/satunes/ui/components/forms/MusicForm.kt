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

import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.database.models.Album
import io.github.antoinepirlot.satunes.database.models.Artist
import io.github.antoinepirlot.satunes.database.models.Folder
import io.github.antoinepirlot.satunes.database.models.Genre
import io.github.antoinepirlot.satunes.database.models.Music
import io.github.antoinepirlot.satunes.ui.components.texts.NormalText

/**
 * @author Antoine Pirlot on 06/07/2024
 */

@Composable
internal fun MusicForm(
    modifier: Modifier = Modifier,
    music: Music
) {
    var musicTitle: String by rememberSaveable { mutableStateOf(music.title) }
    OutlinedTextField(
        value = musicTitle,
        onValueChange = {
            musicTitle = it
            music.title = it
        },
        singleLine = true,
        label = {
            NormalText(text = stringResource(id = R.string.title))
        },
    )
}

@Preview
@Composable
private fun MusicFormPreview() {
    val album = Album(title = "Album title")
    val artist = Artist(title = "Artist title")
    val genre = Genre(title = "Genre title")
    val folder = Folder(title = "Folder title")
    val music = Music(
        id = 0,
        title = "Music title",
        album = album,
        artist = artist,
        genre = genre,
        absolutePath = "",
        context = LocalContext.current,
        folder = folder,
        size = 0,
        duration = 0,
        displayName = "Display Name"
    )
    MusicForm(music = music)
}