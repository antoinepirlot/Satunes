/*
 *  This file is part of MP3 Player.
 *
 *  MP3 Player is free software: you can redistribute it and/or modify it under
 *  the terms of the GNU General Public License as published by the Free Software Foundation,
 *  either version 3 of the License, or (at your option) any later version.
 *
 *  MP3 Player is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *   without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with MP3 Player.
 *  If not, see <https://www.gnu.org/licenses/>.
 *
 *  ***** INFORMATIONS ABOUT THE AUTHOR *****
 *  The author of this file is Antoine Pirlot, the owner of this project.
 *  You find this original project on github.
 *
 *  My github link is: https://github.com/antoinepirlot
 *  This current project's link is: https://github.com/antoinepirlot/MP3-Player
 *
 *  You can contact me via my email: pirlot.antoine@outlook.com
 *  PS: I don't answer quickly.
 */

/*
 *  This file is part of MP3 Player.
 *
 *  MP3 Player is free software: you can redistribute it and/or modify it under
 *  the terms of the GNU General Public License as published by the Free Software Foundation,
 *  either version 3 of the License, or (at your option) any later version.
 *
 *  MP3 Player is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *   without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with MP3 Player.
 *  If not, see <https://www.gnu.org/licenses/>.
 *
 *  ***** INFORMATIONS ABOUT THE AUTHOR *****
 *  The author of this file is Antoine Pirlot, the owner of this project.
 *  You find this original project on github.
 *
 *  My github link is: https://github.com/antoinepirlot
 *  This current project's link is: https://github.com/antoinepirlot/MP3-Player
 *
 *  You can contact me via my email: pirlot.antoine@outlook.com
 *  PS: I don't answer quickly.
 */

package earth.mp3player.router.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.media3.common.MediaItem
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import earth.mp3player.models.Album
import earth.mp3player.models.Artist
import earth.mp3player.models.Folder
import earth.mp3player.models.Genre
import earth.mp3player.models.Music
import earth.mp3player.router.media.MediaDestination
import earth.mp3player.router.media.MediaRouter
import earth.mp3player.ui.views.settings.SettingsView
import java.util.SortedMap

/**
 * @author Antoine Pirlot on 02-03-24
 */
@Composable
fun MainRouter(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    mediaRouterNavController: NavHostController,
    mediaRouterStartDestination: String,
    rootFolderMap: SortedMap<Long, Folder>,
    allArtistSortedMap: SortedMap<String, Artist>,
    allAlbumSortedMap: SortedMap<String, Album>,
    allMusicMediaItemsMap: SortedMap<Music, MediaItem>,
    folderMap: Map<Long, Folder>,
    genreMap: SortedMap<String, Genre>
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = MainDestination.ROOT.link
    ) {
        composable(MainDestination.ROOT.link) {
            MediaRouter(
                navController = mediaRouterNavController,
                startDestination = mediaRouterStartDestination,
                rootFolderMap = rootFolderMap,
                allArtistSortedMap = allArtistSortedMap,
                allAlbumSortedMap = allAlbumSortedMap,
                allMusicMediaItemsMap = allMusicMediaItemsMap,
                folderMap = folderMap,
                genreMap = genreMap
            )
        }

        composable(MediaDestination.SETTINGS.link) {
            SettingsView()
        }
    }
}