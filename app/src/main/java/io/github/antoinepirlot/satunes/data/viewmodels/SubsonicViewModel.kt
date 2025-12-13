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
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import io.github.antoinepirlot.satunes.MainActivity
import io.github.antoinepirlot.satunes.database.models.User
import io.github.antoinepirlot.satunes.database.models.media.SubsonicMusic
import io.github.antoinepirlot.satunes.database.services.settings.SettingsManager
import io.github.antoinepirlot.satunes.internet.subsonic.SubsonicApiRequester
import io.github.antoinepirlot.satunes.ui.utils.showErrorSnackBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * @author Antoine Pirlot 03/09/2025
 */
class SubsonicViewModel : ViewModel() {
    companion object {

    }

    var hasBeenUpdated: Boolean by mutableStateOf(false)
        private set

    var user: User = User(
        url = SettingsManager.subsonicUrl,
        username = SettingsManager.subsonicUsername,
        password = SettingsManager.subsonicPassword,
        salt = SettingsManager.subsonicSalt
    )

    fun updateSubsonicUrl(url: String) {
        this.user.url = url
        this.hasBeenUpdated = true
    }

    fun updateSubsonicUsername(username: String) {
        this.user.username = username
        this.hasBeenUpdated = true
    }

    fun updateSubsonicPassword(password: String) {
        this.user.password = password
        this.hasBeenUpdated = true
    }

    fun updateSubsonicSalt(salt: String) {
        this.user.salt = salt
        this.hasBeenUpdated = true
    }

    fun reset() {
        this.user.url = SettingsManager.subsonicUrl
        this.user.username = SettingsManager.subsonicUsername
        this.user.password = SettingsManager.subsonicPassword
        this.hasBeenUpdated = false
    }

    /**
     * Send ping to server to check if it is available and credentials corrects.
     */
    fun connect(
        scope: CoroutineScope,
        snackbarHostState: SnackbarHostState,
        onFinished: (Boolean) -> Unit
    ) {
        val context: Context = MainActivity.instance.applicationContext
        CoroutineScope(Dispatchers.IO).launch {
            try {
                SettingsManager.updateSubsonicUrl(
                    context = context,
                    url = this@SubsonicViewModel.user.url
                )
                SettingsManager.updateSubsonicUsername(
                    context = context,
                    username = this@SubsonicViewModel.user.username
                )
                SettingsManager.updateSubsonicPassword(
                    context = context,
                    password = this@SubsonicViewModel.user.password
                )
                SettingsManager.updateSubsonicSalt(
                    context = context,
                    salt = this@SubsonicViewModel.user.salt
                )
                this@SubsonicViewModel.hasBeenUpdated = false
                val subsonicApiRequester = SubsonicApiRequester(user = user)
                if (!user.isFilled()) {
                    onFinished(false)
                    return@launch
                }
                subsonicApiRequester.ping(
                    onSucceed = {
                        onFinished(true)
//                        subsonicApiRequester.loadAll()
                    },
                    onError = { onFinished(false) }
                ) //TODO
            } catch (_: Throwable) {
                showErrorSnackBar(
                    scope = scope,
                    snackBarHostState = snackbarHostState,
                    action = {
                        connect(
                            scope = scope,
                            snackbarHostState = snackbarHostState,
                            onFinished = onFinished
                        )
                    }
                )
            }
        }
    }

    private fun loadArtists() {
        val apiRequester = SubsonicApiRequester(user = user)
//        apiRequester.loadArtists()
    }

    fun removeOnlineMusic() {
        TODO("Not yet implemented")
    }

    /**
     * Get random song from API
     */
    fun loadRandomSongs(onDataRetrieved: (Collection<SubsonicMusic>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val apiRequester = SubsonicApiRequester(user = user)
            apiRequester.getRandomSongs(onDataRetrieved = onDataRetrieved)
        }
    }
}