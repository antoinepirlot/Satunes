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
 * You find this original project on Codeberg.
 *
 * My Codeberg link is: https://codeberg.org/antoinepirlot
 * This current project's link is: https://codeberg.org/antoinepirlot/Satunes
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import io.github.antoinepirlot.android.utils.logger.Logger
import io.github.antoinepirlot.satunes.MainActivity
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.data.defaultSortingOption
import io.github.antoinepirlot.satunes.data.states.DataUiState
import io.github.antoinepirlot.satunes.data.states.NavigationUiState
import io.github.antoinepirlot.satunes.database.daos.LIKES_PLAYLIST_TITLE
import io.github.antoinepirlot.satunes.database.data.DEFAULT_ROOT_FILE_PATH
import io.github.antoinepirlot.satunes.database.exceptions.BlankStringException
import io.github.antoinepirlot.satunes.database.exceptions.LikesPlaylistCreationException
import io.github.antoinepirlot.satunes.database.exceptions.PlaylistAlreadyExistsException
import io.github.antoinepirlot.satunes.database.models.FileExtensions
import io.github.antoinepirlot.satunes.database.models.comparators.MediaComparator
import io.github.antoinepirlot.satunes.database.models.media.Album
import io.github.antoinepirlot.satunes.database.models.media.Artist
import io.github.antoinepirlot.satunes.database.models.media.BackFolder
import io.github.antoinepirlot.satunes.database.models.media.Folder
import io.github.antoinepirlot.satunes.database.models.media.Genre
import io.github.antoinepirlot.satunes.database.models.media.Media
import io.github.antoinepirlot.satunes.database.models.media.MediaImpl
import io.github.antoinepirlot.satunes.database.models.media.Music
import io.github.antoinepirlot.satunes.database.models.media.Playlist
import io.github.antoinepirlot.satunes.database.models.media.RootFolder
import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicAlbum
import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicMedia
import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicMusic
import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicPlaylist
import io.github.antoinepirlot.satunes.database.services.data.DataManager
import io.github.antoinepirlot.satunes.database.services.data.LocalDataLoader
import io.github.antoinepirlot.satunes.database.services.database.DatabaseManager
import io.github.antoinepirlot.satunes.database.services.settings.SettingsManager
import io.github.antoinepirlot.satunes.internet.subsonic.SubsonicApiRequester
import io.github.antoinepirlot.satunes.models.radio_buttons.SortOptions
import io.github.antoinepirlot.satunes.ui.utils.showErrorSnackBar
import io.github.antoinepirlot.satunes.ui.utils.showSnackBar
import io.github.antoinepirlot.satunes.utils.getNow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import io.github.antoinepirlot.satunes.database.R as RDb

/**
 * @author Antoine Pirlot on 19/07/2024
 */
class DataViewModel : ViewModel() {
    companion object {
        private val _uiState: MutableStateFlow<DataUiState> = MutableStateFlow(DataUiState())
        private var _playlistToExport: Playlist? = null

        private val _mediaListOnScreen: List<Media> = mutableStateListOf()
    }

    private val _logger: Logger? = Logger.getLogger()
    private val _db: DatabaseManager =
        DatabaseManager.initInstance(context = MainActivity.instance.applicationContext)
    private val _isLoaded: MutableState<Boolean> = LocalDataLoader.isLoaded
    private var _updatePlaylistsJob: Job? = null

    private val _apiRequester: SubsonicApiRequester
        get() = SubsonicApiRequester()

    val uiState: StateFlow<DataUiState> = _uiState.asStateFlow()

    val mediaListOnScreen: List<Media> = _mediaListOnScreen

    var isSharingLoading: Boolean by mutableStateOf(false)
        private set

    var sortOption: SortOptions by mutableStateOf(defaultSortingOption)
        private set

    var reverseSortedOrder: Boolean by mutableStateOf(MediaComparator.DEFAULT_REVERSE_ORDER)
        private set

    var previousReverseOrder: Boolean by mutableStateOf(this.reverseSortedOrder)
        private set


    val isLoaded: Boolean by _isLoaded

    /**
     * File extension used to know which file to import/export
     */
    var fileExtension: FileExtensions by mutableStateOf(FileExtensions.JSON)
        private set

    var rootPlaylistsFilesPath: String by mutableStateOf(DEFAULT_ROOT_FILE_PATH)
        private set

    /**
     * Indicates if the user export only one playlist
     */
    val isExportSinglePlaylist: Boolean
        get() = _playlistToExport != null

    fun getRootFolder(): RootFolder = DataManager.getRootFolder()
    fun getBackFolder(): BackFolder = DataManager.getBackFolder()
    fun getFolderSet(): Set<Folder> = DataManager.getFolderSet()
    fun getArtistSet(): Set<Artist> = DataManager.getArtistSet()
    fun getAlbumSet(): Set<Album> = DataManager.getAlbumSet()
    fun getGenreSet(): Set<Genre> = DataManager.getGenreSet()
    fun getMusicSet(): Set<Music> = DataManager.getMusicSet()
    fun getSubsonicMusicsCollection(): Collection<SubsonicMusic> =
        DataManager.getSubsonicMusicsCollection()

    fun getSubsonicRandomMusicsCollection(): Collection<SubsonicMusic> =
        DataManager.getSubsonicRandomMusicsCollection()

    fun getPlaylistSet(): Set<Playlist> = DataManager.getPlaylistSet()
    fun getSubsonicPlaylists(): Collection<SubsonicPlaylist> = DataManager.getSubsonicPlaylists()

    fun getFolder(id: Long): Folder = DataManager.getFolder(id = id)!!
    fun getArtist(id: Long): Artist = DataManager.getArtist(id = id)!!

    fun getSubsonicArtist(id: String) = DataManager.getSubsonicArtist(id = id)!!

    fun getAlbum(id: Long): Album = DataManager.getAlbum(id = id)!!
    fun getSubsonicAlbum(id: String): SubsonicAlbum? = DataManager.getSubsonicAlbum(id = id)

    fun getGenre(id: Long): Genre = DataManager.getGenre(id = id)!!
    fun getPlaylist(id: Long): Playlist? = DataManager.getPlaylist(id = id)
    fun getPlaylist(title: String): Playlist? = DataManager.getPlaylist(title = title)
    fun getSubsonicPlaylist(id: String): SubsonicPlaylist? =
        DataManager.getSubsonicPlaylist(id = id)

    fun createPlaylist(
        scope: CoroutineScope,
        snackBarHostState: SnackbarHostState,
        playlistTitle: String,
        onPlaylistAdded: ((playlist: Playlist) -> Unit)? = null
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val context: Context = MainActivity.instance.applicationContext
            try {
                val playlist: Playlist = _db.addOnePlaylist(playlistTitle = playlistTitle)
                _mediaListOnScreen as MutableList<Media>
                (_mediaListOnScreen).add(element = playlist)
                _mediaListOnScreen.sort()

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
                    _logger?.warning(e.message)
                    showErrorSnackBar(
                        scope = scope,
                        snackBarHostState = snackBarHostState,
                        action = {
                            createPlaylist(
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

    fun addPlaylist(subsonicPlaylist: SubsonicPlaylist): SubsonicPlaylist =
        DataManager.addPlaylist(playlist = subsonicPlaylist)

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
                _db.updatePlaylistTitle(playlist = playlist)
                showSnackBar(
                    scope = scope,
                    snackBarHostState = snackBarHostState,
                    message = context.getString(R.string.update_playlist_success, playlist),
                    actionLabel = context.getString(R.string.cancel),
                    action = {
                        updatePlaylistTitle(
                            scope = scope,
                            snackBarHostState = snackBarHostState,
                            playlist = playlist,
                            newTitle = oldTitle
                        )
                    }
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

    fun updatePlaylistMusics(
        scope: CoroutineScope,
        snackBarHostState: SnackbarHostState,
        musics: Collection<Music>,
        playlist: Playlist,
    ) {
        this._updatePlaylistsJob?.cancel()
        val oldMusicsList: Collection<Music> = playlist.musicCollection.toList()
        this._updatePlaylistsJob = CoroutineScope(Dispatchers.IO).launch {
            val context: Context = MainActivity.instance.applicationContext
            try {
                _db.updatePlaylistMusics(playlist = playlist, newMusicCollection = musics)
                playlist.clearMusicList()
                playlist.addMusics(musics = musics)
                showSnackBar(
                    scope = scope,
                    snackBarHostState = snackBarHostState,
                    message = context.getString(R.string.update_playlist_success, playlist),
                    actionLabel = context.getString(R.string.cancel),
                    action = {
                        updatePlaylistMusics(
                            scope = scope,
                            snackBarHostState = snackBarHostState,
                            musics = oldMusicsList,
                            playlist = playlist
                        )
                    }
                )
            } catch (_: Throwable) {
                showErrorSnackBar(
                    scope = scope,
                    snackBarHostState = snackBarHostState,
                    action = {
                        updatePlaylistMusics(
                            scope = scope,
                            snackBarHostState = snackBarHostState,
                            musics = musics,
                            playlist = playlist
                        )
                    }
                )
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
                _logger?.warning(e.message)
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

    private fun insertMusicToPlaylist(
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
                _logger?.warning(e.message)
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

    fun updateMusicPlaylist(
        scope: CoroutineScope,
        snackBarHostState: SnackbarHostState,
        music: Music,
        playlists: Collection<Playlist>
    ) {
        this.updateMediaImplToPlaylists(
            scope = scope,
            snackBarHostState = snackBarHostState,
            mediaImpl = music,
            playlists = playlists
        )
    }

    fun updateMediaImplToPlaylists(
        scope: CoroutineScope,
        snackBarHostState: SnackbarHostState,
        mediaImpl: MediaImpl,
        playlists: Collection<Playlist>
    ) {
        this._updatePlaylistsJob?.cancel()
        this._updatePlaylistsJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                if (mediaImpl.isMusic())
                    _db.updateMusicToPlaylists(
                        music = mediaImpl as Music,
                        newPlaylistsCollection = playlists
                    )
                else
                    _db.updateMediaToPlaylists(mediaImpl = mediaImpl, playlists = playlists)
                val context: Context = MainActivity.instance.applicationContext
                showSnackBar(
                    scope = scope,
                    snackBarHostState = snackBarHostState,
                    message = context.getString(R.string.update_playlists_success)
                )
                //Can't cancel easily, to do it easily, add playlist list in each media impl
            } catch (e: Throwable) {
                _logger?.warning(e.message)
                showErrorSnackBar(scope = scope, snackBarHostState = snackBarHostState, action = {
                    updateMediaImplToPlaylists(
                        scope = scope,
                        snackBarHostState = snackBarHostState,
                        mediaImpl = mediaImpl,
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
        this._updatePlaylistsJob?.cancel()
        this._updatePlaylistsJob = CoroutineScope(Dispatchers.IO).launch {
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
                _logger?.warning(e.message)
                showErrorSnackBar(
                    scope = scope,
                    snackBarHostState = snackBarHostState,
                    action = {
                        removeMusicFromPlaylist(
                            scope = scope,
                            snackBarHostState = snackBarHostState,
                            music = music,
                            playlist = playlist
                        )
                    }
                )
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
                music.switchLike()
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

    fun openImportPlaylistDialog() {
        _uiState.update { currentState: DataUiState ->
            currentState.copy(showImportPlaylistDialog = true)
        }
    }

    fun closeImportPlaylistDialog() {
        _uiState.update { currentState: DataUiState ->
            currentState.copy(showImportPlaylistDialog = false)
        }
    }

    fun openExportPlaylistDialog(playlist: Playlist? = null) {
        _playlistToExport = playlist
        _uiState.update { currentState: DataUiState ->
            currentState.copy(showExportPlaylistDialog = true)
        }
    }

    fun closeExportPlaylistDialog() {
        _playlistToExport = null
        _uiState.update { currentState: DataUiState ->
            currentState.copy(showExportPlaylistDialog = false)
        }
    }

    fun updateFileExtension(fileExtension: FileExtensions) {
        this.fileExtension = fileExtension
    }

    fun importPlaylists() =
        MainActivity.instance.openFileToImportPlaylists(fileExtension = fileExtension)

    fun exportPlaylist(playlist: Playlist) {
        DatabaseManager.exportingPlaylist = true
        MainActivity.instance.createFileToExportPlaylist(
            defaultFileName = playlist.title,
            fileExtension = fileExtension,
            playlist = playlist,
            rootPlaylistsFilesPath = if (_uiState.value.changeFileRootPath) this.rootPlaylistsFilesPath else DEFAULT_ROOT_FILE_PATH,
            multipleFiles = _uiState.value.multipleFiles
        )
    }

    fun exportPlaylists(
        scope: CoroutineScope,
        snackBarHostState: SnackbarHostState,
    ) {
        if (this.isExportSinglePlaylist) {
            exportPlaylist(playlist = _playlistToExport!!)
            _playlistToExport = null
            return
        }

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

        val fileName = "Satunes_${getNow()}"
        MainActivity.instance.createFileToExportPlaylists(
            defaultFileName = fileName,
            fileExtension = fileExtension,
            rootPlaylistsFilesPath = if (_uiState.value.changeFileRootPath) this.rootPlaylistsFilesPath else DEFAULT_ROOT_FILE_PATH,
            multipleFiles = _uiState.value.multipleFiles
        )
    }

    fun share(
        scope: CoroutineScope,
        snackBarHostState: SnackbarHostState,
        media: MediaImpl
    ) {
        _logger?.info("Sharing media type: ${media::class.java}")
        isSharingLoading = true
        try {
            var paths: Array<String> = arrayOf()

            if (media.isMusic()) paths += (media as Music).absolutePath
            else if (media.isFolder()) {
                (media as Folder).getAllMusic().forEach { music: Music ->
                    paths += music.absolutePath
                }
            } else {
                @Suppress("NAME_SHADOWING")
                media.musicCollection.forEach { media: Music ->
                    paths += media.absolutePath
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
        } catch (_: NotImplementedError) {
            return
        } catch (e: Throwable) {
            _logger?.severe(e.message)
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
            } catch (_: Throwable) {
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

    fun resetFoldersSettings(scope: CoroutineScope, snackBarHostState: SnackbarHostState) {
        try {
            runBlocking {
                SettingsManager.resetFoldersSettings(context = MainActivity.instance.applicationContext)
                SatunesViewModel.reloadSettings()
            }
        } catch (_: Exception) {
            showErrorSnackBar(
                scope = scope,
                snackBarHostState = snackBarHostState,
                action = {
                    resetFoldersSettings(scope = scope, snackBarHostState = snackBarHostState)
                }
            )
        }
    }

    fun resetLoadingLogicSettings(scope: CoroutineScope, snackBarHostState: SnackbarHostState) {
        try {
            runBlocking {
                SettingsManager.resetLoadingLogicSettings(context = MainActivity.instance.applicationContext)
                SatunesViewModel.reloadSettings()
                updateShowFirstLetter()
            }
        } catch (_: Exception) {
            showErrorSnackBar(
                scope = scope,
                snackBarHostState = snackBarHostState,
                action = {
                    resetLoadingLogicSettings(scope = scope, snackBarHostState = snackBarHostState)
                }
            )
        }
    }

    fun resetBatterySettings(scope: CoroutineScope, snackBarHostState: SnackbarHostState) {
        try {
            runBlocking {
                SettingsManager.resetBatterySettings(context = MainActivity.instance.applicationContext)
                SatunesViewModel.reloadSettings()
            }
        } catch (_: Exception) {
            showErrorSnackBar(
                scope = scope,
                snackBarHostState = snackBarHostState,
                action = {
                    resetBatterySettings(scope = scope, snackBarHostState = snackBarHostState)
                }
            )
        }
    }

    fun resetPlaybackBehaviorSettings(scope: CoroutineScope, snackBarHostState: SnackbarHostState) {
        try {
            runBlocking {
                SettingsManager.resetPlaybackBehaviorSettings(context = MainActivity.instance.applicationContext)
                SatunesViewModel.reloadSettings()
            }
        } catch (_: Exception) {
            showErrorSnackBar(
                scope = scope,
                snackBarHostState = snackBarHostState,
                action = {
                    resetPlaybackBehaviorSettings(
                        scope = scope,
                        snackBarHostState = snackBarHostState
                    )
                }
            )
        }
    }

    fun resetPlaybackModesSettings(scope: CoroutineScope, snackBarHostState: SnackbarHostState) {
        try {
            runBlocking {
                SettingsManager.resetPlaybackModesSettings(context = MainActivity.instance.applicationContext)
                SatunesViewModel.reloadSettings()
            }
        } catch (_: Exception) {
            showErrorSnackBar(
                scope = scope,
                snackBarHostState = snackBarHostState,
                action = {
                    resetPlaybackModesSettings(
                        scope = scope,
                        snackBarHostState = snackBarHostState
                    )
                }
            )
        }
    }

    fun resetDefaultSearchFiltersSettings(
        scope: CoroutineScope,
        snackBarHostState: SnackbarHostState
    ) {
        try {
            runBlocking {
                SettingsManager.resetDefaultSearchFiltersSettings(context = MainActivity.instance.applicationContext)
                SatunesViewModel.reloadSettings()
            }
        } catch (_: Exception) {
            showErrorSnackBar(
                scope = scope,
                snackBarHostState = snackBarHostState,
                action = {
                    resetDefaultSearchFiltersSettings(
                        scope = scope,
                        snackBarHostState = snackBarHostState
                    )
                }
            )
        }
    }

    fun resetNavigationBarSettings(scope: CoroutineScope, snackBarHostState: SnackbarHostState) {
        try {
            runBlocking {
                SettingsManager.resetNavigationBarSettings(context = MainActivity.instance.applicationContext)
                SatunesViewModel.reloadSettings()
            }
        } catch (_: Exception) {
            showErrorSnackBar(
                scope = scope,
                snackBarHostState = snackBarHostState,
                action = {
                    resetNavigationBarSettings(
                        scope = scope,
                        snackBarHostState = snackBarHostState
                    )
                }
            )
        }
    }

    fun resetAllSettings(
        scope: CoroutineScope,
        snackBarHostState: SnackbarHostState
    ) {
        try {
            runBlocking {
                SettingsManager.resetAll(context = MainActivity.instance.applicationContext)
                SatunesViewModel.reloadSettings()
                updateShowFirstLetter()
            }
        } catch (_: Exception) {
            showErrorSnackBar(
                scope = scope,
                snackBarHostState = snackBarHostState,
                action = {
                    resetAllSettings(
                        scope = scope,
                        snackBarHostState = snackBarHostState
                    )
                }
            )
        }
    }

    fun setSorting(sortOption: SortOptions) {
        this.sortOption = sortOption
    }

    fun setReverseOrder(reverseOrder: Boolean) {
        this.reverseSortedOrder = reverseOrder
    }

    fun switchShowFirstLetter(
        scope: CoroutineScope,
        snackBarHostState: SnackbarHostState
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                SettingsManager.switchShowFirstLetter(context = MainActivity.instance.applicationContext)
                updateShowFirstLetter()
            } catch (_: Throwable) {
                showErrorSnackBar(
                    scope = scope,
                    snackBarHostState = snackBarHostState,
                    action = {
                        switchShowFirstLetter(
                            scope = scope,
                            snackBarHostState = snackBarHostState
                        )
                    }
                )
            }
        }
    }

    private fun updateShowFirstLetter() {
        _uiState.update { currentState: DataUiState ->
            currentState.copy(showFirstLetter = SettingsManager.showFirstLetter)
        }
    }

    fun sort(navigationUiState: NavigationUiState) {
        _uiState.update { currentState: DataUiState ->
            currentState.copy(appliedSortOption = this.sortOption)
        }
        _mediaListOnScreen as MutableList<Media>
        if (sortOption == SortOptions.PLAYLIST_ADDED_DATE) {
            val playlist: Playlist = navigationUiState.currentMediaImpl as Playlist
            _mediaListOnScreen.sortBy { mediaImpl: Media ->
                if (reverseSortedOrder) (mediaImpl as Music).getOrder(playlist = playlist)
                else -(mediaImpl as Music).getOrder(playlist = playlist)
            }
        } else if (this.sortOption.comparator != null) {
            sortOption.comparator!!.updateReverseOrder(reverseOrder = this.reverseSortedOrder)
            _mediaListOnScreen.sortWith(comparator = sortOption.comparator!!)
        }
    }

    fun orderChanged() {
        this.previousReverseOrder = this.reverseSortedOrder
    }

    fun resetListsSettings(scope: CoroutineScope, snackBarHostState: SnackbarHostState) {
        try {
            CoroutineScope(Dispatchers.IO).launch {
                SettingsManager.resetListsSettings(context = MainActivity.instance.applicationContext)
                SatunesViewModel.reloadSettings()
            }
        } catch (_: Exception) {
            showErrorSnackBar(
                scope = scope,
                snackBarHostState = snackBarHostState,
                action = {
                    resetListsSettings(scope = scope, snackBarHostState = snackBarHostState)
                }
            )
        }
    }

    fun resetArtworkSettings(scope: CoroutineScope, snackBarHostState: SnackbarHostState) {
        try {
            CoroutineScope(Dispatchers.IO).launch {
                SettingsManager.resetArtworkSettings(context = MainActivity.instance.applicationContext)
                SatunesViewModel.reloadSettings()
            }
        } catch (_: Exception) {
            showErrorSnackBar(
                scope = scope,
                snackBarHostState = snackBarHostState,
                action = {
                    resetArtworkSettings(scope = scope, snackBarHostState = snackBarHostState)
                }
            )
        }
    }

    fun switchChangeFileRootPath() {
        _uiState.update { currentState: DataUiState ->
            currentState.copy(changeFileRootPath = !currentState.changeFileRootPath)
        }
    }

    fun updateRootPlaylistsFilesPath(newValue: String) {
        this.rootPlaylistsFilesPath = newValue
    }

    fun switchMultipleFiles() {
        _uiState.update { currentState: DataUiState ->
            currentState.copy(multipleFiles = !currentState.multipleFiles)
        }
    }

    /**
     * Add [Collection] of [SubsonicMusic] to [DataManager.subsonicRandomMusics] from the getRandomMusic query
     */
    fun addRandomMusics(musics: Collection<SubsonicMusic>) {
        DataManager.addRandomMusic(musics = musics)
    }

    fun loadMediaImplList(collection: Collection<Media>) {
        _mediaListOnScreen as MutableList<Media>
        _mediaListOnScreen.clear()
        _mediaListOnScreen.addAll(elements = collection)
    }

    /**
     * Replace the old [Media] with the new [SubsonicMedia].
     */
    fun replace(old: Media, new: SubsonicMedia) {
        _mediaListOnScreen as MutableList<Media>
        for (i: Int in _mediaListOnScreen.indices)
            if (_mediaListOnScreen[i] === old) {
                _mediaListOnScreen[i] = new
                return
            }
    }
}