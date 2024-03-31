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

package earth.mp3player.ui.components.music.options

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.PlaylistRemove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import earth.mp3player.R

/**
 * @author Antoine Pirlot on 30/03/2024
 */

val SPACER_SIZE = 10.dp

@Composable
fun MusicOptionsDialog(
    modifier: Modifier = Modifier,
    musicTitle: String,
    onAddToPlaylist: () -> Unit,
    onRemoveFromPlaylist: () -> Unit,
    onDismissRequest: () -> Unit
) {
    var showPlaylistSelectionDialog: Boolean by rememberSaveable { mutableStateOf(false) }

    AlertDialog(
        modifier = modifier,
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
                TextButton(onClick = {
                    showPlaylistSelectionDialog = true
                }) {
                    Row {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.QueueMusic,
                            contentDescription = "Playlist Icon"
                        )
                        Spacer(modifier = Modifier.size(SPACER_SIZE))
                        Text(text = stringResource(id = R.string.add_to_playlist))
                    }
                    if (showPlaylistSelectionDialog) {
                        PlaylistSelectionDialog(
                            onDismissRequest = {
                                showPlaylistSelectionDialog = false
                            },
                            onConfirm = onAddToPlaylist
                        )
                    }
                }
                TextButton(onClick = {
                    onRemoveFromPlaylist()
                    onDismissRequest()
                }) {
                    Row {
                        Icon(
                            imageVector = Icons.Rounded.PlaylistRemove,
                            contentDescription = "Playlist remove icon"
                        )
                        Spacer(modifier = Modifier.size(SPACER_SIZE))
                        Text(text = stringResource(id = R.string.remove_from_playlist))
                    }
                }
            }
        },
        onDismissRequest = {
            onDismissRequest()
            showPlaylistSelectionDialog = false
        },
        confirmButton = { /* Nothing */ }
    )
}

@Preview
@Composable
fun MusicOptionsDialogPreview() {
    MusicOptionsDialog(
        musicTitle = "Music Title",
        onAddToPlaylist = {},
        onRemoveFromPlaylist = {},
        onDismissRequest = {}
    )
}