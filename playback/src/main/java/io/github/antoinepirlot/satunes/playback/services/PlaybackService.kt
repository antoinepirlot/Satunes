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
 *  **** INFORMATION ABOUT THE AUTHOR *****
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
import android.os.Environment
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.TrackSelectionParameters
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import io.github.antoinepirlot.satunes.database.services.settings.SettingsManager
import io.github.antoinepirlot.satunes.playback.models.PlaybackSessionCallback
import io.github.antoinepirlot.satunes.utils.logger.SatunesLogger

/**
 * @author Antoine Pirlot on 31/01/24
 */

class PlaybackService : MediaSessionService() {

    companion object {
        var mediaSession: MediaSession? = null
    }

    private lateinit var _logger: SatunesLogger
    private lateinit var _exoPlayer: ExoPlayer

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()
        SatunesLogger.DOCUMENTS_PATH =
            applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)!!.path
        _logger = SatunesLogger.getLogger()

        _exoPlayer = ExoPlayer.Builder(applicationContext)
            .setHandleAudioBecomingNoisy(SettingsManager.pauseIfNoisyChecked) // Pause when bluetooth or headset disconnect
            .setAudioAttributes(
                AudioAttributes.DEFAULT,
                SettingsManager.pauseIfAnotherPlayback
            )
            .setWakeMode(C.WAKE_MODE_LOCAL)
            .build()

        // Add Audio Offload only if the user want it
        val audioOffloadPreferences = TrackSelectionParameters.AudioOffloadPreferences.Builder()
            .setAudioOffloadMode(TrackSelectionParameters.AudioOffloadPreferences.AUDIO_OFFLOAD_MODE_ENABLED)
            .setIsGaplessSupportRequired(true)
            .build()

        if (SettingsManager.audioOffloadChecked) {
            _exoPlayer.trackSelectionParameters = _exoPlayer.trackSelectionParameters
                .buildUpon()
                .setAudioOffloadPreferences(audioOffloadPreferences)
                .build()
        }

        mediaSession = MediaSession.Builder(applicationContext, _exoPlayer)
            .setCallback(PlaybackSessionCallback)
            .build()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        if (
            !SettingsManager.playbackWhenClosedChecked
            || !PlaybackManager.isInitialized.value
            || !PlaybackManager.isPlaying.value
        ) {
            PlaybackManager.release()
            stopSelf()
        }
    }

    override fun onDestroy() {
        if (
            !SettingsManager.playbackWhenClosedChecked
            || !PlaybackManager.isInitialized.value
            || !PlaybackManager.isPlaying.value
        ) {
            mediaSession?.run {
                player.release()
                release()
                mediaSession = null
            }
            super.onDestroy()
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun toString(): String {
        return "PlaybackService with ref \"${super.toString()}\""
    }
}