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

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.jetpack_libs.components.texts.NormalText
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.data.local.LocalMainScope
import io.github.antoinepirlot.satunes.data.local.LocalSnackBarHostState
import io.github.antoinepirlot.satunes.data.viewmodels.DataViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.PlaybackViewModel
import io.github.antoinepirlot.satunes.database.daos.LIKES_PLAYLIST_TITLE
import io.github.antoinepirlot.satunes.database.models.Playlist
import io.github.antoinepirlot.satunes.icons.SatunesIcons
import io.github.antoinepirlot.satunes.ui.components.dialog.media.options.AddToQueueDialogOption
import io.github.antoinepirlot.satunes.ui.components.dialog.media.options.PlayNextMediaOption
import io.github.antoinepirlot.satunes.ui.components.dialog.media.options.RemoveFromQueueOption
import io.github.antoinepirlot.satunes.ui.components.dialog.media.options.ShareMediaOption
import io.github.antoinepirlot.satunes.ui.components.dialog.playlist.options.ExportPlaylistOption
import io.github.antoinepirlot.satunes.ui.components.dialog.playlist.options.RemovePlaylistOption
import kotlinx.coroutines.CoroutineScope
import io.github.antoinepirlot.satunes.database.R as RDb

/**
 * @author Antoine Pirlot on 01/04/2024
 */

@Composable
internal fun PlaylistOptionsDialog(
    modifier: Modifier = Modifier,
    playbackViewModel: PlaybackViewModel = viewModel(),
    dataViewModel: DataViewModel = viewModel(),
    playlist: Playlist,
    onDismissRequest: () -> Unit,
) {
    val scope: CoroutineScope = LocalMainScope.current
    val snackBarHostState: SnackbarHostState = LocalSnackBarHostState.current
    var playlistTitle: String by remember { mutableStateOf(playlist.title) }

    AlertDialog(
        modifier = modifier,
        icon = {
            Icon(
                imageVector = SatunesIcons.PLAYLIST.imageVector,
                contentDescription = "PlaylistDB Options Icon"
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
                ExportPlaylistOption(playlist = playlist)
                RemovePlaylistOption(
                    playlistToRemove = playlist,
                    onDismissRequest = onDismissRequest
                )
                if (playbackViewModel.isLoaded && playlist.musicCount() <= 500) {
                    PlayNextMediaOption(mediaImpl = playlist, onDismissRequest = onDismissRequest)
                    AddToQueueDialogOption(
                        mediaImpl = playlist,
                        onDismissRequest = onDismissRequest
                    )
                    RemoveFromQueueOption(
                        mediaImpl = playlist,
                        onDismissRequest = onDismissRequest
                    )
                }

                /**
                 * Share
                 */
                ShareMediaOption(media = playlist)
            }
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = {
                val oldTitle: String = playlist.title
                playlist.title = playlistTitle
                try {
                    dataViewModel.updatePlaylistTitle(
                        scope = scope,
                        snackBarHostState = snackBarHostState,
                        playlist = playlist
                    )
                } catch (_: Exception) {
                    playlist.title = oldTitle
                } finally {

                    onDismissRequest()
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
        playlist = Playlist(id = 1, "PlaylistDB Title"),
        onDismissRequest = {},
    )
}