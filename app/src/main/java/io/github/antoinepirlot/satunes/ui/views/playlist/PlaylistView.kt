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

package io.github.antoinepirlot.satunes.ui.views.playlist

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.media3.common.MediaItem
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import io.github.antoinepirlot.satunes.database.models.Media
import io.github.antoinepirlot.satunes.database.models.Music
import io.github.antoinepirlot.satunes.database.models.relations.PlaylistWithMusics
import io.github.antoinepirlot.satunes.database.models.tables.Playlist
import io.github.antoinepirlot.satunes.database.services.DataManager
import io.github.antoinepirlot.satunes.database.services.DatabaseManager
import io.github.antoinepirlot.satunes.icons.SatunesIcons
import io.github.antoinepirlot.satunes.playback.services.PlaybackController
import io.github.antoinepirlot.satunes.router.utils.openCurrentMusic
import io.github.antoinepirlot.satunes.router.utils.openMedia
import io.github.antoinepirlot.satunes.services.MediaSelectionManager
import io.github.antoinepirlot.satunes.ui.components.buttons.ExtraButton
import io.github.antoinepirlot.satunes.ui.components.dialog.MediaSelectionDialog
import io.github.antoinepirlot.satunes.ui.components.texts.Title
import io.github.antoinepirlot.satunes.ui.views.MediaListView
import java.util.SortedMap

/**
 * @author Antoine Pirlot on 01/04/2024
 */

@Composable
fun PlaylistView(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    playlist: PlaylistWithMusics,
) {
    //TODO try using nav controller instead try to remember it in an object if possible
    var openAddMusicsDialog: Boolean by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Title(text = playlist.playlist.title)
        val musicMap: SortedMap<Music, MediaItem> = remember { playlist.musicMediaItemSortedMap }

        //Recompose if data changed
        var mapChanged: Boolean by remember { playlist.musicMediaItemSortedMapUpdate }
        if (mapChanged) {
            mapChanged = false
        }
        //

        MediaListView(
            mediaList = musicMap.keys.toList(),
            openMedia = { clickedMedia: Media ->
                PlaybackController.getInstance().loadMusic(
                    musicMediaItemSortedMap = playlist.musicMediaItemSortedMap
                )
                openMedia(navController = navController, media = clickedMedia)
            },
            openedPlaylistWithMusics = playlist,
            onFABClick = { openCurrentMusic(navController = navController) },
            extraButtons = {
                ExtraButton(icon = SatunesIcons.ADD, onClick = { openAddMusicsDialog = true })
                ExtraButton(icon = SatunesIcons.SHUFFLE, onClick = {
                    PlaybackController.getInstance().loadMusic(
                        musicMediaItemSortedMap = playlist.musicMediaItemSortedMap,
                        shuffleMode = true
                    )
                    openMedia(navController = navController)
                })
            }
        )
        if (openAddMusicsDialog) {
            val allMusic: List<Music> = DataManager.musicMediaItemSortedMap.keys.toList()
            val context: Context = LocalContext.current
            MediaSelectionDialog(
                onDismissRequest = { openAddMusicsDialog = false },
                onConfirm = {
                    val db = DatabaseManager(context = context)
                    db.insertMusicsToPlaylist(
                        musics = MediaSelectionManager.checkedMusics,
                        playlist = playlist
                    )
                    openAddMusicsDialog = false
                },
                mediaList = allMusic,
                icon = {
                    val icon = SatunesIcons.PLAYLIST_ADD
                    Icon(imageVector = icon.imageVector, contentDescription = icon.description)
                }
            )
        }
    }
}

@Preview
@Composable
fun PlaylistViewPreview() {
    PlaylistView(
        navController = rememberNavController(),
        playlist = PlaylistWithMusics(
            playlist = Playlist(id = 0, title = "Playlist"),
            musics = mutableListOf()
        )
    )
}