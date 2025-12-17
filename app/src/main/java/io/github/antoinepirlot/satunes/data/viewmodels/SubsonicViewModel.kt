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
import io.github.antoinepirlot.android.utils.utils.runIOThread
import io.github.antoinepirlot.satunes.MainActivity
import io.github.antoinepirlot.satunes.database.models.User
import io.github.antoinepirlot.satunes.database.models.media.Media
import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicAlbum
import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicMedia
import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicMusic
import io.github.antoinepirlot.satunes.database.services.settings.SettingsManager
import io.github.antoinepirlot.satunes.internet.subsonic.SubsonicApiRequester
import io.github.antoinepirlot.satunes.internet.subsonic.models.responses.Error
import io.github.antoinepirlot.satunes.ui.utils.showErrorSnackBar
import kotlinx.coroutines.CoroutineScope

/**
 * @author Antoine Pirlot 03/09/2025
 */
class SubsonicViewModel : ViewModel() {
    var hasBeenUpdated: Boolean by mutableStateOf(false)
        private set

    private val _apiRequester: SubsonicApiRequester
        get() = SubsonicApiRequester()

    var user: User = User(
        url = SettingsManager.subsonicUrl,
        username = SettingsManager.subsonicUsername,
        password = SettingsManager.subsonicPassword,
        salt = SettingsManager.subsonicSalt
    )

    var error: Error? by mutableStateOf(value = null)
        private set

    var mediaRetrieved: Media? = null
        private set

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
        runIOThread {
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
                if (!user.isFilled()) {
                    onFinished(false)
                    return@runIOThread
                }
                _apiRequester.ping(
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
//        apiRequester.loadArtists()
    }

    fun removeOnlineMusic() {
        TODO("Not yet implemented")
    }

    /**
     * Get random song from API
     */
    fun loadRandomSongs(onDataRetrieved: (Collection<SubsonicMusic>) -> Unit) {
        runIOThread {
            _apiRequester.getRandomSongs(onDataRetrieved = onDataRetrieved)
        }
    }

    /**
     * Search for media into the API.
     *
     * @param query the query to send to the API.
     */
    fun search(
        query: String,
        onFinished: () -> Unit,
        onDataRetrieved: (Collection<SubsonicMedia>) -> Unit
    ) {
        runIOThread {
            _apiRequester.search(
                query = query,
                onFinished = onFinished,
                onDataRetrieved = onDataRetrieved
            )
        }
    }

    fun loadAlbum(albumId: Long) {
        runIOThread {
            _apiRequester.getAlbum(
                albumId = albumId,
                onDataRetrieved = { this.mediaRetrieved = it },
                onError = { this@SubsonicViewModel.error = it }
            )
        }
    }

    /**
     * Load album's information and update it with the information from server.
     */
    fun loadAlbum(album: SubsonicAlbum) {
        runIOThread {
            _apiRequester.getAlbum(
                albumId = album.subsonicId,
                onDataRetrieved = { album.update(it) },
                onError = { this@SubsonicViewModel.error = it }
            )
        }
    }

    fun loadArtist(artistId: Long) {
        TODO()
    }
}