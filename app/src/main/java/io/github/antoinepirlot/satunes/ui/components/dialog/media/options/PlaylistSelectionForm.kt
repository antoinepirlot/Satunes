/*
 *
 * This file is part of Satunes.
 * Satunes is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *  Satunes is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License along with Satunes.
 *  If not, see <https://www.gnu.org/licenses/>.
 *
 * *** INFORMATION ABOUT THE AUTHOR *****
 * The author of this file is Antoine Pirlot, the owner of this project.
 * You find this original project on Codeberg.
 *
 * My Codeberg link is: https://codeberg.org/antoinepirlot
 * This current project's link is: https://codeberg.org/antoinepirlot/Satunes
 */

package io.github.antoinepirlot.satunes.ui.components.dialog.media.options

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.jetpack_libs.models.JetpackLibsIcons
import io.github.antoinepirlot.satunes.data.viewmodels.DataViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.SatunesViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.SubsonicViewModel
import io.github.antoinepirlot.satunes.database.models.media.MediaImpl
import io.github.antoinepirlot.satunes.database.models.media.Playlist
import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicPlaylist
import io.github.antoinepirlot.satunes.ui.components.dialog.MediaSelectionDialog

/**
 * @author Antoine Pirlot on 13/01/2026
 */

@Composable
fun PlaylistSelectionForm(
    modifier: Modifier = Modifier,
    satunesViewModel: SatunesViewModel = viewModel(),
    dataViewModel: DataViewModel = viewModel(),
    subsonicViewModel: SubsonicViewModel = viewModel(),
    onConfirm: () -> Unit,
    mediaImpl: MediaImpl,
) {
    val playlists: MutableCollection<Playlist> = mutableStateListOf()

    LaunchedEffect(key1 = Unit) {
        playlists.addAll(elements = dataViewModel.getPlaylistSet())
        if (mediaImpl.isSubsonic()) {
            val cloudPlaylists: Collection<SubsonicPlaylist> =
                dataViewModel.getSubsonicPlaylistCollection()
            if (cloudPlaylists.isEmpty())
                subsonicViewModel.getPlaylists(onDataRetrieved = { playlists.addAll(elements = it) })
            else
                playlists.addAll(elements = cloudPlaylists)
        }
    }

    MediaSelectionDialog(
        modifier = modifier,
        onDismissRequest = { satunesViewModel.hideMediaSelectionDialog() },
        onConfirm = {
            onConfirm()
            satunesViewModel.hideMediaSelectionDialog()
        },
        mediaImplCollection = playlists,
        mediaDestination = mediaImpl,
        jetpackLibsIcons = JetpackLibsIcons.PLAYLIST_ADD
    )
}