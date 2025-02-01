/*
 * This file is part of Satunes.
 *
 * Satunes is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * Satunes is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with Satunes.
 * If not, see <https://www.gnu.org/licenses/>.
 *
 * *** INFORMATION ABOUT THE AUTHOR *****
 * The author of this file is Antoine Pirlot, the owner of this project.
 * You find this original project on github.
 *
 * My github link is: https://github.com/antoinepirlot
 * This current project's link is: https://github.com/antoinepirlot/Satunes
 *
 * PS: I don't answer quickly.
 */

package io.github.antoinepirlot.satunes.ui.components.dialog

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.jetpack_libs.components.texts.NormalText
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.data.local.LocalMainScope
import io.github.antoinepirlot.satunes.data.local.LocalSnackBarHostState
import io.github.antoinepirlot.satunes.data.states.MediaSelectionUiState
import io.github.antoinepirlot.satunes.data.viewmodels.DataViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.MediaSelectionViewModel
import io.github.antoinepirlot.satunes.database.daos.LIKES_PLAYLIST_TITLE
import io.github.antoinepirlot.satunes.database.models.Genre
import io.github.antoinepirlot.satunes.database.models.MediaImpl
import io.github.antoinepirlot.satunes.database.models.Music
import io.github.antoinepirlot.satunes.icons.SatunesIcons
import io.github.antoinepirlot.satunes.ui.components.forms.MediaSelectionForm
import io.github.antoinepirlot.satunes.ui.components.forms.PlaylistCreationForm
import kotlinx.coroutines.CoroutineScope
import io.github.antoinepirlot.satunes.database.R as RDb

/**
 * @author Antoine Pirlot on 30/03/2024
 */

@Composable
internal fun MediaSelectionDialog(
    modifier: Modifier = Modifier,
    mediaSelectionViewModel: MediaSelectionViewModel = viewModel(),
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    mediaImplCollection: Collection<MediaImpl>,
    mediaDestination: MediaImpl,
    playlistTitle: String? = null,
    icon: SatunesIcons,
) {
    val mediaSelectionUiState: MediaSelectionUiState by mediaSelectionViewModel.uiState.collectAsState()

    //No LaunchedEffect as it crash because it's run after composition and Satunes needs this modification
    //on composition
    mediaSelectionViewModel.setCurrentMediaImpl(mediaImpl = mediaDestination)
    val showPlaylistCreation: Boolean = mediaSelectionUiState.showPlaylistCreation

    if (showPlaylistCreation) {
        CreateNewPlaylistForm(
            modifier = modifier,
            onDismissRequest = { mediaSelectionViewModel.setShowPlaylistCreation(value = false) }
        )
    } else {
        MediaSelectionDialogList(
            modifier = modifier,
            onDismissRequest = onDismissRequest,
            onConfirm = onConfirm,
            mediaImplCollection = mediaImplCollection,
            playlistTitle = playlistTitle,
            icon = icon
        )
    }
}

@Composable
private fun CreateNewPlaylistForm(
    modifier: Modifier,
    mediaSelectionViewModel: MediaSelectionViewModel = viewModel(),
    dataViewModel: DataViewModel = viewModel(),
    onDismissRequest: () -> Unit
) {
    val scope: CoroutineScope = LocalMainScope.current
    val snackBarHostState: SnackbarHostState = LocalSnackBarHostState.current

    PlaylistCreationForm(
        modifier = modifier,
        onConfirm = { playlistTitle: String ->
            dataViewModel.addOnePlaylist(
                scope = scope,
                snackBarHostState = snackBarHostState,
                playlistTitle = playlistTitle,
                onPlaylistAdded = {
                    mediaSelectionViewModel.addPlaylist(dataViewModel.getPlaylist(title = playlistTitle))
                }
            )
            mediaSelectionViewModel.setShowPlaylistCreation(value = false)
        },
        onDismissRequest = onDismissRequest
    )
}

@Composable
private fun MediaSelectionDialogList(
    modifier: Modifier,
    mediaSelectionViewModel: MediaSelectionViewModel = viewModel(),
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    mediaImplCollection: Collection<MediaImpl>,
    playlistTitle: String? = null,
    icon: SatunesIcons,
) {
    AlertDialog(
        modifier = modifier.padding(vertical = 50.dp),
        icon = {
            Icon(imageVector = icon.imageVector, contentDescription = icon.description)
        },
        title = {
            if (mediaImplCollection.isEmpty()) {
                NormalText(text = stringResource(id = R.string.no_music))
            } else if (mediaImplCollection.first() is Music) {
                if (playlistTitle == null) {
                    throw IllegalStateException("PlaylistDB title is required when adding music to playlistDB")
                }
                @Suppress("NAME_SHADOWING")
                val playlistTitle: String =
                    if (playlistTitle == LIKES_PLAYLIST_TITLE)
                        stringResource(RDb.string.likes_playlist_title)
                    else
                        playlistTitle

                NormalText(text = stringResource(id = R.string.add_to) + playlistTitle)
            } else {
                NormalText(text = stringResource(id = R.string.add_to_playlist))
            }
        },
        text = {
            MediaSelectionForm(mediaImplCollection = mediaImplCollection)
        },
        onDismissRequest = {
            cancel(
                mediaSelectionViewModel = mediaSelectionViewModel,
                onDismissRequest = onDismissRequest
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                if (mediaImplCollection.isNotEmpty()) {
                    NormalText(text = stringResource(id = R.string.update))
                }
            }
        },
        dismissButton = {
            TextButton(onClick = {
                cancel(
                    mediaSelectionViewModel = mediaSelectionViewModel,
                    onDismissRequest = onDismissRequest
                )
            }) {
                NormalText(text = stringResource(id = R.string.cancel))
            }
        }
    )
}

private fun cancel(mediaSelectionViewModel: MediaSelectionViewModel, onDismissRequest: () -> Unit) {
    mediaSelectionViewModel.clearAll()
    onDismissRequest()
}

@Preview
@Composable
private fun PlaylistSelectionDialogPreview() {
    MediaSelectionDialog(
        icon = SatunesIcons.PLAYLIST_ADD,
        onDismissRequest = {},
        onConfirm = {},
        mediaDestination = Genre(""),
        mediaImplCollection = listOf()
    )
}