/*
 * This file is part of Satunes.
 *
 * Satunes is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * Satunes is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License along with Satunes.
 *
 * If not, see <https://www.gnu.org/licenses/>.
 *
 * **** INFORMATION ABOUT THE AUTHOR *****
 * The author of this file is Antoine Pirlot, the owner of this project.
 * You find this original project on Codeberg.
 *
 * My Codeberg link is: https://codeberg.org/antoinepirlot
 * This current project's link is: https://codeberg.org/antoinepirlot/Satunes
 */

package io.github.antoinepirlot.satunes.data.states

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import io.github.antoinepirlot.satunes.MainActivity
import io.github.antoinepirlot.satunes.data.viewmodels.utils.isAudioAllowed
import io.github.antoinepirlot.satunes.database.models.BarSpeed
import io.github.antoinepirlot.satunes.database.models.media.Media
import io.github.antoinepirlot.satunes.database.models.media.MediaImpl
import io.github.antoinepirlot.satunes.database.services.settings.SettingsManager
import io.github.antoinepirlot.satunes.models.SatunesModes

/**
 * @author Antoine Pirlot on 19/07/2024
 */
@SuppressLint("NewApi")
data class SatunesUiState(
    val whatsNewSeen: Boolean = SettingsManager.whatsNewSeen,
    val includeExcludeSeen: Boolean = SettingsManager.includeExcludeSeen,
    val extraButtons: (@Composable () -> Unit)? = null,
    /**
     * The current [MediaImpl] associated to the view of single media.
     */
    val currentMedia: Media? = null,
    /**
     * Use this in UiSate and ViewModel as it is a particular value. It could change but most of the time it won't change
     */
    val isAudioAllowed: Boolean = isAudioAllowed(context = MainActivity.instance.applicationContext),
    val playbackWhenClosedChecked: Boolean = SettingsManager.playbackWhenClosedChecked,
    val pauseIfNoisyChecked: Boolean = SettingsManager.pauseIfNoisyChecked,
    val pauseIfAnotherPlayback: Boolean = SettingsManager.pauseIfAnotherPlayback,
    val shuffleMode: Boolean = SettingsManager.shuffleMode,
    val repeatMode: Int = SettingsManager.repeatMode,
    val audioOffloadChecked: Boolean = SettingsManager.audioOffloadChecked,
    val barSpeed: BarSpeed = SettingsManager.barSpeed,
    /**
     * This setting is true if the compilation's music has to be added to compilation's artist's music list
     */
    val compilationMusic: Boolean = SettingsManager.compilationMusic,
    val artistReplacement: Boolean = SettingsManager.artistReplacement,
    val showSortDialog: Boolean = false,
    val showMediaSelectionDialog: Boolean = false,
    val isMusicTitleDisplayName: Boolean = SettingsManager.isMusicTitleDisplayName,
    val mode: SatunesModes = SatunesModes.OFFLINE,
)
