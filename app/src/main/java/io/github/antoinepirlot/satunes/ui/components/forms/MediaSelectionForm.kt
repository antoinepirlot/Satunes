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

package io.github.antoinepirlot.satunes.ui.components.forms

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.jetpack_libs.components.texts.NormalText
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.data.states.SatunesUiState
import io.github.antoinepirlot.satunes.data.viewmodels.MediaSelectionViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.SatunesViewModel
import io.github.antoinepirlot.satunes.database.models.MediaImpl
import io.github.antoinepirlot.satunes.models.Destination

/**
 * @author Antoine Pirlot on 30/03/2024
 */

@Composable
internal fun MediaSelectionForm(
    modifier: Modifier = Modifier,
    satunesViewModel: SatunesViewModel = viewModel(),
    mediaSelectionViewModel: MediaSelectionViewModel = viewModel(),
    mediaImplCollection: Collection<MediaImpl>
) {
    val satunesUiState: SatunesUiState by satunesViewModel.uiState.collectAsState()
    val lazyState = rememberLazyListState()
    val mediaList: List<MediaImpl> =
        try {
            mediaImplCollection as List<MediaImpl>
        } catch (_: ClassCastException) {
            mediaImplCollection.toList()
        }
    Column {
        if (satunesUiState.currentDestination != Destination.PLAYLISTS) {
            TextButton(onClick = { mediaSelectionViewModel.setShowPlaylistCreation(value = true) }) {
                NormalText(text = stringResource(id = R.string.create_playlist))
            }
        }
        LazyColumn(
            modifier = modifier,
            state = lazyState
        ) {
            items(
                items = mediaList,
                key = { it.id }
            ) { mediaImpl: MediaImpl ->
                MediaSelectionCheckbox(mediaImpl = mediaImpl)
            }
        }
    }
}

@Preview
@Composable
private fun PlaylistSelectionFormPreview() {
    MediaSelectionForm(mediaImplCollection = listOf())
}