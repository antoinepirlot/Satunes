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

package earth.satunes.ui.views.folder

import android.net.Uri.decode
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import earth.satunes.database.models.Folder
import earth.satunes.database.models.Media
import earth.satunes.database.models.Music
import earth.satunes.playback.services.PlaybackController
import earth.satunes.router.utils.openCurrentMusic
import earth.satunes.router.utils.openMedia
import earth.satunes.router.utils.openMediaFromFolder
import earth.satunes.router.utils.resetOpenedPlaylist
import earth.satunes.ui.components.texts.Title
import earth.satunes.ui.views.MediaListView
import earth.satunes.ui.views.utils.getRootFolderName
import java.util.SortedMap

/**
 * @author Antoine Pirlot on 01/04/2024
 */

@Composable
fun FolderView(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    folder: Folder,
) {
    val playbackController: PlaybackController = PlaybackController.getInstance()
    val folderMusicMediaItemSortedMap: SortedMap<Music, MediaItem> = remember {
        folder.musicMediaItemSortedMap
    }
    val mapToShow: SortedMap<Long, Media> = sortedMapOf()

    //Load sub-folders
    mapToShow.putAll(folder.getSubFolderMapAsMedia())

    //Load sub-folder's musics
    folderMusicMediaItemSortedMap.forEach { (music: Music, _) ->
        mapToShow[music.id] = music
    }

    resetOpenedPlaylist()
    Column(modifier = modifier) {
        if (folder.parentFolder == null) {
            Title(text = '/' + getRootFolderName(title = folder.title))
        } else {
            val allPath: MutableList<String> = folder.absolutePath.split("/").toMutableList()
            allPath.removeFirst()
            allPath[0] = getRootFolderName(title = allPath[0])
            var path: String = ""
            for (s: String in allPath) {
                path += "/${decode(s)}"
            }
            Title(text = path, fontSize = 20.sp)
        }

        MediaListView(
            mediaList = mapToShow.values.toList(),

            openMedia = { clickedMedia: Media ->
                openMediaFromFolder(navController, clickedMedia)
            },

            shuffleMusicAction = {
                playbackController.loadMusic(
                    musicMediaItemSortedMap = folder.getAllMusic(),
                    shuffleMode = true
                )
                openMedia(navController = navController)
            },

            onFABClick = { openCurrentMusic(navController) }
        )
    }
}

@Preview
@Composable
fun FolderViewPreview() {
    FolderView(
        navController = rememberNavController(),
        folder = Folder(id = 0, title = "Folder title")
    )
}