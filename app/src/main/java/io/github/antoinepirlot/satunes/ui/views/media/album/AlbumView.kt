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

package io.github.antoinepirlot.satunes.ui.views.media.album

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.jetpack_libs.components.models.ScreenSizes
import io.github.antoinepirlot.jetpack_libs.components.texts.Title
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.data.local.LocalNavController
import io.github.antoinepirlot.satunes.data.viewmodels.DataViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.SatunesViewModel
import io.github.antoinepirlot.satunes.database.models.media.Album
import io.github.antoinepirlot.satunes.database.models.media.Artist
import io.github.antoinepirlot.satunes.ui.components.bars.media.ArtistBar
import io.github.antoinepirlot.satunes.ui.components.buttons.fab.ExtraButtonList
import io.github.antoinepirlot.satunes.ui.components.images.MediaArtwork
import io.github.antoinepirlot.satunes.ui.views.media.MediaListView

/**
 * @author Antoine Pirlot on 01/04/2024
 */

@Composable
internal fun AlbumView(
    modifier: Modifier = Modifier,
    satunesViewModel: SatunesViewModel = viewModel(),
    dataViewModel: DataViewModel = viewModel(),
    album: Album,
) {

    LaunchedEffect(key1 = Unit) {
        dataViewModel.loadMediaImplList(list = album.musicCollection)
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
        header = { Header(album = album) },
        emptyViewText = stringResource(id = R.string.no_music),
        showGroupIndication = false
    )
}

@Composable
private fun Header(
    modifier: Modifier = Modifier,
    album: Album
) {
    LocalNavController.current

    Column(modifier = modifier.padding(vertical = 16.dp)) {
        val screenWidthDp = LocalConfiguration.current.screenWidthDp
        val albumSize: Dp = if (screenWidthDp < ScreenSizes.VERY_VERY_SMALL)
            100.dp
        else if (screenWidthDp < ScreenSizes.VERY_SMALL)
            170.dp
        else 250.dp
        MediaArtwork(
            modifier = Modifier
                .fillMaxWidth()
                .size(albumSize),
            media = album
        )
        Title(
            bottomPadding = 0.dp,
            text = album.title
        )
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            ArtistBar(
                artist = album.artist,
                title = if (album.year != null) "${album.artist.title} - ${album.year}" else album.artist.title
            )
        }
    }
}

@Preview
@Composable
private fun AlbumViewPreview() {
    AlbumView(
        album = Album(
            title = "Album title",
            artist = Artist(title = "Artist Title"),
        )
    )
}