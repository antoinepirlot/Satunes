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
import android.os.Environment
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
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
        var playbackController: PlaybackController? = null
    }

    private lateinit var logger: SatunesLogger

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()
        SatunesLogger.DOCUMENTS_PATH =
            applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)!!.path
        logger = SatunesLogger.getLogger()

        val exoPlayer = ExoPlayer.Builder(this)
            .setHandleAudioBecomingNoisy(SettingsManager.pauseIfNoisyChecked.value) // Pause when bluetooth or headset disconnect
            .setAudioAttributes(
                AudioAttributes.DEFAULT,
                SettingsManager.pauseIfAnotherPlayback.value
            )
            .build()

        // Add Audio Offload only if the user want it
        val audioOffloadPreferences = TrackSelectionParameters.AudioOffloadPreferences.Builder()
            .setAudioOffloadMode(TrackSelectionParameters.AudioOffloadPreferences.AUDIO_OFFLOAD_MODE_ENABLED)
            .setIsGaplessSupportRequired(true)
            .build()

        if (SettingsManager.audioOffloadChecked.value) {
            exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters
                .buildUpon()
                .setAudioOffloadPreferences(audioOffloadPreferences)
                .build()
        }

        mediaSession = MediaSession.Builder(this, exoPlayer)
            .setCallback(PlaybackSessionCallback)
            .build()

        try {
            playbackController =
                PlaybackController.getInstance() // Called from init instance (session)
        } catch (e: Throwable) {
            logger.warning("Error while getting playback controller. Shutting down $this")
            stopSelf()
        }
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        if (
            !SettingsManager.playbackWhenClosedChecked.value ||
            playbackController == null ||
            !playbackController!!.isPlaying.value
        ) {
            playbackController?.release()
            logger.info("Shutting down $this")
            stopSelf()
        }
    }

    override fun onDestroy() {
        logger.info("Destroying $this")
        if (
            !SettingsManager.playbackWhenClosedChecked.value ||
            playbackController == null ||
            !playbackController!!.isPlaying.value
        ) {
            mediaSession?.run {
                player.release()
                release()
                mediaSession = null
            }
            super.onDestroy()
            logger.info("$this is technically destroyed")
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun toString(): String {
        return "PlaybackService with ref \"${super.toString()}\""
    }
}