/*
 * This file is part of MP3 Player.
 *
 * MP3 Player is free software: you can redistribute it and/or modify it under
 *  the terms of the GNU General Public License as published by the Free Software Foundation,
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

package earth.mp3player.database.services

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import earth.mp3player.database.MP3PlayerDatabase
import earth.mp3player.database.daos.MusicDAO
import earth.mp3player.database.daos.MusicsPlaylistsRelDAO
import earth.mp3player.database.daos.PlaylistDAO
import earth.mp3player.database.models.Music
import earth.mp3player.database.models.relations.PlaylistWithMusics
import earth.mp3player.database.models.tables.MusicDB
import earth.mp3player.database.models.tables.MusicsPlaylistsRel
import earth.mp3player.database.models.tables.Playlist
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * @author Antoine Pirlot on 27/03/2024
 */
class DatabaseManager(context: Context) {
    private val database: MP3PlayerDatabase = MP3PlayerDatabase.getDatabase(context = context)
    private val musicDao: MusicDAO = database.musicDao()
    private val playlistDao: PlaylistDAO = database.playlistDao()
    private val musicsPlaylistsRelDAO: MusicsPlaylistsRelDAO = database.musicsPlaylistsRelDao()

    fun loadAllPlaylistsWithMusic() {
        CoroutineScope(Dispatchers.IO).launch {
            val playlistsWithMusicsList: List<PlaylistWithMusics> =
                playlistDao.getPlaylistsWithMusics()
            playlistsWithMusicsList.forEach { playlistWithMusics: PlaylistWithMusics ->
                DataManager.addPlaylist(playlistWithMusics = playlistWithMusics)
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

    fun insertOne(playlist: Playlist) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                playlist.id = playlistDao.insertOne(playlist = playlist)
            } catch (_: SQLiteConstraintException) {
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
}