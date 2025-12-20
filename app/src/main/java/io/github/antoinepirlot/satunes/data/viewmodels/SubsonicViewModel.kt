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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import io.github.antoinepirlot.android.utils.utils.runIOThread
import io.github.antoinepirlot.satunes.MainActivity
import io.github.antoinepirlot.satunes.data.states.SubsonicUiState
import io.github.antoinepirlot.satunes.database.models.User
import io.github.antoinepirlot.satunes.database.models.media.Album
import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicAlbum
import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicArtist
import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicMedia
import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicMusic
import io.github.antoinepirlot.satunes.database.services.data.DataManager
import io.github.antoinepirlot.satunes.database.services.settings.SettingsManager
import io.github.antoinepirlot.satunes.internet.subsonic.SubsonicApiRequester
import io.github.antoinepirlot.satunes.internet.subsonic.models.responses.Error
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * @author Antoine Pirlot 03/09/2025
 */
class SubsonicViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<SubsonicUiState> =
        MutableStateFlow(value = SubsonicUiState())

    private val _apiRequester: SubsonicApiRequester
        get() = SubsonicApiRequester()

    val uiState: StateFlow<SubsonicUiState> = _uiState.asStateFlow()

    var hasBeenUpdated: Boolean by mutableStateOf(false)
        private set

    var user: User = User(
        url = SettingsManager.subsonicUrl,
        username = SettingsManager.subsonicUsername,
        password = SettingsManager.subsonicPassword,
        salt = SettingsManager.subsonicSalt
    )

    var error: Error? by mutableStateOf(value = null)
        private set

    private var nbReq: Int = 0

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

    @Synchronized
    private fun initRequest() {
        if (this.nbReq++ < 0)
            throw IllegalStateException("nbReq < 0 in initRequest, there's a problem.")
        this.error = null
        if (!_uiState.value.isFetching)
            _uiState.update { currentState: SubsonicUiState ->
                currentState.copy(isFetching = true)
            }
    }

    @Synchronized
    private fun finishRequest() {
        if (this.nbReq-- == 0)
            throw IllegalStateException("nbReq will be < 0 in finishRequest, there's a problem.")
        if (this.nbReq == 0)
            _uiState.update { currentState: SubsonicUiState ->
                currentState.copy(isFetching = false)
            }
    }

    /**
     * Send ping to server to check if it is available and credentials corrects.
     */
    fun connect(onFinished: (Boolean) -> Unit) {
        val context: Context = MainActivity.instance.applicationContext
        this.initRequest()
        runIOThread {
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
                onSucceed = { onFinished(true) },
                onFinished = { this.finishRequest() },
                onError = { onFinished(false) }
            )
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
        this.initRequest()
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
        this.initRequest()
        runIOThread {
            _apiRequester.search(
                query = query,
                onFinished = {
                    onFinished.invoke()
                    this.finishRequest()
                },
                onDataRetrieved = onDataRetrieved
            )
        }
    }

    fun getAlbum(
        albumId: Long,
        onDataRetrieved: (media: SubsonicAlbum) -> Unit,
        onFinished: (() -> Unit)? = null
    ) {
        val album: SubsonicAlbum? = DataManager.getSubsonicAlbum(id = albumId)
        if (album != null)
            this.loadAlbum(
                album = album,
                onDataRetrieved = { onDataRetrieved.invoke(it) },
                onFinished = onFinished
            )
        else {
            this.initRequest()
            runIOThread {
                _apiRequester.getAlbum(
                    albumId = albumId,
                    onDataRetrieved = onDataRetrieved,
                    onError = { this@SubsonicViewModel.error = it },
                    onFinished = {
                        onFinished?.invoke()
                        this.finishRequest()
                    },
                )
            }
        }
    }

    /**
     * Load album's information and update it with the information from server.
     */
    fun loadAlbum(
        album: SubsonicAlbum,
        onDataRetrieved: (media: SubsonicAlbum) -> Unit,
        onFinished: (() -> Unit)? = null
    ) {
        this.initRequest()
        runIOThread {
            _apiRequester.getAlbum(
                albumId = album.subsonicId,
                onDataRetrieved = {
                    if (!album.isSubsonic())
                        onDataRetrieved(album.toSubsonicAlbum(album = it))
                    else onDataRetrieved(it)
                },
                onFinished = {
                    onFinished?.invoke()
                    this.finishRequest()
                },
                onError = { this@SubsonicViewModel.error = it }
            )
        }
    }

    fun getArtist(
        artistId: Long,
        onDataRetrieved: (media: SubsonicArtist) -> Unit,
        onFinished: (() -> Unit)? = null
    ) {
        val artist: SubsonicArtist? = DataManager.getSubsonicArtist(id = artistId)
        if (artist != null)
            this.loadArtist(
                artist = artist,
                onDataRetrieved = onDataRetrieved,
                onFinished = onFinished
            )
        else {
            this.initRequest()
            runIOThread {
                _apiRequester.getArtist(
                    artistId = artistId,
                    onDataRetrieved = onDataRetrieved,
                    onFinished = {
                        onFinished?.invoke()
                        this.finishRequest()
                    },
                    onError = { this@SubsonicViewModel.error = it }
                )
            }
        }
    }

    fun getArtistWithMusics(
        artistId: Long,
        onDataRetrieved: (media: SubsonicArtist) -> Unit,
        onFinished: (() -> Unit)? = null
    ) {
        val artist: SubsonicArtist? = DataManager.getSubsonicArtist(id = artistId)
        if (artist != null)
            this.loadArtist(
                artist = artist,
                onDataRetrieved = { artist: SubsonicArtist ->
                    this.loadArtistWithMusics(artist = artist, onDataRetrieved = onDataRetrieved)
                },
                onFinished = onFinished
            )
        else
            this.getArtist(
                artistId = artistId,
                onDataRetrieved = { artist: SubsonicArtist ->
                    this.loadArtistWithMusics(artist = artist, onDataRetrieved = onDataRetrieved)
                },
                onFinished = onFinished
            )
    }

    private fun loadArtistWithMusics(
        artist: SubsonicArtist,
        onDataRetrieved: (media: SubsonicArtist) -> Unit
    ) {
        var queriesInProgress: Int = 0
        artist.albumCollection.forEach { album: Album ->
            if (album.isSubsonic()) {
                queriesInProgress++
                this.getAlbum(
                    albumId = (album as SubsonicAlbum).subsonicId,
                    onDataRetrieved = { album: SubsonicAlbum ->
                        artist.addMusics(musics = album.musicCollection)
                    },
                    onFinished = { queriesInProgress-- },
                )
            }
        }
        runIOThread {
            while (queriesInProgress > 0);
            onDataRetrieved.invoke(artist)
        }
    }

    /**
     * Load artist's information and update it with the information from server.
     */
    fun loadArtist(
        artist: SubsonicArtist,
        onDataRetrieved: (media: SubsonicArtist) -> Unit,
        onFinished: (() -> Unit)? = null
    ) {
        this.initRequest()
        runIOThread {
            _apiRequester.getArtist(
                artistId = artist.subsonicId,
                onDataRetrieved = {
                    if (!artist.isSubsonic())
                        onDataRetrieved.invoke(artist.toSubsonicArtist(artist = it))
                    else onDataRetrieved(it)
                },
                onFinished = {
                    onFinished?.invoke()
                    this.finishRequest()
                },
                onError = { this@SubsonicViewModel.error = it }
            )
        }
    }
}