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

package io.github.antoinepirlot.satunes.ui.components.dialog.playlist

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.database.daos.LIKES_PLAYLIST_TITLE
import io.github.antoinepirlot.satunes.database.models.relations.PlaylistWithMusics
import io.github.antoinepirlot.satunes.database.models.tables.Playlist
import io.github.antoinepirlot.satunes.database.services.DataManager
import io.github.antoinepirlot.satunes.database.services.DatabaseManager
import io.github.antoinepirlot.satunes.icons.SatunesIcons
import io.github.antoinepirlot.satunes.ui.components.dialog.playlist.options.ExportPlaylistOption
import io.github.antoinepirlot.satunes.ui.components.dialog.playlist.options.RemovePlaylistOption
import io.github.antoinepirlot.satunes.ui.components.texts.NormalText
import io.github.antoinepirlot.satunes.database.R as RDb

/**
 * @author Antoine Pirlot on 01/04/2024
 */

@Composable
internal fun PlaylistOptionsDialog(
    modifier: Modifier = Modifier,
    playlistWithMusics: PlaylistWithMusics,
    onDismissRequest: () -> Unit,
) {
    var playlistTitle: String by remember { mutableStateOf(playlistWithMusics.playlist.title) }
    AlertDialog(
        modifier = modifier,
        icon = {
            Icon(
                imageVector = SatunesIcons.PLAYLIST.imageVector,
                contentDescription = "Playlist Options Icon"
            )
        },
        title = {
            if (playlistTitle == LIKES_PLAYLIST_TITLE) {
                NormalText(text = stringResource(id = RDb.string.likes_playlist_title))
            } else {
                OutlinedTextField(
                    value = playlistTitle,
                    onValueChange = {
                        playlistTitle = it
                    },
                    label = {
                        NormalText(text = stringResource(id = R.string.title))
                    },
                    placeholder = {
                        NormalText(text = stringResource(id = R.string.title))
                    }
                )
            }
        },
        text = {
            Column {
                RemovePlaylistOption(
                    playlistToRemove = playlistWithMusics,
                    onFinished = onDismissRequest
                )

                ExportPlaylistOption(playlistToExport = playlistWithMusics)
            }
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            val context: Context = LocalContext.current
            TextButton(onClick = {
                onDismissRequest()
                val oldTitle: String = playlistWithMusics.playlist.title
                playlistWithMusics.playlist.title = playlistTitle
                // TODO this case must be managed in database module
                try {
                    val db = DatabaseManager(context = context)
                    db.updatePlaylists(playlistWithMusics.playlist)
                    DataManager.updatePlaylist(
                        oldTitle = oldTitle,
                        playlistWithMusics = playlistWithMusics
                    )
                } catch (_: Exception) {
                    playlistWithMusics.playlist.title = oldTitle
                }
            }) {
                NormalText(text = stringResource(id = R.string.ok))
            }
        }
    )
}

@Preview
@Composable
private fun PlaylistOptionsDialogPreview() {
    PlaylistOptionsDialog(
        playlistWithMusics = PlaylistWithMusics(Playlist(1, "Playlist Title"), mutableListOf()),
        onDismissRequest = {},
    )
}