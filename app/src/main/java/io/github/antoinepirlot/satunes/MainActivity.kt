/*
 * This file is part of Satunes.
 * Satunes is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *  Satunes is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License along with Satunes.
 *  If not, see <https://www.gnu.org/licenses/>.
 *
 * **** INFORMATION ABOUT THE AUTHOR *****
 * The author of this file is Antoine Pirlot, the owner of this project.
 * You find this original project on Codeberg.
 *
 * My Codeberg link is: https://codeberg.org/antoinepirlot
 * This current project's link is: https://codeberg.org/antoinepirlot/Satunes
 */

package io.github.antoinepirlot.satunes

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import io.github.antoinepirlot.satunes.data.viewmodels.NavigationViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.utils.isAudioAllowed
import io.github.antoinepirlot.satunes.database.data.DEFAULT_ROOT_FILE_PATH
import io.github.antoinepirlot.satunes.database.models.FileExtensions
import io.github.antoinepirlot.satunes.database.models.FoldersSelection
import io.github.antoinepirlot.satunes.database.models.media.Music
import io.github.antoinepirlot.satunes.database.models.media.Playlist
import io.github.antoinepirlot.satunes.database.services.data.DataLoader
import io.github.antoinepirlot.satunes.database.services.data.DataManager
import io.github.antoinepirlot.satunes.database.services.database.DatabaseManager
import io.github.antoinepirlot.satunes.database.services.settings.SettingsManager
import io.github.antoinepirlot.satunes.playback.services.WidgetPlaybackManager
import io.github.antoinepirlot.satunes.utils.getNow
import io.github.antoinepirlot.satunes.utils.initSatunes
import io.github.antoinepirlot.satunes.utils.logger.SatunesLogger
import io.github.antoinepirlot.satunes.utils.utils.showToastOnUiThread
import io.github.antoinepirlot.satunes.widgets.PlaybackWidget.setRefreshWidget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 * @author Antoine Pirlot on 18/01/24
 */

internal class MainActivity : ComponentActivity() {

    companion object {
        internal lateinit var instance: MainActivity
        private const val IMPORT_PLAYLIST_CODE: Int = 1
        private const val EXPORT_ALL_PLAYLISTS_CODE: Int = 2
        private const val EXPORT_PLAYLIST_CODE: Int = 3
        private const val EXPORT_LOGS_CODE: Int = 4
        const val INCLUDE_FOLDER_TREE_CODE: Int = 5
        const val EXCLUDE_FOLDER_TREE_CODE: Int = 6
        var intentToHandle: Intent? = null

        val DEFAULT_URI: Uri =
            (DEFAULT_ROOT_FILE_PATH + '/' + Environment.DIRECTORY_DOCUMENTS).toUri()

        private val createFileIntent: Intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                putExtra(DocumentsContract.EXTRA_INITIAL_URI, DEFAULT_URI)
            }
        }

        fun getContext(): Context {
            if (!::instance.isInitialized)
                throw NullPointerException("No instances")
            return instance.applicationContext
        }
    }

    private var _fileExtension: FileExtensions? = null
    private var _rootPlaylistsFilesPath: String = DEFAULT_ROOT_FILE_PATH
    private var multipleFiles: Boolean = false
    private var _logger: SatunesLogger? = null
    private var _playlistToExport: Playlist? = null
    var handledMusic: Music? by mutableStateOf(null)
        private set
    private var _loadMusic: Boolean = true

    override fun onStart() {
        super.onStart()
        if (isAudioAllowed(context = applicationContext)) {
            initSatunes(context = applicationContext, satunesViewModel = null)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SatunesLogger.DOCUMENTS_PATH =
            applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)!!.path
        _logger = SatunesLogger.getLogger()
        _logger?.info("Satunes started on API: ${Build.VERSION.SDK_INT}")
        instance = this
        if (intentToHandle != intent) intentToHandle = intent
        else _loadMusic = false
        setRefreshWidget(context = baseContext)
        WidgetPlaybackManager.refreshWidgets()

        setContent {
            //App UI Entry Point
            Satunes()
        }
    }

    fun createFileToExportPlaylist(
        defaultFileName: String,
        fileExtension: FileExtensions,
        playlist: Playlist,
        rootPlaylistsFilesPath: String = DEFAULT_ROOT_FILE_PATH,
        multipleFiles: Boolean
    ) {
        this._playlistToExport = playlist
        this.createExportIntent(
            defaultFileName = defaultFileName,
            fileExtension = fileExtension,
            rootPlaylistsFilesPath = rootPlaylistsFilesPath,
            multipleFiles = multipleFiles,
            requestCode = EXPORT_PLAYLIST_CODE
        )
    }

    fun createFileToExportPlaylists(
        defaultFileName: String,
        fileExtension: FileExtensions,
        rootPlaylistsFilesPath: String = DEFAULT_ROOT_FILE_PATH,
        multipleFiles: Boolean
    ) {
        this.createExportIntent(
            defaultFileName = defaultFileName,
            fileExtension = fileExtension,
            rootPlaylistsFilesPath = rootPlaylistsFilesPath,
            multipleFiles = multipleFiles,
            requestCode = EXPORT_ALL_PLAYLISTS_CODE
        )
    }

    fun openFileToImportPlaylists(fileExtension: FileExtensions) {
        this._fileExtension = fileExtension
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = this@MainActivity._fileExtension!!.mimeType
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                putExtra(DocumentsContract.EXTRA_INITIAL_URI, DEFAULT_URI)
            }
        }
        startActivityForResult(intent, IMPORT_PLAYLIST_CODE)
    }

    fun exportLogs() {
        this.createExportIntent(
            defaultFileName = "Satunes_logs_${getNow()}.txt",
            fileExtension = FileExtensions.TEXT,
            requestCode = EXPORT_LOGS_CODE
        )
    }

    private fun createExportIntent(
        defaultFileName: String,
        fileExtension: FileExtensions,
        rootPlaylistsFilesPath: String = DEFAULT_ROOT_FILE_PATH,
        multipleFiles: Boolean = false,
        requestCode: Int
    ) {
        this.multipleFiles = multipleFiles
        this._rootPlaylistsFilesPath = rootPlaylistsFilesPath
        this._fileExtension = fileExtension
        createFileIntent.putExtra(Intent.EXTRA_TITLE, defaultFileName)
        createFileIntent.type = this._fileExtension!!.mimeType
        startActivityForResult(createFileIntent, requestCode)
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                EXPORT_PLAYLIST_CODE, EXPORT_ALL_PLAYLISTS_CODE, EXPORT_LOGS_CODE -> {
                    data?.data?.also { uri: Uri ->
                        if (uri.path == null) {
                            showToastOnUiThread(
                                context = this,
                                message = this.getString(R.string.no_file_created)
                            )
                        }

                        CoroutineScope(Dispatchers.IO).launch {
                            if (requestCode == EXPORT_LOGS_CODE) {
                                _logger?.exportLogs(context = this@MainActivity, uri = uri)
                            } else {
                                if (requestCode == EXPORT_ALL_PLAYLISTS_CODE) {
                                    DatabaseManager.getInstance().exportPlaylists(
                                        context = this@MainActivity.applicationContext,
                                        fileExtension = this@MainActivity._fileExtension!!,
                                        uri = uri,
                                        rootPlaylistsFilesPath = this@MainActivity._rootPlaylistsFilesPath,
                                        multipleFiles = this@MainActivity.multipleFiles
                                    )
                                } else {
                                    DatabaseManager.getInstance().exportPlaylist(
                                        context = applicationContext,
                                        uri = uri,
                                        playlist = _playlistToExport!!,
                                        fileExtension = this@MainActivity._fileExtension!!,
                                        rootPlaylistsFilesPath = this@MainActivity._rootPlaylistsFilesPath,
                                        multipleFiles = this@MainActivity.multipleFiles
                                    )
                                    _playlistToExport = null
                                }
                            }
                            this@MainActivity._fileExtension = null
                        }
                    }
                }

                IMPORT_PLAYLIST_CODE -> {
                    data?.data?.also {
                        DatabaseManager.getInstance().importPlaylists(
                            context = this,
                            uri = it,
                            fileExtension = this._fileExtension!!
                        )
                    }
                }

                INCLUDE_FOLDER_TREE_CODE, EXCLUDE_FOLDER_TREE_CODE -> {
                    data?.data?.also {
                        val folderSelection: FoldersSelection =
                            if (requestCode == INCLUDE_FOLDER_TREE_CODE) FoldersSelection.INCLUDE
                            else FoldersSelection.EXCLUDE
                        CoroutineScope(Dispatchers.IO).launch {
                            SettingsManager.addPath(
                                context = applicationContext,
                                uri = it,
                                folderSelection = folderSelection
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intentToHandle = intent
        _loadMusic = true
        handleMusic()
    }

    /**
     * Handle the music the user clicked in the files explorer app.
     */
    fun handleMusic() {
        if (!_loadMusic) return
        if (intentToHandle == null) return
        if (intentToHandle!!.action != Intent.ACTION_VIEW) return
        val uri = intentToHandle!!.data ?: return
        if (uri.path!!.endsWith(".m3u")) return
        try {
            val musicPath: String = DEFAULT_ROOT_FILE_PATH + '/' + uri.path!!.split(":").last()
            this.handledMusic = DataManager.getMusic(absolutePath = musicPath)
        } catch (_: NullPointerException) {
            //Music has not been loaded by Satunes
            this.handledMusic = DataLoader.load(context = getContext(), uri = uri)
            DataManager.remove(music = this.handledMusic!!)
            if (this.handledMusic!!.album.musicCount() == 1)
                DataManager.removeAlbum(album = this.handledMusic!!.album)
            if (this.handledMusic!!.genre.musicCount() == 1)
                DataManager.removeGenre(genre = this.handledMusic!!.genre)
            if (this.handledMusic!!.artist.musicCount() == 1)
                DataManager.removeArtist(artist = this.handledMusic!!.artist)
            //Here, the path starts with "document" it will never be in a folder of a loaded music.
            DataManager.removeFolder(folder = this.handledMusic!!.folder.getRoot())
        }
        _loadMusic = false
    }

    override fun onDestroy() {
        super.onDestroy()
        WidgetPlaybackManager.refreshWidgets()
    }

    fun musicHandled() {
        handledMusic = null
    }
}