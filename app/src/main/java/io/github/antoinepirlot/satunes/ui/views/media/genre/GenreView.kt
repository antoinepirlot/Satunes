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

package io.github.antoinepirlot.satunes.ui.views.media.genre

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.data.local.LocalNavController
import io.github.antoinepirlot.satunes.data.viewmodels.PlaybackViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.SatunesViewModel
import io.github.antoinepirlot.satunes.database.models.Album
import io.github.antoinepirlot.satunes.database.models.Genre
import io.github.antoinepirlot.satunes.database.models.MediaImpl
import io.github.antoinepirlot.satunes.database.models.Music
import io.github.antoinepirlot.satunes.router.utils.openMedia
import io.github.antoinepirlot.satunes.ui.components.bars.ExtraButtonList
import io.github.antoinepirlot.satunes.ui.views.media.MediaListView
import io.github.antoinepirlot.satunes.ui.views.media.MediaWithAlbumsHeaderView

/**
 * @author Antoine Pirlot on 01/04/2024
 */

@Composable
internal fun GenreView(
    modifier: Modifier = Modifier,
    satunesViewModel: SatunesViewModel = viewModel(),
    playbackViewModel: PlaybackViewModel = viewModel(),
    genre: Genre,
) {
    val navController: NavHostController = LocalNavController.current
    val musicMap: Set<Music> = genre.getMusicSet()
    val albumSet: Set<Album> = genre.getAlbumSet()

    //Recompose if data changed
    var mapChanged: Boolean by rememberSaveable { genre.musicSetUpdated }
    if (mapChanged) {
        mapChanged = false
    }
    //

    if (albumSet.isNotEmpty())
        satunesViewModel.replaceExtraButtons(extraButtons = {
            ExtraButtonList(
                musicSet = null,
                mediaImplSet = albumSet
            )
        })
    else
        satunesViewModel.clearExtraButtons()

    MediaListView(
        modifier = modifier,
        mediaImplCollection = musicMap,
        openMedia = { clickedMediaImpl: MediaImpl ->
            playbackViewModel.loadMusic(
                musicSet = genre.getMusicSet(),
                musicToPlay = clickedMediaImpl as Music
            )
            openMedia(
                playbackViewModel = playbackViewModel,
                media = clickedMediaImpl,
                navController = navController
            )
        },
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
    GenreView(genre = Genre("Genre"))
}

