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

package io.github.antoinepirlot.satunes.ui.viewmodels

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import io.github.antoinepirlot.satunes.MainActivity
import io.github.antoinepirlot.satunes.database.models.NavBarSection
import io.github.antoinepirlot.satunes.database.services.data.DataLoader
import io.github.antoinepirlot.satunes.database.services.settings.SettingsManager
import io.github.antoinepirlot.satunes.internet.updates.APKDownloadStatus
import io.github.antoinepirlot.satunes.internet.updates.UpdateAvailableStatus
import io.github.antoinepirlot.satunes.internet.updates.UpdateCheckManager
import io.github.antoinepirlot.satunes.ui.states.SatunesUiState
import io.github.antoinepirlot.satunes.ui.viewmodels.utils.isAudioAllowed
import io.github.antoinepirlot.satunes.utils.logger.SatunesLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * @author Antoine Pirlot on 19/07/2024
 */
@SuppressLint("NewApi")
internal class SatunesViewModel : ViewModel() {
    private val _logger: SatunesLogger = SatunesLogger.getLogger()
    private val _uiState: MutableStateFlow<SatunesUiState> = MutableStateFlow(SatunesUiState())
    private val _isLoadingData: MutableState<Boolean> = DataLoader.isLoading
    private val _isDataLoaded: MutableState<Boolean> = DataLoader.isLoaded

    //Use this only for nav bar items as it won't refresh if uiState is updated, idk why.
    private val _foldersChecked: MutableState<Boolean> = SettingsManager.foldersChecked
    private val _artistsChecked: MutableState<Boolean> = SettingsManager.artistsChecked
    private val _albumsChecked: MutableState<Boolean> = SettingsManager.albumsChecked
    private val _genresChecked: MutableState<Boolean> = SettingsManager.genresChecked
    private val _playlistsChecked: MutableState<Boolean> = SettingsManager.playlistsChecked

    @RequiresApi(Build.VERSION_CODES.M)
    private val _updateAvailableStatus: MutableState<UpdateAvailableStatus> =
        UpdateCheckManager.updateAvailableStatus

    @RequiresApi(Build.VERSION_CODES.M)
    private val _isCheckingUpdate: MutableState<Boolean> = UpdateCheckManager.isCheckingUpdate

    @RequiresApi(Build.VERSION_CODES.M)
    private val _latestVersion: MutableState<String?> = UpdateCheckManager.latestVersion

    @RequiresApi(Build.VERSION_CODES.M)
    private val _downloadStatus: MutableState<APKDownloadStatus> = UpdateCheckManager.downloadStatus

    val uiState: StateFlow<SatunesUiState> = _uiState.asStateFlow()

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

    var updateAvailableStatus: UpdateAvailableStatus by _updateAvailableStatus
        private set
    var isCheckingUpdate: Boolean by _isCheckingUpdate
        private set
    var downloadStatus: APKDownloadStatus by _downloadStatus
        private set

    fun loadSettings() {
        try {
            runBlocking {
                SettingsManager.loadSettings(context = MainActivity.instance.applicationContext)
                _uiState.update { SatunesUiState() }
            }
        } catch (e: Throwable) {
            _logger.severe(e.message)
            throw e
        }
    }

    fun seeWhatsNew(permanently: Boolean = false) {
        CoroutineScope(Dispatchers.IO).launch {
            if (permanently) {
                SettingsManager.seeWhatsNew(context = MainActivity.instance.applicationContext)
            }
        }
        _uiState.update { currentState: SatunesUiState ->
            currentState.copy(whatsNewSeen = true)
        }
    }

    fun setCurrentDestination(destination: String) {
        _uiState.update { currentState: SatunesUiState ->
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
        DataLoader.loadAllData(context = MainActivity.instance.applicationContext)
    }

    internal fun updateIsAudioAllowed() {
        this.isAudioAllowed = isAudioAllowed()
        if (this.isAudioAllowed != this._uiState.value.isAudioAllowed) {
            this._uiState.update { currentState: SatunesUiState ->
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
            }
            _uiState.update { currentState: SatunesUiState ->
                currentState.copy(
                    foldersChecked = SettingsManager.foldersChecked.value,
                    artistsChecked = SettingsManager.artistsChecked.value,
                    albumsChecked = SettingsManager.albumsChecked.value,
                    genresChecked = SettingsManager.genresChecked.value,
                    playlistsChecked = SettingsManager.playlistsChecked.value,
                )
            }
        } catch (e: Throwable) {
            _logger.warning(e.message)
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
            _logger.warning(e.message)
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
            _logger.warning(e.message)
        }
    }

    fun switchIncludeRingtones() {
        try {
            runBlocking {
                SettingsManager.switchIncludeRingtones(context = MainActivity.instance.applicationContext)
                _uiState.update { currentState: SatunesUiState ->
                    currentState.copy(
                        shuffleMode = SettingsManager.shuffleMode
                    )
                }
            }
        } catch (e: Throwable) {
            _logger.warning(e.message)
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
            _logger.warning(e.message)
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
            _logger.warning(e.message)
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
            _logger.warning(e.message)
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
                    _logger.severe(e.message)
                    throw e
                }
            }
        } catch (e: Throwable) {
            _logger.warning(e.message)
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
            _logger.warning(e.message)
        }
    }

    fun updateBarSpeed(newValue: Float) {
        try {
            runBlocking {
                SettingsManager.updateBarSpeed(
                    context = MainActivity.instance.applicationContext,
                    newValue = newValue
                )
                _uiState.update { currentState: SatunesUiState ->
                    currentState.copy(
                        barSpeed = SettingsManager.barSpeed
                    )
                }
            }
        } catch (e: Throwable) {
            _logger.warning(e.message)
        }
    }

    fun resetUpdatesStatus() {
        if (updateAvailableStatus != UpdateAvailableStatus.AVAILABLE) {
            updateAvailableStatus = UpdateAvailableStatus.UNDEFINED
        }
    }

    fun checkUpdate() {
        try {
            UpdateCheckManager.checkUpdate(context = MainActivity.instance.applicationContext)
        } catch (e: Throwable) {
            _logger.warning(e.message)
        }
    }

    fun getCurrentVersion(): String {
        try {
            return UpdateCheckManager.getCurrentVersion(context = MainActivity.instance.applicationContext)
        } catch (e: Throwable) {
            _logger.severe(e.message)
            throw e
        }
    }
}