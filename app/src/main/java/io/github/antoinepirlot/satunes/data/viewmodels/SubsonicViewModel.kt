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
import io.github.antoinepirlot.satunes.database.models.internet.ApiError
import io.github.antoinepirlot.satunes.database.models.media.Album
import io.github.antoinepirlot.satunes.database.models.media.Music
import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicAlbum
import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicArtist
import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicMedia
import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicMusic
import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicPlaylist
import io.github.antoinepirlot.satunes.database.services.data.DataManager
import io.github.antoinepirlot.satunes.database.services.settings.SettingsManager
import io.github.antoinepirlot.satunes.internet.subsonic.SubsonicApiRequester
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.lang.Thread.sleep

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

    var error: ApiError? by mutableStateOf(value = null)
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
        albumId: String,
        onDataRetrieved: (media: SubsonicAlbum) -> Unit,
        onFinished: (() -> Unit)? = null
    ) {
        val album: SubsonicAlbum? = DataManager.getSubsonicAlbum(id = albumId)
        if (album != null)
            if (album.isEmpty())
                this.loadAlbum(
                    album = album,
                    onDataRetrieved = { onDataRetrieved.invoke(it) },
                    onFinished = onFinished
                )
            else
                onDataRetrieved(album)
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
                        onDataRetrieved(
                            album.toSubsonicAlbum(
                                apiRequester = _apiRequester,
                                album = it
                            )
                        )
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
        artistId: String,
        onDataRetrieved: (media: SubsonicArtist) -> Unit,
        onFinished: (() -> Unit)? = null
    ) {
        val artist: SubsonicArtist? = DataManager.getSubsonicArtist(id = artistId)
        if (artist != null)
            if (artist.isEmpty())
                this.loadArtist(
                    artist = artist,
                    onDataRetrieved = onDataRetrieved,
                    onFinished = onFinished
                )
            else
                onDataRetrieved(artist)
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
        artistId: String,
        onDataRetrieved: (media: SubsonicArtist) -> Unit,
        onFinished: (() -> Unit)? = null
    ) {
        val artist: SubsonicArtist? = DataManager.getSubsonicArtist(id = artistId)
        if (artist != null)
            if (artist.isEmpty())
                this.loadArtist(
                    artist = artist,
                    onDataRetrieved = { artist: SubsonicArtist ->
                        this.loadArtistWithMusics(
                            artist = artist,
                            onDataRetrieved = onDataRetrieved
                        )
                    },
                    onFinished = onFinished
                )
            else onDataRetrieved(artist)
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
            while (queriesInProgress > 0)
                sleep(10) // Do not remove, without sleep, it can cause no data shown
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

    /**
     * Download music(s) associated to the [media].
     *
     * @param media the [SubsonicMedia] to download.
     */
    fun download(media: SubsonicMedia) {
        TODO()
    }

    /**
     * Create playlist to the server.
     *
     * @param name the playlist's title
     */
    fun createPlaylist(
        name: String,
        onDataRetrieved: ((SubsonicPlaylist) -> Unit)?,
        onFinished: (() -> Unit)? = null,
        onError: (() -> Unit)? = null
    ) {
        this.initRequest()
        runIOThread {
            _apiRequester.createPlaylist(
                title = name,
                onDataRetrieved = { onDataRetrieved?.invoke(it) },
                onFinished = { this.finishRequest() },
                onError = onError
            )
        }
    }

    /**
     * Get all playlists from the server.
     * @param onDataRetrieved the function to invoke when data is received.
     * @param onFinished the function to invoke when the process is finished.
     * @param onError the function to invoke when an error has occurred.
     */
    fun getPlaylists(
        onDataRetrieved: ((Collection<SubsonicPlaylist>) -> Unit)?,
        onFinished: (() -> Unit)? = null,
        onError: (() -> Unit)? = null
    ) {
        this.initRequest()
        runIOThread {
            _apiRequester.getPlaylists(
                onDataRetrieved = { onDataRetrieved?.invoke(it) },
                onFinished = {
                    this.finishRequest()
                    onFinished?.invoke()
                },
                onError = onError,
                onSucceed = null
            )
        }
    }

    fun loadPlaylistMusics(
        id: String,
        onFinished: (() -> Unit)? = null,
        onError: (() -> Unit)? = null
    ) {
        this.initRequest()
        runIOThread {
            _apiRequester.getPlaylist(
                id = id,
                onDataRetrieved = {
                    val playlist: SubsonicPlaylist = DataManager.getSubsonicPlaylist(id = id)!!
                    playlist.addMusics(musics = it)
                },
                onError = onError,
                onFinished = {
                    onFinished?.invoke()
                    this.finishRequest()
                },
            )
        }
    }

    fun updatePlaylistMusics(
        musics: Collection<SubsonicMusic>,
        playlist: SubsonicPlaylist,
        onFinished: (() -> Unit)? = null,
        onError: (() -> Unit)? = null
    ) {
        this.initRequest()
        runIOThread {
            _apiRequester.updatePlaylist(
                playlistId = playlist.subsonicId,
                musicsToAdd = this.getMusicsToAdd(playlist = playlist, collection = musics),
                musicsIndexToRemove = this.getMusicsIndexToRemove(
                    playlist = playlist,
                    collection = musics
                ),
                onError = onError,
                onFinished = {
                    onFinished?.invoke()
                    this.finishRequest()
                },
            )
        }
    }

    private fun getMusicsToAdd(
        playlist: SubsonicPlaylist,
        collection: Collection<SubsonicMusic>
    ): Collection<SubsonicMusic> {
        val musicsToAdd: MutableCollection<SubsonicMusic> = mutableListOf()
        for (music: SubsonicMusic in collection)
            if (!playlist.contains(media = music)) musicsToAdd.add(element = music)
        return musicsToAdd
    }

    private fun getMusicsIndexToRemove(
        playlist: SubsonicPlaylist,
        collection: Collection<SubsonicMusic>
    ): Collection<Int> {
        val musicsIndexToRemove: MutableCollection<Int> = mutableListOf()
        playlist.musicCollection.forEachIndexed { index: Int, music: Music ->
            if (!collection.contains(element = music))
                musicsIndexToRemove.add(element = index)
        }
        return musicsIndexToRemove
    }
}