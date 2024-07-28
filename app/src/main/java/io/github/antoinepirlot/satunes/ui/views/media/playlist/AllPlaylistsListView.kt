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

package io.github.antoinepirlot.satunes.ui.views.media.playlist

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.database.models.MediaImpl
import io.github.antoinepirlot.satunes.database.models.Playlist
import io.github.antoinepirlot.satunes.icons.SatunesIcons
import io.github.antoinepirlot.satunes.router.utils.openCurrentMusic
import io.github.antoinepirlot.satunes.router.utils.openMedia
import io.github.antoinepirlot.satunes.ui.components.buttons.ExtraButton
import io.github.antoinepirlot.satunes.ui.components.forms.PlaylistCreationForm
import io.github.antoinepirlot.satunes.ui.local.LocalMainScope
import io.github.antoinepirlot.satunes.ui.local.LocalSnackBarHostState
import io.github.antoinepirlot.satunes.ui.viewmodels.DataViewModel
import io.github.antoinepirlot.satunes.ui.viewmodels.PlaybackViewModel
import io.github.antoinepirlot.satunes.ui.views.media.MediaListView
import kotlinx.coroutines.CoroutineScope

/**
 * @author Antoine Pirlot on 30/03/2024
 */

@Composable
internal fun PlaylistListView(
    modifier: Modifier = Modifier,
    dataViewModel: DataViewModel = viewModel(),
    playbackViewModel: PlaybackViewModel = viewModel(),
    navController: NavHostController,
) {
    val scope: CoroutineScope = LocalMainScope.current
    val snackBarHostState: SnackbarHostState = LocalSnackBarHostState.current
    var openAlertDialog by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        val playlistSet: Set<Playlist> = dataViewModel.getPlaylistSet()

        //Recompose if data changed
        val mapChanged: Boolean = dataViewModel.playlistSetUpdated
        if (mapChanged) {
            dataViewModel.playlistSetUpdated()
        }
        //

        MediaListView(
            mediaImplCollection = playlistSet,
            navController = navController,
            openMedia = { clickedMediaImpl: MediaImpl ->
                openMedia(
                    playbackViewModel = playbackViewModel,
                    media = clickedMediaImpl,
                    navController = navController
                )
            },
            onFABClick = {
                openCurrentMusic(
                    playbackViewModel = playbackViewModel,
                    navController = navController
                )
            },
            extraButtons = {
                ExtraButton(icon = SatunesIcons.PLAYLIST_ADD, onClick = { openAlertDialog = true })
            },
            emptyViewText = stringResource(id = R.string.no_playlists)
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
    val navController: NavHostController = rememberNavController()
    PlaylistListView(navController = navController)
}