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

package io.github.antoinepirlot.satunes.ui.views.media.forms

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import io.github.antoinepirlot.satunes.database.models.Album
import io.github.antoinepirlot.satunes.database.models.Artist
import io.github.antoinepirlot.satunes.database.models.Folder
import io.github.antoinepirlot.satunes.database.models.Genre
import io.github.antoinepirlot.satunes.database.models.Music
import io.github.antoinepirlot.satunes.ui.components.forms.MusicForm

/**
 * @author Antoine Pirlot on 06/07/2024
 */

@Composable
internal fun MusicFormView(
    modifier: Modifier = Modifier,
    music: Music,
) {
    Column(
        modifier = modifier,
    ) {
        MusicForm(music = music)

        /**
         * Artist
         */

        /**
         * Album
         */

        /**
         * Genre
         */
    }
}

@Preview
@Composable
private fun MusicFormViewPreview() {
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
    MusicFormView(music = music)
}