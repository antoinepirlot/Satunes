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

package io.github.antoinepirlot.satunes.database.services.database

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import io.github.antoinepirlot.satunes.database.R
import io.github.antoinepirlot.satunes.database.daos.LIKES_PLAYLIST_TITLE
import io.github.antoinepirlot.satunes.database.daos.MusicDAO
import io.github.antoinepirlot.satunes.database.daos.MusicsPlaylistsRelDAO
import io.github.antoinepirlot.satunes.database.daos.PlaylistDAO
import io.github.antoinepirlot.satunes.database.exceptions.BlankStringException
import io.github.antoinepirlot.satunes.database.exceptions.LikesPlaylistCreationException
import io.github.antoinepirlot.satunes.database.exceptions.MusicNotFoundException
import io.github.antoinepirlot.satunes.database.exceptions.PlaylistAlreadyExistsException
import io.github.antoinepirlot.satunes.database.models.Music
import io.github.antoinepirlot.satunes.database.models.Playlist
import io.github.antoinepirlot.satunes.database.models.SatunesDatabase
import io.github.antoinepirlot.satunes.database.models.database.relations.PlaylistWithMusics
import io.github.antoinepirlot.satunes.database.models.database.tables.MusicDB
import io.github.antoinepirlot.satunes.database.models.database.tables.MusicsPlaylistsRel
import io.github.antoinepirlot.satunes.database.models.database.tables.PlaylistDB
import io.github.antoinepirlot.satunes.database.services.data.DataManager
import io.github.antoinepirlot.satunes.utils.logger.SatunesLogger
import io.github.antoinepirlot.satunes.utils.utils.readTextFromUri
import io.github.antoinepirlot.satunes.utils.utils.showToastOnUiThread
import io.github.antoinepirlot.satunes.utils.utils.writeToUri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * @author Antoine Pirlot on 27/03/2024
 */
class DatabaseManager private constructor(context: Context) {

    private val database: SatunesDatabase = SatunesDatabase.getDatabase(context = context)
    private val musicDao: MusicDAO = database.musicDao()
    private val playlistDao: PlaylistDAO = database.playlistDao()
    private val musicsPlaylistsRelDAO: MusicsPlaylistsRelDAO = database.musicsPlaylistsRelDao()
    private val _logger = SatunesLogger.getLogger()

    companion object {
        private lateinit var _instance: DatabaseManager
        val importingPlaylist: MutableState<Boolean> = mutableStateOf(false)

        fun getInstance(): DatabaseManager {
            if (!this::_instance.isInitialized) {
                throw IllegalStateException("The DatabaseManager has not been initialized")
            }
            return _instance
        }

        fun initInstance(context: Context): DatabaseManager {
            if (!this::_instance.isInitialized) {
                this._instance = DatabaseManager(context = context)
            }
            return this._instance
        }
    }

    internal fun loadAllPlaylistsWithMusic() {
        try {
            val playlistsWithMusicsList: List<PlaylistWithMusics> =
                playlistDao.getPlaylistsWithMusics()
            playlistsWithMusicsList.forEach { playlistWithMusics: PlaylistWithMusics ->
                val playlist = Playlist(
                    id = playlistWithMusics.playlistDB.id,
                    title = playlistWithMusics.playlistDB.title
                )
                DataManager.addPlaylist(playlist = playlist)
                playlistWithMusics.musics.forEach { musicDB: MusicDB ->
                    if (musicDB.music != null) {
                        if (playlist.title == LIKES_PLAYLIST_TITLE) {
                            val music: Music = musicDB.music!!
                            music.liked.value = true
                        }
                        playlist.addMusic(music = musicDB.music!!)
                    }
                }
            }
        } catch (e: Throwable) {
            _logger.warning(e.message)
            throw e
        }
    }

    internal fun updateMusic(vararg musics: Music) {
        var musicDBs: Array<MusicDB> = arrayOf()
        musics.forEach { music: Music ->
            musicDBs += MusicDB(id = music.id, absolutePath = music.absolutePath)
        }
        this.musicDao.update(musicDBs = musicDBs)
    }

    fun insertMusicToPlaylists(music: Music, playlists: List<Playlist>) {
        playlists.forEach { playlist: Playlist ->
            val musicsPlaylistsRel =
                MusicsPlaylistsRel(
                    musicId = music.id,
                    playlistId = playlist.id
                )
            try {
                musicsPlaylistsRelDAO.insert(musicsPlaylistsRel)
                try {
                    musicDao.insert(MusicDB(id = music.id, absolutePath = music.absolutePath))
                } catch (e: SQLiteConstraintException) {
                    _logger.warning(e.message)
                    // Do nothing
                }
                playlist.addMusic(music = music)
            } catch (e: SQLiteConstraintException) {
                _logger.warning(e.message)
                // Do nothing
            }
            if (playlist.title == LIKES_PLAYLIST_TITLE) {
                musicDao.like(musicId = music.id)
                music.liked.value = true
            }
        }
    }

    /**
     * Create new Playlist if doesn't exist in DB otherwise advertise the user that it already exists.
     *
     * @param playlistTitle the playlist title
     * @param musicList the music contains all music as MusicDB
     * @throws BlankStringException when playlistTitle is blank
     * @throws PlaylistAlreadyExistsException when there's already a playlist with the same playlistTitle
     */
    fun addOnePlaylist(playlistTitle: String, musicList: MutableList<Music>? = null) {
        if (playlistTitle.isBlank()) {
            throw BlankStringException()
        }
        @Suppress("NAME_SHADOWING")
        val playlistTitle: String = playlistTitle.trim()
        if (playlistDao.exists(title = playlistTitle)) throw PlaylistAlreadyExistsException()
        val playlistId: Long =
            playlistDao.insertOne(playlistDB = PlaylistDB(title = playlistTitle))
        val playlistWithMusics: PlaylistWithMusics =
            playlistDao.getPlaylistWithMusics(playlistId = playlistId)!!

        DataManager.addPlaylist(playlist = playlistWithMusics.playlistDB.playlist!!)

        musicList?.forEach { music: Music ->
            insertMusicToPlaylists(
                music = music,
                playlists = listOf(playlistWithMusics.playlistDB.playlist!!),
            )
        }
    }

    fun updatePlaylist(playlist: Playlist) {
        if (playlist.title.isBlank()) throw BlankStringException()
        playlist.title = playlist.title.trim()
        val playlistDB = PlaylistDB(id = playlist.id, title = playlist.title)
        if (playlistDao.exists(title = playlist.title)) {
            playlist.title = this.playlistDao.getOriginalPlaylistTitle(playlistId = playlist.id)
            throw PlaylistAlreadyExistsException()
        }
        try {
            playlistDao.update(playlistDB = playlistDB)
        } catch (e: SQLiteConstraintException) {
            _logger.warning(e.message)
            throw e
        }
    }

    fun removeMusicFromPlaylist(music: Music, playlist: Playlist) {
        if (playlist.title == LIKES_PLAYLIST_TITLE) {
            musicDao.unlike(musicId = music.id)
            music.liked.value = false
        }
        musicsPlaylistsRelDAO.delete(
            musicId = music.id,
            playlistId = playlist.id
        )
        playlist.removeMusic(music = music)
        if (!musicsPlaylistsRelDAO.isMusicInPlaylist(musicId = music.id)) {
            musicDao.delete(MusicDB(id = music.id, absolutePath = music.absolutePath))
        }
    }

    fun removeMusic(id: Long) {
        this.musicDao.delete(musicId = id)
        this.musicsPlaylistsRelDAO.removeAll(musicId = id)
    }

    fun removePlaylist(playlist: Playlist) {
        playlistDao.remove(id = playlist.id)
        playlist.getMusicSet().forEach { music: Music ->
            musicsPlaylistsRelDAO.delete(musicId = music.id, playlistId = playlist.id)
            if (playlist.title == LIKES_PLAYLIST_TITLE) {
                musicDao.unlike(musicId = music.id)
                music.liked.value = false
            }
            if (!musicsPlaylistsRelDAO.isMusicInPlaylist(musicId = music.id)) {
                musicDao.delete(musicId = music.id)
            }
        }
        DataManager.removePlaylist(playlist = playlist)
    }

    fun insertMusicsToPlaylist(musics: Collection<Music>, playlist: Playlist) {
        musics.forEach { music: Music ->
            insertMusicToPlaylists(
                music = music,
                playlists = listOf(playlist),
            )
        }
    }

    private fun getAllPlaylistWithMusics(): List<PlaylistWithMusics> {
        return this.playlistDao.getPlaylistsWithMusics()
    }

    fun exportPlaylists(context: Context, uri: Uri) {
        val playlistsWithMusics: List<PlaylistWithMusics> = _instance.getAllPlaylistWithMusics()
        val json: String = Json.encodeToString(playlistsWithMusics)
        exportJson(context = context, json = json, uri = uri)
    }

    private fun exportJson(context: Context, json: String, uri: Uri) {
        try {
            if (writeToUri(context = context, uri = uri, string = json)) {
                showToastOnUiThread(
                    context = context,
                    message = context.getString(R.string.exporting_success)
                )
            } else {
                showToastOnUiThread(
                    context = context,
                    message = context.getString(R.string.exporting_failed)
                )
            }
        } catch (e: Throwable) {
            _logger.severe(e.message)
            throw e
        }
    }

    fun importPlaylists(context: Context, uri: Uri) {
        importingPlaylist.value = true
        val logger = SatunesLogger.getLogger()
        CoroutineScope(Dispatchers.IO).launch {
            showToastOnUiThread(
                context = context,
                message = context.getString(R.string.importing)
            )
            try {
                if (uri.path == null) {
                    showToastOnUiThread(
                        context = context,
                        message = context.getString(R.string.file_not_found)
                    )
                    return@launch
                }
                val json: String = readTextFromUri(context = context, uri = uri, showToast = true)
                    ?: throw Exception()
                val playlistWithMusics: List<PlaylistWithMusics> = Json.decodeFromString(json)
                importPlaylistToDatabase(playlistWithMusicsList = playlistWithMusics)
                showToastOnUiThread(
                    context = context,
                    message = context.getString(R.string.importing_success)
                )
            } catch (e: MusicNotFoundException) {
                //id is used to store the number of musics missing
                showToastOnUiThread(
                    context = context,
                    message = context.getString(R.string.importing_missed_musics, e.id)
                )
            } catch (e: Throwable) {
                logger.severe(e.message)
                throw e
            } finally {
                importingPlaylist.value = false
            }
        }
    }

    @Throws(NullPointerException::class)
    private fun importPlaylistToDatabase(playlistWithMusicsList: List<PlaylistWithMusics>) {
        var numberOfMusicMissing = 0L
        playlistWithMusicsList.forEach { playlistWithMusics: PlaylistWithMusics ->
            val musicList: MutableList<Music> = mutableListOf()
            playlistWithMusics.musics.forEach { musicDB: MusicDB ->
                val music: Music? = musicDB.music
                if (music != null) { //If null, music
                    musicList.add(musicDB.music!!)
                } else {
                    numberOfMusicMissing++
                }
            }
            try {
                addOnePlaylist(
                    playlistTitle = playlistWithMusics.playlistDB.title, // TODO issue
                    musicList = musicList,
                )
            } catch (_: PlaylistAlreadyExistsException) {
                /* Do nothing */
            }
        }
        if (numberOfMusicMissing > 0) {
            //id is used to store the number of musics missing
            throw MusicNotFoundException(id = numberOfMusicMissing)
        }
    }

    fun like(music: Music) {
        try {
            val likesPlaylist: PlaylistWithMusics? =
                playlistDao.getPlaylistWithMusics(title = LIKES_PLAYLIST_TITLE)
            if (likesPlaylist == null) {
                addOnePlaylist(
                    musicList = mutableListOf(music),
                    playlistTitle = LIKES_PLAYLIST_TITLE
                )
                throw LikesPlaylistCreationException()
            }
            insertMusicToPlaylists(
                music = music,
                playlists = listOf(likesPlaylist.playlistDB.playlist!!)
            )
        } catch (e: Throwable) {
            _logger.severe(e.message)
            throw e
        }
    }

    fun unlike(music: Music) {
        try {
            val likesPlaylist: PlaylistWithMusics =
                playlistDao.getPlaylistWithMusics(title = LIKES_PLAYLIST_TITLE)
                    ?: return
            removeMusicFromPlaylist(
                music = music,
                playlist = DataManager.getPlaylist(id = likesPlaylist.id)!!
            )
            musicDao.unlike(musicId = music.id)
        } catch (e: Throwable) {
            _logger.severe(e.message)
            throw e
        }
    }
}