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

import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import io.github.antoinepirlot.satunes.database.models.Folder
import io.github.antoinepirlot.satunes.database.models.MediaImpl
import io.github.antoinepirlot.satunes.database.models.Music
import io.github.antoinepirlot.satunes.database.services.settings.SettingsManager
import io.github.antoinepirlot.satunes.models.ProgressBarLifecycleCallbacks
import io.github.antoinepirlot.satunes.playback.services.PlaybackController
import java.util.SortedMap

/**
 * @author Antoine Pirlot on 19/07/2024
 */
class PlaybackViewModel : ViewModel() {
    private val _playbackController: PlaybackController = PlaybackController.getInstance()
    private val _isPlaying: MutableState<Boolean> = _playbackController.isPlaying
    private val _musicPlaying: MutableState<Music?> = _playbackController.musicPlaying
    private val _currentPositionProgression: MutableFloatState =
        _playbackController.currentPositionProgression
    private val _repeatMode: MutableIntState = _playbackController.repeatMode
    private val _isShuffle: MutableState<Boolean> = _playbackController.isShuffle
    private val _isLoaded: MutableState<Boolean> = _playbackController.isLoaded
    private val _isEnded: MutableState<Boolean> = _playbackController.isEnded

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
        shuffleMode: Boolean = SettingsManager.shuffleMode.value,
        musicToPlay: Music? = null
    ) {
        this._playbackController.loadMusicFromFolders(
            folders = folders,
            shuffleMode = shuffleMode,
            musicToPlay = musicToPlay
        )
    }

    fun loadMusicFromMedia(
        media: MediaImpl,
        shuffleMode: Boolean = SettingsManager.shuffleMode.value,
        musicToPlay: Music? = null
    ) {
        this._playbackController.loadMusicFromMedia(
            media = media,
            shuffleMode = shuffleMode,
            musicToPlay = musicToPlay
        )
    }

    fun loadMusic(
        musicSet: Set<Music>,
        shuffleMode: Boolean = SettingsManager.shuffleMode.value,
        musicToPlay: Music? = null,
    ) {
        this._playbackController.loadMusic(
            musicSet = musicSet,
            shuffleMode = shuffleMode,
            musicToPlay = musicToPlay
        )
    }

    fun loadMusicFromMedias(
        medias: Set<MediaImpl>,
        shuffleMode: Boolean = SettingsManager.shuffleMode.value,
        musicToPlay: Music? = null,
    ) {
        this._playbackController.loadMusicFromMedias(
            medias = medias,
            shuffleMode = shuffleMode,
            musicToPlay = musicToPlay
        )
    }

    fun loadMusicFromMedias(
        medias: SortedMap<MediaImpl, Any>,
        shuffleMode: Boolean = SettingsManager.shuffleMode.value,
        musicToPlay: Music? = null,
    ) {
        this._playbackController.loadMusicFromMedias(
            medias = medias,
            shuffleMode = shuffleMode,
            musicToPlay = musicToPlay
        )
    }

    fun loadMusicFromStringMediasMedia(
        medias: SortedMap<String, MediaImpl>,
        shuffleMode: Boolean = SettingsManager.shuffleMode.value,
        musicToPlay: Music? = null
    ) {
        this._playbackController.loadMusicFromStringMediasMedia(
            medias = medias,
            shuffleMode = shuffleMode,
            musicToPlay = musicToPlay
        )
    }

    fun getPlaylist(): List<Music> {
        return this._playbackController.getPlaylist()
    }

    fun seekTo(positionPercentage: Float) {
        this._playbackController.seekTo(positionPercentage = positionPercentage)
    }

    fun playNext() {
        this._playbackController.playNext()
    }

    fun playPause() {
        this._playbackController.playPause()
    }

    fun playPrevious() {
        this._playbackController.playPrevious()
    }

    fun switchRepeatMode() {
        this._playbackController.switchRepeatMode()
    }

    fun switchShuffleMode() {
        this._playbackController.switchShuffleMode()
    }

    fun getMusicPlayingIndexPosition(): Int {
        return this._playbackController.getMusicPlayingIndexPosition()
    }

    fun addToQueue(mediaImpl: MediaImpl) {
        this._playbackController.addToQueue(mediaImpl = mediaImpl)
    }

    fun addNext(mediaImpl: MediaImpl) {
        this._playbackController.addNext(mediaImpl = mediaImpl)
    }

    fun isMusicInQueue(music: Music): Boolean {
        return this._playbackController.isMusicInQueue(music = music)
    }

    fun start(mediaToPlay: Music? = null) {
        this._playbackController.start(musicToPlay = mediaToPlay)
    }

    fun updateCurrentPosition() {
        this._playbackController.updateCurrentPosition()
    }
}