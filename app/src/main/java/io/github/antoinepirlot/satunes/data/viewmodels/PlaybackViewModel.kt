/*
 * This file is part of Satunes.
 *
 * Satunes is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * Satunes is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with Satunes.
 * If not, see <https://www.gnu.org/licenses/>.
 *
 * *** INFORMATION ABOUT THE AUTHOR *****
 * The author of this file is Antoine Pirlot, the owner of this project.
 * You find this original project on Codeberg.
 *
 * My Codeberg link is: https://codeberg.org/antoinepirlot
 * This current project's link is: https://codeberg.org/antoinepirlot/Satunes
 */

package io.github.antoinepirlot.satunes.data.viewmodels

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import io.github.antoinepirlot.android.utils.logger.Logger
import io.github.antoinepirlot.satunes.MainActivity
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.data.states.PlaybackUiState
import io.github.antoinepirlot.satunes.database.models.custom_action.CustomActions
import io.github.antoinepirlot.satunes.database.models.media.Folder
import io.github.antoinepirlot.satunes.database.models.media.MediaImpl
import io.github.antoinepirlot.satunes.database.models.media.Music
import io.github.antoinepirlot.satunes.database.services.settings.SettingsManager
import io.github.antoinepirlot.satunes.models.Destination
import io.github.antoinepirlot.satunes.models.ProgressBarLifecycleCallbacks
import io.github.antoinepirlot.satunes.models.Timer
import io.github.antoinepirlot.satunes.playback.services.PlaybackManager
import io.github.antoinepirlot.satunes.ui.utils.showErrorSnackBar
import io.github.antoinepirlot.satunes.ui.utils.showSnackBar
import io.github.antoinepirlot.satunes.utils.getMediaTitle
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
class PlaybackViewModel : ViewModel() {

    companion object {
        private val _uiState: MutableStateFlow<PlaybackUiState> =
            MutableStateFlow(PlaybackUiState())
    }

    private val _logger: Logger? = Logger.getLogger()
    private var _isPlaying: MutableState<Boolean> = PlaybackManager.isPlaying
    private var _musicPlaying: MutableState<Music?> = PlaybackManager.musicPlaying
    private var _currentPositionProgression: MutableFloatState =
        PlaybackManager.currentPositionProgression
    private var _repeatMode: MutableIntState = PlaybackManager.repeatMode

    private val _forwardMs: MutableState<Long> = SettingsManager.forwardMs
    private val _rewindMs: MutableState<Long> = SettingsManager.rewindMs
    private var _isShuffle: MutableState<Boolean> = PlaybackManager.isShuffle
    private var _isLoaded: MutableState<Boolean> = PlaybackManager.isLoaded
    private var _isEnded: MutableState<Boolean> = PlaybackManager.isEnded
    private var _isInitialed: MutableState<Boolean> = PlaybackManager.isInitialized

    val uiState: StateFlow<PlaybackUiState> = _uiState.asStateFlow()

    val isPlaying: Boolean by _isPlaying
    val musicPlaying: Music? by _musicPlaying
    val currentPositionProgression: Float by _currentPositionProgression
    val repeatMode: Int by _repeatMode
    val isShuffle: Boolean by _isShuffle
    val isLoaded: Boolean by _isLoaded
    val isEnded: Boolean by _isEnded
    val isInitialized: Boolean by _isInitialed

    val forwardMs: Long by this._forwardMs

    val rewindMs: Long by this._rewindMs

    val customActionsOrder: Collection<CustomActions> = SettingsManager.customActionsOrder

    init {
        // Needed to refresh progress bar
        ProgressBarLifecycleCallbacks.playbackViewModel = this
    }

    fun loadMusicFromMedia(
        media: MediaImpl,
        shuffleMode: Boolean = SettingsManager.shuffleMode,
        musicToPlay: Music? = null
    ) {
        val musicSet: Set<Music> =
            if (media.isFolder()) {
                (media as Folder).getAllMusic()
            } else {
                val musicSet: MutableSet<Music> = mutableSetOf()
                media.musicList.forEach { music: Music ->
                    musicSet.addAll(elements = music.musicList)
                }
                musicSet
            }
        this.loadMusics(
            musics = musicSet,
            shuffleMode = shuffleMode,
            musicToPlay = musicToPlay
        )
    }

    fun loadMusicFromMedias(
        medias: Collection<MediaImpl>,
        currentDestination: Destination,
        shuffleMode: Boolean = SettingsManager.shuffleMode,
        musicToPlay: Music? = null,
    ) {
        val musicSet: MutableSet<Music> = mutableSetOf()
        //TODO add setting to let the user choose if for folder, the app should also load subfolders in v3.2.0.
        val isInFolderView: Boolean =
            currentDestination == Destination.FOLDERS || currentDestination == Destination.FOLDER
        if (isInFolderView)
            medias.forEach { media: MediaImpl ->
                if (media.isMusic()) musicSet.add(media as Music)
                else return@forEach
            }

        medias.forEach { mediaImpl: MediaImpl ->
            if (isInFolderView && mediaImpl.isMusic()) return@forEach
            if (mediaImpl.isMusic()) musicSet.add(mediaImpl as Music)
            else if (mediaImpl.isFolder()) musicSet.addAll((mediaImpl as Folder).getAllMusic())
            else musicSet.addAll(mediaImpl.musicList)
        }
        this.loadMusics(
            musics = musicSet,
            shuffleMode = shuffleMode,
            musicToPlay = musicToPlay
        )
    }

    fun loadMusics(
        musics: Collection<Music>,
        shuffleMode: Boolean = SettingsManager.shuffleMode,
        musicToPlay: Music? = null,
    ) {
        PlaybackManager.loadMusics(
            context = MainActivity.instance.applicationContext,
            musics = musics,
            shuffleMode = shuffleMode,
            musicToPlay = musicToPlay
        )
    }

    fun getPlaylist(): List<Music> {
        return PlaybackManager.getPlaylist(context = MainActivity.instance.applicationContext)
    }

    fun seekTo(positionPercentage: Float) {
        PlaybackManager.seekTo(
            context = MainActivity.instance.applicationContext,
            positionPercentage = positionPercentage
        )
    }

    fun playNext() {
        PlaybackManager.playNext(context = MainActivity.instance.applicationContext)
    }

    fun playPause() {
        PlaybackManager.playPause(context = MainActivity.instance.applicationContext)
    }

    fun playPrevious() {
        PlaybackManager.playPrevious(context = MainActivity.instance.applicationContext)
    }

    fun switchRepeatMode() {
        PlaybackManager.switchRepeatMode(context = MainActivity.instance.applicationContext)
    }

    fun switchShuffleMode() {
        PlaybackManager.switchShuffleMode(context = MainActivity.instance.applicationContext)
    }

    fun getMusicPlayingIndexPosition(): Int {
        return PlaybackManager.getMusicPlayingIndexPosition(context = MainActivity.instance.applicationContext)
    }

    fun getNextMusic(): Music? {
        return try {
            PlaybackManager.getNextMusic(context = MainActivity.instance.applicationContext)
        } catch (_: Throwable) {
            _logger?.severe("Can't get the next music in queue")
            null
        }
    }

    fun addToQueue(
        scope: CoroutineScope,
        snackBarHostState: SnackbarHostState,
        mediaImpl: MediaImpl
    ) {
        val context: Context = MainActivity.instance.applicationContext
        try {
            PlaybackManager.addToQueue(
                context = MainActivity.instance.applicationContext,
                mediaImpl = mediaImpl
            )
            showSnackBar(
                scope = scope,
                snackBarHostState = snackBarHostState,
                message = mediaImpl.title + ' ' + context.getString(R.string.add_to_queue_success),
            )
        } catch (e: Throwable) {
            _logger?.warning(e.message)
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

    fun removeFromQueue(
        scope: CoroutineScope,
        snackBarHostState: SnackbarHostState,
        mediaImpl: MediaImpl
    ) {
        val context: Context = MainActivity.instance.applicationContext
        try {
            PlaybackManager.removeFromQueue(
                context = MainActivity.instance.applicationContext,
                mediaImpl = mediaImpl
            )
            showSnackBar(
                scope = scope,
                snackBarHostState = snackBarHostState,
                message = context.getString(
                    R.string.remove_from_queue_success,
                    getMediaTitle(mediaImpl = mediaImpl)
                ),
            )
        } catch (e: Throwable) {
            _logger?.warning(e.message)
            showErrorSnackBar(
                scope = scope,
                snackBarHostState = snackBarHostState,
                action = {
                    removeFromQueue(
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
            PlaybackManager.addNext(
                context = MainActivity.instance.applicationContext,
                mediaImpl = mediaImpl
            )
            showSnackBar(
                scope = scope,
                snackBarHostState = snackBarHostState,
                message = mediaImpl.title + ' ' + context.getString(R.string.play_next_success)
            )
        } catch (e: Throwable) {
            _logger?.warning(e.message)
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
        return PlaybackManager.isMusicInQueue(
            context = MainActivity.instance.applicationContext,
            music = music
        )
    }

    fun start(mediaToPlay: Music? = null, reset: Boolean = false) {
        PlaybackManager.start(
            context = MainActivity.instance.applicationContext,
            musicToPlay = mediaToPlay,
            reset = reset
        )
    }

    fun updateCurrentPosition(log: Boolean = true) {
        PlaybackManager.updateCurrentPosition(
            context = MainActivity.instance.applicationContext,
            log = log
        )
    }

    fun release() {
        _logger?.info("Release")
        PlaybackManager.release()
        onCleared()
    }

    fun stop() {
        _logger?.info("Stop")
        PlaybackManager.stop()
    }

    /**
     * Set a timer for [minutes] minutes.
     *
     * @param scope
     * @param snackBarHostState
     * @param minutes the number of minutes as [Int]
     */
    fun setTimer(
        scope: CoroutineScope,
        snackBarHostState: SnackbarHostState,
        hours: Int,
        minutes: Int,
        seconds: Int
    ) {
        if (hours <= 0 && minutes <= 0 && seconds <= 0) return
        val context: Context = MainActivity.instance.applicationContext
        val timer = Timer(
            function = {
                PlaybackManager.pause(context = context)
                _uiState.update { currentState: PlaybackUiState ->
                    currentState.copy(timer = null, timerRemainingTime = 0L)
                }
                showSnackBar(
                    scope = scope,
                    snackBarHostState = snackBarHostState,
                    message = context.getString(R.string.pause_media_timer_snackbar)
                )
            },
            hours = hours,
            minutes = minutes,
            seconds = seconds
        )
        try {
            _uiState.value.timer?.cancel()
            _uiState.update { currentState: PlaybackUiState ->
                currentState.copy(
                    timer = timer,
                    timerRemainingTime = timer.getRemainingTime()
                )
            }
        } catch (e: Throwable) {
            _logger?.severe(e.message)
            showErrorSnackBar(
                scope = scope,
                snackBarHostState = snackBarHostState,
                action = {
                    this.setTimer(
                        scope = scope,
                        snackBarHostState = snackBarHostState,
                        hours = hours,
                        minutes = minutes,
                        seconds = seconds
                    )
                }
            )
        }
    }

    fun cancelTimer(scope: CoroutineScope, snackBarHostState: SnackbarHostState) {
        try {
            _uiState.value.timer?.cancel()
            _uiState.update { currentState: PlaybackUiState ->
                currentState.copy(timer = null, timerRemainingTime = 0L)
            }
            showSnackBar(
                scope = scope,
                snackBarHostState = snackBarHostState,
                message = MainActivity.instance.getString(R.string.timer_cancelled_snackbar_content)
            )
        } catch (e: Throwable) {
            _logger?.severe(e.message)
            showErrorSnackBar(
                scope = scope,
                snackBarHostState = snackBarHostState,
                action = {
                    this.cancelTimer(scope = scope, snackBarHostState = snackBarHostState)
                }
            )
        }
    }

    fun isTimerRunning() = uiState.value.timer != null

    fun forward(scope: CoroutineScope, snackBarHostState: SnackbarHostState) {
        try {
            PlaybackManager.forward(context = MainActivity.instance.applicationContext)
        } catch (e: Exception) {
            _logger?.severe(e.message)
            showErrorSnackBar(
                scope = scope,
                snackBarHostState = snackBarHostState,
                action = {
                    this.forward(
                        scope = scope,
                        snackBarHostState = snackBarHostState
                    )
                }
            )
        }
    }

    fun rewind(scope: CoroutineScope, snackBarHostState: SnackbarHostState) {
        try {
            PlaybackManager.rewind(context = MainActivity.instance.applicationContext)
        } catch (e: Exception) {
            _logger?.severe(e.message)
            showErrorSnackBar(
                scope = scope,
                snackBarHostState = snackBarHostState,
                action = {
                    this.rewind(
                        scope = scope,
                        snackBarHostState = snackBarHostState,
                    )
                }
            )
        }
    }

    fun updateForward(scope: CoroutineScope, snackBarHostState: SnackbarHostState, seconds: Int) {
        try {
            runBlocking {
                SettingsManager.updateForwardMs(
                    context = MainActivity.instance.applicationContext,
                    seconds = seconds
                )
            }
        } catch (_: Exception) {
            showErrorSnackBar(
                scope = scope,
                snackBarHostState = snackBarHostState,
                action = {
                    this.updateForward(
                        scope = scope,
                        snackBarHostState = snackBarHostState,
                        seconds = seconds
                    )
                }
            )
        }
    }

    fun updateRewind(scope: CoroutineScope, snackBarHostState: SnackbarHostState, seconds: Int) {
        try {
            runBlocking {
                SettingsManager.updateRewindMs(
                    context = MainActivity.instance.applicationContext,
                    seconds = seconds
                )
            }
        } catch (_: Exception) {
            showErrorSnackBar(
                scope = scope,
                snackBarHostState = snackBarHostState,
                action = {
                    this.updateForward(
                        scope = scope,
                        snackBarHostState = snackBarHostState,
                        seconds = seconds
                    )
                }
            )
        }
    }

    fun refreshRemainingTime() {
        val timer: Timer? = _uiState.value.timer
        if (timer != null) {
            _uiState.update { currentState: PlaybackUiState ->
                currentState.copy(timerRemainingTime = timer.getRemainingTime())
            }
        }
    }

    fun moveUp(customAction: CustomActions) {
        //TODO use the new error handler system (implementation later)
        CoroutineScope(Dispatchers.IO).launch {
            SettingsManager.moveUp(
                context = MainActivity.instance.applicationContext,
                customAction = customAction
            )
        }
    }

    fun moveDown(customAction: CustomActions) {
        //TODO use the new error handler system (implementation later)
        CoroutineScope(Dispatchers.IO).launch {
            SettingsManager.moveDown(
                context = MainActivity.instance.applicationContext,
                customAction = customAction
            )
        }
    }
}