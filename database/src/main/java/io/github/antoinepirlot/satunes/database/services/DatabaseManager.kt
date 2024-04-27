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
import android.os.Environment
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
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
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

/**
 * @author Antoine Pirlot on 27/03/2024
 */
class DatabaseManager(context: Context) {

    private val database: SatunesDatabase = SatunesDatabase.getDatabase(context = context)
    private val musicDao: MusicDAO = database.musicDao()
    private val playlistDao: PlaylistDAO = database.playlistDao()
    private val musicsPlaylistsRelDAO: MusicsPlaylistsRelDAO = database.musicsPlaylistsRelDao()

    companion object {
        private const val PLAYLIST_JSON_OBJECT_NAME = "all_playlists"
        private val FILE_PATH: String =
            Environment.getExternalStorageDirectory().absolutePath + '/' + Environment.DIRECTORY_DOCUMENTS + "/Satunes.json"
        val importingPlaylist: MutableState<Boolean> = mutableStateOf(false)
    }

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
        val activity = Activity()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                playlist.id = playlistDao.insertOne(playlist = playlist)
            } catch (_: SQLiteConstraintException) {
                val message: String = context.getString(R.string.playlist_already_exist)
                showToastOnUiThread(context = context, activity = activity, message = message)
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

    fun exportPlaylist(context: Context, playlistWithMusics: PlaylistWithMusics) {
        val activity = Activity()
        CoroutineScope(Dispatchers.IO).launch {
            val json: String = Json.encodeToString(playlistWithMusics)
            exportJson(context = context, activity = activity, json = json)
        }
    }

    fun exportAll(context: Context) {
        val activity = Activity()
        CoroutineScope(Dispatchers.IO).launch {
            var json = "{\"${Companion.PLAYLIST_JSON_OBJECT_NAME}\":["
            DataManager.playlistWithMusicsMap.values.forEach { playlistWithMusics: PlaylistWithMusics? ->
                json += Json.encodeToString(playlistWithMusics)
                if (playlistWithMusics != DataManager.playlistWithMusicsMap.values.last()) {
                    json += ','
                }
            }
            json += "]}"
            exportJson(context = context, activity = activity, json = json)
        }
    }

    private fun exportJson(context: Context, activity: Activity, json: String) {
        try {
            val file =
                File(FILE_PATH)
            if (file.exists()) {
                file.delete()
            }
            file.createNewFile()
            file.writeText(text = json, charset = Charsets.UTF_8)
        } catch (e: Exception) {
            val message: String = context.getString(R.string.exporting_failed)
            showToastOnUiThread(context = context, activity = activity, message = message)
            e.printStackTrace()
        }
    }

    fun importPlaylists(context: Context) {
        importingPlaylist.value = true
        val activity = Activity()
        CoroutineScope(Dispatchers.IO).launch {
            showToastOnUiThread(
                context = context,
                activity = activity,
                message = context.getString(R.string.importing)
            )
            try {
                val file = File(FILE_PATH)
                if (!file.exists()) {
                    showToastOnUiThread(
                        context,
                        activity = activity,
                        message = context.getString(R.string.file_not_found)
                    )
                    //TODO check if finally is even done
                    importingPlaylist.value = false
                    return@launch
                }

                val json: String = file.readText(charset = Charsets.UTF_8)
                if (!json.startsWith("{\"$PLAYLIST_JSON_OBJECT_NAME\":[") && !json.endsWith("]}")) {
                    throw IllegalArgumentException("It is not a json object")
                }
                val playlistsList: List<String> = json.split("{\"all_playlists\":[")[1].removeRange(
                    json.lastIndex - 1,
                    json.lastIndex
                ).split(",")
                // playlists is a list of json object of PlaylistWithMusics

                playlistsList.forEach { playlist: String ->
                    val playlistWithMusics: PlaylistWithMusics = Json.decodeFromString(playlist)
                    insertOne(context = context, playlist = playlistWithMusics.playlist)
                }
            } catch (e: Exception) {
                showToastOnUiThread(
                    context = context,
                    activity = activity,
                    message = context.getString(R.string.importing_failed)
                )
                e.printStackTrace()
            } finally {
                importingPlaylist.value = false
            }
        }
    }
}