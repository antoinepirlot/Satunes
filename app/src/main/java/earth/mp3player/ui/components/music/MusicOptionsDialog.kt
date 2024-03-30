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

package earth.mp3player.ui.components.music

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import earth.mp3player.R

/**
 * @author Antoine Pirlot on 30/03/2024
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicOptionsDialog(
    modifier: Modifier = Modifier,
    musicTitle: String,
    onAddToPlaylist: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        icon = {
            Icon(
                imageVector = Icons.Rounded.MusicNote,
                contentDescription = "Music Options Icon"
            )
        },
        title = {
            Text(text = musicTitle)
        },
        text = {
            Column {
                TextButton(onClick = onAddToPlaylist) {
                    Row {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.QueueMusic,
                            contentDescription = "Playlist Icon"
                        )
                        Spacer(modifier = Modifier.size(10.dp))
                        Text(text = stringResource(id = R.string.add_to_playlist))
                    }
                }
            }
        },
        onDismissRequest = onDismissRequest,
        confirmButton = { /* Nothing to do */ }
    )
}

@Preview
@Composable
fun MusicOptionsDialogPreview() {
    MusicOptionsDialog(
        musicTitle = "Music Title",
        onAddToPlaylist = {},
        onDismissRequest = {}
    )
}