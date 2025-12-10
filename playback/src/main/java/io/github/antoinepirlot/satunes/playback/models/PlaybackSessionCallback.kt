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

package io.github.antoinepirlot.satunes.playback.models

import android.os.Bundle
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSession.ConnectionResult
import androidx.media3.session.MediaSession.ConnectionResult.AcceptedResultBuilder
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionError
import androidx.media3.session.SessionResult
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import io.github.antoinepirlot.android.utils.logger.Logger
import io.github.antoinepirlot.satunes.playback.services.PlaybackController

/**
 * @author Antoine Pirlot on 20/07/2024
 */

@UnstableApi
object PlaybackSessionCallback : MediaSession.Callback {

    private val _logger: Logger? = Logger.getLogger()
    internal val SHUFFLE_COMMAND: SessionCommand = SessionCommand("SHUFFLE_COMMAND", Bundle.EMPTY)
    internal val REPEAT_COMMAND: SessionCommand = SessionCommand("REPEAT_COMMAND", Bundle.EMPTY)

    override fun onConnect(
        session: MediaSession,
        controller: MediaSession.ControllerInfo
    ): ConnectionResult {
        val sessionCommands = ConnectionResult.DEFAULT_SESSION_COMMANDS.buildUpon()
            .add(SHUFFLE_COMMAND)
            .add(REPEAT_COMMAND)
            .build()

        return AcceptedResultBuilder(session)
            .setAvailableSessionCommands(sessionCommands)
            .build()
    }

    override fun onCustomCommand(
        session: MediaSession,
        controller: MediaSession.ControllerInfo,
        customCommand: SessionCommand,
        args: Bundle
    ): ListenableFuture<SessionResult> {
        return when (customCommand.customAction) {
            SHUFFLE_COMMAND.customAction -> shuffleCommand()
            REPEAT_COMMAND.customAction -> repeatCommand()
            else -> super.onCustomCommand(session, controller, customCommand, args)
        }
    }

    private fun shuffleCommand(): ListenableFuture<SessionResult> {
        _logger?.info("Shuffle from notification")
        return try {
            val playbackController: PlaybackController = PlaybackController.getInstance()
            playbackController.switchShuffleMode()
            Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
        } catch (e: Throwable) {
            _logger?.severe(e.message)
            Futures.immediateFuture(SessionResult(SessionError.ERROR_INVALID_STATE))
        }
    }

    private fun repeatCommand(): ListenableFuture<SessionResult> {
        _logger?.info("Repeat from notification")
        return try {
            val playbackController: PlaybackController = PlaybackController.getInstance()
            playbackController.switchRepeatMode()
            Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
        } catch (e: Throwable) {
            _logger?.severe(e.message)
            Futures.immediateFuture(SessionResult(SessionError.ERROR_INVALID_STATE))
        }

    }

    override fun onPlaybackResumption(
        mediaSession: MediaSession,
        controller: MediaSession.ControllerInfo
    ): ListenableFuture<MediaSession.MediaItemsWithStartPosition> {
        _logger?.info("onPlaybackResumption called")
        return super.onPlaybackResumption(mediaSession, controller)
    }
}