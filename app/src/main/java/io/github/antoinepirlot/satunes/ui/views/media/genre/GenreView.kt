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

package io.github.antoinepirlot.satunes.ui.views.media.genre

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.data.viewmodels.DataViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.SatunesViewModel
import io.github.antoinepirlot.satunes.database.models.media.Album
import io.github.antoinepirlot.satunes.database.models.media.Genre
import io.github.antoinepirlot.satunes.ui.components.buttons.fab.ExtraButtonList
import io.github.antoinepirlot.satunes.ui.views.media.MediaListView
import io.github.antoinepirlot.satunes.ui.views.media.MediaWithAlbumsHeaderView

/**
 * @author Antoine Pirlot on 01/04/2024
 */

@Composable
internal fun GenreView(
    modifier: Modifier = Modifier,
    satunesViewModel: SatunesViewModel = viewModel(),
    dataViewModel: DataViewModel = viewModel(),
    genre: Genre,
) {
    val albumSet: Set<Album> = genre.getAlbumSet()

    LaunchedEffect(key1 = Unit) {
        dataViewModel.loadMediaImplList(list = genre.musicList)
    }

    LaunchedEffect(key1 = dataViewModel.mediaImplListOnScreen.size) {
        if (dataViewModel.mediaImplListOnScreen.isNotEmpty())
            satunesViewModel.replaceExtraButtons(extraButtons = {
                ExtraButtonList()
            })
        else
            satunesViewModel.clearExtraButtons()
    }

    MediaListView(
        modifier = modifier,
        header = if (albumSet.isNotEmpty()) {
            {
                MediaWithAlbumsHeaderView(
                    mediaImpl = genre,
                    albumCollection = albumSet
                )
            }
        } else null,
        emptyViewText = stringResource(id = R.string.no_music)
    )
}

@Preview
@Composable
private fun GenreViewPreview() {
    GenreView(genre = Genre(title = "Genre"))
}

