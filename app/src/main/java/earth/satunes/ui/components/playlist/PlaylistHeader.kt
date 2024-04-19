/*
 * This file is part of Satunes.
 *
 * Satunes is free software: you can redistribute it and/or modify it under
 *  the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * Satunes is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Satunes.
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

package earth.satunes.ui.components.playlist

import android.content.Context
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import earth.satunes.database.models.Music
import earth.satunes.database.models.relations.PlaylistWithMusics
import earth.satunes.database.models.tables.MusicDB
import earth.satunes.database.models.tables.Playlist
import earth.satunes.database.services.DatabaseManager
import earth.satunes.ui.components.playlist.buttons.AddMusicsToPlaylistButtons

/**
 * @author Antoine Pirlot on 19/04/2024
 */

@Composable
fun PlaylistHeader(
    modifier: Modifier = Modifier,
    playlist: PlaylistWithMusics
) {
    val context: Context = LocalContext.current
    Row(modifier = modifier) {
        AddMusicsToPlaylistButtons(
            onClick = {
                //TODO show pop up with a list of all musics
                val database = DatabaseManager(context = context)
                val musics: MutableList<Music> = playlist.playlist.musicMediaItemSortedMap.keys.toMutableList()
                database.insertMusicsToPlaylist(musics = musics, playlist = playlist)
            }
        )
    }
}

@Preview
@Composable
fun PlaylistHeaderPreview() {
    PlaylistHeader(playlist = PlaylistWithMusics(
            playlist = Playlist(0 , "playlist"),
            musics = mutableListOf()
        )
    )
}