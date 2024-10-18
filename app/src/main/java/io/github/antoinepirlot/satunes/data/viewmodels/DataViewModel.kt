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
import androidx.compose.runtime.mutableStateOf
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
import io.github.antoinepirlot.satunes.database.models.MediaImpl
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

    var isSharingLoading: Boolean by mutableStateOf(false)
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
    fun getPlaylist(title: String): Playlist = DataManager.getPlaylist(title = title)!!

    fun addOnePlaylist(
        scope: CoroutineScope,
        snackBarHostState: SnackbarHostState,
        playlistTitle: String,
        onPlaylistAdded: ((playlist: Playlist) -> Unit)? = null,
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val context: Context = MainActivity.instance.applicationContext
            try {
                val playlist: Playlist = _db.addOnePlaylist(playlistTitle = playlistTitle)
                onPlaylistAdded?.invoke(playlist)
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
        playlist: Playlist,
        newTitle: String
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val context: Context = MainActivity.instance.applicationContext
            val oldTitle: String = playlist.title
            playlist.title = newTitle
            try {
                _db.updatePlaylist(playlist = playlist)
                showSnackBar(
                    scope = scope,
                    snackBarHostState = snackBarHostState,
                    message = context.getString(R.string.update_playlist_success, playlist)
                )
            } catch (e: Throwable) {
                playlist.title = oldTitle
                val message: String? = when (e) {
                    is BlankStringException -> {
                        context.getString(RDb.string.blank_string_error)
                    }

                    is PlaylistAlreadyExistsException -> {
                        context.getString(RDb.string.playlist_already_exist, newTitle)
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
                                newTitle = newTitle,
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

    fun share(
        scope: CoroutineScope,
        snackBarHostState: SnackbarHostState,
        media: MediaImpl
    ) {
        _logger.info("Sharing media type: ${media::class.java}")
        isSharingLoading = true
        try {
            var paths: Array<String> = arrayOf()

            when (media) {
                is Music -> paths += media.absolutePath
                is Folder -> {
                    media.getAllMusic().forEach { music: Music ->
                        paths += music.absolutePath
                    }
                }

                else -> {
                    media.getMusicSet().forEach { media: Music ->
                        paths += media.absolutePath
                    }
                }
            }

            if (paths.size > 850) {
                isSharingLoading = false
                scope.launch {
                    showSnackBar(
                        scope = scope,
                        snackBarHostState = snackBarHostState,
                        message = MainActivity.instance.getString(R.string.oversize_sharing_message)
                    )
                }
                return
            }

            if (paths.isEmpty()) {
                isSharingLoading = false
                scope.launch {
                    showSnackBar(
                        scope = scope,
                        snackBarHostState = snackBarHostState,
                        message = MainActivity.instance.getString(R.string.sharing_failed_no_data)
                    )
                }
                return
            }

            val uris: ArrayList<Uri> = arrayListOf()

            MediaScannerConnection.scanFile(
                MainActivity.instance.applicationContext,
                paths,
                arrayOf("audio/*")
            ) { _: String, uri: Uri ->
                val extension: String? = MimeTypeMap.getFileExtensionFromUrl(uri.path)
                var mimeType: String? =
                    MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
                if (mimeType.isNullOrBlank()) {
                    mimeType = "audio/*"
                }

                uris += uri

                if (uris.size == paths.size) {
                    // Loading is finished, now they can be exported
                    isSharingLoading = false
                    val sendIntent: Intent = Intent().apply {
                        if (uris.size == 1) {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_STREAM, uris[0])
                        } else if (uris.size > 1) {
                            action = Intent.ACTION_SEND_MULTIPLE
                            putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
                        } else {
                            throw IllegalStateException("Uri size is: 0")
                        }
                        type = mimeType
                        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    }

                    val shareIntent = Intent.createChooser(sendIntent, null)
                    MainActivity.instance.startActivity(shareIntent)
                }
            }
        } catch (e: NotImplementedError) {
            return
        } catch (e: Throwable) {
            _logger.severe(e.message)
        }
    }

    /**
     * Asks DatabaseManager to clean playlists of not loaded musics.
     *
     * @param scope the screen [CoroutineScope]
     * @param snackBarHostState a [SnackbarHostState]
     */
    fun cleanPlaylists(scope: CoroutineScope, snackBarHostState: SnackbarHostState) {
        val context: Context = MainActivity.instance.applicationContext
        showSnackBar(
            scope = scope,
            snackBarHostState = snackBarHostState,
            message = context.getString(R.string.cleaning_snack_message)
        )
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val dbManager: DatabaseManager = DatabaseManager.getInstance()
                dbManager.cleanPlaylists()
                showSnackBar(
                    scope = scope,
                    snackBarHostState = snackBarHostState,
                    message = context.getString(R.string.cleaned_snackbar_text)
                )
            } catch (e: Throwable) {
                showErrorSnackBar(
                    scope = scope,
                    snackBarHostState = snackBarHostState,
                    action = {
                        cleanPlaylists(
                            scope = scope,
                            snackBarHostState = snackBarHostState
                        )
                    }
                )
            }
        }
    }
}