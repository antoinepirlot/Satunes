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
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.media3.common.MediaItem
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.database.models.Album
import io.github.antoinepirlot.satunes.database.models.Genre
import io.github.antoinepirlot.satunes.database.models.MediaImpl
import io.github.antoinepirlot.satunes.database.models.Music
import io.github.antoinepirlot.satunes.icons.SatunesIcons
import io.github.antoinepirlot.satunes.router.utils.openCurrentMusic
import io.github.antoinepirlot.satunes.router.utils.openMedia
import io.github.antoinepirlot.satunes.ui.components.buttons.ExtraButton
import io.github.antoinepirlot.satunes.ui.viewmodels.PlaybackViewModel
import io.github.antoinepirlot.satunes.ui.views.media.MediaListView
import io.github.antoinepirlot.satunes.ui.views.media.MediaWithAlbumsHeaderView
import java.util.SortedSet

/**
 * @author Antoine Pirlot on 01/04/2024
 */

@Composable
internal fun GenreView(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    playbackViewModel: PlaybackViewModel = PlaybackViewModel(context = LocalContext.current),
    genre: Genre,
) {
    val musicMap: Map<Music, MediaItem> = genre.getMusicMap()

    //Recompose if data changed
    var mapChanged: Boolean by rememberSaveable { genre.musicMediaItemMapUpdate }
    if (mapChanged) {
        mapChanged = false
    }
    //

    MediaListView(
        modifier = modifier,
        navController = navController,
        mediaImplList = musicMap.keys.toList(),
        openMedia = { clickedMediaImpl: MediaImpl ->
            playbackViewModel.loadMusic(
                musicMediaItemSortedMap = genre.getMusicMap(),
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
            //Recompose if data changed
            @Suppress("NAME_SHADOWING")
            var mapChanged: Boolean by remember { genre.musicMediaItemMapUpdate }
            if (mapChanged) {
                mapChanged = false
            }
            //

            val albumSet: SortedSet<Album> = sortedSetOf()
            musicMap.forEach { (music: Music, _: MediaItem) ->
                albumSet.add(music.album)
            }
            MediaWithAlbumsHeaderView(
                navController = navController,
                mediaImpl = genre,
                albumList = albumSet.toList()
            )
        },
        extraButtons = {
            if (genre.getMusicMap().isNotEmpty()) {
                ExtraButton(icon = SatunesIcons.PLAY, onClick = {
                    playbackViewModel.loadMusic(musicMediaItemSortedMap = genre.getMusicMap())
                    openMedia(playbackViewModel = playbackViewModel, navController = navController)
                })
                ExtraButton(icon = SatunesIcons.SHUFFLE, onClick = {
                    playbackViewModel.loadMusic(
                        musicMediaItemSortedMap = genre.getMusicMap(),
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
private fun GenreViewPreview() {
    val navController: NavHostController = rememberNavController()
    GenreView(navController = navController, genre = Genre("Genre"))
}

