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

package earth.satunes.playback.services

import android.content.Intent
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.Player
import androidx.media3.common.TrackSelectionParameters.AudioOffloadPreferences
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import earth.satunes.database.services.settings.SettingsManager

/**
 * @author Antoine Pirlot on 31/01/24
 */

class PlaybackService : MediaSessionService() {

    companion object {
        var mediaSession: MediaSession? = null
    }

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()

        val exoPlayer = ExoPlayer.Builder(this)
            .setHandleAudioBecomingNoisy(SettingsManager.pauseIfNoisy.value) // Pause when bluetooth or headset disconnect
            .setAudioAttributes(AudioAttributes.DEFAULT, true)
            .build()

        //TODO check if don't cause android auto issues
        val audioOffloadPreferences = AudioOffloadPreferences.Builder()
            .setAudioOffloadMode(AudioOffloadPreferences.AUDIO_OFFLOAD_MODE_ENABLED)
            .setIsGaplessSupportRequired(true)
            .build()

        exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters
            .buildUpon()
            .setAudioOffloadPreferences(audioOffloadPreferences)
            .build()

        mediaSession = MediaSession.Builder(this, exoPlayer).build()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        if (!SettingsManager.playbackWhenClosedChecked.value) {
            val player: Player = mediaSession!!.player
            player.stop()
            player.release()
            stopSelf()
            super.onTaskRemoved(rootIntent)
        }
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.stop()
            player.release()
            release()
            mediaSession = null
        }

        super.onDestroy()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }
}