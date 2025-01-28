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

package io.github.antoinepirlot.satunes.data.viewmodels

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.DocumentsContract
import androidx.annotation.RequiresApi
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import io.github.antoinepirlot.satunes.MainActivity
import io.github.antoinepirlot.satunes.MainActivity.Companion.DEFAULT_URI
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.data.availableSpeeds
import io.github.antoinepirlot.satunes.data.states.SatunesUiState
import io.github.antoinepirlot.satunes.data.viewmodels.utils.isAudioAllowed
import io.github.antoinepirlot.satunes.database.models.BarSpeed
import io.github.antoinepirlot.satunes.database.models.FoldersSelection
import io.github.antoinepirlot.satunes.database.models.MediaImpl
import io.github.antoinepirlot.satunes.database.models.NavBarSection
import io.github.antoinepirlot.satunes.database.services.data.DataLoader
import io.github.antoinepirlot.satunes.database.services.database.DatabaseManager
import io.github.antoinepirlot.satunes.database.services.settings.SettingsManager
import io.github.antoinepirlot.satunes.internet.updates.APKDownloadStatus
import io.github.antoinepirlot.satunes.internet.updates.UpdateAvailableStatus
import io.github.antoinepirlot.satunes.internet.updates.UpdateCheckManager
import io.github.antoinepirlot.satunes.internet.updates.UpdateDownloadManager
import io.github.antoinepirlot.satunes.models.Destination
import io.github.antoinepirlot.satunes.ui.utils.showErrorSnackBar
import io.github.antoinepirlot.satunes.ui.utils.showSnackBar
import io.github.antoinepirlot.satunes.utils.logger.SatunesLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.math.floor
import io.github.antoinepirlot.satunes.internet.R as RInternet


/**
 * @author Antoine Pirlot on 19/07/2024
 */
internal class SatunesViewModel : ViewModel() {
    companion object {
        private val _uiState: MutableStateFlow<SatunesUiState> = MutableStateFlow(SatunesUiState())
    }

    private val _logger: SatunesLogger? = SatunesLogger.getLogger()
    private val _isLoadingData: MutableState<Boolean> = DataLoader.isLoading
    private val _isDataLoaded: MutableState<Boolean> = DataLoader.isLoaded
    private val _defaultNavBarSection: MutableState<NavBarSection> =
        mutableStateOf(_uiState.value.defaultNavBarSection)

    //Use this only for nav bar items as it won't refresh if uiState is updated, idk why.
    private val _foldersChecked: MutableState<Boolean> = SettingsManager.foldersNavbar
    private val _artistsChecked: MutableState<Boolean> = SettingsManager.artistsNavbar
    private val _albumsChecked: MutableState<Boolean> = SettingsManager.albumsNavbar
    private val _genresChecked: MutableState<Boolean> = SettingsManager.genresNavbar
    private val _playlistsChecked: MutableState<Boolean> = SettingsManager.playlistsNavbar

    @RequiresApi(Build.VERSION_CODES.M)
    private val _updateAvailableStatus: MutableState<UpdateAvailableStatus> =
        UpdateCheckManager.updateAvailableStatus

    @RequiresApi(Build.VERSION_CODES.M)
    private val _downloadStatus: MutableState<APKDownloadStatus> = UpdateCheckManager.downloadStatus

    private val _foldersPathsSelectedSet: MutableState<Set<String>> =
        SettingsManager.foldersPathsSelectedSet

    val uiState: StateFlow<SatunesUiState> = _uiState.asStateFlow()
    var defaultNavBarSection: NavBarSection by _defaultNavBarSection
        private set

    val isLoadingData: Boolean by _isLoadingData
    val isDataLoaded: Boolean by _isDataLoaded
    val foldersChecked: Boolean by _foldersChecked
    val artistsChecked: Boolean by _artistsChecked
    val albumsChecked: Boolean by _albumsChecked
    val genresChecked: Boolean by _genresChecked
    val playlistsChecked: Boolean by _playlistsChecked

    //Use this in UiSate and ViewModel as it is a particular value. It could change but most of the time it won't change
    var isAudioAllowed: Boolean by mutableStateOf(_uiState.value.isAudioAllowed)
        private set

    @delegate:RequiresApi(Build.VERSION_CODES.M)
    var updateAvailableStatus: UpdateAvailableStatus by _updateAvailableStatus
        private set

    @delegate:RequiresApi(Build.VERSION_CODES.M)
    var isCheckingUpdate: Boolean by mutableStateOf(false)
        private set

    @delegate:RequiresApi(Build.VERSION_CODES.M)
    var downloadStatus: APKDownloadStatus by _downloadStatus
        private set

    val foldersPathsSelectedSet: Set<String> by _foldersPathsSelectedSet

    fun loadSettings() {
        try {
            runBlocking {
                SettingsManager.loadSettings(context = MainActivity.instance.applicationContext)
                _uiState.update { SatunesUiState() }
            }
        } catch (e: Throwable) {
            _logger?.severe(e.message)
            throw e
        }
    }

    fun seeWhatsNew(
        scope: CoroutineScope,
        snackBarHostState: SnackbarHostState,
        permanently: Boolean = false
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val context: Context = MainActivity.instance.applicationContext
            if (permanently) {
                SettingsManager.seeWhatsNew(context = context)
                showSnackBar(
                    scope = scope,
                    snackBarHostState = snackBarHostState,
                    message = context.getString(R.string.update_modal_permanently),
                    actionLabel = context.getString(R.string.cancel),
                    duration = SnackbarDuration.Long,
                    action = { seeWhatsNew(scope = scope, snackBarHostState = snackBarHostState) }
                )
            } else if (SettingsManager.whatsNewSeen) {
                SettingsManager.unSeeWhatsNew(context = context)
                showSnackBar(
                    scope = scope,
                    snackBarHostState = snackBarHostState,
                    message = context.getString(R.string.update_modal_not_permanently)
                )
            }
        }
        _uiState.update { currentState: SatunesUiState ->
            currentState.copy(whatsNewSeen = true)
        }
    }

    fun setCurrentDestination(destination: String) {
        _uiState.update { currentState: SatunesUiState ->
            @Suppress("NAME_SHADOWING")
            val destination: Destination = Destination.getDestination(destination = destination)
            _logger?.info("Going to destination: ${destination.name}")
            currentState.copy(currentDestination = destination)
        }
    }

    fun selectNavBarSection(navBarSection: NavBarSection) {
        _uiState.update { currentState: SatunesUiState ->
            currentState.copy(
                selectedNavBarSection = navBarSection
            )
        }
    }

    fun loadAllData() {
        CoroutineScope(Dispatchers.IO).launch {
            DataLoader.loadAllData(context = MainActivity.instance.applicationContext)
        }
    }

    fun reloadAllData(playbackViewModel: PlaybackViewModel) {
        CoroutineScope(Dispatchers.IO).launch {
            while (DatabaseManager.exportingPlaylist) {
                // Wait
            }
            runBlocking(Dispatchers.Main) {
                // run from MAIN thread as media controller seems to not be reachable from IO thread
                playbackViewModel.stop()
            }
            DataLoader.resetAllData()
            this@SatunesViewModel.loadAllData()
        }
    }

    internal fun updateIsAudioAllowed() {
        this.isAudioAllowed = isAudioAllowed(context = MainActivity.instance.applicationContext)
        if (this.isAudioAllowed != _uiState.value.isAudioAllowed) {
            _uiState.update { currentState: SatunesUiState ->
                currentState.copy(isAudioAllowed = this.isAudioAllowed)
            }
        }
    }

    fun switchNavBarSection(navBarSection: NavBarSection) {
        try {
            runBlocking {
                SettingsManager.switchNavBarSection(
                    context = MainActivity.instance.applicationContext,
                    navBarSection = navBarSection
                )
                if (
                    this@SatunesViewModel.defaultNavBarSection == navBarSection
                    && !navBarSection.isEnabled
                ) {
                    selectDefaultNavBarSection(navBarSection = NavBarSection.MUSICS)
                }
            }
            _uiState.update { currentState: SatunesUiState ->
                currentState.copy(
                    foldersNavbar = SettingsManager.foldersNavbar.value,
                    artistsNavbar = SettingsManager.artistsNavbar.value,
                    albumsNavbar = SettingsManager.albumsNavbar.value,
                    genresNavbar = SettingsManager.genresNavbar.value,
                    playlistsNavbar = SettingsManager.playlistsNavbar.value,
                )
            }
        } catch (e: Throwable) {
            _logger?.warning(e.message)
        }
    }

    fun switchPlaybackWhenClosedChecked() {
        try {
            runBlocking {
                SettingsManager.switchPlaybackWhenClosedChecked(context = MainActivity.instance.applicationContext)
                _uiState.update { currentState: SatunesUiState ->
                    currentState.copy(
                        playbackWhenClosedChecked = SettingsManager.playbackWhenClosedChecked
                    )
                }
            }
        } catch (e: Throwable) {
            _logger?.warning(e.message)
        }
    }

    fun switchPauseIfNoisy() {
        try {
            runBlocking {
                SettingsManager.switchPauseIfNoisy(context = MainActivity.instance.applicationContext)
                _uiState.update { currentState: SatunesUiState ->
                    currentState.copy(
                        pauseIfNoisyChecked = SettingsManager.pauseIfNoisyChecked
                    )
                }
            }
        } catch (e: Throwable) {
            _logger?.warning(e.message)
        }
    }

    fun setShuffleModeOn() {
        try {
            runBlocking {
                SettingsManager.setShuffleModeOn(context = MainActivity.instance.applicationContext)
                _uiState.update { currentState: SatunesUiState ->
                    currentState.copy(
                        shuffleMode = SettingsManager.shuffleMode
                    )
                }
            }
        } catch (e: Throwable) {
            _logger?.warning(e.message)
        }
    }

    fun setShuffleModeOff() {
        try {
            runBlocking {
                SettingsManager.setShuffleModeOff(context = MainActivity.instance.applicationContext)
                _uiState.update { currentState: SatunesUiState ->
                    currentState.copy(
                        shuffleMode = SettingsManager.shuffleMode
                    )
                }
            }
        } catch (e: Throwable) {
            _logger?.warning(e.message)
        }
    }

    fun switchPauseIfAnotherPlayback() {
        try {
            runBlocking {
                SettingsManager.switchPauseIfAnotherPlayback(context = MainActivity.instance.applicationContext)
                _uiState.update { currentState: SatunesUiState ->
                    currentState.copy(
                        pauseIfAnotherPlayback = SettingsManager.pauseIfAnotherPlayback
                    )
                }
            }
        } catch (e: Throwable) {
            _logger?.warning(e.message)
        }
    }

    fun updateRepeatMode(newValue: Int) {
        try {
            runBlocking {
                try {
                    SettingsManager.updateRepeatMode(
                        context = MainActivity.instance.applicationContext,
                        newValue = newValue
                    )
                    _uiState.update { currentState: SatunesUiState ->
                        currentState.copy(
                            repeatMode = SettingsManager.repeatMode
                        )
                    }
                } catch (e: Throwable) {
                    _logger?.severe(e.message)
                    throw e
                }
            }
        } catch (e: Throwable) {
            _logger?.warning(e.message)
        }
    }

    fun switchAudioOffload() {
        try {
            runBlocking {
                SettingsManager.switchAudioOffload(context = MainActivity.instance.applicationContext)
                _uiState.update { currentState: SatunesUiState ->
                    currentState.copy(
                        audioOffloadChecked = SettingsManager.audioOffloadChecked
                    )
                }
            }
        } catch (e: Throwable) {
            _logger?.warning(e.message)
        }
    }

    fun updateBarSpeed(
        scope: CoroutineScope,
        snackBarHostState: SnackbarHostState,
        newSpeedValue: Float
    ) {
        try {
            runBlocking {
                SettingsManager.updateBarSpeed(
                    context = MainActivity.instance.applicationContext,
                    newSpeedBar = getBarSpeed(speed = newSpeedValue)
                )
                _uiState.update { currentState: SatunesUiState ->
                    currentState.copy(
                        barSpeed = SettingsManager.barSpeed
                    )
                }
            }
        } catch (e: Throwable) {
            _logger?.warning(e.message)
            showErrorSnackBar(
                scope = scope,
                snackBarHostState = snackBarHostState,
                action = {
                    updateBarSpeed(
                        scope = scope,
                        snackBarHostState = snackBarHostState,
                        newSpeedValue = newSpeedValue
                    )
                }
            )
        }
    }

    fun getBarSpeed(speed: Float): BarSpeed {
        try {
            return availableSpeeds[floor(speed).toInt()]
        } catch (e: Throwable) {
            _logger?.severe(e.message)
            throw e
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun resetUpdatesStatus() {
        if (updateAvailableStatus != UpdateAvailableStatus.AVAILABLE) {
            updateAvailableStatus = UpdateAvailableStatus.UNDEFINED
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun checkUpdate(scope: CoroutineScope, snackBarHostState: SnackbarHostState) {
        isCheckingUpdate = true
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val context: Context = MainActivity.instance.applicationContext
                showSnackBar(
                    scope = scope,
                    snackBarHostState = snackBarHostState,
                    message = context.getString(RInternet.string.checking_update)
                )
                UpdateCheckManager.checkUpdate(context = MainActivity.instance.applicationContext)
                isCheckingUpdate = false
                when (updateAvailableStatus) {
                    UpdateAvailableStatus.UP_TO_DATE -> {
                        showSnackBar(
                            scope = scope,
                            snackBarHostState = snackBarHostState,
                            message = context.getString(RInternet.string.no_update)
                        )
                    }

                    UpdateAvailableStatus.AVAILABLE -> {
                        showSnackBar(
                            scope = scope,
                            snackBarHostState = snackBarHostState,
                            message = context.getString(RInternet.string.update_available)
                        )
                    }

                    UpdateAvailableStatus.CANNOT_CHECK -> {
                        showSnackBar(
                            scope = scope,
                            snackBarHostState = snackBarHostState,
                            message = context.getString(RInternet.string.cannot_check_update)
                        )
                    }

                    UpdateAvailableStatus.UNDEFINED -> { /* DO NOTHING */
                    }
                }
            } catch (e: Throwable) {
                _logger?.warning(e.message)
                showErrorSnackBar(
                    scope = scope,
                    snackBarHostState = snackBarHostState,
                    action = {
                        checkUpdate(
                            scope = scope,
                            snackBarHostState = snackBarHostState
                        )
                    }
                )
            } finally {
                isCheckingUpdate = false
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun getCurrentVersion(): String {
        try {
            return UpdateCheckManager.getCurrentVersion(context = MainActivity.instance.applicationContext)
        } catch (e: Throwable) {
            _logger?.severe(e.message)
            throw e
        }
    }

    fun mediaOptionsIsOpen() {
        _uiState.update { currentState: SatunesUiState ->
            currentState.copy(isMediaOptionsOpened = true)
        }
    }

    fun mediaOptionsIsClosed() {
        _uiState.update { currentState: SatunesUiState ->
            currentState.copy(isMediaOptionsOpened = false)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun downloadUpdateApk(
        scope: CoroutineScope,
        snackBarHostState: SnackbarHostState,
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val context: Context = MainActivity.instance.applicationContext
                showSnackBar(
                    scope = scope,
                    snackBarHostState = snackBarHostState,
                    message = context.getString(RInternet.string.downloading)
                )
                UpdateDownloadManager.downloadUpdateApk(context = context)
                if (downloadStatus == APKDownloadStatus.DOWNLOADED) {
                    showSnackBar(
                        scope = scope,
                        snackBarHostState = snackBarHostState,
                        message = context.getString(RInternet.string.downloaded)
                    )
                } else if (downloadStatus == APKDownloadStatus.NOT_FOUND) {
                    showSnackBar(
                        scope = scope,
                        snackBarHostState = snackBarHostState,
                        message = context.getString(RInternet.string.download_not_found)
                    )
                }
            } catch (e: Throwable) {
                _logger?.warning(e.message)
                showErrorSnackBar(
                    scope = scope,
                    snackBarHostState = snackBarHostState,
                    action = {
                        downloadUpdateApk(scope = scope, snackBarHostState = snackBarHostState)
                    }
                )
            }
        }
    }

    fun selectFoldersSelection(foldersSelection: FoldersSelection) {
        CoroutineScope(Dispatchers.IO).launch {
            SettingsManager.selectFoldersSelection(
                context = MainActivity.instance.applicationContext,
                foldersSelection = foldersSelection
            )
            _uiState.update { currentState: SatunesUiState ->
                currentState.copy(foldersSelectionSelected = foldersSelection)
            }
        }
    }

    fun addPath() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                putExtra(DocumentsContract.EXTRA_INITIAL_URI, DEFAULT_URI)
            }
        }
        MainActivity.instance.startActivityForResult(intent, MainActivity.SELECT_FOLDER_TREE_CODE)
    }

    fun removePath(scope: CoroutineScope, snackBarHostState: SnackbarHostState, path: String) {
        CoroutineScope(Dispatchers.IO).launch {
            SettingsManager.removePath(
                context = MainActivity.instance.applicationContext,
                path = path
            )
            showSnackBar(
                scope = scope,
                snackBarHostState = snackBarHostState,
                message = MainActivity.instance.applicationContext.getString(
                    R.string.path_removed,
                    path.removeSuffix("%")
                ),
                actionLabel = MainActivity.instance.applicationContext.getString(R.string.cancel),
                action = {
                    CoroutineScope(Dispatchers.IO).launch {
                        SettingsManager.addPath(
                            context = MainActivity.instance.applicationContext,
                            path = path
                        )
                    }
                }
            )
        }
    }

    fun selectDefaultNavBarSection(navBarSection: NavBarSection) {
        try {
            runBlocking {
                SettingsManager.selectDefaultNavBarSection(
                    context = MainActivity.instance.applicationContext,
                    navBarSection = navBarSection
                )
            }
            this.defaultNavBarSection = SettingsManager.defaultNavBarSection
        } catch (e: Throwable) {
            _logger?.severe("Error while selecting new default nav bar section: ${navBarSection.name}")
        }
    }

    fun switchCompilationMusic() {
        try {
            runBlocking {
                SettingsManager.switchCompilationMusic(context = MainActivity.instance.applicationContext)
                _uiState.update { currentState: SatunesUiState ->
                    currentState.copy(compilationMusic = SettingsManager.compilationMusic)
                }
            }
        } catch (e: Throwable) {
            _logger?.severe("Error while switching compilation music setting")
        }
    }

    fun switchArtistReplacement(
        scope: CoroutineScope,
        snackBarHostState: SnackbarHostState
    ) {
        try {
            runBlocking {
                SettingsManager.switchArtistReplacement(context = MainActivity.instance.applicationContext)
                _uiState.update { currentState: SatunesUiState ->
                    currentState.copy(artistReplacement = SettingsManager.artistReplacement)
                }
            }
        } catch (e: Throwable) {
            showErrorSnackBar(
                scope = scope,
                snackBarHostState = snackBarHostState,
                action = {
                    this.switchArtistReplacement(
                        scope = scope,
                        snackBarHostState = snackBarHostState
                    )
                }
            )
        }
    }

    fun replaceExtraButtons(extraButtons: @Composable () -> Unit) {
        _uiState.update { currentState: SatunesUiState ->
            currentState.copy(extraButtons = extraButtons)
        }
    }

    fun clearExtraButtons() {
        _uiState.update { currentState: SatunesUiState ->
            currentState.copy(extraButtons = null)
        }
    }

    fun showSortDialog() {
        _uiState.update { currentState: SatunesUiState ->
            currentState.copy(showSortDialog = true)
        }
    }

    fun hideSortDialog() {
        _uiState.update { currentState: SatunesUiState ->
            currentState.copy(showSortDialog = false)
        }
    }

    fun setCurrentMediaImpl(mediaImpl: MediaImpl) {
        _uiState.update { currentState: SatunesUiState ->
            currentState.copy(currentMediaImpl = mediaImpl)
        }
    }

    fun clearCurrentMediaImpl() {
        _uiState.update { currentState: SatunesUiState ->
            currentState.copy(currentMediaImpl = null)
        }
    }
}