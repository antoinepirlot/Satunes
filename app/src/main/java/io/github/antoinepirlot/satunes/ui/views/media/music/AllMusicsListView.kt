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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.data.states.SatunesUiState
import io.github.antoinepirlot.satunes.data.viewmodels.DataViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.SatunesViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.SubsonicViewModel
import io.github.antoinepirlot.satunes.database.models.media.MediaImpl
import io.github.antoinepirlot.satunes.models.SatunesModes
import io.github.antoinepirlot.satunes.ui.components.buttons.fab.ExtraButtonList
import io.github.antoinepirlot.satunes.ui.views.media.MediaListView

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
        OnlineMode(modifier = modifier)
    else
        OfflineMode(modifier = modifier)

    // /!\ put this launch effect here as it must be ran after media impl list loaded
    LaunchedEffect(key1 = Unit) {
        if (dataViewModel.mediaImplListOnScreen.isNotEmpty())
            satunesViewModel.replaceExtraButtons(extraButtons = {
                ExtraButtonList()
            })
        else
            satunesViewModel.clearExtraButtons()
    }
}

@Composable
private fun OnlineMode(
    modifier: Modifier = Modifier,
    subsonicViewModel: SubsonicViewModel = viewModel(),
) {
    val musicSet: MutableList<MediaImpl> = remember { mutableStateListOf() }

    LaunchedEffect(key1 = Unit) {
        subsonicViewModel.loadRandomSongs(onDataRetrieved = {
            musicSet.addAll(it)
        })
    }
    MediaListView(
        modifier = modifier,
        emptyViewText = stringResource(id = R.string.no_music)
    )
}

@Composable
private fun OfflineMode(
    modifier: Modifier = Modifier,
    satunesViewModel: SatunesViewModel = viewModel(),
    dataViewModel: DataViewModel = viewModel(),
) {
    val musicSet: Set<MediaImpl> = dataViewModel.getMusicSet()

    LaunchedEffect(key1 = Unit) {
        dataViewModel.loadMediaImplList(list = dataViewModel.getMusicSet())
    }

    MediaListView(
        modifier = modifier,
        emptyViewText = stringResource(id = R.string.no_music)
    )
}

@Preview
@Composable
private fun MusicsListViewPreview() {
    AllMusicsListView()
}
