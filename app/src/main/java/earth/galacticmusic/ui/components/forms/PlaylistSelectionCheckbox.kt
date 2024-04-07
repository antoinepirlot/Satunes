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

package earth.galacticmusic.ui.components.forms

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
import earth.galacticmusic.database.models.relations.PlaylistWithMusics
import earth.galacticmusic.database.models.tables.Playlist
import earth.galacticmusic.services.PlaylistSelectionManager

/**
 * @author Antoine Pirlot on 30/03/2024
 */

@Composable
fun PlaylistSelectionCheckbox(
    modifier: Modifier = Modifier,
    playlistWithMusics: PlaylistWithMusics
) {
    var checked: Boolean by rememberSaveable { mutableStateOf(false) }
    Row(modifier = modifier) {
        Checkbox(checked = checked, onCheckedChange = {
            checked = !checked
            if (checked) {
                PlaylistSelectionManager.checkedPlaylistWithMusics.add(playlistWithMusics)
            } else {
                PlaylistSelectionManager.checkedPlaylistWithMusics.remove(playlistWithMusics)
            }
        })
        Spacer(modifier = modifier.size(10.dp))
        Text(
            modifier = Modifier.align(Alignment.CenterVertically),
            text = playlistWithMusics.playlist.title
        )
    }
}

@Preview
@Composable
fun PlaylistSelectionCheckboxPreview() {
    PlaylistSelectionCheckbox(
        playlistWithMusics = PlaylistWithMusics(
            playlist = Playlist(id = 0, title = ""),
            musics = mutableListOf()
        )
    )
}