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
 * You find this original project on github.
 *
 * My github link is: https://github.com/antoinepirlot
 * This current project's link is: https://github.com/antoinepirlot/Satunes
 *
 * PS: I don't answer quickly.
 */



package io.github.antoinepirlot.satunes.playback.models

import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import io.github.antoinepirlot.satunes.playback.services.PlaybackController
import io.github.antoinepirlot.satunes.utils.logger.SatunesLogger

/**
 * @author Antoine Pirlot on 23/03/2024
 */
open class PlaybackListener : Player.Listener {

    private val logger = SatunesLogger.getLogger()

    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)
        val playbackController: PlaybackController = PlaybackController.getInstance()

        if (playbackState == Player.STATE_ENDED) {
            playbackController.isEnded = true
            playbackController.currentPositionProgression = 1f
        }
    }

    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
        // Do nothing
    }

    override fun onRepeatModeChanged(repeatMode: Int) {
        super.onRepeatModeChanged(repeatMode)
        PlaybackController.getInstance().repeatMode = repeatMode
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)
        val playbackController: PlaybackController = PlaybackController.getInstance()

        playbackController.isPlaying = isPlaying
        playbackController.isEnded = PlaybackController.DEFAULT_IS_ENDED
    }

    /**
     * Prevents MediaController to shuffle music
     */
    @OptIn(UnstableApi::class)
    override fun onEvents(player: Player, events: Player.Events) {
        super.onEvents(player, events)

        if (events.contains(Player.EVENT_POSITION_DISCONTINUITY)) {
            val playbackController: PlaybackController = PlaybackController.getInstance()
            playbackController.updateCurrentPosition()
        }

        if (events.contains(Player.EVENT_SHUFFLE_MODE_ENABLED_CHANGED)) {
            //Stop making media controller shuffled
            //Shuffle is managed by PlaybackController not by media controller.
            //This line avoid mismatch with song on screen and the one playing
            player.shuffleModeEnabled = false
        }

        if (events.contains(Player.EVENT_PLAYER_ERROR)) {
            val playbackController: PlaybackController = PlaybackController.getInstance()
            val message = """
                An error happens with the player. 
                Here's the status of playback Controller:
                $playbackController
            """.trimIndent()
            logger.warning(message)
        }
    }

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        if (
            reason == Player.MEDIA_ITEM_TRANSITION_REASON_SEEK
            || reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO
            || reason == Player.MEDIA_ITEM_TRANSITION_REASON_PLAYLIST_CHANGED
        ) {
            super.onMediaItemTransition(mediaItem, reason)
            val playbackController: PlaybackController = PlaybackController.getInstance()
            playbackController.musicPlayingIndex =
                playbackController.mediaController.currentMediaItemIndex
            playbackController.musicPlaying =
                playbackController.playlist!!.musicList[playbackController.musicPlayingIndex]
            updateHasNext()
            updateHasPrevious()
            playbackController.mediaController.play()
        }
    }

    private fun updateHasPrevious() {
        val playbackController: PlaybackController = PlaybackController.getInstance()
        playbackController.hasPrevious =
            playbackController.musicPlaying != playbackController.playlist!!.musicList.last()
    }

    private fun updateHasNext() {
        val playbackController: PlaybackController = PlaybackController.getInstance()
        playbackController.hasNext =
            playbackController.musicPlaying != playbackController.playlist!!.musicList.last()
    }
}