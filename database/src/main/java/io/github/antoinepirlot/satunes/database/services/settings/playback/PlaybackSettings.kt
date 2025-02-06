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
 * You find this original project on github.
 *
 * My github link is: https://github.com/antoinepirlot
 * This current project's link is: https://github.com/antoinepirlot/Satunes
 *
 * PS: I don't answer quickly.
 */

package io.github.antoinepirlot.satunes.database.services.settings.playback

import android.content.Context
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.media3.common.Player
import io.github.antoinepirlot.satunes.database.models.BarSpeed
import io.github.antoinepirlot.satunes.database.services.settings.SettingsManager.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * @author Antoine Pirlot 03/02/2025
 */
internal object PlaybackSettings {
    // DEFAULT VALUES

    //App stop after removed app from multi-task if false
    private const val DEFAULT_PLAYBACK_WHEN_CLOSED_CHECKED = false
    private const val DEFAULT_PAUSE_IF_NOISY = true
    private val DEFAULT_BAR_SPEED_VALUE: BarSpeed = BarSpeed.NORMAL
    private const val DEFAULT_REPEAT_MODE: Int = Player.REPEAT_MODE_OFF
    private const val DEFAULT_SHUFFLE_MODE: Boolean = false
    private const val DEFAULT_PAUSE_IF_ANOTHER_PLAYBACK_CHECKED: Boolean = true
    private const val DEFAULT_AUDIO_OFFLOAD_CHECKED: Boolean = false
    private const val DEFAULT_FORWARD_MS: Long = 5000L
    private const val DEFAULT_REWIND_MS: Long = DEFAULT_FORWARD_MS

    // KEYS

    private val PLAYBACK_WHEN_CLOSED_CHECKED_PREFERENCES_KEY: Preferences.Key<Boolean> =
        booleanPreferencesKey("playback_when_closed_checked")
    private val PAUSE_IF_NOISY_PREFERENCES_KEY: Preferences.Key<Boolean> =
        booleanPreferencesKey("pause_if_noisy")
    private val BAR_SPEED_KEY: Preferences.Key<Float> =
        floatPreferencesKey("bar_speed")
    private val REPEAT_MODE_KEY: Preferences.Key<Int> =
        intPreferencesKey("repeat_mode")
    private val SHUFFLE_MODE_KEY: Preferences.Key<Boolean> =
        booleanPreferencesKey("shuffle_mode")
    private val PAUSE_IF_ANOTHER_PLAYBACK_KEY: Preferences.Key<Boolean> =
        booleanPreferencesKey("pause_if_another_playback")
    private val AUDIO_OFFLOAD_CHECKED_KEY: Preferences.Key<Boolean> =
        booleanPreferencesKey("audio_offload_checked")
    private val FORWARD_MS_KEY: Preferences.Key<Long> =
        longPreferencesKey("forward_ms")
    private val REWIND_MS_KEY: Preferences.Key<Long> =
        longPreferencesKey("rewind_ms")

    // VARIABLES

    var playbackWhenClosedChecked: Boolean = DEFAULT_PLAYBACK_WHEN_CLOSED_CHECKED
        private set
    var pauseIfNoisyChecked: Boolean = DEFAULT_PAUSE_IF_NOISY
        private set
    var barSpeed: BarSpeed = DEFAULT_BAR_SPEED_VALUE
        private set
    var repeatMode: Int = DEFAULT_REPEAT_MODE
        private set
    var shuffleMode: Boolean = DEFAULT_SHUFFLE_MODE
        private set
    var pauseIfAnotherPlayback: Boolean = DEFAULT_PAUSE_IF_ANOTHER_PLAYBACK_CHECKED
        private set
    var audioOffloadChecked: Boolean = DEFAULT_AUDIO_OFFLOAD_CHECKED
        private set
    var forwardMs: Long = DEFAULT_FORWARD_MS
        private set
    var rewindMs: Long = DEFAULT_REWIND_MS
        private set

    suspend fun loadSettings(context: Context) {
        context.dataStore.data.map { preferences: Preferences ->

            playbackWhenClosedChecked =
                preferences[PLAYBACK_WHEN_CLOSED_CHECKED_PREFERENCES_KEY]
                    ?: DEFAULT_PLAYBACK_WHEN_CLOSED_CHECKED

            pauseIfNoisyChecked =
                preferences[PAUSE_IF_NOISY_PREFERENCES_KEY] ?: DEFAULT_PAUSE_IF_NOISY

            barSpeed = getBarSpeed(preferences[BAR_SPEED_KEY])

            repeatMode = preferences[REPEAT_MODE_KEY] ?: DEFAULT_REPEAT_MODE

            shuffleMode =
                preferences[SHUFFLE_MODE_KEY] ?: DEFAULT_SHUFFLE_MODE

            pauseIfAnotherPlayback = preferences[PAUSE_IF_ANOTHER_PLAYBACK_KEY]
                ?: DEFAULT_PAUSE_IF_ANOTHER_PLAYBACK_CHECKED

            audioOffloadChecked =
                preferences[AUDIO_OFFLOAD_CHECKED_KEY] ?: DEFAULT_AUDIO_OFFLOAD_CHECKED

            forwardMs = preferences[FORWARD_MS_KEY] ?: DEFAULT_FORWARD_MS
            rewindMs = preferences[REWIND_MS_KEY] ?: DEFAULT_REWIND_MS

        }.first() //Without .first() settings are not loaded correctly
    }

    private fun getBarSpeed(speed: Float?): BarSpeed {
        return when (speed) {
            BarSpeed.REAL_TIME.speed -> BarSpeed.REAL_TIME
            BarSpeed.FAST.speed -> BarSpeed.FAST
            BarSpeed.NORMAL.speed -> BarSpeed.NORMAL
            BarSpeed.SLOW.speed -> BarSpeed.SLOW
            BarSpeed.VERY_SLOW.speed -> BarSpeed.VERY_SLOW
            else -> DEFAULT_BAR_SPEED_VALUE
        }
    }

    suspend fun switchPlaybackWhenClosedChecked(context: Context) {
        context.dataStore.edit { preferences: MutablePreferences ->
            playbackWhenClosedChecked = !playbackWhenClosedChecked
            preferences[PLAYBACK_WHEN_CLOSED_CHECKED_PREFERENCES_KEY] =
                playbackWhenClosedChecked
        }
    }

    suspend fun switchPauseIfNoisy(context: Context) {
        context.dataStore.edit { preferences: MutablePreferences ->
            pauseIfNoisyChecked = !pauseIfNoisyChecked
            preferences[PAUSE_IF_NOISY_PREFERENCES_KEY] = pauseIfNoisyChecked
        }
    }

    suspend fun updateBarSpeed(context: Context, newSpeedBar: BarSpeed) {
        context.dataStore.edit { preferences: MutablePreferences ->
            barSpeed = newSpeedBar
            preferences[BAR_SPEED_KEY] = barSpeed.speed
        }
    }

    suspend fun updateRepeatMode(context: Context, newValue: Int) {
        if (newValue !in listOf(
                Player.REPEAT_MODE_OFF,
                Player.REPEAT_MODE_ALL,
                Player.REPEAT_MODE_ONE
            )
        ) {
            throw IllegalArgumentException("Update repeat mode must be 0, 1 or 2. $newValue has been received.")
        }
        context.dataStore.edit { preferences: MutablePreferences ->
            repeatMode = newValue
            preferences[REPEAT_MODE_KEY] = repeatMode
        }
    }

    suspend fun setShuffleModeOn(context: Context) {
        context.dataStore.edit { preferences: MutablePreferences ->
            shuffleMode = true
            preferences[SHUFFLE_MODE_KEY] = true
        }
    }

    suspend fun setShuffleModeOff(context: Context) {
        context.dataStore.edit { preferences: MutablePreferences ->
            shuffleMode = false
            preferences[SHUFFLE_MODE_KEY] = false
        }
    }

    suspend fun switchPauseIfAnotherPlayback(context: Context) {
        context.dataStore.edit { preferences: MutablePreferences ->
            pauseIfAnotherPlayback = !pauseIfAnotherPlayback
            preferences[PAUSE_IF_ANOTHER_PLAYBACK_KEY] = pauseIfAnotherPlayback
        }
    }

    suspend fun switchAudioOffload(context: Context) {
        context.dataStore.edit { preferences: MutablePreferences ->
            audioOffloadChecked = !audioOffloadChecked
            preferences[AUDIO_OFFLOAD_CHECKED_KEY] = audioOffloadChecked
        }
    }

    suspend fun updateForwardMs(context: Context, seconds: Int) {
        context.dataStore.edit { preferences: MutablePreferences ->
            this.forwardMs = seconds.toLong() * 1000L
            preferences[FORWARD_MS_KEY] = this.forwardMs
        }
    }

    suspend fun updateRewindMs(context: Context, seconds: Int) {
        context.dataStore.edit { preferences: MutablePreferences ->
            this.rewindMs = seconds.toLong() * 1000L
            preferences[REWIND_MS_KEY] = this.rewindMs
        }
    }

    suspend fun resetBatterySettings(context: Context) {
        context.dataStore.edit { preferences: MutablePreferences ->
            this.audioOffloadChecked = DEFAULT_AUDIO_OFFLOAD_CHECKED
            preferences[AUDIO_OFFLOAD_CHECKED_KEY] = this.audioOffloadChecked
        }
    }

    suspend fun resetPlaybackBehaviorSettings(context: Context) {
        context.dataStore.edit { preferences: MutablePreferences ->
            this.playbackWhenClosedChecked = DEFAULT_PLAYBACK_WHEN_CLOSED_CHECKED
            this.pauseIfNoisyChecked = DEFAULT_PAUSE_IF_NOISY
            this.pauseIfAnotherPlayback = DEFAULT_PAUSE_IF_ANOTHER_PLAYBACK_CHECKED
            this.barSpeed = DEFAULT_BAR_SPEED_VALUE
            preferences[PLAYBACK_WHEN_CLOSED_CHECKED_PREFERENCES_KEY] =
                this.playbackWhenClosedChecked
            preferences[PAUSE_IF_NOISY_PREFERENCES_KEY] = this.pauseIfNoisyChecked
            preferences[PAUSE_IF_ANOTHER_PLAYBACK_KEY] = this.pauseIfAnotherPlayback
            preferences[BAR_SPEED_KEY] = this.barSpeed.speed
        }
    }

    suspend fun resetPlaybackModesSettings(context: Context) {
        context.dataStore.edit { preferences: MutablePreferences ->
            this.shuffleMode = DEFAULT_SHUFFLE_MODE
            this.repeatMode = DEFAULT_REPEAT_MODE
            this.forwardMs = DEFAULT_FORWARD_MS
            this.rewindMs = DEFAULT_REWIND_MS
            preferences[SHUFFLE_MODE_KEY] = this.shuffleMode
            preferences[REPEAT_MODE_KEY] = this.repeatMode
            preferences[FORWARD_MS_KEY] = this.forwardMs
            preferences[REWIND_MS_KEY] = this.rewindMs
        }
    }
}