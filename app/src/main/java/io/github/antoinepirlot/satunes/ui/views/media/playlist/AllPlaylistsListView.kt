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
 * You find this original project on Codeberg.
 *
 * My Codeberg link is: https://codeberg.org/antoinepirlot
 * This current project's link is: https://codeberg.org/antoinepirlot/Satunes
 */

package io.github.antoinepirlot.satunes.ui.views.media.playlist

import androidx.compose.foundation.layout.Box
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
import io.github.antoinepirlot.jetpack_libs.models.JetpackLibsIcons
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.data.local.LocalMainScope
import io.github.antoinepirlot.satunes.data.local.LocalSnackBarHostState
import io.github.antoinepirlot.satunes.data.viewmodels.DataViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.SatunesViewModel
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

    LaunchedEffect(key1 = Unit) {
        dataViewModel.loadMediaImplList(list = dataViewModel.getPlaylistSet())
    }

    LaunchedEffect(key1 = dataViewModel.isLoaded) {
        satunesViewModel.replaceExtraButtons {
            ExtraButton(
                jetpackLibsIcons = JetpackLibsIcons.EXPORT,
                onClick = {
                    dataViewModel.openExportPlaylistDialog()
                }
            )
            ExtraButton(
                jetpackLibsIcons = JetpackLibsIcons.IMPORT,
                onClick = { dataViewModel.openImportPlaylistDialog() }
            )
            ExtraButton(
                jetpackLibsIcons = JetpackLibsIcons.PLAYLIST_ADD,
                onClick = { openAlertDialog = true })
        }
    }

    Box(modifier = modifier) {
        MediaListView(
            emptyViewText = stringResource(id = R.string.no_playlists),
            canBeSorted = false,
        )

        if (openAlertDialog) {
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

@Preview
@Composable
private fun PlaylistListViewPreview() {
    PlaylistListView()
}