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

package io.github.antoinepirlot.satunes.ui.components.dialog.playlist.options

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import io.github.antoinepirlot.jetpack_libs.models.JetpackLibsIcons
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.data.local.LocalMainScope
import io.github.antoinepirlot.satunes.data.local.LocalNavController
import io.github.antoinepirlot.satunes.data.local.LocalSnackBarHostState
import io.github.antoinepirlot.satunes.data.states.NavigationUiState
import io.github.antoinepirlot.satunes.data.viewmodels.DataViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.NavigationViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.SubsonicViewModel
import io.github.antoinepirlot.satunes.database.models.media.Playlist
import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicPlaylist
import io.github.antoinepirlot.satunes.ui.components.dialog.RemoveConfirmationDialog
import io.github.antoinepirlot.satunes.ui.components.dialog.options.DialogOption
import kotlinx.coroutines.CoroutineScope

/**
 * @author Antoine Pirlot on 01/06/2024
 */

@Composable
internal fun RemovePlaylistOption(
    modifier: Modifier = Modifier,
    dataViewModel: DataViewModel = viewModel(),
    navigationViewModel: NavigationViewModel = viewModel(),
    subsonicViewModel: SubsonicViewModel = viewModel(),
    playlistToRemove: Playlist,
    onDismissRequest: () -> Unit,
) {
    val navigationUiState: NavigationUiState by navigationViewModel.uiState.collectAsState()
    val navController: NavHostController = LocalNavController.current
    val scope: CoroutineScope = LocalMainScope.current
    val snackBarHostState: SnackbarHostState = LocalSnackBarHostState.current
    var showRemoveConfirmation: Boolean by rememberSaveable { mutableStateOf(false) }

    DialogOption(
        modifier = modifier,
        onClick = { showRemoveConfirmation = true },
        jetpackLibsIcons = JetpackLibsIcons.PLAYLIST_REMOVE,
        text = stringResource(id = R.string.remove_playlist)
    )

    if (showRemoveConfirmation) {
        RemoveConfirmationDialog(
            onDismissRequest = { showRemoveConfirmation = false },
            onRemoveRequest = {
                if (playlistToRemove.isSubsonic())
                    subsonicViewModel.deletePlaylist(
                        playlist = playlistToRemove as SubsonicPlaylist,
                        onRemoved = {
                            if (navigationUiState.currentMediaImpl == playlistToRemove)
                                navigationViewModel.popBackStack(navController = navController)
                            else //Here the view is all playlists. It needs to be refreshed
                                dataViewModel.loadMediaImplList(collection = dataViewModel.getSubsonicPlaylists())
                        }
                    )
                else
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