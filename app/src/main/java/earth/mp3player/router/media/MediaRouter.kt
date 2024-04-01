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

package earth.mp3player.router.media

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import earth.mp3player.database.models.Album
import earth.mp3player.database.models.Artist
import earth.mp3player.database.models.Folder
import earth.mp3player.database.models.Genre
import earth.mp3player.database.models.relations.PlaylistWithMusics
import earth.mp3player.database.services.DataManager
import earth.mp3player.playback.services.playback.PlaybackController
import earth.mp3player.ui.views.PlayBackView
import earth.mp3player.ui.views.main.album.AlbumView
import earth.mp3player.ui.views.main.album.AllAlbumsListView
import earth.mp3player.ui.views.main.artist.AllArtistsListView
import earth.mp3player.ui.views.main.artist.ArtistView
import earth.mp3player.ui.views.main.folder.FolderView
import earth.mp3player.ui.views.main.folder.RootFolderView
import earth.mp3player.ui.views.main.genre.AllGenresListView
import earth.mp3player.ui.views.main.genre.GenreView
import earth.mp3player.ui.views.main.music.AllMusicsListView
import earth.mp3player.ui.views.main.playlist.PlaylistListView
import earth.mp3player.ui.views.main.playlist.PlaylistView
import java.util.SortedMap

/**
 * @author Antoine Pirlot on 23-01-24
 */

@Composable
fun MediaRouter(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: String,
) {
    val playbackController: PlaybackController = remember { PlaybackController.getInstance() }

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(MediaDestination.FOLDERS.link) {
            // /!\ This route prevent back gesture to exit the app
            RootFolderView(navController = navController)
        }

        composable("${MediaDestination.FOLDERS.link}/{id}") {
            val folderId = it.arguments!!.getString("id")!!.toLong()
            val folder: Folder = remember { DataManager.getFolder(folderId = folderId) }
            FolderView(navController = navController, folder = folder)
        }

        composable(MediaDestination.ARTISTS.link) {
            AllArtistsListView(navController = navController)
        }

        composable("${MediaDestination.ARTISTS.link}/{name}") {
            val artistName: String = it.arguments!!.getString("name")!!
            val artist: Artist = remember { DataManager.getArtist(artistName) }
            ArtistView(navController = navController, artist = artist)
        }

        composable(MediaDestination.ALBUMS.link) {
            AllAlbumsListView(navController = navController)
        }

        composable("${MediaDestination.ALBUMS.link}/{id}") {
            val albumId: Long = it.arguments!!.getString("id")!!.toLong()
            val album: Album = remember { DataManager.getAlbum(albumId) }
            AlbumView(navController = navController, album = album)
        }

        composable(MediaDestination.GENRES.link) {
            AllGenresListView(navController = navController)
        }

        composable("${MediaDestination.GENRES.link}/{name}") {
            val genreName: String = it.arguments!!.getString("name")!!
            val genreMap: SortedMap<String, Genre> = remember { DataManager.genreMap }
            val genre = remember { DataManager.getGenre(genreName = genreName) }
            GenreView(navController = navController, genre = genre)
        }

        composable(MediaDestination.PLAYLISTS.link) {
            PlaylistListView(navController = navController)
        }

        composable("${MediaDestination.PLAYLISTS.link}/{id}") {
            val playlistId: Long = it.arguments!!.getString("id")!!.toLong()
            val playlist: PlaylistWithMusics = remember {
                DataManager.getPlaylist(playlistId = playlistId)
            }
            PlaylistView(navController = navController, playlist = playlist)
        }

        composable(MediaDestination.MUSICS.link) {
            AllMusicsListView(navController = navController)
        }

        composable(MediaDestination.PLAYBACK.link) {
            PlayBackView()
        }
    }
}