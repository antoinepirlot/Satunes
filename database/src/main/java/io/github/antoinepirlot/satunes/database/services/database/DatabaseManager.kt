/*
 * This file is part of Satunes.
 *
 * Satunes is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * Satunes is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with Satunes.
 * If not, see <https://www.gnu.org/licenses/>.
 *
 * *** INFORMATION ABOUT THE AUTHOR *****
 * The author of this file is Antoine Pirlot, the owner of this project.
 * You find this original project on github.
 *
 * My github link is: https://github.com/antoinepirlot
 * This current project's link is: https://github.com/antoinepirlot/Satunes
 *
 * PS: I don't answer quickly.
 */

package io.github.antoinepirlot.satunes.database.services.database

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.net.Uri
import io.github.antoinepirlot.satunes.database.R
import io.github.antoinepirlot.satunes.database.daos.LIKES_PLAYLIST_TITLE
import io.github.antoinepirlot.satunes.database.daos.MusicDAO
import io.github.antoinepirlot.satunes.database.daos.MusicsPlaylistsRelDAO
import io.github.antoinepirlot.satunes.database.daos.PlaylistDAO
import io.github.antoinepirlot.satunes.database.exceptions.BlankStringException
import io.github.antoinepirlot.satunes.database.exceptions.LikesPlaylistCreationException
import io.github.antoinepirlot.satunes.database.exceptions.MusicNotFoundException
import io.github.antoinepirlot.satunes.database.exceptions.PlaylistAlreadyExistsException
import io.github.antoinepirlot.satunes.database.exceptions.PlaylistNotFoundException
import io.github.antoinepirlot.satunes.database.models.Folder
import io.github.antoinepirlot.satunes.database.models.MediaImpl
import io.github.antoinepirlot.satunes.database.models.Music
import io.github.antoinepirlot.satunes.database.models.Playlist
import io.github.antoinepirlot.satunes.database.models.SatunesDatabase
import io.github.antoinepirlot.satunes.database.models.database.relations.PlaylistWithMusics
import io.github.antoinepirlot.satunes.database.models.database.tables.MusicDB
import io.github.antoinepirlot.satunes.database.models.database.tables.MusicsPlaylistsRel
import io.github.antoinepirlot.satunes.database.models.database.tables.PlaylistDB
import io.github.antoinepirlot.satunes.database.services.data.DataLoader
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
    private val _logger: SatunesLogger? = SatunesLogger.getLogger()

    companion object {
        private lateinit var _instance: DatabaseManager
        var importingPlaylist: Boolean = false
        var exportingPlaylist: Boolean = false

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
                var playlist = Playlist(
                    id = playlistWithMusics.playlistDB.id,
                    title = playlistWithMusics.playlistDB.title
                )
                playlist = DataManager.addPlaylist(playlist = playlist)
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
            _logger?.warning(e.message)
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

    /**
     * Create new Playlist if doesn't exist in DB otherwise advertise the user that it already exists.
     *
     * @param playlistTitle the playlist title
     * @param musicList the music contains all music as MusicDB
     * @throws BlankStringException when playlistTitle is blank
     * @throws IllegalArgumentException when playlistTitle is in incorrect format
     * @throws PlaylistAlreadyExistsException when there's already a playlist with the same playlistTitle
     */
    fun addOnePlaylist(playlistTitle: String, musicList: MutableList<Music>? = null): Playlist {
        checkString(string = playlistTitle)
        @Suppress("NAME_SHADOWING")
        val playlistTitle: String = playlistTitle.trim()
        if (playlistDao.exists(title = playlistTitle)) throw PlaylistAlreadyExistsException()
        val playlistId: Long =
            playlistDao.insertOne(playlistDB = PlaylistDB(title = playlistTitle))
        var playlist = Playlist(id = playlistId, title = playlistTitle)
        playlist = DataManager.addPlaylist(playlist = playlist)

        musicList?.forEach { music: Music ->
            insertMusicToPlaylist(
                music = music,
                playlist = playlist
            )
        }
        return playlist
    }

    /**
     * Checks the validity of string.
     *
     * @throws BlankStringException if the string is blank.
     * @throws IllegalArgumentException if the string contains spaces or tabulations
     */
    private fun checkString(string: String) {
        if (string.isBlank()) throw BlankStringException()
        if (string.contains("\n") || string.contains("\t"))
            throw IllegalArgumentException("string contains spaces or tabulations")
    }

    fun updatePlaylistTitle(playlist: Playlist) {
        checkString(playlist.title)
        playlist.title = playlist.title.trim()
        val playlistDB = PlaylistDB(id = playlist.id, title = playlist.title)
        if (playlistDao.exists(title = playlist.title)) {
            playlist.title = this.playlistDao.getOriginalPlaylistTitle(playlistId = playlist.id)
            throw PlaylistAlreadyExistsException()
        }
        try {
            playlistDao.update(playlistDB = playlistDB)
        } catch (e: SQLiteConstraintException) {
            _logger?.warning(e.message)
            throw e
        }
    }

    /**
     * Update the list of music of playlist.
     *
     * @param playlist is the playlist with the old music list
     * @param newMusicCollection the new collection of [Music]
     *
     */
    fun updatePlaylistMusics(playlist: Playlist, newMusicCollection: Collection<Music>) {
        if (!playlistDao.exists(title = playlist.title)) throw PlaylistNotFoundException(playlist.id)
        try {
            val oldMusicCollection: Collection<Music> = playlist.getMusicSet()
            val newMusicSet: MutableCollection<Music> = newMusicCollection.toMutableSet()
            val removedMusic: MutableCollection<Music> = mutableListOf()
            for (music: Music in oldMusicCollection)
                if (!newMusicCollection.contains(music))
                    removedMusic.add(element = music)
                else newMusicSet.remove(element = music)

            removeMusicsFromPlaylist(musics = removedMusic, playlist = playlist)
            insertMusicsToPlaylist(musics = newMusicSet, playlist = playlist)
        } catch (e: Throwable) {
            _logger?.severe(e.message)
            throw e
        }
    }

    fun updateMediaToPlaylists(mediaImpl: MediaImpl, playlists: Collection<Playlist>) {
        val musics: Set<Music> = if (mediaImpl is Folder) {
            mediaImpl.getAllMusic()
        } else {
            mediaImpl.getMusicSet()
        }
        val allPlaylists: Collection<Playlist> = DataManager.getPlaylistSet()
        for (playlist: Playlist in allPlaylists) {
            val newMusicCollection: MutableCollection<Music> = playlist.getMusicSet().toMutableSet()
            if (playlists.contains(element = playlist)) {
                //Playlist has been checked
                newMusicCollection.addAll(elements = playlist.getMusicSet().toMutableSet())
                newMusicCollection.addAll(elements = musics)
                this.updatePlaylistMusics(playlist = playlist, newMusicCollection)
            } else {
                //Playlist has been unchecked (only remove if all of its music was inside)
                if (playlist.getMusicSet().containsAll(musics)) {
                    newMusicCollection.addAll(elements = playlist.getMusicSet().toMutableSet())
                    newMusicCollection.removeAll(elements = musics)
                    this.updatePlaylistMusics(playlist = playlist, newMusicCollection)
                }
            }
        }
    }

    fun insertMusicToPlaylist(music: Music, playlist: Playlist) {
        val musicsPlaylistsRel =
            MusicsPlaylistsRel(
                musicId = music.id,
                playlistId = playlist.id,
                addedDateMs = System.currentTimeMillis()
            )
        try {
            musicsPlaylistsRelDAO.insert(musicsPlaylistsRel)
            musicDao.insert(MusicDB(id = music.id, absolutePath = music.absolutePath))
        } catch (e: SQLiteConstraintException) {
            _logger?.warning(e.message)
            // Do nothing
        }
        if (playlist.title == LIKES_PLAYLIST_TITLE) {
            musicDao.like(musicId = music.id)
            music.liked.value = true
        }
    }

    fun insertMusicsToPlaylist(musics: Collection<Music>, playlist: Playlist) {
        musics.forEach { music: Music ->
            insertMusicToPlaylist(music = music, playlist = playlist)
        }
    }

    fun updateMusicToPlaylists(music: Music, newPlaylistsCollection: Collection<Playlist>) {
        val oldPlaylistCollection: Collection<Long> =
            musicsPlaylistsRelDAO.getAllPlaylistsIdsOf(musicId = music.id)
        val newPlaylists: MutableCollection<Playlist> = newPlaylistsCollection.toMutableSet()
        val removedPlaylist: MutableCollection<Playlist> = mutableSetOf()
        for (oldPlaylistId: Long in oldPlaylistCollection) {
            val playlist: Playlist = DataManager.getPlaylist(id = oldPlaylistId)!!
            if (!newPlaylistsCollection.contains(element = playlist)) removedPlaylist.add(element = playlist)
            else newPlaylists.remove(element = playlist)
        }
        removeMusicFromPlaylists(music = music, playlists = removedPlaylist)
        insertMusicsToPlaylists(musics = listOf(music), playlists = newPlaylists)
    }

    fun insertMusicsToPlaylists(musics: Collection<Music>, playlists: Collection<Playlist>) {
        for (playlist: Playlist in playlists) {
            this.insertMusicsToPlaylist(musics = musics, playlist = playlist)
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

    fun removeMusicsFromPlaylist(musics: Collection<Music>, playlist: Playlist) {
        for (music: Music in musics) {
            this.removeMusicFromPlaylist(music = music, playlist = playlist)
        }
    }

    fun removeMusicFromPlaylists(music: Music, playlists: Collection<Playlist>) {
        playlists.forEach { playlist: Playlist ->
            removeMusicFromPlaylist(music = music, playlist = playlist)
        }
    }

    fun removeMusicsFromPlaylists(musics: Collection<Music>, playlists: Collection<Playlist>) {
        for (playlist: Playlist in playlists) {
            this.removeMusicsFromPlaylist(musics = musics, playlist = playlist)
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

    private fun getAllPlaylistWithMusics(): List<PlaylistWithMusics> {
        return this.playlistDao.getPlaylistsWithMusics()
    }

    fun exportPlaylist(context: Context, uri: Uri, playlist: Playlist) {
        val playlistWithMusics: PlaylistWithMusics =
            this.playlistDao.getPlaylistWithMusics(playlistId = playlist.id)!!
        val json: String = Json.encodeToString(playlistWithMusics)
        exportJson(context = context, json = json, uri = uri)
        exportingPlaylist = false
    }

    fun exportPlaylists(context: Context, uri: Uri) {
        val playlistsWithMusics: List<PlaylistWithMusics> = this.getAllPlaylistWithMusics()
        val json: String = Json.encodeToString(playlistsWithMusics)
        exportJson(context = context, json = json, uri = uri)
        exportingPlaylist = false
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
            _logger?.severe(e.message)
            throw e
        }
    }

    fun importPlaylists(context: Context, uri: Uri) {
        importingPlaylist = true
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
                val playlistsWithMusics: List<PlaylistWithMusics> = try {
                    Json.decodeFromString(json)
                } catch (_: IllegalArgumentException) {
                    val playlistWithMusics: PlaylistWithMusics = Json.decodeFromString(json)
                    listOf(playlistWithMusics)
                }

                importPlaylistsToDatabase(playlistWithMusicsList = playlistsWithMusics)
                try {
                    showToastOnUiThread(
                        context = context,
                        message = context.getString(R.string.importing_success)
                    )
                } catch (_: Throwable) {
                    /*TODO use snackbar instead of this buggy thing */
                }
            } catch (e: MusicNotFoundException) {
                //id is used to store the number of musics missing
                showToastOnUiThread(
                    context = context,
                    message = context.getString(R.string.importing_missed_musics, e.id)
                )
            } catch (e: Throwable) {
                logger?.severe(e.message)
                throw e
            } finally {
                importingPlaylist = false
            }
        }
    }

    @Throws(NullPointerException::class)
    private fun importPlaylistsToDatabase(playlistWithMusicsList: List<PlaylistWithMusics>) {
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
                    playlistTitle = playlistWithMusics.playlistDB.title,
                    musicList = musicList,
                )
            } catch (_: PlaylistAlreadyExistsException) {
                this.insertMusicsToPlaylist(
                    musics = musicList,
                    playlist = playlistWithMusics.playlistDB.playlist!!
                )
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
            insertMusicToPlaylist(music = music, playlist = likesPlaylist.playlistDB.playlist!!)
        } catch (e: Throwable) {
            _logger?.severe(e.message)
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
            _logger?.severe(e.message)
            throw e
        }
    }

    /**
     * Cleans playlists of not loaded musics.
     *
     * For each musics from all playlists, check if Satunes has loaded them, if not, remove them
     * from playlists.
     */
    suspend fun cleanPlaylists() {
        _logger?.info("Cleaning playlists")
        val musicsPlaylistsRelList: List<Long> = musicsPlaylistsRelDAO.getAllMusicIds()
        for (musicId: Long in musicsPlaylistsRelList) {
            val musicDB: MusicDB? = musicDao.get(id = musicId)
            if (musicDB == null) {
                _logger?.warning("Not musicDB matching with id in relation (it's weird)")
                musicsPlaylistsRelDAO.removeAll(musicId = musicId)
                musicDao.delete(musicId = musicId)
            } else if (musicDB.music == null) {
                _logger?.info("Removing not loaded music")
                musicsPlaylistsRelDAO.removeAll(musicId = musicId)
                musicDao.delete(musicId = musicId)
            }
        }
    }

    suspend fun getOrder(playlist: Playlist, music: Music): Long {
        if (!DataLoader.isLoading.value) _logger?.info("Get Order") // It will reduce startup speed if executed
        val musicsPlaylistsRelList: List<MusicsPlaylistsRel> =
            musicsPlaylistsRelDAO.getAllFromPlaylist(playlistId = playlist.id)
        val musicsPlaylistsRel: MusicsPlaylistsRel =
            musicsPlaylistsRelList.first { it.musicId == music.id }
        return musicsPlaylistsRel.addedDateMs
    }
}