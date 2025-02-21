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

package io.github.antoinepirlot.satunes.models

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import io.github.antoinepirlot.satunes.data.viewmodels.PlaybackViewModel
import io.github.antoinepirlot.satunes.database.services.settings.SettingsManager
import io.github.antoinepirlot.satunes.utils.logger.SatunesLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * @author Antoine Pirlot on 02/05/2024
 */
internal object ProgressBarLifecycleCallbacks : DefaultLifecycleObserver {
    private val _logger: SatunesLogger? = SatunesLogger.getLogger()
    private var _updatingJob: Job? = null;
    var isUpdatingPosition: Boolean = false
    private var stopRefresh: Boolean = false
    private var resumed: Boolean = false // used to avoid refresh when widget is used (optimization)
    lateinit var playbackViewModel: PlaybackViewModel

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        resumed = true
        startUpdatingCurrentPosition()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        resumed = false
        stopRefresh = true
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        resumed = false
        stopRefresh = true
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        resumed = false
        stopRefresh = true
    }

    fun startUpdatingCurrentPosition() {
        if (!resumed) return
        stopRefresh = false
        refreshCurrentPosition()
    }

    /**
     * Launch a coroutine where the currentPositionProgression is updated every 1 second.
     * If this function is already running, just return by using isUpdatingPosition.
     */
    private fun refreshCurrentPosition() {
        if (isUpdatingPosition || playbackViewModel.musicPlaying == null) {
            return
        }
        isUpdatingPosition = true
        _logger?.info("Update current position")
        _updatingJob?.cancel()
        _updatingJob = CoroutineScope(Dispatchers.Main).launch {
            updateCurrentPosition()
            while (playbackViewModel.isPlaying && !stopRefresh) {
                updateCurrentPosition(log = false)
                val timeMillis: Long = (SettingsManager.barSpeed.speed * 1000f).toLong()
                delay(timeMillis) // Wait one second to avoid refreshing all the time
            }
            isUpdatingPosition = false
        }
    }

    fun updateCurrentPosition(log: Boolean = true) {
        if (playbackViewModel.isEnded) {
            // It means the music has reached the end of playlistDB and the music is finished
            return
        }
        playbackViewModel.updateCurrentPosition(log = log)
    }
}