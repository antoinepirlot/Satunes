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

import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi

/**
 * @author Antoine Pirlot on 23/03/2024
 */
open class PlaybackListener : Player.Listener {

    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)
        val playbackController: PlaybackController = PlaybackController.getInstance()

        if (playbackState == Player.STATE_ENDED) {
            playbackController.isEnded = !PlaybackController.DEFAULT_IS_ENDED
        }
    }

    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
        // Do nothing
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)
        val playbackController: PlaybackController = PlaybackController.getInstance()

        playbackController.isPlaying.value = isPlaying
        playbackController.isEnded = PlaybackController.DEFAULT_IS_ENDED
    }

    /**
     * Prevents MediaController to shuffle music
     */
    @OptIn(UnstableApi::class)
    override fun onEvents(player: Player, events: Player.Events) {
        super.onEvents(player, events)

        if (events.contains(Player.EVENT_SHUFFLE_MODE_ENABLED_CHANGED)) {
            //Stop making media controller shuffled
            //Shuffle is managed by PlaybackController not by media controller.
            //This line avoid mismatch with song on screen and the one playing
            player.shuffleModeEnabled = false
        }
    }

    override fun onPositionDiscontinuity(
        oldPosition: Player.PositionInfo,
        newPosition: Player.PositionInfo,
        reason: Int
    ) {
        super.onPositionDiscontinuity(oldPosition, newPosition, reason)
        val playbackController: PlaybackController = PlaybackController.getInstance()

        playbackController.currentPositionProgression.floatValue =
            if (playbackController.musicPlaying.value == null || playbackController.musicPlaying.value!!.duration == 0L) {
                0f
            } else {
                newPosition.positionMs.toFloat() / playbackController.musicPlaying.value!!.duration.toFloat()
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
            playbackController.musicPlaying.value =
                playbackController.playlist.musicList[playbackController.musicPlayingIndex]
            updateHasNext()
            updateHasPrevious()
            playbackController.mediaController.play()
        }
    }

    private fun updateHasPrevious() {
        val playbackController: PlaybackController = PlaybackController.getInstance()
        playbackController.hasPrevious.value =
            playbackController.musicPlaying.value != playbackController.playlist.musicList.last()
    }

    private fun updateHasNext() {
        val playbackController: PlaybackController = PlaybackController.getInstance()
        playbackController.hasNext.value =
            playbackController.musicPlaying.value != playbackController.playlist.musicList.last()
    }
}