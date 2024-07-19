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
import androidx.lifecycle.ViewModel
import io.github.antoinepirlot.satunes.database.services.data.DataLoader
import io.github.antoinepirlot.satunes.database.services.settings.SettingsManager
import io.github.antoinepirlot.satunes.ui.states.SatunesUiState
import io.github.antoinepirlot.satunes.utils.logger.SatunesLogger
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
    private val _logger: SatunesLogger = SatunesLogger(this::class.java.name)

    val uiState: StateFlow<SatunesUiState> = _uiState.asStateFlow()

    val isLoadingData: Boolean by _isLoadingData
    val isDataLoaded: Boolean by _isDataLoaded

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

}