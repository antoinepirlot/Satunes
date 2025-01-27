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

package io.github.antoinepirlot.satunes.ui.components.bars.bottom

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.satunes.data.local.LocalMainScope
import io.github.antoinepirlot.satunes.data.local.LocalSnackBarHostState
import io.github.antoinepirlot.satunes.data.viewmodels.DataViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.MediaSelectionViewModel
import io.github.antoinepirlot.satunes.database.models.Playlist
import io.github.antoinepirlot.satunes.icons.SatunesIcons
import io.github.antoinepirlot.satunes.ui.components.buttons.fab.ExtraButton
import io.github.antoinepirlot.satunes.ui.components.dialog.MediaSelectionDialog
import kotlinx.coroutines.CoroutineScope

@Composable
internal fun PlaylistExtraButtonList(
    modifier: Modifier = Modifier,
    dataViewModel: DataViewModel = viewModel(),
    mediaSelectionViewModel: MediaSelectionViewModel = viewModel(),
    playlist: Playlist,
) {
    val scope: CoroutineScope = LocalMainScope.current
    val snackbarHostState: SnackbarHostState = LocalSnackBarHostState.current

    var openAddMusicsDialog: Boolean by rememberSaveable { mutableStateOf(false) }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ExtraButton(
            icon = SatunesIcons.EXPORT,
            onClick = { dataViewModel.exportPlaylist(playlist = playlist) }
        )
        ExtraButton(
            icon = SatunesIcons.ADD,
            onClick = { openAddMusicsDialog = true },
        )
    }

    if (openAddMusicsDialog) {
        MediaSelectionDialog(
            onDismissRequest = { openAddMusicsDialog = false },
            onConfirm = {
                dataViewModel.insertMusicsToPlaylist(
                    scope = scope,
                    snackBarHostState = snackbarHostState,
                    musics = mediaSelectionViewModel.getCheckedMusics(),
                    playlist = playlist
                )
                openAddMusicsDialog = false
            },
            mediaImplCollection = dataViewModel.getMusicSet(),
            icon = SatunesIcons.PLAYLIST_ADD,
            playlistTitle = playlist.title
        )
    }
}