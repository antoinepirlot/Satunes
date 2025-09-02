/*
 * This file is part of Satunes.
 * Satunes is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *  Satunes is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License along with Satunes.
 *  If not, see <https://www.gnu.org/licenses/>.
 *
 * **** INFORMATION ABOUT THE AUTHOR *****
 * The author of this file is Antoine Pirlot, the owner of this project.
 * You find this original project on Codeberg.
 *
 * My Codeberg link is: https://codeberg.org/antoinepirlot
 * This current project's link is: https://codeberg.org/antoinepirlot/Satunes
 */

package io.github.antoinepirlot.satunes.playback.services

import android.content.Context
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import io.github.antoinepirlot.satunes.database.models.MediaImpl
import io.github.antoinepirlot.satunes.database.models.Music
import io.github.antoinepirlot.satunes.database.services.data.DataLoader
import io.github.antoinepirlot.satunes.database.services.data.DataManager
import io.github.antoinepirlot.satunes.database.services.settings.SettingsManager
import io.github.antoinepirlot.satunes.playback.models.PlaybackListener
import io.github.antoinepirlot.satunes.playback.models.Playlist
import io.github.antoinepirlot.satunes.playback.services.PlaybackController.Companion.DEFAULT_CURRENT_POSITION_PROGRESSION
import io.github.antoinepirlot.satunes.playback.services.PlaybackController.Companion.DEFAULT_HAS_NEXT
import io.github.antoinepirlot.satunes.playback.services.PlaybackController.Companion.DEFAULT_HAS_PREVIOUS
import io.github.antoinepirlot.satunes.playback.services.PlaybackController.Companion.DEFAULT_IS_ENDED
import io.github.antoinepirlot.satunes.playback.services.PlaybackController.Companion.DEFAULT_IS_LOADED
import io.github.antoinepirlot.satunes.playback.services.PlaybackController.Companion.DEFAULT_IS_LOADING
import io.github.antoinepirlot.satunes.playback.services.PlaybackController.Companion.DEFAULT_IS_PLAYING_VALUE
import io.github.antoinepirlot.satunes.playback.services.PlaybackController.Companion.DEFAULT_IS_SHUFFLE
import io.github.antoinepirlot.satunes.playback.services.PlaybackController.Companion.DEFAULT_MUSIC_PLAYING
import io.github.antoinepirlot.satunes.playback.services.PlaybackController.Companion.DEFAULT_REPEAT_MODE
import io.github.antoinepirlot.satunes.utils.logger.SatunesLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

/**
 * @author Antoine Pirlot on 10/08/2024
 */
object PlaybackManager {

    private val _logger: SatunesLogger? = SatunesLogger.getLogger()
    private var _playbackController: PlaybackController? = null

    internal var playlist: Playlist? = null

    private var listener: PlaybackListener? = null

    val isEnded: MutableState<Boolean> = mutableStateOf(DEFAULT_IS_ENDED)

    val isInitialized: MutableState<Boolean> = mutableStateOf(false)

    // Mutable var are used in ui, it needs to be recomposed
    // I use mutable to avoid using function with multiples params like to add listener
    var musicPlaying: MutableState<Music?> = mutableStateOf(DEFAULT_MUSIC_PLAYING)
        private set
    var isPlaying: MutableState<Boolean> = mutableStateOf(DEFAULT_IS_PLAYING_VALUE)
        private set
    var repeatMode: MutableIntState = mutableIntStateOf(DEFAULT_REPEAT_MODE)
        private set
    var isShuffle: MutableState<Boolean> = mutableStateOf(DEFAULT_IS_SHUFFLE)
        private set
    var hasNext: MutableState<Boolean> = mutableStateOf(DEFAULT_HAS_NEXT)
        private set
    var hasPrevious: MutableState<Boolean> = mutableStateOf(DEFAULT_HAS_PREVIOUS)
        private set
    var isLoading: MutableState<Boolean> = mutableStateOf(DEFAULT_IS_LOADING)
        private set
    var isLoaded: MutableState<Boolean> = mutableStateOf(DEFAULT_IS_LOADED)
        private set
    var currentPositionProgression: MutableFloatState =
        mutableFloatStateOf(DEFAULT_CURRENT_POSITION_PROGRESSION)
        private set

    private fun initPlayback(
        context: Context,
        listener: PlaybackListener? = this.listener,
        loadAllMusics: Boolean = false
    ) {
        _logger?.info("Init playback")
        this.listener = listener
        this._playbackController =
            PlaybackController.initInstance(
                context = context,
                listener = listener,
                loadAllMusics = loadAllMusics
            )
        reset()
    }

    fun playbackControllerExists(): Boolean =
        this._playbackController != null || PlaybackController.isInitialized()

    private fun initPlaybackWithAllMusics(
        context: Context,
        listener: PlaybackListener? = this.listener
    ) {
        _logger?.info("Init playback with all musics")
        if (!DataLoader.isLoaded.value && !DataLoader.isLoading.value) {
            DataLoader.resetAllData()
            runBlocking(Dispatchers.IO) {
                DataLoader.loadAllData(context = context)
            }
            this.initPlayback(context = context, listener = listener, loadAllMusics = true)
        } else {
            if (this.isLoading.value) return
            if (this.playlist != null) {
                this.initPlayback(context = context, listener = listener, loadAllMusics = false)
                this._playbackController!!.loadMusics(playlist = playlist!!)
            } else {
                this.initPlayback(context = context, listener = listener, loadAllMusics = true)
            }
        }
    }

    /**
     * Checks if the PlaybackController is initialized.
     * If that's not the case, it starts the process of creating new one.
     *
     * @param context the [Context] of the app
     * @param listener the [PlaybackListener] to use.
     * @param loadAllMusics is a [Boolean] value that is
     *          true means it will load all musics if the playback controller doesn't exists.
     *          false means it won't load all musics in the playback controller if it doesn't exist.
     * @param log a [Boolean] value that indicates to log in this function.
     */
    fun checkPlaybackController(
        context: Context,
        listener: PlaybackListener? = this.listener,
        loadAllMusics: Boolean = false,
        log: Boolean = true
    ) {
        if (log) _logger?.info("Check Playback Controller")
        if (playbackControllerExists()) return
        if (loadAllMusics) {
            this.initPlaybackWithAllMusics(
                context = context,
                listener = listener
            )
        } else {
            this.initPlayback(context = context, listener = listener)
        }
    }

    private fun reset() {
        _logger?.info("Reset")
        musicPlaying.value = _playbackController!!.musicPlaying
        isPlaying.value = _playbackController!!.isPlaying
        repeatMode.intValue = _playbackController!!.repeatMode
        isShuffle.value = _playbackController!!.isShuffle
        hasNext.value = _playbackController!!.hasNext
        hasPrevious.value = _playbackController!!.hasPrevious
        isLoading.value = _playbackController!!.isLoading
        isLoaded.value = _playbackController!!.isLoaded
        currentPositionProgression.floatValue = _playbackController!!.currentPositionProgression
    }

    fun start(context: Context, musicToPlay: Music? = null, reset: Boolean = false) {
        _logger?.info("Start")
        checkPlaybackController(context = context, loadAllMusics = true)
        if (reset) {
            this._playbackController!!.loadMusics(
                musics = DataManager.getMusicSet(),
                musicToPlay = musicToPlay
            )
        } else {
            this._playbackController!!.start(musicToPlay = musicToPlay)
        }
    }

    fun playPause(context: Context) {
        _logger?.info("Play pause")
        checkPlaybackController(context = context)
        this._playbackController!!.playPause()
    }

    fun play(context: Context) {
        _logger?.info("Play")
        checkPlaybackController(context = context, loadAllMusics = true)
        if (musicPlaying.value == null) {
            this._playbackController!!.start()
        } else {
            this._playbackController!!.play()
        }
    }

    fun pause(context: Context) {
        _logger?.info("Pause")
        checkPlaybackController(context = context)
        this._playbackController!!.pause()
    }

    fun getCurrentPosition(context: Context): Long {
        _logger?.info("Get current position")
        checkPlaybackController(context = context)
        return this._playbackController!!.getCurrentPosition()
    }

    fun getMusicPlayingIndexPosition(context: Context): Int {
        _logger?.info("Get music playing index position")
        checkPlaybackController(context = context)
        return this._playbackController!!.getMusicPlayingIndexPosition()
    }

    fun playNext(context: Context) {
        _logger?.info("Play next")
        checkPlaybackController(context = context)
        this._playbackController!!.playNext()
    }

    fun playPrevious(context: Context) {
        _logger?.info("Play previous")
        checkPlaybackController(context = context)
        if (this.playlist == null) {
            return
        }
        this._playbackController!!.playPrevious()
    }

    fun forward(context: Context) {
        _logger?.info("Forward")
        checkPlaybackController(context = context)
        this._playbackController!!.forward()
    }

    fun rewind(context: Context) {
        _logger?.info("Rewind")
        checkPlaybackController(context = context)
        this._playbackController!!.rewind()
    }

    fun seekTo(context: Context, positionMs: Long) {
        _logger?.info("Seek to with position ms")
        checkPlaybackController(context = context)
        this._playbackController!!.seekTo(positionMs = positionMs)
    }

    fun seekTo(context: Context, positionPercentage: Float) {
        _logger?.info("Seek to with position percentage")
        checkPlaybackController(context = context)
        this._playbackController!!.seekTo(positionPercentage = positionPercentage)
    }

    fun seekTo(context: Context, music: Music, positionMs: Long = 0) {
        _logger?.info("Seek to with music and position ms")
        checkPlaybackController(context = context)
        this._playbackController!!.seekTo(music = music, positionMs = positionMs)
    }

    fun seekTo(context: Context, musicId: Long, positionMs: Long = 0) {
        _logger?.info("Seek to with music id and position ms = $positionMs")
        checkPlaybackController(context = context)
        val music: Music = DataManager.getMusic(musicId)
        this.seekTo(context = context, music = music, positionMs = positionMs)
    }

    fun loadMusics(
        context: Context,
        musics: Collection<Music>,
        shuffleMode: Boolean = SettingsManager.shuffleMode,
        musicToPlay: Music? = null,
    ) {
        _logger?.info("Load musics")
        if (this.playlist?.hasPlaylistMusicCollection(musics = musics) == true && this.musicPlaying.value == musicToPlay) {
            _logger?.info("MusicSet is already loaded.")
            return
        }
        if (this.isLoading.value) return
        checkPlaybackController(context = context, loadAllMusics = false)
        this._playbackController!!.loadMusics(
            musics = musics,
            shuffleMode = shuffleMode,
            musicToPlay = musicToPlay
        )
    }

    fun addToQueue(context: Context, mediaImpl: MediaImpl) {
        _logger?.info("Add to queue with media impl")
        checkPlaybackController(context = context)
        this._playbackController!!.addToQueue(mediaImpl = mediaImpl)
    }

    fun removeFromQueue(context: Context, mediaImpl: MediaImpl) {
        _logger?.info("Remove from queue with media impl")
        checkPlaybackController(context = context)
        this._playbackController!!.removeFromQueue(mediaImpl = mediaImpl)
    }

    fun addNext(context: Context, mediaImpl: MediaImpl) {
        _logger?.info("Add next with media impl")
        checkPlaybackController(context = context)
        this._playbackController!!.addNext(mediaImpl = mediaImpl)
    }

    fun switchShuffleMode(context: Context) {
        _logger?.info("Switch Shuffle Mode")
        checkPlaybackController(context = context)
        this._playbackController!!.switchShuffleMode()
    }

    fun switchRepeatMode(context: Context) {
        _logger?.info("Switch repeat mode")
        checkPlaybackController(context = context)
        this._playbackController!!.switchRepeatMode()
    }

    fun stop() {
        _logger?.info("Stop")
        this._playbackController?.stop()
    }

    fun release() {
        _logger?.info("Release")
        this._playbackController?.release()
        this._playbackController = null
    }

    fun getPlaylist(context: Context): SnapshotStateList<Music> {
        _logger?.info("Get playlist")
        checkPlaybackController(context = context)
        return this._playbackController!!.getPlaylist()
    }

    fun isMusicInQueue(context: Context, music: Music): Boolean {
        _logger?.info("Is music in queue")
        checkPlaybackController(context = context)
        return this._playbackController!!.isMusicInQueue(music = music)
    }

    fun updateCurrentPosition(context: Context, log: Boolean = true) {
        if (log) _logger?.info("Update current position")
        checkPlaybackController(context = context, log = log)
        this._playbackController!!.updateCurrentPosition()
    }

    fun getNextMusic(context: Context): Music? {
        _logger?.info("Get next music")
        checkPlaybackController(context = context)
        return this._playbackController!!.getNextMusic()
    }
}