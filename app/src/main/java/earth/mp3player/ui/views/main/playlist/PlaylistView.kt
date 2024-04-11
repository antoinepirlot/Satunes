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

package earth.mp3player.ui.views.main.playlist

import android.net.Uri.decode
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import earth.mp3player.database.models.Media
import earth.mp3player.database.models.relations.PlaylistWithMusics
import earth.mp3player.database.models.tables.MusicDB
import earth.mp3player.database.models.tables.Playlist
import earth.mp3player.playback.services.playback.PlaybackController
import earth.mp3player.router.media.utils.openCurrentMusic
import earth.mp3player.router.media.utils.openMedia
import earth.mp3player.services.PlaylistSelectionManager
import earth.mp3player.ui.components.texts.Title
import earth.mp3player.ui.views.main.MediaListView
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
    PlaylistSelectionManager.openedPlaylist = playlist
    val musicSortedMap: SortedMap<String, Media> = sortedMapOf()
    playlist.musics.forEach { musicDb: MusicDB ->
        musicSortedMap[musicDb.music.title] = musicDb.music
    }

    Column(modifier = modifier) {
        Title(text = decode(playlist.playlist.title))

        MediaListView(
            mediaMap = musicSortedMap,
            openMedia = { clickedMedia: Media ->
                PlaybackController.getInstance().loadMusic(
                    musicMediaItemSortedMap = playlist.musicMediaItemSortedMap
                )
                openMedia(navController = navController, media = clickedMedia)
            },
            shuffleMusicAction = {
                PlaybackController.getInstance().loadMusic(
                    musicMediaItemSortedMap = playlist.musicMediaItemSortedMap,
                    shuffleMode = true
                )
                openMedia(navController = navController)
            },
            onFABClick = { openCurrentMusic(navController = navController) }
        )
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