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

package io.github.antoinepirlot.satunes.ui.views.media.artist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.data.viewmodels.DataViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.SatunesViewModel
import io.github.antoinepirlot.satunes.database.models.media.Album
import io.github.antoinepirlot.satunes.database.models.media.Artist
import io.github.antoinepirlot.satunes.database.models.media.Music
import io.github.antoinepirlot.satunes.ui.components.buttons.fab.ExtraButtonList
import io.github.antoinepirlot.satunes.ui.views.media.MediaListView
import io.github.antoinepirlot.satunes.ui.views.media.MediaWithAlbumsHeaderView

/**
 * @author Antoine Pirlot on 01/04/2024
 */

@Composable
internal fun ArtistView(
    modifier: Modifier = Modifier,
    satunesViewModel: SatunesViewModel = viewModel(),
    dataViewModel: DataViewModel = viewModel(),
    artist: Artist,
) {
    val musicSet: Set<Music> = artist.getMusicSet()

    //Recompose if data changed
    var musicSetChanged: Boolean by rememberSaveable { artist.musicSetUpdated }
    if (musicSetChanged) {
        musicSetChanged = false
    }
    //

    val albumSet: Set<Album> = artist.getAlbumSet()

    LaunchedEffect(key1 = dataViewModel.isLoaded) {
        if (albumSet.isNotEmpty())
            satunesViewModel.replaceExtraButtons(extraButtons = {
                ExtraButtonList()
            })
        else
            satunesViewModel.clearExtraButtons()
    }

    MediaListView(
        modifier = modifier,
        mediaImplCollection = musicSet,
        collectionChanged = musicSetChanged,
        header = if (albumSet.isNotEmpty()) {
            {
                //Recompose if data changed
                var albumMapChanged: Boolean by remember { artist.albumSortedSetUpdate }
                if (albumMapChanged) {
                    albumMapChanged = false
                }
                //

                MediaWithAlbumsHeaderView(
                    mediaImpl = artist,
                    albumCollection = albumSet,
                )
            }
        } else null,
        emptyViewText = stringResource(id = R.string.no_music)
    )
}

@Preview
@Composable
private fun ArtistViewPreview() {
    ArtistView(artist = Artist(title = "Artist title"))
}