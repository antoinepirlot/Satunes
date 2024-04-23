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

package io.github.antoinepirlot.satunes.database.services

import android.app.Activity
import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.widget.Toast
import io.github.antoinepirlot.satunes.database.R
import io.github.antoinepirlot.satunes.database.SatunesDatabase
import io.github.antoinepirlot.satunes.database.daos.MusicDAO
import io.github.antoinepirlot.satunes.database.daos.MusicsPlaylistsRelDAO
import io.github.antoinepirlot.satunes.database.daos.PlaylistDAO
import io.github.antoinepirlot.satunes.database.exceptions.MusicNotFoundException
import io.github.antoinepirlot.satunes.database.models.Music
import io.github.antoinepirlot.satunes.database.models.relations.PlaylistWithMusics
import io.github.antoinepirlot.satunes.database.models.tables.MusicDB
import io.github.antoinepirlot.satunes.database.models.tables.MusicsPlaylistsRel
import io.github.antoinepirlot.satunes.database.models.tables.Playlist
import io.github.antoinepirlot.utils.showToastOnUiThread
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * @author Antoine Pirlot on 27/03/2024
 */
class DatabaseManager(context: Context) {
    private val database: SatunesDatabase = SatunesDatabase.getDatabase(context = context)
    private val musicDao: MusicDAO = database.musicDao()
    private val playlistDao: PlaylistDAO = database.playlistDao()
    private val musicsPlaylistsRelDAO: MusicsPlaylistsRelDAO = database.musicsPlaylistsRelDao()

    fun loadAllPlaylistsWithMusic() {
        CoroutineScope(Dispatchers.IO).launch {
            var playlistsWithMusicsList: List<PlaylistWithMusics>? = null
            while (playlistsWithMusicsList == null) {
                try {
                    playlistsWithMusicsList = playlistDao.getPlaylistsWithMusics()
                    playlistsWithMusicsList.forEach { playlistWithMusics: PlaylistWithMusics ->
                        DataManager.addPlaylist(playlistWithMusics = playlistWithMusics)
                    }
                } catch (e: MusicNotFoundException) {
                    musicDao.remove(musicId = e.musicId)
                }
            }
        }
    }

    fun insertMusicToPlaylists(music: Music, playlists: MutableList<PlaylistWithMusics>) {
        CoroutineScope(Dispatchers.IO).launch {
            var musicDb: MusicDB? = musicDao.get(music.id)
            if (musicDb == null) {
                musicDb = MusicDB(id = music.id)
                musicDao.insert(musicDb)
            }
            playlists.forEach { playlistWithMusics: PlaylistWithMusics ->
                val musicsPlaylistsRel =
                    MusicsPlaylistsRel(
                        musicId = music.id,
                        playlistId = playlistWithMusics.playlist.id
                    )
                try {
                    musicsPlaylistsRelDAO.insert(musicsPlaylistsRel)
                    playlistWithMusics.addMusic(music = music)
                } catch (_: SQLiteConstraintException) {
                    return@launch
                }
            }
        }
    }

    fun insertOne(context: Context, playlist: Playlist) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                playlist.id = playlistDao.insertOne(playlist = playlist)
            } catch (_: SQLiteConstraintException) {
                val message: String = context.getString(R.string.playlist_already_exist)
                showToastOnUiThread(context = context, activity = Activity(), message = message)
                return@launch
            }
            val playlistWithMusics: PlaylistWithMusics =
                playlistDao.getPlaylistWithMusics(playlistId = playlist.id)!!
            DataManager.addPlaylist(playlistWithMusics = playlistWithMusics)
        }
    }

    fun removeMusicFromPlaylist(music: Music, playlist: PlaylistWithMusics) {
        CoroutineScope(Dispatchers.IO).launch {
            musicsPlaylistsRelDAO.delete(musicId = music.id, playlistId = playlist.playlist.id)
            playlist.removeMusic(music = music)
            if (!musicsPlaylistsRelDAO.isMusicInPlaylist(musicId = music.id)) {
                musicDao.delete(MusicDB(id = music.id))
            }
        }
    }

    fun removePlaylist(playlistToRemove: PlaylistWithMusics) {
        CoroutineScope(Dispatchers.IO).launch {
            playlistDao.remove(playlistToRemove.playlist)
            playlistToRemove.musics.forEach { musicDb: MusicDB ->
                musicsPlaylistsRelDAO.delete(
                    musicId = musicDb.music.id,
                    playlistId = playlistToRemove.playlist.id
                )
                if (!musicsPlaylistsRelDAO.isMusicInPlaylist(musicId = musicDb.id)) {
                    musicDao.delete(musicDb)
                }
            }
            DataManager.removePlaylist(playlistWithMusics = playlistToRemove)
        }
    }

    fun insertMusicsToPlaylist(musics: MutableList<Music>, playlist: PlaylistWithMusics) {
        CoroutineScope(Dispatchers.IO).launch {
            musics.forEach { music: Music ->
                insertMusicToPlaylists(music = music, playlists = mutableListOf(playlist))
            }
        }
    }
}