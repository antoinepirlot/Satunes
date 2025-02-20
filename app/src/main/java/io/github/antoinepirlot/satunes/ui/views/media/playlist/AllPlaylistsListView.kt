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

package io.github.antoinepirlot.satunes.ui.views.media.playlist

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.data.local.LocalMainScope
import io.github.antoinepirlot.satunes.data.local.LocalSnackBarHostState
import io.github.antoinepirlot.satunes.data.viewmodels.DataViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.SatunesViewModel
import io.github.antoinepirlot.satunes.database.models.Playlist
import io.github.antoinepirlot.satunes.icons.SatunesIcons
import io.github.antoinepirlot.satunes.ui.components.buttons.fab.ExtraButton
import io.github.antoinepirlot.satunes.ui.components.forms.PlaylistCreationForm
import io.github.antoinepirlot.satunes.ui.views.media.MediaListView
import kotlinx.coroutines.CoroutineScope

/**
 * @author Antoine Pirlot on 30/03/2024
 */

@Composable
internal fun PlaylistListView(
    modifier: Modifier = Modifier,
    satunesViewModel: SatunesViewModel = viewModel(),
    dataViewModel: DataViewModel = viewModel(),
) {
    val scope: CoroutineScope = LocalMainScope.current
    val snackBarHostState: SnackbarHostState = LocalSnackBarHostState.current
    var openAlertDialog by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = dataViewModel.isLoaded) {
        satunesViewModel.replaceExtraButtons {
            ExtraButton(
                icon = SatunesIcons.EXPORT,
                onClick = {
                    dataViewModel.exportPlaylists(
                        scope = scope,
                        snackBarHostState = snackBarHostState
                    )
                }
            )
            ExtraButton(
                icon = SatunesIcons.IMPORT,
                onClick = { dataViewModel.importPlaylists() }
            )
            ExtraButton(icon = SatunesIcons.PLAYLIST_ADD, onClick = { openAlertDialog = true })
        }
    }

    Column(modifier = modifier) {
        var playlistSet: Set<Playlist>? by remember { mutableStateOf(null) }

        //Recompose if data changed
        val setChanged: Boolean = dataViewModel.playlistSetUpdated
        if (setChanged) {
            dataViewModel.playlistSetUpdated()
            dataViewModel.listSetUpdatedUnprocessed()
        }

        LaunchedEffect(key1 = dataViewModel.playlistSetUpdated) {
            playlistSet = dataViewModel.getPlaylistSet()
        }

        if (playlistSet == null) return

        MediaListView(
            mediaImplCollection = playlistSet!!,
            collectionChanged = setChanged,
            emptyViewText = stringResource(id = R.string.no_playlists),
            showGroupIndication = false,
            sort = false,
        )

        when {
            openAlertDialog -> {
                PlaylistCreationForm(
                    onConfirm = { playlistTitle: String ->
                        dataViewModel.addOnePlaylist(
                            scope = scope,
                            snackBarHostState = snackBarHostState,
                            playlistTitle = playlistTitle
                        )
                        openAlertDialog = false
                    },
                    onDismissRequest = { openAlertDialog = false }
                )
            }
        }
    }
}

@Preview
@Composable
private fun PlaylistListViewPreview() {
    PlaylistListView()
}