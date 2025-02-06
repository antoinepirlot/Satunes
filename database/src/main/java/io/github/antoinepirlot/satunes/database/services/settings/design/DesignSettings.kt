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

package io.github.antoinepirlot.satunes.database.services.settings.design

import android.content.Context
import androidx.compose.runtime.MutableLongState
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import io.github.antoinepirlot.satunes.database.models.NavBarSection
import io.github.antoinepirlot.satunes.database.models.Playlist
import io.github.antoinepirlot.satunes.database.models.custom_action.CustomActions
import io.github.antoinepirlot.satunes.database.services.data.DataManager
import io.github.antoinepirlot.satunes.database.services.settings.SettingsManager.dataStore
import io.github.antoinepirlot.satunes.database.utils.getNavBarSection
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * @author Antoine Pirlot 03/02/2025
 */
internal object DesignSettings {
    // DEFAULT VALUES
    private const val DEFAULT_FOLDERS_NAVBAR: Boolean = true
    private const val DEFAULT_ARTISTS_NAVBAR: Boolean = true
    private const val DEFAULT_ALBUMS_NAVBAR: Boolean = true
    private const val DEFAULT_GENRE_NAVBAR: Boolean = true
    private const val DEFAULT_PLAYLIST_NAVBAR: Boolean = true
    internal val DEFAULT_DEFAULT_NAV_BAR_SECTION: NavBarSection = NavBarSection.MUSICS
    private const val DEFAULT_PLAYLIST_ID: Long = -1
    private const val DEFAULT_SHOW_FIRST_LETTER = true
    private val DEFAULT_CUSTOM_ACTIONS_ORDER: Collection<CustomActions> = setOf(
        CustomActions.LIKE,
        CustomActions.ADD_TO_PLAYLIST,
        CustomActions.SHARE,
        CustomActions.TIMER
    )

    // KEYS
    private val FOLDERS_NAVBAR_PREFERENCES_KEY: Preferences.Key<Boolean> =
        booleanPreferencesKey("folders_navbar")
    private val ARTISTS_NAVBAR_PREFERENCES_KEY: Preferences.Key<Boolean> =
        booleanPreferencesKey("artist_navbar")
    private val ALBUMS_NAVBAR_PREFERENCES_KEY: Preferences.Key<Boolean> =
        booleanPreferencesKey("albums_navbar")
    private val GENRES_NAVBAR_PREFERENCES_KEY: Preferences.Key<Boolean> =
        booleanPreferencesKey("genres_navbar")
    private val PLAYLISTS_NAVBAR_PREFERENCES_KEY: Preferences.Key<Boolean> =
        booleanPreferencesKey("playlists_navbar")
    private val DEFAULT_NAV_BAR_SECTION_KEY: Preferences.Key<Int> =
        intPreferencesKey("default_nav_bar_section")
    private val DEFAULT_PLAYLIST_ID_KEY: Preferences.Key<Long> =
        longPreferencesKey("default_playlist_id_key")
    private val SHOW_FIRST_LETTER_KEY: Preferences.Key<Boolean> =
        booleanPreferencesKey("show_first_letter")
    private val CUSTOM_ACTIONS_ORDER_KEY: Preferences.Key<String> =
        stringPreferencesKey("custom_actions_order")

    // VARIABLES
    var defaultNavBarSection: NavBarSection = DEFAULT_DEFAULT_NAV_BAR_SECTION
        private set
    var defaultPlaylistId: MutableLongState = mutableLongStateOf(DEFAULT_PLAYLIST_ID)
        private set
    var showFirstLetter: Boolean = DEFAULT_SHOW_FIRST_LETTER
        private set
    val customActionsOrder: MutableList<CustomActions> = mutableStateListOf()


    internal suspend fun loadSettings(context: Context) {
        context.dataStore.data.map { preferences: Preferences ->
            NavBarSection.FOLDERS.isEnabled.value =
                preferences[FOLDERS_NAVBAR_PREFERENCES_KEY] ?: DEFAULT_FOLDERS_NAVBAR
            NavBarSection.ARTISTS.isEnabled.value =
                preferences[ARTISTS_NAVBAR_PREFERENCES_KEY] ?: DEFAULT_ARTISTS_NAVBAR
            NavBarSection.ALBUMS.isEnabled.value =
                preferences[ALBUMS_NAVBAR_PREFERENCES_KEY] ?: DEFAULT_ALBUMS_NAVBAR
            NavBarSection.GENRES.isEnabled.value =
                preferences[GENRES_NAVBAR_PREFERENCES_KEY] ?: DEFAULT_GENRE_NAVBAR
            NavBarSection.PLAYLISTS.isEnabled.value =
                preferences[PLAYLISTS_NAVBAR_PREFERENCES_KEY] ?: DEFAULT_PLAYLIST_NAVBAR
            showFirstLetter = preferences[SHOW_FIRST_LETTER_KEY] ?: DEFAULT_SHOW_FIRST_LETTER


            defaultNavBarSection = getNavBarSection(preferences[DEFAULT_NAV_BAR_SECTION_KEY])
            defaultPlaylistId.longValue =
                preferences[DEFAULT_PLAYLIST_ID_KEY] ?: DEFAULT_PLAYLIST_ID

            if (preferences[CUSTOM_ACTIONS_ORDER_KEY] != null)
                this.customActionsOrder.addAll(Json.decodeFromString(preferences[CUSTOM_ACTIONS_ORDER_KEY]!!))
            else
                this.customActionsOrder.addAll(elements = DEFAULT_CUSTOM_ACTIONS_ORDER)
        }.first() //Without .first() settings are not loaded correctly
    }

    internal suspend fun switchNavBarSection(context: Context, navBarSection: NavBarSection) {
        when (navBarSection) {
            NavBarSection.FOLDERS -> {
                context.dataStore.edit { preferences: MutablePreferences ->
                    NavBarSection.FOLDERS.isEnabled.value = !NavBarSection.FOLDERS.isEnabled.value
                    preferences[FOLDERS_NAVBAR_PREFERENCES_KEY] =
                        NavBarSection.FOLDERS.isEnabled.value
                }
            }

            NavBarSection.ARTISTS -> {
                context.dataStore.edit { preferences: MutablePreferences ->
                    NavBarSection.ARTISTS.isEnabled.value = !NavBarSection.ARTISTS.isEnabled.value
                    preferences[ARTISTS_NAVBAR_PREFERENCES_KEY] =
                        NavBarSection.ARTISTS.isEnabled.value
                }
            }

            NavBarSection.ALBUMS -> {
                context.dataStore.edit { preferences: MutablePreferences ->
                    NavBarSection.ALBUMS.isEnabled.value = !NavBarSection.ALBUMS.isEnabled.value
                    preferences[ALBUMS_NAVBAR_PREFERENCES_KEY] =
                        NavBarSection.ALBUMS.isEnabled.value
                }
            }

            NavBarSection.GENRES -> {
                context.dataStore.edit { preferences: MutablePreferences ->
                    NavBarSection.GENRES.isEnabled.value = !NavBarSection.GENRES.isEnabled.value
                    preferences[GENRES_NAVBAR_PREFERENCES_KEY] =
                        NavBarSection.GENRES.isEnabled.value
                }
            }

            NavBarSection.PLAYLISTS -> {
                context.dataStore.edit { preferences: MutablePreferences ->
                    NavBarSection.PLAYLISTS.isEnabled.value =
                        !NavBarSection.PLAYLISTS.isEnabled.value
                    preferences[PLAYLISTS_NAVBAR_PREFERENCES_KEY] =
                        NavBarSection.PLAYLISTS.isEnabled.value
                }
            }

            NavBarSection.MUSICS -> { /* DO NOTHING */
            }
        }
    }

    suspend fun selectDefaultNavBarSection(context: Context, navBarSection: NavBarSection) {
        context.dataStore.edit { preferences: MutablePreferences ->
            this.defaultNavBarSection = navBarSection
            preferences[DEFAULT_NAV_BAR_SECTION_KEY] = this.defaultNavBarSection.id
        }
    }

    suspend fun resetNavigationBarSettings(context: Context) {
        context.dataStore.edit { preferences: MutablePreferences ->
            this.defaultNavBarSection = DEFAULT_DEFAULT_NAV_BAR_SECTION
            NavBarSection.enableAll()
            preferences[FOLDERS_NAVBAR_PREFERENCES_KEY] = DEFAULT_FOLDERS_NAVBAR
            preferences[ARTISTS_NAVBAR_PREFERENCES_KEY] = DEFAULT_ARTISTS_NAVBAR
            preferences[ALBUMS_NAVBAR_PREFERENCES_KEY] = DEFAULT_ALBUMS_NAVBAR
            preferences[GENRES_NAVBAR_PREFERENCES_KEY] = DEFAULT_GENRE_NAVBAR
            preferences[PLAYLISTS_NAVBAR_PREFERENCES_KEY] = DEFAULT_PLAYLIST_NAVBAR
            preferences[DEFAULT_NAV_BAR_SECTION_KEY] = DEFAULT_DEFAULT_NAV_BAR_SECTION.id
        }
    }

    suspend fun selectDefaultPlaylist(context: Context, playlist: Playlist?) {
        context.dataStore.edit { preferences: MutablePreferences ->
            this.defaultPlaylistId.longValue = playlist?.id ?: DEFAULT_PLAYLIST_ID
            preferences[DEFAULT_PLAYLIST_ID_KEY] = playlist?.id ?: DEFAULT_PLAYLIST_ID
        }
    }

    /**
     * Check if the default playlist is correct.
     * If the value stored is > 0 but the matching playlist is null. Then reset the value.
     */
    suspend fun checkDefaultPlaylistSetting(context: Context) {
        if (DataManager.getPlaylist(id = defaultPlaylistId.longValue) == null) {
            selectDefaultPlaylist(context = context, playlist = null)
            defaultPlaylistId.longValue = DEFAULT_PLAYLIST_ID
        }
    }

    suspend fun switchShowFirstLetter(context: Context) {
        context.dataStore.edit { preferences: MutablePreferences ->
            this.showFirstLetter = !this.showFirstLetter
            preferences[SHOW_FIRST_LETTER_KEY] = this.showFirstLetter
        }
    }

    suspend fun moveUp(context: Context, customAction: CustomActions) {
        context.dataStore.edit { preferences: MutablePreferences ->
            val newIndex: Int = this.customActionsOrder.indexOf(customAction) - 1
            if (newIndex < 0) return@edit //prevent crash if too fast
            this.customActionsOrder.remove(element = customAction)
            this.customActionsOrder.add(index = newIndex, element = customAction)
            preferences[CUSTOM_ACTIONS_ORDER_KEY] = Json.encodeToString(this.customActionsOrder)
        }
    }

    suspend fun moveDown(context: Context, customAction: CustomActions) {
        context.dataStore.edit { preferences: MutablePreferences ->
            val newIndex: Int = this.customActionsOrder.indexOf(customAction) + 1
            if (newIndex > this.customActionsOrder.size) return@edit //prevent crash if too fast
            this.customActionsOrder.remove(element = customAction)
            this.customActionsOrder.add(index = newIndex, element = customAction)
            preferences[CUSTOM_ACTIONS_ORDER_KEY] = Json.encodeToString(this.customActionsOrder)
        }
    }

    suspend fun resetListsSettings(context: Context) {
        context.dataStore.edit { preferences: MutablePreferences ->
            this.showFirstLetter = DEFAULT_SHOW_FIRST_LETTER
            preferences[SHOW_FIRST_LETTER_KEY] = this.showFirstLetter
        }
    }

    suspend fun resetCustomActions(context: Context) {
        context.dataStore.edit { preferences: MutablePreferences ->
            this.customActionsOrder.clear()
            this.customActionsOrder.addAll(elements = DEFAULT_CUSTOM_ACTIONS_ORDER)
            preferences[CUSTOM_ACTIONS_ORDER_KEY] = Json.encodeToString(this.customActionsOrder)
        }
    }

    suspend fun resetAll(context: Context) {
        this.resetListsSettings(context = context)
        this.resetNavigationBarSettings(context = context)
        this.resetCustomActions(context = context)
    }
}