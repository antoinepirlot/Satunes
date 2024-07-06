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

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.database.models.Album
import io.github.antoinepirlot.satunes.database.models.Artist
import io.github.antoinepirlot.satunes.database.models.Folder
import io.github.antoinepirlot.satunes.database.models.Genre
import io.github.antoinepirlot.satunes.database.models.Music
import io.github.antoinepirlot.satunes.database.services.DataUpdater
import io.github.antoinepirlot.satunes.ui.components.forms.AlbumForm
import io.github.antoinepirlot.satunes.ui.components.forms.ArtistForm
import io.github.antoinepirlot.satunes.ui.components.forms.GenreForm
import io.github.antoinepirlot.satunes.ui.components.forms.MusicForm
import io.github.antoinepirlot.satunes.ui.components.texts.NormalText
import io.github.antoinepirlot.satunes.ui.components.texts.Title

/**
 * @author Antoine Pirlot on 06/07/2024
 */

@Composable
@RequiresApi(Build.VERSION_CODES.R)
internal fun MusicFormView(
    modifier: Modifier = Modifier,
    music: Music,
) {
    val musicToUpdate: Music = music.clone() as Music
    val scrollState: ScrollState = rememberScrollState()
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(state = scrollState),
    ) {
        Title(text = stringResource(id = R.string.edit_dialog_option))

        MusicForm(music = musicToUpdate)
        Spacer(modifier = Modifier.size(16.dp))

        ArtistForm(artist = musicToUpdate.artist)
        Spacer(modifier = Modifier.size(16.dp))

        AlbumForm(album = musicToUpdate.album)
        Spacer(modifier = Modifier.size(16.dp))

        GenreForm(genre = musicToUpdate.genre)
        Spacer(modifier = Modifier.size(16.dp))

        val context: Context = LocalContext.current
        Button(
            modifier = Modifier.align(alignment = Alignment.End),
            onClick = {
                DataUpdater.update(context = context, music = musicToUpdate)
            }
        ) {
            NormalText(text = stringResource(id = R.string.submit_edit_view))
        }
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