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

package io.github.antoinepirlot.satunes.ui.views.media.folder

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.media3.common.MediaItem
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.database.models.Folder
import io.github.antoinepirlot.satunes.database.models.MediaImpl
import io.github.antoinepirlot.satunes.database.models.Music
import io.github.antoinepirlot.satunes.icons.SatunesIcons
import io.github.antoinepirlot.satunes.playback.services.PlaybackController
import io.github.antoinepirlot.satunes.router.utils.openCurrentMusic
import io.github.antoinepirlot.satunes.router.utils.openMedia
import io.github.antoinepirlot.satunes.router.utils.openMediaFromFolder
import io.github.antoinepirlot.satunes.ui.components.bars.FolderPath
import io.github.antoinepirlot.satunes.ui.components.buttons.ExtraButton
import io.github.antoinepirlot.satunes.ui.views.media.MediaListView
import java.util.SortedMap
import java.util.SortedSet

/**
 * @author Antoine Pirlot on 01/04/2024
 */


@Composable
internal fun FolderView(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    folder: Folder,
) {
    val playbackController: PlaybackController = PlaybackController.getInstance()
    val folderMusicMediaItemSortedMap: SortedMap<Music, MediaItem> = folder.musicMediaItemMap

    //Recompose if data changed
    var mapChanged: Boolean by rememberSaveable { folder.musicMediaItemMapUpdate }
    if (mapChanged) {
        mapChanged = false
    }
    //

    val subMediaImplMap: SortedSet<MediaImpl> = sortedSetOf()

    Column(modifier = modifier) {
        FolderPath(folder)
        loadSubFolders(
            subMediaImplMap = subMediaImplMap,
            folder = folder,
            folderMusicMediaItemSortedMap = folderMusicMediaItemSortedMap
        )
        MediaListView(
            navController = navController,
            mediaImplList = subMediaImplMap.toList(),
            openMedia = { clickedMediaImpl: MediaImpl ->
                openMediaFromFolder(clickedMediaImpl, navController = navController)
            },
            onFABClick = { openCurrentMusic(navController = navController) },
            extraButtons = {
                if (folder.isNotEmpty()) {
                    ExtraButton(icon = SatunesIcons.PLAY, onClick = {
                        loadPlaybackFromFolder(
                            navController = navController,
                            folder = folder,
                            shuffleMode = false
                        )
                    })
                    ExtraButton(icon = SatunesIcons.SHUFFLE, onClick = {
                        loadPlaybackFromFolder(
                            navController = navController,
                            folder = folder,
                            shuffleMode = true
                        )
                    })
                }
            },
            emptyViewText = stringResource(id = R.string.no_music)
        )
    }
}

private fun loadPlaybackFromFolder(
    navController: NavHostController,
    folder: Folder,
    shuffleMode: Boolean
) {
    val folderMusics: MutableMap<Music, MediaItem> = folder.getAllMusic()
    val playbackController: PlaybackController = PlaybackController.getInstance()
    playbackController.loadMusic(musicMediaItemSortedMap = folderMusics, shuffleMode = shuffleMode)
    openMedia(navController = navController)
}

private fun loadSubFolders(
    subMediaImplMap: SortedSet<MediaImpl>,
    folder: Folder,
    folderMusicMediaItemSortedMap: SortedMap<Music, MediaItem>
) {
    //Load sub-folders
    subMediaImplMap.addAll(folder.getSubFolderMapAsMediaImpl().values)

    //Load sub-folder's musics
    folderMusicMediaItemSortedMap.forEach { (music: Music, _) ->
        subMediaImplMap.add(music)
    }
}

@Preview
@Composable
private fun FolderViewPreview() {
    val navController: NavHostController = rememberNavController()
    FolderView(navController = navController, folder = Folder(title = "Folder title"))
}