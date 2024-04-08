/*
 * This file is part of MP3 Player.
 *
 * MP3 Player is free software: you can redistribute it and/or modify it under
 *  the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * MP3 Player is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with MP3 Player.
 * If not, see <https://www.gnu.org/licenses/>.
 *
 * **** INFORMATIONS ABOUT THE AUTHOR *****
 * The author of this file is Antoine Pirlot, the owner of this project.
 * You find this original project on github.
 *
 * My github link is: https://github.com/antoinepirlot
 * This current project's link is: https://github.com/antoinepirlot/MP3-Player
 *
 * You can contact me via my email: pirlot.antoine@outlook.com
 * PS: I don't answer quickly.
 */

package earth.mp3player.car.playback

import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.STATE_PAUSED
import android.support.v4.media.session.PlaybackStateCompat.STATE_PLAYING
import androidx.media.utils.MediaConstants
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import earth.mp3player.car.playback.MP3PlayerCarCallBack.ACTIONS_ON_PAUSE
import earth.mp3player.car.playback.MP3PlayerCarCallBack.ACTIONS_ON_PLAY
import earth.mp3player.database.models.Music
import earth.mp3player.playback.services.playback.PlaybackController
import earth.mp3player.playback.services.playback.PlaybackListener

/**
 * @author Antoine Pirlot on 23/03/2024
 */
object MP3CarPlaybackListener : PlaybackListener() {
    override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)

        if (isPlaying) {
            updatePlaybackState(state = STATE_PLAYING, actions = ACTIONS_ON_PLAY)
        } else {
            updatePlaybackState(state = STATE_PAUSED, actions = ACTIONS_ON_PAUSE)
        }
    }

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        super.onMediaItemTransition(mediaItem, reason)

        updateMediaPlaying()
        updatePlaybackState(state = STATE_PLAYING, actions = ACTIONS_ON_PLAY)
    }

    override fun onEvents(player: Player, events: Player.Events) {
        super.onEvents(player, events)
        if (events.contains(Player.EVENT_PLAYER_ERROR)) {
            // No a good practice but i don't understand why music pause sometimes in android auto
            // After switching from queue and playing next automatically.
            val playbackController: PlaybackController = PlaybackController.getInstance()
            playbackController.play()
        }
    }

    internal fun updateMediaPlaying() {
        val playbackController: PlaybackController = PlaybackController.getInstance()
        if (!playbackController.isLoaded.value) {
            return
        }
        val musicPlaying: Music = playbackController.musicPlaying.value!!
        val metaData: MediaMetadataCompat = MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, musicPlaying.id.toString())
            .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, musicPlaying.title)
            .putString(
                MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE,
                musicPlaying.artist?.title
            )
            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, musicPlaying.duration)
            .build()
        MP3PlayerCarMusicService.session.setMetadata(metaData)
    }

    /**
     * Update the session playback.
     *
     * @param state the state of playback.
     * @param actions to run if clicked on button in playback screen.
     */
    internal fun updatePlaybackState(state: Int, actions: Long) {
        val playbackController: PlaybackController = PlaybackController.getInstance()
        if (!playbackController.isLoaded.value) {
            return
        }
        val musicPlaying: Music = playbackController.musicPlaying.value!!
        val currentPosition: Long = playbackController.getCurrentPosition()
        val extras = Bundle()
        extras.putString(
            MediaConstants.PLAYBACK_STATE_EXTRAS_KEY_MEDIA_ID,
            musicPlaying.id.toString()
        )

        val playbackState: PlaybackStateCompat = PlaybackStateCompat.Builder()
            .setState(state, currentPosition, 1F)
            .setActions(actions)
            .setActiveQueueItemId(musicPlaying.id)
            .setExtras(extras)
            .build()
        MP3PlayerCarMusicService.session.setPlaybackState(playbackState)
    }
}