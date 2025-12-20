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

package io.github.antoinepirlot.satunes.ui.views.media.music

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.satunes.data.states.SatunesUiState
import io.github.antoinepirlot.satunes.data.viewmodels.DataViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.SatunesViewModel
import io.github.antoinepirlot.satunes.models.SatunesModes
import io.github.antoinepirlot.satunes.ui.components.buttons.fab.ExtraButtonList
import io.github.antoinepirlot.satunes.ui.views.media.music.local.LocalAllMusicsListView
import io.github.antoinepirlot.satunes.ui.views.media.music.subsonic.SubsonicRandomMusicsView

/**
 * @author Antoine Pirlot on 01/04/2024
 */

@Composable
internal fun AllMusicsListView(
    modifier: Modifier = Modifier,
    satunesViewModel: SatunesViewModel = viewModel(),
    dataViewModel: DataViewModel = viewModel()
) {
    val satunesUiState: SatunesUiState by satunesViewModel.uiState.collectAsState()

    if (satunesUiState.mode == SatunesModes.ONLINE)
        SubsonicRandomMusicsView(modifier = modifier)
    else
        LocalAllMusicsListView(modifier = modifier)

    // /!\ put this launch effect here as it must be ran after media impl list loaded
    LaunchedEffect(key1 = dataViewModel.mediaImplListOnScreen.size) {
        if (dataViewModel.mediaImplListOnScreen.isNotEmpty())
            satunesViewModel.replaceExtraButtons(extraButtons = {
                ExtraButtonList()
            })
        else
            satunesViewModel.clearExtraButtons()
    }
}

@Preview
@Composable
private fun MusicsListViewPreview() {
    AllMusicsListView()
}
