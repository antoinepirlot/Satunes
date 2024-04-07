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

package earth.galacticmusic.ui.components.music.options

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import earth.galacticmusic.R
import earth.galacticmusic.ui.components.forms.PlaylistSelectionForm
import earth.galacticmusic.ui.views.MP3PlayerIcons

/**
 * @author Antoine Pirlot on 30/03/2024
 */

@Composable
fun PlaylistSelectionDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        modifier = modifier,
        icon = {
            Icon(
                imageVector = MP3PlayerIcons.PLAYLIST_ADD.imageVector,
                contentDescription = "Playlist Selection Icon"
            )
        },
        title = {
            Text(text = stringResource(id = R.string.add_to_playlist))
        },
        text = {
            Column {
                PlaylistSelectionForm()
            }
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = stringResource(id = R.string.add))
            }
        }
    )
}

@Preview
@Composable
fun PlaylistSelectionDialogPreview() {
    PlaylistSelectionDialog(
        onDismissRequest = {},
        onConfirm = {}
    )
}