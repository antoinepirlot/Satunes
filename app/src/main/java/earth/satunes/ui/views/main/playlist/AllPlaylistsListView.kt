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

package earth.satunes.ui.views.main.playlist

import android.content.Context
import android.net.Uri.encode
import androidx.compose.foundation.layout.Column
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
import earth.satunes.database.models.tables.Playlist
import earth.satunes.database.services.DataManager
import earth.satunes.database.services.DatabaseManager
import earth.satunes.router.utils.openCurrentMusic
import earth.satunes.router.utils.openMedia
import earth.satunes.router.utils.resetOpenedPlaylist
import earth.satunes.ui.components.forms.PlaylistCreationForm
import earth.satunes.ui.components.playlist.AddPlaylistButton
import earth.satunes.ui.views.main.MediaListView
import java.util.SortedMap

/**
 * @author Antoine Pirlot on 30/03/2024
 */

@Composable
fun PlaylistListView(
    modifier: Modifier = Modifier,
    navController: NavHostController,
) {
    val context: Context = LocalContext.current
    var openAlertDialog by remember { mutableStateOf(false) }
    resetOpenedPlaylist()
    Column(modifier = modifier) {
        @Suppress("UNCHECKED_CAST")
        val playlistMap: SortedMap<String, Media> =
            remember { DataManager.playlistWithMusicsMap as SortedMap<String, Media> }

        MediaListView(
            mediaList = playlistMap.values.toList(),
            openMedia = { clickedMedia: Media ->
                openMedia(navController = navController, media = clickedMedia)
            },
            shuffleMusicAction = { /* Nothing to do TODO find a way to disable this button */ },
            onFABClick = { openCurrentMusic(navController = navController) }
        ) {
            AddPlaylistButton(onClick = { openAlertDialog = true })
        }

        when {
            openAlertDialog -> {
                PlaylistCreationForm(
                    onConfirm = { playlistTitle: String ->
                        val playlist = Playlist(id = 0, title = encode(playlistTitle))
                        DatabaseManager(context = context).insertOne(playlist = playlist)
                        openAlertDialog = false
                    },
                    onDismissRequest = { openAlertDialog = false }
                )
            }
        }
    }
}

@Preview
@Composable
fun PlaylistListViewPreview() {
    PlaylistListView(navController = rememberNavController())
}