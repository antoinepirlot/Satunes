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

package io.github.antoinepirlot.satunes.database.services.settings

import android.content.Context
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import io.github.antoinepirlot.satunes.database.services.settings.SettingsManager.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * Settings related to UI of Satunes
 * @author Antoine Pirlot 03/02/2025
 */
internal object SatunesSettings {

    // DEFAULT VALUES

    private const val DEFAULT_WHATS_NEW_SEEN: Boolean = false
    private const val DEFAULT_WHATS_NEW_VERSION_SEEN: String = ""
    private const val DEFAULT_LOGS_ACTIVATION: Boolean = true

    // KEYS

    private val WHATS_NEW_SEEN_KEY = booleanPreferencesKey("whats_new_seen")
    private val WHATS_NEW_VERSION_SEEN_KEY = stringPreferencesKey("whats_new_version_seen")
    private val LOGS_ACTIVATION_KEY = booleanPreferencesKey("logs_activation")

    // VARIABLES

    var whatsNewSeen: Boolean = DEFAULT_WHATS_NEW_SEEN
        private set
    private var whatsNewVersionSeen: String = DEFAULT_WHATS_NEW_VERSION_SEEN
    var logsActivation: Boolean = DEFAULT_LOGS_ACTIVATION
        private set

    suspend fun loadSettings(context: Context) {
        context.dataStore.data.map { preferences: Preferences ->
            this.whatsNewSeen = preferences[WHATS_NEW_SEEN_KEY] ?: DEFAULT_WHATS_NEW_SEEN
            this.whatsNewVersionSeen =
                preferences[WHATS_NEW_VERSION_SEEN_KEY] ?: DEFAULT_WHATS_NEW_VERSION_SEEN
            this.logsActivation = preferences[LOGS_ACTIVATION_KEY] ?: DEFAULT_LOGS_ACTIVATION
            if (this.whatsNewSeen) {
                val packageManager = context.packageManager
                val packageInfo = packageManager.getPackageInfo(context.packageName, 0)
                val versionName = 'v' + packageInfo.versionName!!
                if (this.whatsNewVersionSeen != versionName) {
                    this@SatunesSettings.unSeeWhatsNew(context = context)
                }
            }
        }.first() //Without .first() settings are not loaded correctly
    }

    suspend fun seeWhatsNew(context: Context) {
        context.dataStore.edit { preferences: MutablePreferences ->
            this.whatsNewSeen = true
            preferences[WHATS_NEW_SEEN_KEY] = true
            val packageManager = context.packageManager
            val packageInfo = packageManager.getPackageInfo(context.packageName, 0)
            val versionName = 'v' + packageInfo.versionName!!
            preferences[WHATS_NEW_VERSION_SEEN_KEY] = versionName
            whatsNewVersionSeen = versionName
        }
    }

    suspend fun unSeeWhatsNew(context: Context) {
        context.dataStore.edit { preferences: MutablePreferences ->
            this.whatsNewSeen = false
            preferences[WHATS_NEW_SEEN_KEY] = this.whatsNewSeen
        }
    }

    suspend fun switchLogsActivation(context: Context) {
        context.dataStore.edit { preferences: MutablePreferences ->
            this.logsActivation = !this.logsActivation
            preferences[LOGS_ACTIVATION_KEY] = this.logsActivation
        }
    }

    suspend fun reset(context: Context) {
        context.dataStore.edit { preferences: MutablePreferences ->
            this.logsActivation = DEFAULT_LOGS_ACTIVATION
            preferences[LOGS_ACTIVATION_KEY] = this.logsActivation
            this.whatsNewSeen = DEFAULT_WHATS_NEW_SEEN
            preferences[WHATS_NEW_SEEN_KEY] = this.whatsNewSeen
            this.whatsNewVersionSeen = DEFAULT_WHATS_NEW_VERSION_SEEN
            preferences[WHATS_NEW_VERSION_SEEN_KEY] = this.whatsNewVersionSeen
        }
    }
}