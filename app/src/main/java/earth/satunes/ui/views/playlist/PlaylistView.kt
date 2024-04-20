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

package earth.satunes.ui.views.playlist

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
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import earth.satunes.database.models.Media
import earth.satunes.database.models.Music
import earth.satunes.database.models.relations.PlaylistWithMusics
import earth.satunes.database.models.tables.Playlist
import earth.satunes.database.services.DataManager
import earth.satunes.database.services.DatabaseManager
import earth.satunes.playback.services.PlaybackController
import earth.satunes.router.utils.openCurrentMusic
import earth.satunes.router.utils.openMedia
import earth.satunes.services.MediaSelectionManager
import earth.satunes.ui.components.buttons.ExtraButton
import earth.satunes.ui.components.dialog.MediaSelectionDialog
import earth.satunes.ui.components.texts.Title
import earth.satunes.icons.SatunesIcons
import earth.satunes.ui.views.MediaListView

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
    MediaSelectionManager.openedPlaylist = playlist
    var openAddMusicsDialog: Boolean by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Title(text = playlist.playlist.title)
        MediaListView(
            mediaList = playlist.musics,
            openMedia = { clickedMedia: Media ->
                PlaybackController.getInstance().loadMusic(
                    musicMediaItemSortedMap = playlist.musicMediaItemSortedMap
                )
                openMedia(navController = navController, media = clickedMedia)
            },
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