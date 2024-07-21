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
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import io.github.antoinepirlot.satunes.MainActivity
import io.github.antoinepirlot.satunes.database.models.NavBarSection
import io.github.antoinepirlot.satunes.database.services.data.DataLoader
import io.github.antoinepirlot.satunes.database.services.settings.SettingsManager
import io.github.antoinepirlot.satunes.internet.updates.APKDownloadStatus
import io.github.antoinepirlot.satunes.internet.updates.UpdateAvailableStatus
import io.github.antoinepirlot.satunes.internet.updates.UpdateCheckManager
import io.github.antoinepirlot.satunes.models.Destination
import io.github.antoinepirlot.satunes.models.SearchChips
import io.github.antoinepirlot.satunes.models.settingsDestinations
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
@SuppressLint("NewApi")
internal class SatunesViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<SatunesUiState> = MutableStateFlow(SatunesUiState())
    private val _isLoadingData: MutableState<Boolean> = DataLoader.isLoading
    private val _isDataLoaded: MutableState<Boolean> = DataLoader.isLoaded

    //Use this only for nav bar items as it won't refresh if uiState is updated, idk why.
    private val _foldersChecked: MutableState<Boolean> = SettingsManager.foldersChecked
    private val _artistsChecked: MutableState<Boolean> = SettingsManager.artistsChecked
    private val _albumsChecked: MutableState<Boolean> = SettingsManager.albumsChecked
    private val _genresChecked: MutableState<Boolean> = SettingsManager.genresChecked
    private val _playlistsChecked: MutableState<Boolean> = SettingsManager.playlistsChecked

    private val _filtersList: MutableMap<SearchChips, Boolean> = mutableMapOf(
        Pair(SearchChips.MUSICS, SettingsManager.musicsFilter),
        Pair(SearchChips.ALBUMS, SettingsManager.albumsFilter),
        Pair(SearchChips.ARTISTS, SettingsManager.artistsFilter),
        Pair(SearchChips.GENRES, SettingsManager.genresFilter),
        Pair(SearchChips.FOLDERS, SettingsManager.foldersFilter),
        Pair(SearchChips.PLAYLISTS, SettingsManager.playlistsFilter),
    )

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

    val selectedSearchChips: MutableList<SearchChips> = SnapshotStateList()

    var updateAvailableStatus: UpdateAvailableStatus by _updateAvailableStatus
        private set
    var isCheckingUpdate: Boolean by _isCheckingUpdate
        private set
    var latestVersion: String? by _latestVersion
        private set
    var downloadStatus: APKDownloadStatus by _downloadStatus
        private set

    init {
        selectedSearchChips.addAll(_filtersList.filter { it.value }.keys)
    }

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

    fun switchFilter(filterSetting: NavBarSection) {
        runBlocking {
            SettingsManager.switchFilter(
                context = MainActivity.instance.applicationContext,
                filterSetting = filterSetting
            )
            _uiState.update { currentState: SatunesUiState ->
                currentState.copy(
                    musicsFilter = SettingsManager.musicsFilter,
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

    fun resetSelectedChips(context: Context) {
        runBlocking {
            selectedSearchChips.clear()
            SettingsManager.loadFilters(context = context)
            _filtersList[SearchChips.MUSICS] = SettingsManager.musicsFilter
            _filtersList[SearchChips.ALBUMS] = SettingsManager.albumsFilter
            _filtersList[SearchChips.ARTISTS] = SettingsManager.artistsFilter
            _filtersList[SearchChips.GENRES] = SettingsManager.genresFilter
            _filtersList[SearchChips.FOLDERS] = SettingsManager.foldersFilter
            _filtersList[SearchChips.PLAYLISTS] = SettingsManager.playlistsFilter
            _filtersList.forEach { (searchChip: SearchChips, checked: Boolean) ->
                if (checked) {
                    selectedSearchChips.add(searchChip)
                }
            }
        }
    }

    fun select(searchChip: SearchChips) {
        _filtersList[searchChip] = true
        if (!selectedSearchChips.contains(element = searchChip)) {
            selectedSearchChips.add(searchChip)
        }
    }

    fun unselect(searchChip: SearchChips) {
        _filtersList[searchChip] = false
        selectedSearchChips.remove(searchChip)
    }

    /**
     * When currentDestination is the settings list, then return to app only if audio permission has been allowed.
     * Otherwise navigate to settings
     */
    private fun onSettingButtonClick(
        uiState: SatunesUiState,
        navController: NavHostController,
        satunesViewModel: SatunesViewModel,
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (satunesViewModel.updateAvailableStatus != UpdateAvailableStatus.AVAILABLE) {
                satunesViewModel.updateAvailableStatus =
                    UpdateAvailableStatus.UNDEFINED
            }
        }

        when (val currentDestination: String = uiState.currentDestination) {
            in settingsDestinations -> {
                if (currentDestination == Destination.PERMISSIONS_SETTINGS.link && uiState.isAudioAllowed) {
                    return
                } else {
                    navController.popBackStack()
                    if (navController.currentBackStackEntry == null) {
                        navController.navigate(Destination.FOLDERS.link)
                        navController.navigate(Destination.SETTINGS.link)
                    }
                }
            }

            else -> navController.navigate(Destination.SETTINGS.link)
        }
    }

    fun resetUpdatesStatus() {
        if (updateAvailableStatus != UpdateAvailableStatus.AVAILABLE) {
            updateAvailableStatus = UpdateAvailableStatus.UNDEFINED
        }
    }

    fun checkUpdate() {
        UpdateCheckManager.checkUpdate(context = MainActivity.instance.applicationContext)
    }

    fun getCurrentVersion(): String =
        UpdateCheckManager.getCurrentVersion(context = MainActivity.instance.applicationContext)
}