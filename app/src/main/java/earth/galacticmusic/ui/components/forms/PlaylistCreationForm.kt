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

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import earth.galacticmusic.R
import earth.galacticmusic.ui.views.MP3PlayerIcons

/**
 * @author Antoine Pirlot on 30/03/2024
 */

@Composable
fun PlaylistCreationForm(
    modifier: Modifier = Modifier,
    onConfirm: (playlistTitle: String) -> Unit,
    onDismissRequest: () -> Unit,
) {
    var playlistTitle: String by rememberSaveable { mutableStateOf("") }

    AlertDialog(
        icon = {
            val icon: MP3PlayerIcons = MP3PlayerIcons.PLAYLIST_ADD
            Icon(
                imageVector = icon.imageVector,
                contentDescription = "Playlist Creation Button Icon"
            )
        },
        title = {
            Text(text = stringResource(id = R.string.create_playlist))
        },
        text = {
            Column(
                modifier = modifier
            ) {
                OutlinedTextField(
                    value = playlistTitle,
                    onValueChange = { playlistTitle = it },
                    label = { Text(text = stringResource(id = R.string.playlist_form)) }
                )
            }
        },
        onDismissRequest = { onDismissRequest() },
        confirmButton = {
            TextButton(onClick = { onConfirm(playlistTitle) }) {
                Text(text = stringResource(id = R.string.create))
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismissRequest() }) {
                Text(text = stringResource(id = R.string.cancel))
            }
        }
    )

}

@Preview
@Composable
fun PlaylistCreationFormPreview() {
    PlaylistCreationForm(onConfirm = {}, onDismissRequest = {})
}