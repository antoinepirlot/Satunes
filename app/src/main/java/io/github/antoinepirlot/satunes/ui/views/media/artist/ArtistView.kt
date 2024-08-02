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

package io.github.antoinepirlot.satunes.ui.views.media.artist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.database.models.Album
import io.github.antoinepirlot.satunes.database.models.Artist
import io.github.antoinepirlot.satunes.database.models.MediaImpl
import io.github.antoinepirlot.satunes.database.models.Music
import io.github.antoinepirlot.satunes.icons.SatunesIcons
import io.github.antoinepirlot.satunes.router.utils.openCurrentMusic
import io.github.antoinepirlot.satunes.router.utils.openMedia
import io.github.antoinepirlot.satunes.ui.components.buttons.ExtraButton
import io.github.antoinepirlot.satunes.data.viewmodels.PlaybackViewModel
import io.github.antoinepirlot.satunes.ui.views.media.MediaListView
import io.github.antoinepirlot.satunes.ui.views.media.MediaWithAlbumsHeaderView

/**
 * @author Antoine Pirlot on 01/04/2024
 */

@Composable
internal fun ArtistView(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    playbackViewModel: PlaybackViewModel = viewModel(),
    artist: Artist,
) {
    //TODO create mediaViewModel and UiState when you select media
    val musicMap: Set<Music> = artist.getMusicSet()
    //Recompose if data changed
    var musicMapChanged: Boolean by rememberSaveable { artist.musicSetUpdated }
    if (musicMapChanged) {
        musicMapChanged = false
    }
    //

    MediaListView(
        modifier = modifier,
        navController = navController,
        mediaImplCollection = musicMap,
        openMedia = { clickedMediaImpl: MediaImpl ->
            playbackViewModel.loadMusic(
                musicSet = artist.getMusicSet(),
                musicToPlay = clickedMediaImpl as Music
            )
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
        header = {
            val albumSet: Set<Album> = artist.getAlbumSet()

            //Recompose if data changed
            var albumMapChanged: Boolean by remember { artist.albumSortedMapUpdate }
            if (albumMapChanged) {
                albumMapChanged = false
            }
            //

            MediaWithAlbumsHeaderView(
                mediaImpl = artist,
                albumCollection = albumSet,
                navController = navController
            )
        },
        extraButtons = {
            if (artist.getMusicSet().isNotEmpty()) {
                ExtraButton(icon = SatunesIcons.PLAY, onClick = {
                    playbackViewModel.loadMusic(musicSet = artist.getMusicSet())
                    openMedia(playbackViewModel = playbackViewModel, navController = navController)
                })
                ExtraButton(icon = SatunesIcons.SHUFFLE, onClick = {
                    playbackViewModel.loadMusic(
                        musicSet = artist.getMusicSet(),
                        shuffleMode = true
                    )
                    openMedia(playbackViewModel = playbackViewModel, navController = navController)
                })
            }
        },
        emptyViewText = stringResource(id = R.string.no_music)
    )
}

@Preview
@Composable
private fun ArtistViewPreview() {
    val navController: NavHostController = rememberNavController()
    ArtistView(
        navController = navController,
        artist = Artist(title = "Artist title")
    )
}