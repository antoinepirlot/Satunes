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

package io.github.antoinepirlot.satunes.ui.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import io.github.antoinepirlot.satunes.MainActivity
import io.github.antoinepirlot.satunes.database.models.Album
import io.github.antoinepirlot.satunes.database.models.Artist
import io.github.antoinepirlot.satunes.database.models.Folder
import io.github.antoinepirlot.satunes.database.models.Genre
import io.github.antoinepirlot.satunes.database.models.Music
import io.github.antoinepirlot.satunes.database.models.Playlist
import io.github.antoinepirlot.satunes.database.services.data.DataManager
import io.github.antoinepirlot.satunes.database.services.database.DatabaseManager

/**
 * @author Antoine Pirlot on 19/07/2024
 */
class DataViewModel : ViewModel() {
    private val _playlistSetUpdated: MutableState<Boolean> = DataManager.playlistsMapUpdated
    private val _db: DatabaseManager =
        DatabaseManager(context = MainActivity.instance.applicationContext)

    val playlistSetUpdated: Boolean by _playlistSetUpdated

    fun playlistMapUpdated() {
        this._playlistSetUpdated.value = false
    }

    fun getRootFolderSet(): Set<Folder> = DataManager.getRootFolderSet()
    fun getArtistSet(): Set<Artist> = DataManager.getArtistSet()
    fun getAlbumSet(): Set<Album> = DataManager.getAlbumSet()
    fun getGenreSet(): Set<Genre> = DataManager.getGenreSet()
    fun getMusicSet(): Set<Music> = DataManager.getMusicSet()
    fun getPlaylistSet(): Set<Playlist> = DataManager.getPlaylistSet()

    fun getFolder(id: Long): Folder = DataManager.getFolder(id = id)
    fun getArtist(id: Long): Artist = DataManager.getArtist(id = id)
    fun getAlbum(id: Long): Album = DataManager.getAlbum(id = id)
    fun getGenre(id: Long): Genre = DataManager.getGenre(id = id)
    fun getPlaylist(id: Long): Playlist = DataManager.getPlaylist(id = id)

    fun insertMusicToPlaylists(music: Music, playlists: List<Playlist>) {
        _db.insertMusicToPlaylists(music = music, playlists = playlists)
    }

    fun addOnePlaylist(playlistTitle: String) {
        _db.addOnePlaylist(
            context = MainActivity.instance.applicationContext,
            playlistTitle = playlistTitle
        )
    }

    fun insertMusicsToPlaylist(musics: Collection<Music>, playlist: Playlist) {
        _db.insertMusicsToPlaylist(musics = musics, playlist = playlist)
    }

    fun removeMusicFromPlaylist(music: Music, playlist: Playlist) {
        _db.removeMusicFromPlaylist(music = music, playlist = playlist)
    }

    fun updatePlaylists(vararg playlists: Playlist) {
        _db.updatePlaylists(playlists = playlists)
    }

    fun removePlaylist(playlist: Playlist) {
        _db.removePlaylist(playlist = playlist)
    }
}