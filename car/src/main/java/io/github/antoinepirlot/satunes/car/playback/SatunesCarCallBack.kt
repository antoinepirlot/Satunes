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

package io.github.antoinepirlot.satunes.car.playback

import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat.ACTION_PAUSE
import android.support.v4.media.session.PlaybackStateCompat.ACTION_PLAY
import android.support.v4.media.session.PlaybackStateCompat.ACTION_SEEK_TO
import android.support.v4.media.session.PlaybackStateCompat.ACTION_SKIP_TO_NEXT
import android.support.v4.media.session.PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
import android.support.v4.media.session.PlaybackStateCompat.STATE_PAUSED
import android.support.v4.media.session.PlaybackStateCompat.STATE_PLAYING
import io.github.antoinepirlot.satunes.car.pages.ScreenPages
import io.github.antoinepirlot.satunes.database.models.MediaImpl
import io.github.antoinepirlot.satunes.database.models.Music
import io.github.antoinepirlot.satunes.database.services.data.DataManager
import io.github.antoinepirlot.satunes.playback.services.PlaybackController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * @author Antoine Pirlot on 16/03/2024
 */
internal object SatunesCarCallBack : MediaSessionCompat.Callback() {
    internal const val ACTIONS_ON_PLAY: Long =
        ACTION_PAUSE or ACTION_SKIP_TO_NEXT or ACTION_SKIP_TO_PREVIOUS or ACTION_SEEK_TO
    internal const val ACTIONS_ON_PAUSE: Long =
        ACTION_PLAY or ACTION_SKIP_TO_NEXT or ACTION_SKIP_TO_PREVIOUS or ACTION_SEEK_TO

    internal const val ACTION_SHUFFLE = "ACTION_SHUFFLE"
    internal const val ACTION_REPEAT = "ACTION_REPEAT"

    private lateinit var _job: Job

    override fun onPlay() {
        val playbackController: PlaybackController = PlaybackController.getInstance()
        if (!playbackController.isLoaded.value) {
            return
        }
        playbackController.play()
    }

    override fun onPause() {
        val playbackController: PlaybackController = PlaybackController.getInstance()
        playbackController.pause()
    }

    override fun onSkipToQueueItem(queueId: Long) {
        val playbackController: PlaybackController = PlaybackController.getInstance()
        playbackController.seekTo(musicId = queueId)
    }

    override fun onSeekTo(position: Long) {
        val playbackController: PlaybackController = PlaybackController.getInstance()
        playbackController.seekTo(positionMs = position)
    }

    override fun onPlayFromMediaId(mediaId: String, extras: Bundle?) {
        RouteManager.setShuffleButtonSelected(selected = true)
        val shuffleMode: Boolean = RouteManager.isShuffleButtonSelected()
        loadMusic()
        RouteManager.setShuffleButtonSelected(selected = false)
        val playbackController: PlaybackController = PlaybackController.getInstance()
        var musicToPlay: Music? = null
        if (!shuffleMode) {
            musicToPlay = DataManager.getMusic(id = mediaId.toLong())
        }
        playbackController.start(musicToPlay = musicToPlay)
    }

    override fun onPlayFromSearch(query: String?, extras: Bundle?) {
        super.onPlayFromSearch(query, extras)
    }

    override fun onSkipToNext() {
        val playbackController: PlaybackController = PlaybackController.getInstance()
        playbackController.playNext()
    }

    override fun onSkipToPrevious() {
        val playbackController: PlaybackController = PlaybackController.getInstance()
        playbackController.playPrevious()
    }

    override fun onCustomAction(action: String?, extras: Bundle?) {
        super.onCustomAction(action, extras)
        when (action) {
            ACTION_SHUFFLE -> switchShuffleMode()
            ACTION_REPEAT -> switchRepeatMode()
        }
        //Update playback state from here as no listener function is called for this action.
        val playbackController: PlaybackController = PlaybackController.getInstance()
        val state: Int =
            if (playbackController.isPlaying.value) STATE_PLAYING else STATE_PAUSED
        val actions: Long =
            if (playbackController.isPlaying.value) ACTIONS_ON_PLAY else ACTIONS_ON_PAUSE
        SatunesPlaybackListener.updatePlaybackState(state = state, actions = actions)
    }

    private fun switchShuffleMode() {
        val playbackController: PlaybackController = PlaybackController.getInstance()
        playbackController.switchShuffleMode()
    }

    private fun switchRepeatMode() {
        val playbackController: PlaybackController = PlaybackController.getInstance()
        playbackController.switchRepeatMode()
    }

    /**
     * Load music from the last route deque route.
     */
    private fun loadMusic() {
        val selectedTab: ScreenPages = RouteManager.getSelectedTab()
        val playbackController: PlaybackController = PlaybackController.getInstance()
        var musicToPlay: Music? = null
        val selectedMediaImpl: MediaImpl? = RouteManager.getSelectedMediaImpl()
        var musicSet: Set<Music> = DataManager.getMusicSet()
        if (selectedMediaImpl != null) {
            musicSet = when (selectedTab) {
                ScreenPages.ALL_FOLDERS -> selectedMediaImpl.getMusicSet()
                ScreenPages.ALL_ALBUMS -> selectedMediaImpl.getMusicSet()
                ScreenPages.ALL_ARTISTS -> selectedMediaImpl.getMusicSet()
                ScreenPages.ALL_GENRES -> selectedMediaImpl.getMusicSet()
                ScreenPages.ALL_PLAYLISTS -> selectedMediaImpl.getMusicSet()
                ScreenPages.ALL_MUSICS -> DataManager.getMusicSet()
                else -> {
                    musicToPlay = selectedMediaImpl as Music
                    musicSet
                }
            }
        }
        playbackController.loadMusic(
            musicSet = musicSet,
            shuffleMode = RouteManager.isShuffleButtonSelected(),
            musicToPlay = musicToPlay
        )
        loadInQueue(musicSet = musicSet)
    }

    private fun loadInQueue(musicSet: Set<Music>) {
        if (this::_job.isInitialized) {
            _job.cancel()
        }
        _job = CoroutineScope(Dispatchers.IO).launch {
            SatunesCarMusicService.resetQueue()
            musicSet.forEach { music: Music ->
                SatunesCarMusicService.addToQueue(media = music)
            }
            SatunesCarMusicService.updateQueue()
        }
    }
}