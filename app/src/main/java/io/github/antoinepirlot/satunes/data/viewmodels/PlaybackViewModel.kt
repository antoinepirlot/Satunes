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

package io.github.antoinepirlot.satunes.data.viewmodels

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import io.github.antoinepirlot.satunes.MainActivity
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.database.models.Folder
import io.github.antoinepirlot.satunes.database.models.MediaImpl
import io.github.antoinepirlot.satunes.database.models.Music
import io.github.antoinepirlot.satunes.database.services.settings.SettingsManager
import io.github.antoinepirlot.satunes.models.ProgressBarLifecycleCallbacks
import io.github.antoinepirlot.satunes.playback.services.PlaybackManager
import io.github.antoinepirlot.satunes.ui.utils.showErrorSnackBar
import io.github.antoinepirlot.satunes.ui.utils.showSnackBar
import io.github.antoinepirlot.satunes.utils.logger.SatunesLogger
import kotlinx.coroutines.CoroutineScope

/**
 * @author Antoine Pirlot on 19/07/2024
 */
class PlaybackViewModel : ViewModel() {
    private val _logger: SatunesLogger = SatunesLogger.getLogger()
    private var _playbackManager: PlaybackManager = PlaybackManager
    private var _isPlaying: MutableState<Boolean> = _playbackManager.isPlaying
    private var _musicPlaying: MutableState<Music?> = _playbackManager.musicPlaying
    private var _currentPositionProgression: MutableFloatState =
        _playbackManager.currentPositionProgression
    private var _repeatMode: MutableIntState = _playbackManager.repeatMode
    private var _isShuffle: MutableState<Boolean> = _playbackManager.isShuffle
    private var _isLoaded: MutableState<Boolean> = _playbackManager.isLoaded
    private var _isEnded: MutableState<Boolean> = _playbackManager.isEnded

    val isPlaying: Boolean by _isPlaying
    val musicPlaying: Music? by _musicPlaying
    val currentPositionProgression: Float by _currentPositionProgression
    val repeatMode: Int by _repeatMode
    val isShuffle: Boolean by _isShuffle
    val isLoaded: Boolean by _isLoaded
    val isEnded: Boolean by _isEnded

    init {
        // Needed to refresh progress bar
        ProgressBarLifecycleCallbacks.playbackViewModel = this
    }

    fun loadMusicFromFolders(
        folders: Set<Folder>,
        shuffleMode: Boolean = SettingsManager.shuffleMode,
        musicToPlay: Music? = null
    ) {
        val musicSet: MutableSet<Music> = mutableSetOf()
        folders.forEach { folder: Folder ->
            musicSet.addAll(elements = folder.getAllMusic())
        }
        this.loadMusic(
            musicSet = musicSet,
            shuffleMode = shuffleMode,
            musicToPlay = musicToPlay
        )
    }

    fun loadMusicFromMedia(
        media: MediaImpl,
        shuffleMode: Boolean = SettingsManager.shuffleMode,
        musicToPlay: Music? = null
    ) {
        val musicSet: Set<Music> =
            if (media is Folder) {
                media.getAllMusic()
            } else {
                val musicSet: MutableSet<Music> = mutableSetOf()
                media.getMusicSet().forEach { music: Music ->
                    musicSet.addAll(elements = music.getMusicSet())
                }
                musicSet
            }
        this.loadMusic(
            musicSet = musicSet,
            shuffleMode = shuffleMode,
            musicToPlay = musicToPlay
        )
    }

    fun loadMusicFromMedias(
        medias: Set<MediaImpl>,
        shuffleMode: Boolean = SettingsManager.shuffleMode,
        musicToPlay: Music? = null,
    ) {
        val musicSet: MutableSet<Music> = mutableSetOf()
        medias.forEach { mediaImpl: MediaImpl ->
            musicSet.addAll(mediaImpl.getMusicSet())
        }
        this.loadMusic(
            musicSet = musicSet,
            shuffleMode = shuffleMode,
            musicToPlay = musicToPlay
        )
    }

    fun loadMusic(
        musicSet: Set<Music>,
        shuffleMode: Boolean = SettingsManager.shuffleMode,
        musicToPlay: Music? = null,
    ) {
        this._playbackManager.loadMusic(
            context = MainActivity.instance.applicationContext,
            musicSet = musicSet,
            shuffleMode = shuffleMode,
            musicToPlay = musicToPlay
        )
    }

    fun getPlaylist(): List<Music> {
        return this._playbackManager.getPlaylist(context = MainActivity.instance.applicationContext)
    }

    fun seekTo(positionPercentage: Float) {
        this._playbackManager.seekTo(
            context = MainActivity.instance.applicationContext,
            positionPercentage = positionPercentage
        )
    }

    fun playNext() {
        this._playbackManager.playNext(context = MainActivity.instance.applicationContext)
    }

    fun playPause() {
        this._playbackManager.playPause(context = MainActivity.instance.applicationContext)
    }

    fun playPrevious() {
        this._playbackManager.playPrevious(context = MainActivity.instance.applicationContext)
    }

    fun switchRepeatMode() {
        this._playbackManager.switchRepeatMode(context = MainActivity.instance.applicationContext)
    }

    fun switchShuffleMode() {
        this._playbackManager.switchShuffleMode(context = MainActivity.instance.applicationContext)
    }

    fun getMusicPlayingIndexPosition(): Int {
        return this._playbackManager.getMusicPlayingIndexPosition(context = MainActivity.instance.applicationContext)
    }

    fun addToQueue(
        scope: CoroutineScope,
        snackBarHostState: SnackbarHostState,
        mediaImpl: MediaImpl
    ) {
        val context: Context = MainActivity.instance.applicationContext
        try {
            this._playbackManager.addToQueue(
                context = MainActivity.instance.applicationContext,
                mediaImpl = mediaImpl
            )
            showSnackBar(
                scope = scope,
                snackBarHostState = snackBarHostState,
                message = mediaImpl.title + ' ' + context.getString(R.string.add_to_queue_success),
            )
        } catch (e: Throwable) {
            _logger.warning(e.message)
            showErrorSnackBar(
                scope = scope,
                snackBarHostState = snackBarHostState,
                action = {
                    addToQueue(
                        scope = scope,
                        snackBarHostState = snackBarHostState,
                        mediaImpl = mediaImpl
                    )
                }
            )
        }
    }

    fun addNext(
        scope: CoroutineScope,
        snackBarHostState: SnackbarHostState,
        mediaImpl: MediaImpl
    ) {
        val context: Context = MainActivity.instance.applicationContext
        try {
            this._playbackManager.addNext(
                context = MainActivity.instance.applicationContext,
                mediaImpl = mediaImpl
            )
            showSnackBar(
                scope = scope,
                snackBarHostState = snackBarHostState,
                message = mediaImpl.title + ' ' + context.getString(R.string.play_next_success)
            )
        } catch (e: Throwable) {
            _logger.warning(e.message)
            showErrorSnackBar(
                scope = scope,
                snackBarHostState = snackBarHostState,
                action = {
                    addNext(
                        scope = scope,
                        snackBarHostState = snackBarHostState,
                        mediaImpl = mediaImpl
                    )
                }
            )
        }
    }

    fun isMusicInQueue(music: Music): Boolean {
        return this._playbackManager.isMusicInQueue(
            context = MainActivity.instance.applicationContext,
            music = music
        )
    }

    fun start(mediaToPlay: Music? = null) {
        this._playbackManager.start(
            context = MainActivity.instance.applicationContext,
            musicToPlay = mediaToPlay
        )
    }

    fun updateCurrentPosition() {
        this._playbackManager.updateCurrentPosition(context = MainActivity.instance.applicationContext)
    }

    fun release() {
        this._playbackManager.release(context = MainActivity.instance.applicationContext)
        onCleared()
    }
}