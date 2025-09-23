/*
 * This file is part of Satunes.
 *
 * Satunes is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * Satunes is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License along with Satunes.
 *
 * If not, see <https://www.gnu.org/licenses/>.
 *
 * **** INFORMATION ABOUT THE AUTHOR *****
 * The author of this file is Antoine Pirlot, the owner of this project.
 * You find this original project on Codeberg.
 *
 * My Codeberg link is: https://codeberg.org/antoinepirlot
 * This current project's link is: https://codeberg.org/antoinepirlot/Satunes
 */

package io.github.antoinepirlot.satunes.data.viewmodels

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import io.github.antoinepirlot.satunes.MainActivity
import io.github.antoinepirlot.satunes.data.states.SubsonicUiState
import io.github.antoinepirlot.satunes.database.services.settings.SettingsManager
import io.github.antoinepirlot.satunes.database.utils.md5
import io.github.antoinepirlot.satunes.internet.subsonic.SubsonicApiRequester
import io.github.antoinepirlot.satunes.ui.utils.showErrorSnackBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * @author Antoine Pirlot 03/09/2025
 */
@RequiresApi(Build.VERSION_CODES.M)
class SubsonicViewModel : ViewModel() {
    companion object {
        private val _uiState: MutableStateFlow<SubsonicUiState> =
            MutableStateFlow(SubsonicUiState())

        private var _subsonicApiRequester: SubsonicApiRequester? = null

    }

    val uiState: StateFlow<SubsonicUiState> = _uiState.asStateFlow()

    var url: String by mutableStateOf(SettingsManager.subsonicUrl)
        private set

    var username: String by mutableStateOf(SettingsManager.subsonicUsername)
        private set

    var password: String by mutableStateOf(SettingsManager.subsonicPassword)
        private set

    var hasBeenUpdated: Boolean by mutableStateOf(false)
        private set

    fun updateSubsonicUrl(url: String) {
        this.url = url
        this.hasBeenUpdated = true
    }

    fun updateSubsonicUsername(username: String) {
        this.username = username
        this.hasBeenUpdated = true
    }

    fun updateSubsonicPassword(password: String) {
        this.password = password
        this.hasBeenUpdated = true
    }

    fun reset() {
        this.url = SettingsManager.subsonicUrl
        this.username = SettingsManager.subsonicUsername
        this.password = SettingsManager.subsonicPassword
        this.hasBeenUpdated = false
    }

    fun disconnect() {
        _subsonicApiRequester?.disconnect(context = MainActivity.instance.applicationContext)
    }

    /**
     * Send ping to server to check if it is available and credentials corrects.
     */
    fun testConnection(scope: CoroutineScope, snackbarHostState: SnackbarHostState) {
        val context: Context = MainActivity.instance.applicationContext
        CoroutineScope(Dispatchers.IO).launch {
            try {
                SettingsManager.updateSubsonicUrl(
                    context = context,
                    url = this@SubsonicViewModel.url
                )
                SettingsManager.updateSubsonicUsername(
                    context = context,
                    username = this@SubsonicViewModel.username
                )
                SettingsManager.updateSubsonicPassword(
                    context = context,
                    password = this@SubsonicViewModel.password
                )
                this@SubsonicViewModel.hasBeenUpdated = false
                _subsonicApiRequester = SubsonicApiRequester(
                    url = this@SubsonicViewModel.url,
                    username = this@SubsonicViewModel.username,
                    md5Password = this@SubsonicViewModel.password.md5(),
                    onSubsonicStateChanged = {
                        _uiState.update { currentState: SubsonicUiState ->
                            currentState.copy(subsonicState = it)
                        }
                    }
                )
                _subsonicApiRequester!!.ping(context = context)
            } catch (_: Throwable) {
                showErrorSnackBar(
                    scope = scope,
                    snackBarHostState = snackbarHostState,
                    action = {
                        testConnection(
                            scope = scope,
                            snackbarHostState = snackbarHostState
                        )
                    }
                )
            }
        }
    }
}