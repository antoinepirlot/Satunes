/*
 * This file is part of Satunes.
 *
 * Satunes is free software: you can redistribute it and/or modify it under
 *  the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * Satunes is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Satunes.
 * If not, see <https://www.gnu.org/licenses/>.
 *
 * **** INFORMATIONS ABOUT THE AUTHOR *****
 * The author of this file is Antoine Pirlot, the owner of this project.
 * You find this original project on github.
 *
 * My github link is: https://github.com/antoinepirlot
 * This current project's link is: https://github.com/antoinepirlot/MP3-Player
 *
 * You can contact me via my email: pirlot.antoine@outlook.com
 * PS: I don't answer quickly.
 */

package earth.satunes.ui.components.dialog

import android.net.Uri.decode
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
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
import earth.satunes.R
import earth.satunes.database.models.relations.PlaylistWithMusics
import earth.satunes.database.services.DataManager
import earth.satunes.services.MediaSelectionManager
import earth.satunes.ui.components.cards.dialog.DialogOption
import earth.satunes.ui.views.SatunesIcons

/**
 * @author Antoine Pirlot on 30/03/2024
 */


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
                imageVector = SatunesIcons.MUSIC.imageVector,
                contentDescription = "Music Options Icon"
            )
        },
        title = {
            Text(text = decode(musicTitle))
        },
        text = {
            Column {
                DialogOption(
                    onClick = { showPlaylistSelectionDialog = true },
                    icon = {
                        val playlistIcon: SatunesIcons = SatunesIcons.PLAYLIST_ADD
                        Icon(
                            imageVector = playlistIcon.imageVector,
                            contentDescription = playlistIcon.description
                        )
                    },
                    text = stringResource(id = R.string.add_to_playlist)
                )
                if (showPlaylistSelectionDialog) {
                    val playlistList: List<PlaylistWithMusics> =
                        DataManager.playlistWithMusicsMap.values.toList()
                    MediaSelectionDialog(
                        onDismissRequest = {
                            showPlaylistSelectionDialog = false
                        },
                        onConfirm = onAddToPlaylist,
                        mediaList = playlistList,
                        icon = {
                            Icon(
                                imageVector = SatunesIcons.PLAYLIST_ADD.imageVector,
                                contentDescription = "Playlist Selection Icon"
                            )
                        }
                    )
                }
                if (MediaSelectionManager.openedPlaylist != null) {
                    DialogOption(
                        onClick = {
                            onRemoveFromPlaylist()
                            onDismissRequest()
                        },
                        icon = {
                            val playlistRemoveIcon: SatunesIcons = SatunesIcons.PLAYLIST_REMOVE
                            Icon(
                                imageVector = playlistRemoveIcon.imageVector,
                                contentDescription = playlistRemoveIcon.description
                            )
                        },
                        text = stringResource(id = R.string.remove_from_playlist)
                    )
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