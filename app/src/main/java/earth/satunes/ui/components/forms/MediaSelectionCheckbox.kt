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

package earth.satunes.ui.components.forms

import android.net.Uri.decode
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import earth.satunes.database.models.Media
import earth.satunes.database.models.Music
import earth.satunes.database.models.relations.PlaylistWithMusics
import earth.satunes.database.models.tables.Playlist
import earth.satunes.services.MediaSelectionManager

/**
 * @author Antoine Pirlot on 30/03/2024
 */

@Composable
fun MediaSelectionCheckbox(
    modifier: Modifier = Modifier,
    media: Media
) {
    var checked: Boolean by rememberSaveable { mutableStateOf(false) }
    val text: String = if (media is PlaylistWithMusics) decode(media.playlist.title)
                       else decode(media.title)

    Row(modifier = modifier) {
        Checkbox(checked = checked, onCheckedChange = {
            checked = !checked
            if (media is PlaylistWithMusics) {
                checkPlaylist(checked = checked, playlist = media)
            } else if (media is Music) {
                checkMusic(checked = checked, music = media)
            }
        })
        Spacer(modifier = modifier.size(10.dp))
        Text(
            modifier = Modifier.align(Alignment.CenterVertically),
            text = text
        )
    }
}

private fun checkPlaylist(checked: Boolean, playlist: PlaylistWithMusics) {
    if (checked) {
        MediaSelectionManager.checkedPlaylistWithMusics.add(playlist)
    } else {
        MediaSelectionManager.checkedPlaylistWithMusics.remove(playlist)
    }
}

private fun checkMusic(checked: Boolean, music: Music) {
    if (checked) {
        MediaSelectionManager.checkedMusics.add(music)
    } else {
        MediaSelectionManager.checkedMusics.remove(music)
    }
}

@Preview
@Composable
fun PlaylistSelectionCheckboxPreview() {
    MediaSelectionCheckbox(
        media = PlaylistWithMusics(
            playlist = Playlist(id = 0, title = ""),
            musics = mutableListOf()
        )
    )
}