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

package io.github.antoinepirlot.satunes.data.viewmodels

import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import io.github.antoinepirlot.satunes.MainActivity
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.database.daos.LIKES_PLAYLIST_TITLE
import io.github.antoinepirlot.satunes.database.exceptions.BlankStringException
import io.github.antoinepirlot.satunes.database.exceptions.LikesPlaylistCreationException
import io.github.antoinepirlot.satunes.database.exceptions.PlaylistAlreadyExistsException
import io.github.antoinepirlot.satunes.database.models.Album
import io.github.antoinepirlot.satunes.database.models.Artist
import io.github.antoinepirlot.satunes.database.models.Folder
import io.github.antoinepirlot.satunes.database.models.Genre
import io.github.antoinepirlot.satunes.database.models.Music
import io.github.antoinepirlot.satunes.database.models.Playlist
import io.github.antoinepirlot.satunes.database.services.data.DataLoader
import io.github.antoinepirlot.satunes.database.services.data.DataManager
import io.github.antoinepirlot.satunes.database.services.database.DatabaseManager
import io.github.antoinepirlot.satunes.ui.utils.showErrorSnackBar
import io.github.antoinepirlot.satunes.ui.utils.showSnackBar
import io.github.antoinepirlot.satunes.utils.logger.SatunesLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import io.github.antoinepirlot.satunes.database.R as RDb

/**
 * @author Antoine Pirlot on 19/07/2024
 */
class DataViewModel : ViewModel() {
    private val _logger: SatunesLogger = SatunesLogger.getLogger()
    private val _playlistSetUpdated: MutableState<Boolean> = DataManager.playlistsMapUpdated
    private val _db: DatabaseManager =
        DatabaseManager.initInstance(context = MainActivity.instance.applicationContext)

    var playlistSetUpdated: Boolean by _playlistSetUpdated
        private set

    fun playlistSetUpdated() {
        this._playlistSetUpdated.value = false
    }

    fun getRootFolderSet(): Set<Folder> = DataManager.getRootFolderSet()
    fun getFolderSet(): Set<Folder> = DataManager.getFolderSet()
    fun getArtistSet(): Set<Artist> = DataManager.getArtistSet()
    fun getAlbumSet(): Set<Album> = DataManager.getAlbumSet()
    fun getGenreSet(): Set<Genre> = DataManager.getGenreSet()
    fun getMusicSet(): Set<Music> = DataManager.getMusicSet()
    fun getPlaylistSet(): Set<Playlist> = DataManager.getPlaylistSet()

    fun getFolder(id: Long): Folder = DataManager.getFolder(id = id)!!
    fun getArtist(id: Long): Artist = DataManager.getArtist(id = id)!!
    fun getAlbum(id: Long): Album = DataManager.getAlbum(id = id)!!
    fun getGenre(id: Long): Genre = DataManager.getGenre(id = id)!!
    fun getPlaylist(id: Long): Playlist = DataManager.getPlaylist(id = id)!!

    fun addOnePlaylist(
        scope: CoroutineScope,
        snackBarHostState: SnackbarHostState,
        playlistTitle: String
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val context: Context = MainActivity.instance.applicationContext
            try {
                _db.addOnePlaylist(playlistTitle = playlistTitle)
                showSnackBar(
                    scope = scope,
                    snackBarHostState = snackBarHostState,
                    message = context.getString(RDb.string.add_playlist_success, playlistTitle)
                )
            } catch (e: Throwable) {
                val message: String? = when (e) {
                    is BlankStringException -> {
                        context.getString(RDb.string.blank_string_error)
                    }

                    is PlaylistAlreadyExistsException -> {
                        context.getString(RDb.string.playlist_already_exist, playlistTitle)
                    }

                    else -> null
                }

                if (message != null) {
                    showSnackBar(
                        scope = scope,
                        snackBarHostState = snackBarHostState,
                        message = message
                    )
                } else {
                    _logger.warning(e.message)
                    showErrorSnackBar(
                        scope = scope,
                        snackBarHostState = snackBarHostState,
                        action = {
                            addOnePlaylist(
                                scope = scope,
                                snackBarHostState = snackBarHostState,
                                playlistTitle = playlistTitle
                            )
                        }
                    )
                }
            }
        }
    }

    fun updatePlaylistTitle(
        scope: CoroutineScope,
        snackBarHostState: SnackbarHostState,
        playlist: Playlist
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val context: Context = MainActivity.instance.applicationContext
            val updatedTitle: String = playlist.title
            try {
                _db.updatePlaylist(playlist = playlist)
                showSnackBar(
                    scope = scope,
                    snackBarHostState = snackBarHostState,
                    message = context.getString(R.string.update_playlist_success, playlist)
                )
            } catch (e: Throwable) {
                val message: String? = when (e) {
                    is BlankStringException -> {
                        context.getString(RDb.string.blank_string_error)
                    }

                    is PlaylistAlreadyExistsException -> {
                        context.getString(RDb.string.playlist_already_exist, updatedTitle)
                    }

                    else -> null
                }

                if (message != null) {
                    showSnackBar(
                        scope = scope,
                        snackBarHostState = snackBarHostState,
                        message = message
                    )
                } else {
                    showErrorSnackBar(
                        scope = scope,
                        snackBarHostState = snackBarHostState,
                        action = {
                            updatePlaylistTitle(
                                scope = scope,
                                snackBarHostState = snackBarHostState,
                                playlist = playlist
                            )
                        }
                    )
                }
            }
        }
    }

    fun removePlaylist(
        scope: CoroutineScope,
        snackBarHostState: SnackbarHostState,
        playlist: Playlist
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                _db.removePlaylist(playlist = playlist)
                val context: Context = MainActivity.instance.applicationContext
                val message: String =
                    if (playlist.title == LIKES_PLAYLIST_TITLE) {
                        context.getString(RDb.string.likes_playlist_title)
                    } else {
                        playlist.title
                    } + ' ' + context.getString(R.string.remove_playlist_success)
                showSnackBar(
                    scope = scope,
                    snackBarHostState = snackBarHostState,
                    message = message,
                )
            } catch (e: Throwable) {
                _logger.warning(e.message)
                showErrorSnackBar(
                    scope = scope,
                    snackBarHostState = snackBarHostState,
                    action = {
                        removePlaylist(
                            scope = scope,
                            snackBarHostState = snackBarHostState,
                            playlist = playlist
                        )
                    }
                )
            }
        }
    }

    fun insertMusicToPlaylist(
        scope: CoroutineScope,
        snackBarHostState: SnackbarHostState,
        music: Music,
        playlist: Playlist
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                _db.insertMusicToPlaylist(music = music, playlist = playlist)
                val context: Context = MainActivity.instance.applicationContext
                showSnackBar(
                    scope = scope,
                    snackBarHostState = snackBarHostState,
                    message = context.getString(
                        R.string.add_music_to_playlist_success,
                        music.title,
                        if (playlist.title == LIKES_PLAYLIST_TITLE) {
                            context.getString(RDb.string.likes_playlist_title)
                        } else {
                            playlist.title
                        }
                    ),
                    actionLabel = context.getString(R.string.cancel),
                    action = {
                        removeMusicFromPlaylist(
                            scope = scope,
                            snackBarHostState = snackBarHostState,
                            music = music,
                            playlist = playlist
                        )
                    }
                )
            } catch (e: Throwable) {
                _logger.warning(e.message)
                showErrorSnackBar(scope = scope, snackBarHostState = snackBarHostState, action = {
                    insertMusicToPlaylist(
                        scope = scope,
                        snackBarHostState = snackBarHostState,
                        music = music,
                        playlist = playlist
                    )
                })
            }
        }
    }

    fun insertMusicsToPlaylist(
        scope: CoroutineScope,
        snackBarHostState: SnackbarHostState,
        musics: Collection<Music>,
        playlist: Playlist
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val context: Context = MainActivity.instance.applicationContext
            try {
                _db.insertMusicsToPlaylist(musics = musics, playlist = playlist)
                showSnackBar(
                    scope = scope,
                    snackBarHostState = snackBarHostState,
                    message = context.getString(R.string.insert_musics_to_playlist_success) + ' ' + if (playlist.title == LIKES_PLAYLIST_TITLE) context.getString(
                        RDb.string.likes_playlist_title
                    )
                    else playlist.title,
                    actionLabel = context.getString(R.string.cancel),
                    action = {
                        removeMusicsFromPlaylist(
                            scope = scope,
                            snackBarHostState = snackBarHostState,
                            musics = musics,
                            playlist = playlist
                        )
                    }
                )
            } catch (e: Throwable) {
                _logger.warning(e.message)
                showErrorSnackBar(scope = scope, snackBarHostState = snackBarHostState, action = {
                    insertMusicsToPlaylist(
                        scope = scope,
                        snackBarHostState = snackBarHostState,
                        musics = musics,
                        playlist = playlist
                    )
                })
            }
        }
    }

    fun insertMusicToPlaylists(
        scope: CoroutineScope,
        snackBarHostState: SnackbarHostState,
        music: Music,
        playlists: Collection<Playlist>
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val context: Context = MainActivity.instance.applicationContext

            @Suppress("NAME_SHADOWING")
            val playlists: List<Playlist> = playlists.toList()

            try {
                _db.insertMusicToPlaylists(music = music, playlists = playlists)
                showSnackBar(
                    scope = scope,
                    snackBarHostState = snackBarHostState,
                    message = music.title + ' ' + context.getString(R.string.insert_music_to_playlists_success),
                    actionLabel = context.getString(R.string.cancel),
                    action = {
                        removeMusicFromPlaylists(
                            scope = scope,
                            snackBarHostState = snackBarHostState,
                            music = music,
                            playlists = playlists
                        )
                    }
                )
            } catch (e: Throwable) {
                _logger.warning(e.message)
                showErrorSnackBar(scope = scope, snackBarHostState = snackBarHostState, action = {
                    insertMusicToPlaylists(
                        scope = scope,
                        snackBarHostState = snackBarHostState,
                        music = music,
                        playlists = playlists
                    )
                })
            }
        }
    }

    fun insertMusicsToPlaylists(
        scope: CoroutineScope,
        snackBarHostState: SnackbarHostState,
        musics: Collection<Music>,
        playlists: Collection<Playlist>
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                _db.insertMusicsToPlaylists(musics = musics, playlists = playlists)
                val context: Context = MainActivity.instance.applicationContext
                showSnackBar(
                    scope = scope,
                    snackBarHostState = snackBarHostState,
                    message = context.getString(R.string.add_musics_to_playlists_success),
                    actionLabel = context.getString(R.string.cancel),
                    action = {
                        removeMusicsFromPlaylists(
                            scope = scope,
                            snackBarHostState = snackBarHostState,
                            musics = musics,
                            playlists = playlists
                        )
                    }
                )
            } catch (e: Throwable) {
                _logger.warning(e.message)
                showErrorSnackBar(scope = scope, snackBarHostState = snackBarHostState, action = {
                    insertMusicsToPlaylists(
                        scope = scope,
                        snackBarHostState = snackBarHostState,
                        musics = musics,
                        playlists = playlists
                    )
                })
            }
        }
    }

    fun removeMusicFromPlaylist(
        scope: CoroutineScope,
        snackBarHostState: SnackbarHostState,
        music: Music,
        playlist: Playlist
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                _db.removeMusicFromPlaylist(music = music, playlist = playlist)
                val context: Context = MainActivity.instance.applicationContext
                showSnackBar(
                    scope = scope,
                    snackBarHostState = snackBarHostState,
                    message = music.title + ' ' + context.getString(R.string.remove_from_playlist_success) + ' ' + if (playlist.title == LIKES_PLAYLIST_TITLE) {
                        context.getString(RDb.string.likes_playlist_title)
                    } else {
                        playlist.title
                    },
                    actionLabel = context.getString(R.string.cancel),
                    action = {
                        insertMusicToPlaylist(
                            scope = scope,
                            snackBarHostState = snackBarHostState,
                            music = music,
                            playlist = playlist
                        )
                    }
                )
            } catch (e: Throwable) {
                _logger.warning(e.message)
                showErrorSnackBar(scope = scope, snackBarHostState = snackBarHostState, action = {
                    removeMusicFromPlaylist(
                        scope = scope,
                        snackBarHostState = snackBarHostState,
                        music = music,
                        playlist = playlist
                    )
                })
            }
        }
    }

    fun removeMusicsFromPlaylist(
        scope: CoroutineScope,
        snackBarHostState: SnackbarHostState,
        musics: Collection<Music>,
        playlist: Playlist
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val context: Context = MainActivity.instance.applicationContext
            try {
                _db.removeMusicsFromPlaylist(musics = musics, playlist = playlist)
                showSnackBar(
                    scope = scope,
                    snackBarHostState = snackBarHostState,
                    message = context.getString(
                        R.string.remove_musics_from_playlist_success,
                        if (playlist.title == LIKES_PLAYLIST_TITLE)
                            context.getString(RDb.string.likes_playlist_title)
                        else playlist.title
                    ),
                    actionLabel = context.getString(R.string.cancel),
                    action = {
                        insertMusicsToPlaylist(
                            scope = scope,
                            snackBarHostState = snackBarHostState,
                            musics = musics,
                            playlist = playlist
                        )
                    }
                )
            } catch (e: Throwable) {
                _logger.warning(e.message)
                showErrorSnackBar(scope = scope, snackBarHostState = snackBarHostState, action = {
                    removeMusicsFromPlaylist(
                        scope = scope,
                        snackBarHostState = snackBarHostState,
                        musics = musics,
                        playlist = playlist
                    )
                })
            }
        }
    }

    fun removeMusicFromPlaylists(
        scope: CoroutineScope,
        snackBarHostState: SnackbarHostState,
        music: Music,
        playlists: Collection<Playlist>
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                _db.removeMusicFromPlaylists(music = music, playlists = playlists)
                val context: Context = MainActivity.instance.applicationContext
                showSnackBar(
                    scope = scope,
                    snackBarHostState = snackBarHostState,
                    message = music.title + ' ' + context.getString(R.string.remove_from_playlist_success) + ' ' + context.getString(
                        RDb.string.playlists
                    ).lowercase(),
                    actionLabel = context.getString(R.string.cancel),
                    action = {
                        insertMusicToPlaylists(
                            scope = scope,
                            snackBarHostState = snackBarHostState,
                            music = music,
                            playlists = playlists
                        )
                    }
                )
            } catch (e: Throwable) {
                _logger.warning(e.message)
                showErrorSnackBar(scope = scope, snackBarHostState = snackBarHostState, action = {
                    removeMusicFromPlaylists(
                        scope = scope,
                        snackBarHostState = snackBarHostState,
                        music = music,
                        playlists = playlists
                    )
                })
            }
        }
    }

    fun removeMusicsFromPlaylists(
        scope: CoroutineScope,
        snackBarHostState: SnackbarHostState,
        musics: Collection<Music>,
        playlists: Collection<Playlist>
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                _db.removeMusicsFromPlaylists(musics = musics, playlists = playlists)
                val context: Context = MainActivity.instance.applicationContext
                showSnackBar(
                    scope = scope,
                    snackBarHostState = snackBarHostState,
                    message = context.getString(R.string.add_musics_to_playlists_success),
                    actionLabel = context.getString(R.string.cancel),
                    action = {
                        insertMusicsToPlaylists(
                            scope = scope,
                            snackBarHostState = snackBarHostState,
                            musics = musics,
                            playlists = playlists
                        )
                    }
                )
            } catch (e: Throwable) {
                _logger.warning(e.message)
                showErrorSnackBar(scope = scope, snackBarHostState = snackBarHostState, action = {
                    removeMusicsFromPlaylists(
                        scope = scope,
                        snackBarHostState = snackBarHostState,
                        musics = musics,
                        playlists = playlists
                    )
                })
            }
        }
    }

    fun switchLike(
        scope: CoroutineScope,
        snackBarHostState: SnackbarHostState,
        music: Music
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val context: Context = MainActivity.instance.applicationContext
            try {
                music.switchLike(context = context)
            } catch (e: Throwable) {
                if (e is LikesPlaylistCreationException) {
                    showSnackBar(
                        scope = scope,
                        snackBarHostState = snackBarHostState,
                        message = context.getString(
                            RDb.string.add_playlist_success,
                            context.getString(RDb.string.likes_playlist_title)
                        )
                    )
                } else {
                    showErrorSnackBar(
                        scope = scope,
                        snackBarHostState = snackBarHostState,
                        action = {
                            switchLike(
                                scope = scope,
                                snackBarHostState = snackBarHostState,
                                music = music
                            )
                        }
                    )
                }
            }
        }
    }

    fun importPlaylists() = MainActivity.instance.openFileToImportPlaylists()

    fun exportPlaylist(playlist: Playlist) {
        DatabaseManager.exportingPlaylist = true
        MainActivity.instance.createFileToExportPlaylist(
            defaultFileName = playlist.title,
            playlist = playlist
        )
    }

    fun exportPlaylists(
        scope: CoroutineScope,
        snackBarHostState: SnackbarHostState,
    ) {
        DatabaseManager.exportingPlaylist = true
        if (DataManager.getPlaylistSet().isEmpty()) {
            showSnackBar(
                scope = scope,
                snackBarHostState = snackBarHostState,
                message = MainActivity.instance.getString(RDb.string.no_playlist)
            )
            DatabaseManager.exportingPlaylist = false
            return
        }

        MainActivity.instance.createFileToExportPlaylists(defaultFileName = "Satunes")
    }

    fun resetAllData() {
        DataLoader.resetAllData()
    }

    fun share(music: Music) {
        val listener: MediaScannerConnection.OnScanCompletedListener =
            MediaScannerConnection.OnScanCompletedListener { _: String, uri: Uri ->
                val extension: String? = MimeTypeMap.getFileExtensionFromUrl(uri.path)
                var type: String? = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
                if (type.isNullOrBlank()) {
                    type = "audio/*"
                }
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_STREAM, uri)
                    setDataAndType(uri, type)
                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                }

                val shareIntent = Intent.createChooser(sendIntent, null)
                MainActivity.instance.startActivity(shareIntent)
            }
        MediaScannerConnection.scanFile(
            MainActivity.instance.applicationContext,
            arrayOf(music.absolutePath),
            arrayOf("audio/*"),
            listener
        )
    }
}