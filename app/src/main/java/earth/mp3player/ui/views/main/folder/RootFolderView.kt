/*
 * This file is part of MP3 Player.
 *
 * MP3 Player is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * MP3 Player is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with MP3 Player.
 * If not, see <https://www.gnu.org/licenses/>.
 *
 * **** INFORMATIONS ABOUT THE AUTHOR *****
 * The author of this file is Antoine Pirlot, the owner of this project.
 * You find this original project on github.
 *
 * My github link is: https://github.com/antoinepirlot
 * This current project's link is: https://github.com/antoinepirlot/MP3-Player
 *
 * You can contact me via my email: pirlot.antoine@outlook.com
 * PS: I don't answer quickly.
 */

package earth.mp3player.ui.views.main.folder

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.media3.common.MediaItem
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import earth.mp3player.database.models.Folder
import earth.mp3player.database.models.Media
import earth.mp3player.database.models.Music
import earth.mp3player.database.services.DataManager
import earth.mp3player.playback.services.playback.PlaybackController
import earth.mp3player.router.media.utils.openCurrentMusic
import earth.mp3player.router.media.utils.openMedia
import earth.mp3player.router.media.utils.openMediaFromFolder
import earth.mp3player.router.media.utils.resetOpenedPlaylist
import earth.mp3player.ui.views.main.MediaListView
import java.util.SortedMap

/**
 * @author Antoine Pirlot on 01/04/2024
 */

@Composable
fun RootFolderView(
    modifier: Modifier = Modifier,
    navController: NavHostController,
) {
    val playbackController: PlaybackController = PlaybackController.getInstance()

    val rootFolderMap: SortedMap<Long, Folder> = remember { DataManager.rootFolderMap }
    (resetOpenedPlaylist())
    MediaListView(
        modifier = modifier,
        mediaMap = rootFolderMap,

        openMedia = { clickedMedia: Media ->
            openMediaFromFolder(navController, clickedMedia)
        },

        shuffleMusicAction = {
            val musicMediaItemSortedMap: SortedMap<Music, MediaItem> = sortedMapOf()
            @Suppress("NAME_SHADOWING")
            rootFolderMap.forEach { (_, folder: Media) ->
                val folder = folder as Folder
                musicMediaItemSortedMap.putAll(folder.getAllMusic())
            }
            playbackController.loadMusic(
                musicMediaItemSortedMap = musicMediaItemSortedMap,
                shuffleMode = true
            )
            openMedia(navController = navController)
        },

        onFABClick = { openCurrentMusic(navController) }
    )
}

@Preview
@Composable
fun RootFolderViewPreview() {
    RootFolderView(navController = rememberNavController())
}