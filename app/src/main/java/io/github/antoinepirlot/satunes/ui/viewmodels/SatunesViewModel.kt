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

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import io.github.antoinepirlot.satunes.MainActivity
import io.github.antoinepirlot.satunes.database.models.MenuTitle
import io.github.antoinepirlot.satunes.database.services.data.DataLoader
import io.github.antoinepirlot.satunes.database.services.settings.SettingsManager
import io.github.antoinepirlot.satunes.ui.states.SatunesUiState
import io.github.antoinepirlot.satunes.ui.viewmodels.utils.isAudioAllowed
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
internal class SatunesViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<SatunesUiState> = MutableStateFlow(SatunesUiState())
    private val _isLoadingData: MutableState<Boolean> = DataLoader.isLoading
    private val _isDataLoaded: MutableState<Boolean> = DataLoader.isLoaded
    private var _selectedMenuTitle: MenuTitle = _uiState.value.selectedMenuTitle

    val uiState: StateFlow<SatunesUiState> = _uiState.asStateFlow()

    val isLoadingData: Boolean by _isLoadingData
    val isDataLoaded: Boolean by _isDataLoaded

    //Use this in UiSate and ViewModel as it is a particular value. It could change but most of the time it won't change
    var isAudioAllowed: Boolean by mutableStateOf(_uiState.value.isAudioAllowed)
        private set

    fun loadSettings() {
        runBlocking {
            SettingsManager.loadSettings(context = MainActivity.instance.applicationContext)
            _uiState.update { SatunesUiState() }
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

    fun selectMenuTitle(menuTitle: MenuTitle) {
        this._selectedMenuTitle = menuTitle
        this._uiState.update { currentState: SatunesUiState ->
            currentState.copy(selectedMenuTitle = menuTitle)
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

    fun switchMenuTitle(menuTitle: MenuTitle) {
        runBlocking {
            SettingsManager.switchMenuTitle(
                context = MainActivity.instance.applicationContext,
                menuTitle = menuTitle
            )
            _uiState.update { currentState: SatunesUiState ->
                currentState.copy(
                    foldersChecked = SettingsManager.foldersChecked,
                    artistsChecked = SettingsManager.artistsChecked,
                    albumsChecked = SettingsManager.albumsChecked,
                    genresChecked = SettingsManager.genresChecked,
                    playlistsChecked = SettingsManager.playlistsChecked,
                )
            }
        }
    }

    fun switchPlaybackWhenClosedChecked() {
        runBlocking {
            SettingsManager.switchPlaybackWhenClosedChecked(context = MainActivity.instance.applicationContext)
            _uiState.update { currentState: SatunesUiState ->
                currentState.copy(
                    playbackWhenClosedChecked = SettingsManager.playbackWhenClosedChecked
                )
            }
        }
    }

    fun switchPauseIfNoisy() {
        runBlocking {
            SettingsManager.switchPauseIfNoisy(context = MainActivity.instance.applicationContext)
            _uiState.update { currentState: SatunesUiState ->
                currentState.copy(
                    pauseIfNoisyChecked = SettingsManager.pauseIfNoisyChecked
                )
            }
        }
    }

    fun switchIncludeRingtones() {
        runBlocking {
            SettingsManager.switchIncludeRingtones(context = MainActivity.instance.applicationContext)
            _uiState.update { currentState: SatunesUiState ->
                currentState.copy(
                    includeRingtonesChecked = SettingsManager.includeRingtonesChecked
                )
            }
        }
    }

    fun switchShuffleMode() {
        runBlocking {
            SettingsManager.switchShuffleMode(context = MainActivity.instance.applicationContext)
            _uiState.update { currentState: SatunesUiState ->
                currentState.copy(
                    shuffleMode = SettingsManager.shuffleMode
                )
            }
        }
    }

    fun switchPauseIfAnotherPlayback() {
        runBlocking {
            SettingsManager.switchPauseIfAnotherPlayback(context = MainActivity.instance.applicationContext)
            _uiState.update { currentState: SatunesUiState ->
                currentState.copy(
                    pauseIfAnotherPlayback = SettingsManager.pauseIfAnotherPlayback
                )
            }
        }
    }

    fun switchFilter(filterSetting: MenuTitle) {
        runBlocking {
            SettingsManager.switchFilter(
                context = MainActivity.instance.applicationContext,
                filterSetting = filterSetting
            )
            _uiState.update { currentState: SatunesUiState ->
                currentState.copy(
                    foldersFilter = SettingsManager.foldersFilter,
                    artistsFilter = SettingsManager.artistsFilter,
                    albumsFilter = SettingsManager.albumsFilter,
                    genresFilter = SettingsManager.genresFilter,
                    playlistsFilter = SettingsManager.playlistsFilter,
                )
            }
        }
    }

    fun updateRepeatMode(newValue: Int) {
        runBlocking {
            SettingsManager.updateRepeatMode(
                context = MainActivity.instance.applicationContext,
                newValue = newValue
            )
            _uiState.update { currentState: SatunesUiState ->
                currentState.copy(
                    repeatMode = SettingsManager.repeatMode
                )
            }
        }
    }

    fun switchAudioOffload() {
        runBlocking {
            SettingsManager.switchAudioOffload(context = MainActivity.instance.applicationContext)
            _uiState.update { currentState: SatunesUiState ->
                currentState.copy(
                    audioOffloadChecked = SettingsManager.audioOffloadChecked
                )
            }
        }
    }

    fun updateBarSpeed(newValue: Float) {
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
    }
}