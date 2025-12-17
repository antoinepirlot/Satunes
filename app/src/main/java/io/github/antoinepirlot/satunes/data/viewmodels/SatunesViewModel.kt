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
import android.os.Build
import android.provider.DocumentsContract
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableLongState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import io.github.antoinepirlot.android.utils.logger.Logger
import io.github.antoinepirlot.satunes.MainActivity
import io.github.antoinepirlot.satunes.MainActivity.Companion.DEFAULT_URI
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.data.availableSpeeds
import io.github.antoinepirlot.satunes.data.states.SatunesUiState
import io.github.antoinepirlot.satunes.data.viewmodels.utils.isAudioAllowed
import io.github.antoinepirlot.satunes.database.models.BarSpeed
import io.github.antoinepirlot.satunes.database.models.FoldersSelection
import io.github.antoinepirlot.satunes.database.models.NavBarSection
import io.github.antoinepirlot.satunes.database.models.UpdateChannel
import io.github.antoinepirlot.satunes.database.models.media.Media
import io.github.antoinepirlot.satunes.database.models.media.Playlist
import io.github.antoinepirlot.satunes.database.services.data.LocalDataLoader
import io.github.antoinepirlot.satunes.database.services.database.DatabaseManager
import io.github.antoinepirlot.satunes.database.services.settings.SettingsManager
import io.github.antoinepirlot.satunes.internet.updates.APKDownloadStatus
import io.github.antoinepirlot.satunes.internet.updates.UpdateAvailableStatus
import io.github.antoinepirlot.satunes.internet.updates.UpdateCheckManager
import io.github.antoinepirlot.satunes.internet.updates.UpdateDownloadManager
import io.github.antoinepirlot.satunes.models.SatunesModes
import io.github.antoinepirlot.satunes.ui.utils.showErrorSnackBar
import io.github.antoinepirlot.satunes.ui.utils.showSnackBar
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
class SatunesViewModel : ViewModel() {
    companion object {
        private val _uiState: MutableStateFlow<SatunesUiState> = MutableStateFlow(SatunesUiState())

        fun reloadSettings() {
            _uiState.update { currentState: SatunesUiState ->
                currentState.copy(
                    playbackWhenClosedChecked = SettingsManager.playbackWhenClosedChecked,
                    pauseIfNoisyChecked = SettingsManager.pauseIfNoisyChecked,
                    pauseIfAnotherPlayback = SettingsManager.pauseIfAnotherPlayback,
                    shuffleMode = SettingsManager.shuffleMode,
                    repeatMode = SettingsManager.repeatMode,
                    audioOffloadChecked = SettingsManager.audioOffloadChecked,
                    barSpeed = SettingsManager.barSpeed,
                    compilationMusic = SettingsManager.compilationMusic,
                    artistReplacement = SettingsManager.artistReplacement,
                )
            }
        }
    }

    private val _logger: Logger? = Logger.getLogger()
    private val _isLoadingData: MutableState<Boolean> = LocalDataLoader.isLoading
    private val _isDataLoaded: MutableState<Boolean> = LocalDataLoader.isLoaded
    private val _defaultNavBarSection: MutableState<NavBarSection> =
        SettingsManager.defaultNavBarSection
    private val _defaultPlaylistId: MutableLongState = SettingsManager.defaultPlaylistId

    private val _updateAvailableStatus: MutableState<UpdateAvailableStatus> =
        UpdateCheckManager.updateAvailableStatus

    private val _downloadStatus: MutableState<APKDownloadStatus> = UpdateCheckManager.downloadStatus
    private val _artworkAnimation: MutableState<Boolean> = SettingsManager.artworkAnimation
    private val _artworkCircleShape: MutableState<Boolean> = SettingsManager.artworkCircleShape
    private val _logsActivation: MutableState<Boolean> = SettingsManager.logsActivation

    private val _updateChannel: MutableState<UpdateChannel> = SettingsManager.updateChannel

    val uiState: StateFlow<SatunesUiState> = _uiState.asStateFlow()

    val isLoadingData: Boolean by _isLoadingData
    val isDataLoaded: Boolean by _isDataLoaded

    var updateAvailableStatus: UpdateAvailableStatus by _updateAvailableStatus
        private set

    var isCheckingUpdate: Boolean by mutableStateOf(false)
        private set

    var downloadStatus: APKDownloadStatus by _downloadStatus
        private set

    val defaultNavBarSection: NavBarSection by this._defaultNavBarSection
    val defaultPlaylistId: Long by this._defaultPlaylistId

    val artworkAnimation: Boolean by this._artworkAnimation
    val artworkCircleShape: Boolean by this._artworkCircleShape
    val logsActivation: Boolean by this._logsActivation

    val updateChannel: UpdateChannel by this._updateChannel

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

    /**
     * Mark notification as read and show snack bar with a message depending of the action taken by the user.
     *
     * @param scope
     * @param snackbarHostState
     * @param permanentAction the [Unit] action to run when the action is permanent (won't be shown again)
     * @param nonPermanentAction a [Unit] action to run when the action is not permanent (will be shown next time)
     * @param permanently a [Boolean] indicating if the action is permanent or not.
     */
    private fun seeNotification(
        scope: CoroutineScope,
        snackbarHostState: SnackbarHostState,
        permanentAction: suspend () -> Unit,
        nonPermanentAction: suspend () -> Unit,
        permanently: Boolean = false
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val context: Context = MainActivity.instance.applicationContext
            if (permanently) {
                permanentAction()
                showSnackBar(
                    scope = scope,
                    snackBarHostState = snackbarHostState,
                    message = context.getString(R.string.update_modal_permanently),
                    actionLabel = context.getString(R.string.cancel),
                    duration = SnackbarDuration.Long,
                    action = {
                        seeNotification(
                            scope = scope,
                            snackbarHostState = snackbarHostState,
                            permanently = false,
                            permanentAction = permanentAction,
                            nonPermanentAction = nonPermanentAction
                        )
                    }
                )
            } else {
                nonPermanentAction()
                showSnackBar(
                    scope = scope,
                    snackBarHostState = snackbarHostState,
                    message = context.getString(R.string.update_modal_not_permanently)
                )
            }
        }
        _uiState.update { currentState: SatunesUiState ->
            currentState.copy(whatsNewSeen = true)
        }
    }

    fun seeWhatsNew(
        scope: CoroutineScope,
        snackBarHostState: SnackbarHostState,
        permanently: Boolean = false
    ) {
        this.seeNotification(
            scope = scope,
            snackbarHostState = snackBarHostState,
            permanently = permanently,
            permanentAction = { SettingsManager.seeWhatsNew(context = MainActivity.instance.applicationContext) },
            nonPermanentAction = { SettingsManager.unSeeWhatsNew(context = MainActivity.instance.applicationContext) }
        )
    }

    fun loadAllData() {
        CoroutineScope(Dispatchers.IO).launch {
            LocalDataLoader.loadAllData(context = MainActivity.instance.applicationContext)
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
            LocalDataLoader.resetAllData()
            this@SatunesViewModel.loadAllData()
        }
    }

    internal fun updateIsAudioAllowed() {
        val isAudioAllowed: Boolean =
            isAudioAllowed(context = MainActivity.instance.applicationContext)
        if (isAudioAllowed != _uiState.value.isAudioAllowed) {
            _uiState.update { currentState: SatunesUiState ->
                currentState.copy(isAudioAllowed = isAudioAllowed)
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
                    defaultNavBarSection == navBarSection
                    && !navBarSection.isEnabled.value
                ) {
                    selectDefaultNavBarSection(navBarSection = NavBarSection.MUSICS)
                }
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

    /**
     * Reset update status.
     * @param force will make it always work if it's true. Otherwise it will be reset only if the status is not Available.
     */
    fun resetUpdatesStatus(force: Boolean = false) {
        if (force || updateAvailableStatus != UpdateAvailableStatus.AVAILABLE) {
            updateAvailableStatus = UpdateAvailableStatus.UNDEFINED
        }
    }

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

    fun getCurrentVersion(): String {
        try {
            return UpdateCheckManager.getCurrentVersion(context = MainActivity.instance.applicationContext)
        } catch (e: Throwable) {
            _logger?.severe(e.message)
            throw e
        }
    }

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

    fun addPath(folderSelection: FoldersSelection) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                putExtra(DocumentsContract.EXTRA_INITIAL_URI, DEFAULT_URI)
            }
        }
        MainActivity.instance.startActivityForResult(
            intent,
            if (folderSelection == FoldersSelection.INCLUDE) MainActivity.INCLUDE_FOLDER_TREE_CODE else MainActivity.EXCLUDE_FOLDER_TREE_CODE
        )
    }

    fun removePath(
        scope: CoroutineScope,
        snackBarHostState: SnackbarHostState,
        path: String,
        folderSelection: FoldersSelection
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            SettingsManager.removePath(
                context = MainActivity.instance.applicationContext,
                path = path,
                folderSelection = folderSelection
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
                            path = path,
                            folderSelection = folderSelection
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
        } catch (_: Throwable) {
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
        } catch (_: Throwable) {
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
        } catch (_: Throwable) {
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

    fun switchIsMusicTitleDisplayName(
        scope: CoroutineScope,
        snackBarHostState: SnackbarHostState
    ) {
        try {
            runBlocking {
                SettingsManager.switchIsMusicTitleDisplayName(context = MainActivity.instance.applicationContext)
                _uiState.update { currentState: SatunesUiState ->
                    currentState.copy(isMusicTitleDisplayName = SettingsManager.isMusicTitleDisplayName)
                }
            }
        } catch (_: Throwable) {
            showErrorSnackBar(
                scope = scope,
                snackBarHostState = snackBarHostState,
                action = {
                    this.switchIsMusicTitleDisplayName(
                        scope = scope,
                        snackBarHostState = snackBarHostState
                    )
                }
            )
        }
    }

    fun switchArtworkAnimation() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                SettingsManager.switchArtworkAnimation(context = MainActivity.instance.applicationContext)
            } catch (e: Throwable) {
                _logger?.warning(e.message)
            }
        }
    }

    fun switchArtworkCircleShape() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                SettingsManager.switchArtworkCircleShape(context = MainActivity.instance.applicationContext)
            } catch (e: Throwable) {
                _logger?.warning(e.message)
            }
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

    fun showMediaSelectionDialog() {
        _uiState.update { currentState: SatunesUiState ->
            currentState.copy(showMediaSelectionDialog = true)
        }
    }

    fun hideMediaSelectionDialog() {
        _uiState.update { currentState: SatunesUiState ->
            currentState.copy(showMediaSelectionDialog = false)
        }
    }

    fun selectDefaultPlaylist(playlist: Playlist?) {
        try {
            runBlocking {
                SettingsManager.selectDefaultPlaylist(
                    context = MainActivity.instance.applicationContext,
                    playlist = playlist
                )
            }
        } catch (e: Throwable) {
            _logger?.severe("Error while selecting new default playlist")
            throw e
        }
    }

    fun resetCustomActions(scope: CoroutineScope, snackBarHostState: SnackbarHostState) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                SettingsManager.resetCustomActions(context = MainActivity.instance.applicationContext)
            } catch (_: Throwable) {
                showErrorSnackBar(
                    scope = scope,
                    snackBarHostState = snackBarHostState,
                    action = {
                        resetCustomActions(
                            scope = scope,
                            snackBarHostState = snackBarHostState
                        )
                    }
                )
            }
        }
    }

    fun switchLogsActivation(scope: CoroutineScope, snackBarHostState: SnackbarHostState) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                SettingsManager.switchLogsActivation(context = MainActivity.instance.applicationContext)
            } catch (_: Throwable) {
                showErrorSnackBar(
                    scope = scope,
                    snackBarHostState = snackBarHostState,
                    action = {
                        switchLogsActivation(
                            scope = scope,
                            snackBarHostState = snackBarHostState
                        )
                    }
                )
            }
        }
    }

    fun seeIncludeExcludeInfo(
        scope: CoroutineScope,
        snackbarHostState: SnackbarHostState,
        permanently: Boolean = false
    ) {
        this.seeNotification(
            scope = scope,
            snackbarHostState = snackbarHostState,
            permanently = permanently,
            permanentAction = {
                SettingsManager.seeIncludeExcludeInfo(context = MainActivity.instance.applicationContext)
                _uiState.update { currentState: SatunesUiState ->
                    currentState.copy(includeExcludeSeen = true)
                }
            },
            nonPermanentAction = {
                SettingsManager.unSeeIncludeExcludeInfo(context = MainActivity.instance.applicationContext)
                _uiState.update { currentState: SatunesUiState ->
                    currentState.copy(includeExcludeSeen = false)
                }
            }
        )

    }

    fun selectUpdateChannel(channel: UpdateChannel) {
        try {
            CoroutineScope(Dispatchers.IO).launch {
                if (updateChannel != channel) resetUpdatesStatus(force = true)
                SettingsManager.selectUpdateChannel(
                    context = MainActivity.instance.applicationContext,
                    channel = channel
                )
            }
        } catch (_: Throwable) {
            _logger?.severe("Error while selecting update channel '${channel.name}'")
        }
    }

    /**
     * Change the cloud mode.
     * It pings the server if is turning on
     */
    fun switchCloudMode(
        scope: CoroutineScope,
        snackbarHostState: SnackbarHostState,
        subsonicViewModel: SubsonicViewModel
    ) {
        if (_uiState.value.mode == SatunesModes.ONLINE)
            turnOffCloud()
        else
            subsonicViewModel.connect(
                scope = scope,
                snackbarHostState = snackbarHostState,
                onFinished = { isConnected: Boolean ->
                    if (isConnected)
                        turnOnCloud()
                }
            )
    }

    fun turnOffCloud() {
        if (_uiState.value.mode == SatunesModes.ONLINE) {
            _uiState.update { currentState: SatunesUiState ->
                currentState.copy(mode = SatunesModes.OFFLINE)
            }
        }
    }

    fun turnOnCloud() {
        if (_uiState.value.mode == SatunesModes.OFFLINE) {
            _uiState.update { currentState: SatunesUiState ->
                currentState.copy(mode = SatunesModes.ONLINE)
            }
// TODO        subsonicViewModel.removeOnlineMusic()
        }
    }
}