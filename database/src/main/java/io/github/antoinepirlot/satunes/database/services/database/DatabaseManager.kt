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
import io.github.antoinepirlot.satunes.database.models.SatunesDatabase
import io.github.antoinepirlot.satunes.database.daos.LIKES_PLAYLIST_TITLE
import io.github.antoinepirlot.satunes.database.daos.MusicDAO
import io.github.antoinepirlot.satunes.database.daos.MusicsPlaylistsRelDAO
import io.github.antoinepirlot.satunes.database.daos.PlaylistDAO
import io.github.antoinepirlot.satunes.database.models.MediaImpl
import io.github.antoinepirlot.satunes.database.models.Music
import io.github.antoinepirlot.satunes.database.models.Playlist
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
class DatabaseManager(context: Context) {

    private val database: SatunesDatabase = SatunesDatabase.getDatabase(context = context)
    private val musicDao: MusicDAO = database.musicDao()
    private val playlistDao: PlaylistDAO = database.playlistDao()
    private val musicsPlaylistsRelDAO: MusicsPlaylistsRelDAO = database.musicsPlaylistsRelDao()
    private val logger = SatunesLogger(name = this::class.java.name)

    companion object {
        private const val PLAYLIST_JSON_OBJECT_NAME = "all_playlists"
        val importingPlaylist: MutableState<Boolean> = mutableStateOf(false)
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
                    if (playlist.title == LIKES_PLAYLIST_TITLE) {
                        val music: Music = musicDB.music!!
                        music.liked.value = true
                    }
                    playlist.addMusic(music = musicDB.music!!)
                }
            }
        } catch (e: Exception) {
            logger.warning(e.message)
            e.printStackTrace()
        }
    }

    fun insertMusicToPlaylists(
        music: Music,
        playlists: List<Playlist>,
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            playlists.forEach { playlist: Playlist ->
                val musicsPlaylistsRel =
                    MusicsPlaylistsRel(
                        musicId = music.id,
                        playlistId = playlist.id
                    )
                try {
                    musicsPlaylistsRelDAO.insert(musicsPlaylistsRel)
                    try {
                        musicDao.insert(MusicDB(id = music.id))
                    } catch (_: SQLiteConstraintException) {
                        // Do nothing
                    }
                    playlist.addMusic(music = music)
                } catch (_: SQLiteConstraintException) {
                    // Do nothing
                }
                if (playlist.title == LIKES_PLAYLIST_TITLE) {
                    musicDao.like(musicId = music.id)
                    music.liked.value = true
                }
            }
        }
    }

    /**
     * Insert one playlistDB to db with its eventual music list
     *
     * @param context
     * @param playlist the playlistDB to insert
     * @param musicList the music contains all music as MusicDB
     * @param showToast true if you want the app showing toast
     */
    fun insertPlaylistWithMusics(
        context: Context,
        playlist: MediaImpl,
        musicList: MutableList<Music>? = null,
        showToast: Boolean = true
    ) {
        if (playlist !is Playlist) {
            val message: String = "Playlist is not the right type"
            logger.severe(message)
            throw IllegalArgumentException()
        }

        CoroutineScope(Dispatchers.IO).launch {
            if (playlistDao.playlistExist(title = playlist.title)) {
                val message: String =
                    playlist.title + context.getString(R.string.playlist_already_exist)
                showToastOnUiThread(context = context, message = message)
                return@launch
            }
            val playlistDB = PlaylistDB(id = playlist.id, title = playlist.title)
            playlist.id = playlistDao.insertOne(playlistDB = playlistDB)
            val playlistWithMusics: PlaylistWithMusics =
                playlistDao.getPlaylistWithMusics(playlistId = playlist.id)!!
            DataManager.addPlaylist(playlist = playlistWithMusics.playlistDB.playlist!!)

            musicList?.forEach { music: Music ->
                insertMusicToPlaylists(
                    music = music,
                    playlists = listOf(playlist),
                )
            }
            if (showToast) {
                showToastOnUiThread(
                    context = context,
                    message = context.getString(R.string.created)
                )
            }
        }
    }

    fun updatePlaylists(vararg playlists: Playlist) {
        CoroutineScope(Dispatchers.IO).launch {
            val playlistDBs: MutableList<PlaylistDB> = mutableListOf()
            playlists.forEach { playlist: Playlist ->
                playlistDBs.add(PlaylistDB(id = playlist.id, title = playlist.title))
            }
            try {
                playlistDao.update(*playlistDBs.toTypedArray())
            } catch (e: SQLiteConstraintException) {
                logger.severe(e.message)
                throw e
            }
        }
    }

    fun removeMusicFromPlaylist(music: Music, playlist: Playlist) {
        CoroutineScope(Dispatchers.IO).launch {
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
                musicDao.delete(MusicDB(id = music.id))
            }
        }
    }

    fun removePlaylist(playlist: Playlist) {
        CoroutineScope(Dispatchers.IO).launch {
            playlistDao.remove(id = playlist.id)
            playlist.musicMediaItemMap.keys.forEach { music: Music ->
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
    }

    fun insertMusicsToPlaylist(
        musics: List<Music>,
        playlist: Playlist
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            musics.forEach { music: Music ->
                insertMusicToPlaylists(
                    music = music,
                    playlists = listOf(playlist),
                )
            }
        }
    }

    fun exportPlaylists(
        context: Context,
        vararg playlists: Playlist,
        uri: Uri
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val playlistsDBs: MutableList<PlaylistDB> = mutableListOf()
            playlists.forEach { playlist: Playlist ->
                playlistsDBs.add(element = PlaylistDB(id = playlist.id, title = playlist.title))
            }

            var json = "{\"$PLAYLIST_JSON_OBJECT_NAME\":["
            playlistsDBs.forEach { playlistDB: PlaylistDB ->
                json += Json.encodeToString(playlistDB) + ','
            }
            json += "]}"
            exportJson(context = context, json = json, uri = uri)
        }
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
            logger.severe(e.message)
            e.printStackTrace()
            throw e
        }
    }

    fun importPlaylists(context: Context, uri: Uri) {
        importingPlaylist.value = true
        val logger = SatunesLogger(this::class.java.name)

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
                var json: String =
                    readTextFromUri(context = context, uri = uri, showToast = true)
                        ?: throw Exception()
                if (!json.startsWith("{\"$PLAYLIST_JSON_OBJECT_NAME\":[") && !json.endsWith("]}")) {
                    throw IllegalArgumentException("It is not the correct file")
                }
                json = json.split("{\"all_playlists\":[")[1]
                json = json.removeSuffix(",]}")
                if (json.isBlank()) {
                    throw IllegalArgumentException("JSON file is blank")
                }
                var playlistList: List<String> = json.split("\"playlistDB\":")
                playlistList = playlistList.subList(fromIndex = 1, toIndex = playlistList.size)
                playlistList.forEach { s: String ->
                    json = "{\"playlistDB\":" + s.removeSuffix(",{")
                    val playlistWithMusics: PlaylistWithMusics = Json.decodeFromString(json)
                    importPlaylistToDatabase(
                        context = context,
                        playlistWithMusics = playlistWithMusics
                    )
                }
                showToastOnUiThread(
                    context = context,
                    message = context.getString(R.string.importing_success)
                )
            } catch (e: IllegalArgumentException) {
                showToastOnUiThread(
                    context = context,
                    message = context.getString(R.string.importing_failed)
                )
                logger.warning(e.message)
                e.printStackTrace()
            } catch (e: Throwable) {
                logger.severe(e.message)
                throw e
            } finally {
                importingPlaylist.value = false
            }
        }
    }

    private fun importPlaylistToDatabase(
        context: Context,
        playlistWithMusics: PlaylistWithMusics
    ) {
        try {
            playlistWithMusics.playlistDB.id = 0
            playlistWithMusics.id = 0

            val musicList: MutableList<Music> = mutableListOf()
            playlistWithMusics.musics.forEach { musicDB: MusicDB ->
                musicList.add(musicDB.music!!)
            }
            insertPlaylistWithMusics(
                context = context,
                playlist = playlistWithMusics.playlistDB.playlist!!, // TODO issue
                musicList = musicList,
                showToast = false
            )
        } catch (_: Exception) {
            // Do nothing
        }
    }

    fun like(context: Context, music: Music) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val likesPlaylist: PlaylistWithMusics? =
                    playlistDao.getPlaylistWithMusics(title = LIKES_PLAYLIST_TITLE)
                if (likesPlaylist == null) {
                    insertPlaylistWithMusics(
                        context = context,
                        musicList = mutableListOf(MusicDB(id = music.id).music!!),
                        playlist = PlaylistDB(id = 0, title = LIKES_PLAYLIST_TITLE).playlist!!
                    )
                } else {
                    insertMusicToPlaylists(
                        music = music,
                        playlists = listOf(likesPlaylist.playlistDB.playlist!!)
                    )
                }
            } catch (e: Exception) {
                logger.warning(e.message)
                e.printStackTrace()
            }
        }
    }

    fun unlike(music: Music) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val likesPlaylist: PlaylistWithMusics =
                    playlistDao.getPlaylistWithMusics(title = LIKES_PLAYLIST_TITLE)
                        ?: return@launch
                removeMusicFromPlaylist(
                    music = music,
                    playlist = DataManager.getPlaylist(id = likesPlaylist.id)
                )
                musicDao.unlike(musicId = music.id)
            } catch (e: Exception) {
                logger.warning(e.message)
                e.printStackTrace()
            }
        }
    }
}