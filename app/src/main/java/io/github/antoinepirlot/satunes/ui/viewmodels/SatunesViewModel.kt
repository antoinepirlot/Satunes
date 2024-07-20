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

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import io.github.antoinepirlot.satunes.database.models.MenuTitle
import io.github.antoinepirlot.satunes.database.services.data.DataLoader
import io.github.antoinepirlot.satunes.database.services.settings.SettingsManager
import io.github.antoinepirlot.satunes.models.SwitchSettings
import io.github.antoinepirlot.satunes.ui.states.SatunesUiState
import io.github.antoinepirlot.satunes.ui.viewmodels.utils.isAudioAllowed
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * @author Antoine Pirlot on 19/07/2024
 */
internal class SatunesViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<SatunesUiState> = MutableStateFlow(SatunesUiState())
    private val _isLoadingData: MutableState<Boolean> = DataLoader.isLoading
    private val _isDataLoaded: MutableState<Boolean> = DataLoader.isLoaded
    private var _selectedMenuTitle: MenuTitle = _uiState.value.selectedMenuTitle

    //Exclusion
    private val _includeRingtonesChecked: MutableState<Boolean> =
        SettingsManager.includeRingtonesChecked

    //Nav Bar Items
    private val _foldersChecked: MutableState<Boolean> = SettingsManager.foldersChecked
    private val _artistsChecked: MutableState<Boolean> = SettingsManager.artistsChecked
    private val _albumsChecked: MutableState<Boolean> = SettingsManager.albumsChecked
    private val _genresChecked: MutableState<Boolean> = SettingsManager.genresChecked
    private val _playlistsChecked: MutableState<Boolean> = SettingsManager.playlistsChecked

    //Search Filters
    private val _foldersFilter: MutableState<Boolean> = SettingsManager.foldersFilter
    private val _artistsFilter: MutableState<Boolean> = SettingsManager.artistsFilter
    private val _albumsFilter: MutableState<Boolean> = SettingsManager.albumsFilter
    private val _genresFilter: MutableState<Boolean> = SettingsManager.genresFilter
    private val _playlistsFilter: MutableState<Boolean> = SettingsManager.playlistsFilter
    private val _musicsFilter: MutableState<Boolean> = SettingsManager.musicsFilter

    // Playback
    private val _playbackWhenClosedChecked: MutableState<Boolean> =
        SettingsManager.playbackWhenClosedChecked
    private val _pauseIfNoisyChecked: MutableState<Boolean> = SettingsManager.pauseIfNoisyChecked
    private val _pauseIfAnotherPlayback: MutableState<Boolean> =
        SettingsManager.pauseIfAnotherPlayback

    val uiState: StateFlow<SatunesUiState> = _uiState.asStateFlow()

    val isLoadingData: Boolean by _isLoadingData
    val isDataLoaded: Boolean by _isDataLoaded

    //TODO adapt to use ui state. Settings Manager must not contains mutable states
    //Exclusion
    val includeRingtonesChecked: Boolean by _includeRingtonesChecked

    val exclusionSettingsChecked: Map<SwitchSettings, Boolean> = mapOf(
        Pair(first = SwitchSettings.INCLUDE_RINGTONES, second = includeRingtonesChecked)
    )

    //Nav Bar Items
    val foldersChecked: Boolean by _foldersChecked
    val artistsChecked: Boolean by _artistsChecked
    val albumsChecked: Boolean by _albumsChecked
    val genresChecked: Boolean by _genresChecked
    val playlistsChecked: Boolean by _playlistsChecked

    val navBarItemSettingsChecked: Map<SwitchSettings, Boolean> = mapOf(
        Pair(first = SwitchSettings.FOLDERS_CHECKED, second = foldersChecked),
        Pair(first = SwitchSettings.ARTISTS_CHECKED, second = artistsChecked),
        Pair(first = SwitchSettings.ALBUMS_CHECKED, second = albumsChecked),
        Pair(first = SwitchSettings.GENRES_CHECKED, second = genresChecked),
        Pair(first = SwitchSettings.PLAYLISTS_CHECKED, second = playlistsChecked),
    )

    //Search Filters
    val foldersFilter: Boolean by _foldersFilter
    val artistsFilter: Boolean by _artistsFilter
    val albumsFilter: Boolean by _albumsFilter
    val genresFilter: Boolean by _genresFilter
    val playlistsFilter: Boolean by _playlistsFilter
    val musicsFilter: Boolean by _musicsFilter

    val filterSettingsChecked: Map<SwitchSettings, Boolean> = mapOf(
        Pair(SwitchSettings.MUSICS_FILTER, musicsFilter),
        Pair(SwitchSettings.ALBUMS_FILTER, albumsFilter),
        Pair(SwitchSettings.ARTISTS_FILTER, artistsFilter),
        Pair(SwitchSettings.GENRES_FILTER, genresFilter),
        Pair(SwitchSettings.FOLDERS_FILTER, foldersFilter),
        Pair(SwitchSettings.PLAYLISTS_FILTER, playlistsFilter)
    )

    //Playback
    val playbackWhenClosedChecked: Boolean by _playbackWhenClosedChecked
    val pauseIfNoisyChecked: Boolean by _pauseIfNoisyChecked
    val pauseIfAnotherPlayback: Boolean by _pauseIfAnotherPlayback
    val playbackSettingsChecked: Map<SwitchSettings, Boolean> = mapOf(
        Pair(first = SwitchSettings.PLAYBACK_WHEN_CLOSED, second = playbackWhenClosedChecked),
        Pair(first = SwitchSettings.PAUSE_IF_NOISY, second = pauseIfNoisyChecked),
        Pair(first = SwitchSettings.PAUSE_IF_ANOTHER_PLAYBACK, second = pauseIfAnotherPlayback)
    )

    //Use this in UiSate and ViewModel as it is a particular value. It could change but most of the time it won't change
    var isAudioAllowed: Boolean by mutableStateOf(_uiState.value.isAudioAllowed)
        private set

    fun seeWhatsNew(context: Context, permanently: Boolean = false) {
        if (permanently) {
            SettingsManager.seeWhatsNew(context = context)
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

    fun loadAllData(context: Context) {
        DataLoader.loadAllData(context = context)
    }

    internal fun updateIsAudioAllowed() {
        this.isAudioAllowed = isAudioAllowed()
        if (this.isAudioAllowed != this._uiState.value.isAudioAllowed) {
            this._uiState.update { currentState: SatunesUiState ->
                currentState.copy(isAudioAllowed = this.isAudioAllowed)
            }
        }
    }
}