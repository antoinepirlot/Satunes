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

import android.content.Intent
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.TrackSelectionParameters.AudioOffloadPreferences
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import io.github.antoinepirlot.satunes.database.services.settings.SettingsManager
import kotlin.system.exitProcess

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
            .setHandleAudioBecomingNoisy(SettingsManager.pauseIfNoisyChecked.value) // Pause when bluetooth or headset disconnect
            .setAudioAttributes(AudioAttributes.DEFAULT, true)
            .build()

        //TODO try by remove setIsGaplessSupportRequired and see if it also make issues on playback
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
        super.onTaskRemoved(rootIntent)
        if (!SettingsManager.playbackWhenClosedChecked.value) {
            onDestroy()
        }
    }

    override fun onDestroy() {
        stopSelf()
        mediaSession?.run {
            PlaybackController.getInstance().release()
            player.stop()
            player.release()
            release()
            stopSelf()
            mediaSession = null
        }
        super.onDestroy()
        //Use exit process as sometimes, when closing app from multi task with playback when closed
        // is false, then the player is release but the UI is still in the old view, and causing issue
        // with playback. Best way I found at this time
        exitProcess(0)
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }
}