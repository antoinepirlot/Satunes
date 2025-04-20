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

package io.github.antoinepirlot.satunes.car.playback

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.CustomAction
import android.support.v4.media.session.PlaybackStateCompat.STATE_PAUSED
import android.support.v4.media.session.PlaybackStateCompat.STATE_PLAYING
import androidx.core.graphics.drawable.toBitmap
import androidx.media.utils.MediaConstants
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import io.github.antoinepirlot.satunes.car.playback.SatunesCarCallBack.ACTIONS_ON_PAUSE
import io.github.antoinepirlot.satunes.car.playback.SatunesCarCallBack.ACTIONS_ON_PLAY
import io.github.antoinepirlot.satunes.car.playback.SatunesCarCallBack.ACTION_LIKE
import io.github.antoinepirlot.satunes.car.playback.SatunesCarCallBack.ACTION_REPEAT
import io.github.antoinepirlot.satunes.car.playback.SatunesCarCallBack.ACTION_SHUFFLE
import io.github.antoinepirlot.satunes.database.models.Music
import io.github.antoinepirlot.satunes.icons.R
import io.github.antoinepirlot.satunes.playback.models.PlaybackListener
import io.github.antoinepirlot.satunes.playback.services.PlaybackManager

/**
 * @author Antoine Pirlot on 23/03/2024
 */
internal object SatunesPlaybackListener : PlaybackListener() {
    override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)

        updateMediaPlaying() //Keep it prevent first media not showing when only opening via AA
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

    internal fun updateMediaPlaying() {
        val context: Context = SatunesCarMusicService.instance.applicationContext
        val musicPlaying: Music = PlaybackManager.musicPlaying.value ?: return
        var artwork: Bitmap? = musicPlaying.getAlbumArtwork(context = context)
        if (artwork == null) {
            artwork = context.getDrawable(R.mipmap.empty_album_artwork_foreground)?.toBitmap()
        }
        val metaData: MediaMetadataCompat = MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, musicPlaying.id.toString())
            .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, musicPlaying.title)
            .putString(
                MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE,
                musicPlaying.artist.title
            )
            .putBitmap(
                MediaMetadataCompat.METADATA_KEY_ALBUM_ART,
                artwork
            )
            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, musicPlaying.duration)
            .build()
        SatunesCarMusicService.session.setMetadata(metaData)
    }

    /**
     * Update the session playback.
     *
     * @param state the state of playback.
     * @param actions to run if clicked on button in playback screen.
     */
    internal fun updatePlaybackState(state: Int, actions: Long) {
        val musicPlaying: Music = PlaybackManager.musicPlaying.value ?: return
        val currentPosition: Long = PlaybackManager.getCurrentPosition(
            context = SatunesCarMusicService.instance.applicationContext
        )
        val extras = Bundle()
        extras.putString(
            MediaConstants.PLAYBACK_STATE_EXTRAS_KEY_MEDIA_ID,
            musicPlaying.id.toString()
        )
        val shuffleAction = CustomAction.Builder(
            ACTION_SHUFFLE,
            "Shuffle Mode",
            if (PlaybackManager.isShuffle.value) R.drawable.shuffle_on else R.drawable.shuffle_off
        ).build()
        val repeatAction = CustomAction.Builder(
            ACTION_REPEAT,
            "Repeat Mode",
            when (PlaybackManager.repeatMode.intValue) {
                Player.REPEAT_MODE_ALL -> R.drawable.repeat_all
                Player.REPEAT_MODE_ONE -> R.drawable.repeat_one_on
                else -> R.drawable.repeat_off
            }
        ).build()
        val likeAction = CustomAction.Builder(
            ACTION_LIKE, "Like",
            if (musicPlaying.liked.value) R.drawable.favorite else R.drawable.unfavorite
        ).build()
        val playbackState: PlaybackStateCompat = PlaybackStateCompat.Builder()
            .addCustomAction(likeAction)
            .addCustomAction(shuffleAction)
            .addCustomAction(repeatAction)
            .setState(state, currentPosition, 1F)
            .setActions(actions)
            .setActiveQueueItemId(musicPlaying.id)
            .setExtras(extras)
            .build()
        SatunesCarMusicService.session.setPlaybackState(playbackState)
    }
}