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
import io.github.antoinepirlot.satunes.playback.services.PlaybackController.Companion.DEFAULT_IS_PLAYING_VALUE
import io.github.antoinepirlot.satunes.playback.services.PlaybackController.Companion.DEFAULT_IS_SHUFFLE
import io.github.antoinepirlot.satunes.playback.services.PlaybackController.Companion.DEFAULT_MUSIC_PLAYING
import io.github.antoinepirlot.satunes.playback.services.PlaybackController.Companion.DEFAULT_REPEAT_MODE
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

/**
 * @author Antoine Pirlot on 10/08/2024
 */
object PlaybackManager {

    private var _playbackController: PlaybackController? = null

    internal lateinit var playlist: Playlist

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
    var isLoaded: MutableState<Boolean> = mutableStateOf(DEFAULT_IS_LOADED)
        private set
    var currentPositionProgression: MutableFloatState =
        mutableFloatStateOf(DEFAULT_CURRENT_POSITION_PROGRESSION)
        private set

    fun initPlayback(context: Context, listener: PlaybackListener? = this.listener) {
        this.listener = listener
        this._playbackController =
            PlaybackController.initInstance(context = context, listener = listener)
        reset()
    }

    private fun playbackControllerNotExists(): Boolean = this._playbackController == null

    fun isConfigured(): Boolean = !this.playbackControllerNotExists()

    fun initPlaybackWithAllMusics(context: Context) {
        this.initPlayback(context = context)
        if (!DataLoader.isLoaded.value && !DataLoader.isLoading.value) {
            DataLoader.resetAllData()
            DataLoader.loadAllData(context = context)
        } else {
            if (this::playlist.isInitialized) {
                this._playbackController!!.loadMusics(playlist = playlist)
                return
            }
        }
        while (DataLoader.isLoading.value) {
            //Wait
            runBlocking {
                delay(50) //Wait (use delay to reduce cpu usage)
            }
        }
        this._playbackController!!.loadMusics(musicSet = DataManager.getMusicSet())
    }

    private fun checkPlaybackController(context: Context, loadAllMusic: Boolean = true) {
        if (playbackControllerNotExists()) {
            if (loadAllMusic) {
                this.initPlaybackWithAllMusics(context = context)
            } else {
                this.initPlayback(context = context)
            }
        }
    }

    private fun reset() {
        musicPlaying.value = _playbackController!!.musicPlaying
        isPlaying.value = _playbackController!!.isPlaying
        repeatMode.intValue = _playbackController!!.repeatMode
        isShuffle.value = _playbackController!!.isShuffle
        hasNext.value = _playbackController!!.hasNext
        hasPrevious.value = _playbackController!!.hasPrevious
        isLoaded.value = _playbackController!!.isLoaded
        currentPositionProgression.floatValue = _playbackController!!.currentPositionProgression
    }

    fun start(context: Context, musicToPlay: Music? = null) {
        checkPlaybackController(context = context)
        this._playbackController!!.start(musicToPlay = musicToPlay)
    }

    fun playPause(context: Context) {
        checkPlaybackController(context = context)
        this._playbackController!!.playPause()
    }

    fun play(context: Context) {
        checkPlaybackController(context = context)
        if (!this::playlist.isInitialized) {
            this.loadMusics(context = context, musicSet = DataManager.getMusicSet())
            this._playbackController!!.start()
        } else {
            this._playbackController!!.play()
        }
    }

    fun pause(context: Context) {
        checkPlaybackController(context = context)
        this._playbackController!!.pause()
    }

    fun getCurrentPosition(context: Context): Long {
        checkPlaybackController(context = context)
        return this._playbackController!!.getCurrentPosition()
    }

    fun getMusicPlayingIndexPosition(context: Context): Int {
        checkPlaybackController(context = context)
        return this._playbackController!!.getMusicPlayingIndexPosition()
    }

    fun playNext(context: Context) {
        checkPlaybackController(context = context)
        this._playbackController!!.playNext()
    }

    fun playPrevious(context: Context) {
        checkPlaybackController(context = context)
        if (!this::playlist.isInitialized) {
            return
        }
        this._playbackController!!.playPrevious()
    }

    fun seekTo(context: Context, positionMs: Long) {
        checkPlaybackController(context = context)
        this._playbackController!!.seekTo(positionMs = positionMs)
    }

    fun seekTo(context: Context, positionPercentage: Float) {
        checkPlaybackController(context = context)
        this._playbackController!!.seekTo(positionPercentage = positionPercentage)
    }

    fun seekTo(context: Context, music: Music, positionMs: Long = 0) {
        checkPlaybackController(context = context)
        this._playbackController!!.seekTo(music = music, positionMs = positionMs)
    }

    fun seekTo(context: Context, musicId: Long, positionMs: Long = 0) {
        checkPlaybackController(context = context)
        this._playbackController!!.seekTo(musicId = musicId, positionMs = positionMs)
    }

    fun seekTo(context: Context, musicIndex: Int, positionMs: Long = 0) {
        checkPlaybackController(context = context)
        this._playbackController!!.seekTo(musicIndex = musicIndex, positionMs = positionMs)
    }

    fun loadMusics(
        context: Context,
        musicSet: Set<Music>,
        shuffleMode: Boolean = SettingsManager.shuffleMode,
        musicToPlay: Music? = null,
    ) {
        checkPlaybackController(context = context, loadAllMusic = false)
        this._playbackController!!.loadMusics(
            musicSet = musicSet,
            shuffleMode = shuffleMode,
            musicToPlay = musicToPlay
        )
        this.playlist = this._playbackController!!.playlist
    }

    fun addToQueue(context: Context, mediaImplList: Collection<MediaImpl>) {
        checkPlaybackController(context = context)
        this._playbackController!!.addToQueue(mediaImplList = mediaImplList)
    }

    fun addToQueue(context: Context, mediaImpl: MediaImpl) {
        checkPlaybackController(context = context)
        this._playbackController!!.addToQueue(mediaImpl = mediaImpl)
    }

    fun removeFromQueue(context: Context, mediaImplList: Collection<MediaImpl>) {
        checkPlaybackController(context = context)
        this._playbackController!!.removeFromQueue(mediaImplList = mediaImplList)
    }

    fun removeFromQueue(context: Context, mediaImpl: MediaImpl) {
        checkPlaybackController(context = context)
        this._playbackController!!.removeFromQueue(mediaImpl = mediaImpl)
    }

    fun addNext(context: Context, mediaImpl: MediaImpl) {
        checkPlaybackController(context = context)
        this._playbackController!!.addNext(mediaImpl = mediaImpl)
    }

    fun switchShuffleMode(context: Context) {
        checkPlaybackController(context = context)
        this._playbackController!!.switchShuffleMode()
    }

    fun switchRepeatMode(context: Context) {
        checkPlaybackController(context = context)
        this._playbackController!!.switchRepeatMode()
    }

    fun stop() {
        this._playbackController?.stop()
    }

    fun release() {
        this._playbackController?.release()
        this._playbackController = null
    }

    fun getPlaylist(context: Context): SnapshotStateList<Music> {
        checkPlaybackController(context = context)
        return this._playbackController!!.getPlaylist()
    }

    fun isMusicInQueue(context: Context, music: Music): Boolean {
        checkPlaybackController(context = context)
        return this._playbackController!!.isMusicInQueue(music = music)
    }

    fun updateCurrentPosition(context: Context) {
        checkPlaybackController(context = context)
        this._playbackController!!.updateCurrentPosition()
    }
}