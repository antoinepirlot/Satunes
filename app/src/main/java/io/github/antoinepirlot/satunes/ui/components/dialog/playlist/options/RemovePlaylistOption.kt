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

package io.github.antoinepirlot.satunes.ui.components.dialog.playlist.options

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.database.models.Playlist
import io.github.antoinepirlot.satunes.icons.SatunesIcons
import io.github.antoinepirlot.satunes.ui.components.dialog.RemoveConfirmationDialog
import io.github.antoinepirlot.satunes.ui.components.dialog.options.DialogOption
import io.github.antoinepirlot.satunes.data.local.LocalMainScope
import io.github.antoinepirlot.satunes.data.local.LocalSnackBarHostState
import io.github.antoinepirlot.satunes.data.viewmodels.DataViewModel
import kotlinx.coroutines.CoroutineScope

/**
 * @author Antoine Pirlot on 01/06/2024
 */

@Composable
internal fun RemovePlaylistOption(
    modifier: Modifier = Modifier,
    dataViewModel: DataViewModel = viewModel(),
    playlistToRemove: Playlist,
    onDismissRequest: () -> Unit,
) {
    val scope: CoroutineScope = LocalMainScope.current
    val snackBarHostState: SnackbarHostState = LocalSnackBarHostState.current
    var showRemoveConfirmation: Boolean by rememberSaveable { mutableStateOf(false) }

    DialogOption(
        modifier = modifier,
        onClick = { showRemoveConfirmation = true },
        icon = SatunesIcons.PLAYLIST_REMOVE,
        text = stringResource(id = R.string.remove_playlist)
    )

    if (showRemoveConfirmation) {
        RemoveConfirmationDialog(
            onDismissRequest = { showRemoveConfirmation = false },
            onRemoveRequest = {
                dataViewModel.removePlaylist(
                    scope = scope,
                    snackBarHostState = snackBarHostState,
                    playlist = playlistToRemove
                )
                onDismissRequest()
            }
        )
    }
}