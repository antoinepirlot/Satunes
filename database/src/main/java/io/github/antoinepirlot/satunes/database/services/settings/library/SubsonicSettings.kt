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

package io.github.antoinepirlot.satunes.database.services.settings.library

import android.content.Context
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import io.github.antoinepirlot.satunes.database.services.settings.SettingsManager.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * @author Antoine Pirlot 03/09/2025
 */
internal object SubsonicSettings {
    private const val HASH_ALGORITHM: String = "MD5"
    private const val DEFAULT_SUBSONIC_URL: String = ""
    private const val DEFAULT_SUBSONIC_USERNAME: String = ""
    private const val DEFAULT_SUBSONIC_PASSWORD: String = ""

    private val SUBSONIC_URL_KEY: Preferences.Key<String> = stringPreferencesKey("subsonic_url")
    private val SUBSONIC_USERNAME_KEY: Preferences.Key<String> =
        stringPreferencesKey("subsonic_username")
    private val SUBSONIC_PASSWORD_KEY: Preferences.Key<String> =
        stringPreferencesKey("subsonic_password")

    var subsonicUrl: String = DEFAULT_SUBSONIC_URL
        private set
    var subsonicUsername: String = DEFAULT_SUBSONIC_USERNAME
        private set
    var subsonicPassword: String = DEFAULT_SUBSONIC_PASSWORD
        private set

    suspend fun loadSettings(context: Context) {
        context.dataStore.data.map { preferences: Preferences ->
            this.subsonicUrl = preferences[SUBSONIC_URL_KEY] ?: DEFAULT_SUBSONIC_URL
            this.subsonicUsername = preferences[SUBSONIC_USERNAME_KEY] ?: DEFAULT_SUBSONIC_USERNAME
            this.subsonicPassword = preferences[SUBSONIC_PASSWORD_KEY] ?: DEFAULT_SUBSONIC_PASSWORD
        }.first() //Without .first() settings are not loaded correctly
    }

    suspend fun updateSubsonicUrl(context: Context, url: String) {
        context.dataStore.edit { preferences: MutablePreferences ->
            preferences[SUBSONIC_URL_KEY] = url
            this.subsonicUrl = url
        }
    }

    suspend fun updateSubsonicUsername(context: Context, username: String) {
        context.dataStore.edit { preferences: MutablePreferences ->
            preferences[SUBSONIC_USERNAME_KEY] = username
            this.subsonicUsername = username
        }
    }

    /**
     * Get normal password and transform it to MD5
     */
    suspend fun updateSubsonicPassword(context: Context, password: String) {
        context.dataStore.edit { preferences: MutablePreferences ->
            preferences[SUBSONIC_PASSWORD_KEY] = password
            this.subsonicPassword = password
        }
    }

    suspend fun resetAll(context: Context) {
        this.updateSubsonicUrl(context = context, url = DEFAULT_SUBSONIC_URL)
        this.updateSubsonicUsername(context = context, username = DEFAULT_SUBSONIC_USERNAME)
        this.updateSubsonicPassword(context = context, password = DEFAULT_SUBSONIC_PASSWORD)
    }
}