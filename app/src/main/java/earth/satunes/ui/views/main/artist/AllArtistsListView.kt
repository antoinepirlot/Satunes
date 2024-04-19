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

package earth.satunes.ui.views.main.artist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.media3.common.MediaItem
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import earth.satunes.database.models.Artist
import earth.satunes.database.models.Media
import earth.satunes.database.models.Music
import earth.satunes.database.services.DataManager
import earth.satunes.playback.services.PlaybackController
import earth.satunes.router.utils.openCurrentMusic
import earth.satunes.router.utils.openMedia
import earth.satunes.router.utils.resetOpenedPlaylist
import earth.satunes.ui.views.main.MediaListView
import java.util.SortedMap

/**
 * @author Antoine Pirlot on 01/04/2024
 */

@Composable
fun AllArtistsListView(
    modifier: Modifier = Modifier,
    navController: NavHostController,
) {
    val playbackController: PlaybackController = PlaybackController.getInstance()

    val musicMediaItemMap: SortedMap<Music, MediaItem> =
        remember { DataManager.musicMediaItemSortedMap }
    val artistMap: SortedMap<String, Artist> = remember { DataManager.artistMap }

    resetOpenedPlaylist()

    @Suppress("UNCHECKED_CAST")
    MediaListView(
        modifier = modifier,
        mediaList = artistMap.values.toList(),

        openMedia = { clickedMedia: Media ->
            openMedia(
                navController,
                clickedMedia
            )
        },

        shuffleMusicAction = {
            playbackController.loadMusic(
                musicMediaItemSortedMap = musicMediaItemMap,
                shuffleMode = true
            )
            openMedia(navController = navController)
        },

        onFABClick = { openCurrentMusic(navController) }
    )
}

@Preview
@Composable
fun AllArtistsListViewPreview() {
    AllArtistsListView(navController = rememberNavController())
}