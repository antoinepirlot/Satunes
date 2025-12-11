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

package io.github.antoinepirlot.satunes.ui.components.forms

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.jetpack_libs.components.texts.NormalText
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.data.states.NavigationUiState
import io.github.antoinepirlot.satunes.data.viewmodels.MediaSelectionViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.NavigationViewModel
import io.github.antoinepirlot.satunes.database.models.media.MediaImpl
import io.github.antoinepirlot.satunes.models.Destination

/**
 * @author Antoine Pirlot on 30/03/2024
 */

@Composable
internal fun MediaSelectionForm(
    modifier: Modifier = Modifier,
    mediaSelectionViewModel: MediaSelectionViewModel = viewModel(),
    navigationViewModel: NavigationViewModel = viewModel(),
    mediaImplCollection: Collection<MediaImpl>
) {
    val navigationUiState: NavigationUiState by navigationViewModel.uiState.collectAsState()
    val lazyState = rememberLazyListState()
    val mediaList: List<MediaImpl> =
        try {
            mediaImplCollection as List<MediaImpl>
        } catch (_: ClassCastException) {
            mediaImplCollection.toList()
        }
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (navigationUiState.currentDestination != Destination.PLAYLISTS)
            TextButton(
                modifier = modifier,
                onClick = { mediaSelectionViewModel.setShowPlaylistCreation(value = true) }) {
                NormalText(text = stringResource(id = R.string.create_playlist))
            }
        LazyColumn(state = lazyState) {
            items(
                items = mediaList,
                key = {
                    if (it.isSubsonic()) "cloud-${it.id}"
                    else it.id
                }
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