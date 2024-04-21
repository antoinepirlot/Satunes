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

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import earth.satunes.R
import earth.satunes.database.models.relations.PlaylistWithMusics
import earth.satunes.database.models.tables.Playlist
import earth.satunes.ui.components.texts.NormalText
import earth.satunes.icons.SatunesIcons

/**
 * @author Antoine Pirlot on 01/04/2024
 */

private val SPACER_SIZE = 10.dp

@Composable
fun PlaylistOptionsDialog(
    modifier: Modifier = Modifier,
    playlistWithMusics: PlaylistWithMusics,
    onRemovePlaylist: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        modifier = modifier,
        icon = {
            Icon(
                imageVector = SatunesIcons.PLAYLIST.imageVector,
                contentDescription = "Playlist Options Icon"
            )
        },
        title = {
            NormalText(text = playlistWithMusics.playlist.title)
        },
        text = {
            Column {
                DialogOption(onClick = {
                    onRemovePlaylist()
                    onDismissRequest()
                    },
                    icon = {
                        val playlistRemoveIcon: SatunesIcons = SatunesIcons.PLAYLIST_REMOVE
                        Icon(
                            imageVector = playlistRemoveIcon.imageVector,
                            contentDescription = playlistRemoveIcon.description
                        )
                    },
                    text = stringResource(id = R.string.remove_playlist)
                )
            }
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = { /* Nothing */ }
    )
}

@Preview
@Composable
fun PlaylistOptionsDialogPreview() {
    PlaylistOptionsDialog(
        playlistWithMusics = PlaylistWithMusics(Playlist(1, "Playlist Title"), mutableListOf()),
        onRemovePlaylist = {},
        onDismissRequest = {},
    )
}