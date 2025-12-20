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

package io.github.antoinepirlot.satunes.database.services.settings.search

import android.content.Context
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import io.github.antoinepirlot.satunes.database.models.NavBarSection
import io.github.antoinepirlot.satunes.database.services.settings.SettingsManager.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * @author Antoine Pirlot 03/02/2025
 */
internal object SearchSettings {
    // DEFAULT VALUES

    private const val DEFAULT_MUSICS_FILTER: Boolean = true
    private const val DEFAULT_ARTISTS_FILTER: Boolean = false
    private const val DEFAULT_ALBUMS_FILTER: Boolean = false
    private const val DEFAULT_GENRES_FILTER: Boolean = false
    private const val DEFAULT_FOLDERS_FILTER: Boolean = false
    private const val DEFAULT_PLAYLISTS_FILTER: Boolean = false

    // KEYS

    private val MUSICS_FILTER_KEY: Preferences.Key<Boolean> =
        booleanPreferencesKey("musics_filter")
    private val ARTISTS_FILTER_KEY: Preferences.Key<Boolean> =
        booleanPreferencesKey("artists_filter")
    private val ALBUMS_FILTER_KEY: Preferences.Key<Boolean> =
        booleanPreferencesKey("albums_filter")
    private val GENRES_FILTER_KEY: Preferences.Key<Boolean> =
        booleanPreferencesKey("genres_filter")
    private val FOLDERS_FILTER_KEY: Preferences.Key<Boolean> =
        booleanPreferencesKey("folders_filter")
    private val PLAYLISTS_FILTER_KEY: Preferences.Key<Boolean> =
        booleanPreferencesKey("playlists_filter")

    // VARIABLES

    var foldersFilter: Boolean = DEFAULT_FOLDERS_FILTER
        private set
    var artistsFilter: Boolean = DEFAULT_ARTISTS_FILTER
        private set
    var albumsFilter: Boolean = DEFAULT_ALBUMS_FILTER
        private set
    var genresFilter: Boolean = DEFAULT_GENRES_FILTER
        private set
    var playlistsFilter: Boolean = DEFAULT_PLAYLISTS_FILTER
        private set
    var musicsFilter: Boolean = DEFAULT_MUSICS_FILTER
        private set

    suspend fun loadSettings(context: Context) {
        context.dataStore.data.map { preferences: Preferences ->
            this.foldersFilter = preferences[FOLDERS_FILTER_KEY] ?: DEFAULT_FOLDERS_FILTER
            this.artistsFilter = preferences[ARTISTS_FILTER_KEY] ?: DEFAULT_ARTISTS_FILTER
            this.albumsFilter = preferences[ALBUMS_FILTER_KEY] ?: DEFAULT_ALBUMS_FILTER
            this.genresFilter = preferences[GENRES_FILTER_KEY] ?: DEFAULT_GENRES_FILTER
            this.playlistsFilter =
                preferences[PLAYLISTS_FILTER_KEY] ?: DEFAULT_PLAYLISTS_FILTER
            this.musicsFilter = preferences[MUSICS_FILTER_KEY] ?: DEFAULT_MUSICS_FILTER
        }.first() //Without .first() settings are not loaded correctly
    }

    suspend fun switchFilter(context: Context, filterSetting: NavBarSection) {
        when (filterSetting) {
            NavBarSection.MUSICS -> {
                context.dataStore.edit { preferences: MutablePreferences ->
                    this.musicsFilter = !this.musicsFilter
                    preferences[MUSICS_FILTER_KEY] = this.musicsFilter
                }
            }

            NavBarSection.ARTISTS -> {
                context.dataStore.edit { preferences: MutablePreferences ->
                    this.artistsFilter = !this.artistsFilter
                    preferences[ARTISTS_FILTER_KEY] = this.artistsFilter
                }
            }

            NavBarSection.ALBUMS -> {
                context.dataStore.edit { preferences: MutablePreferences ->
                    this.albumsFilter = !this.albumsFilter
                    preferences[ALBUMS_FILTER_KEY] = this.albumsFilter
                }
            }

            NavBarSection.GENRES -> {
                context.dataStore.edit { preferences: MutablePreferences ->
                    this.genresFilter = !this.genresFilter
                    preferences[GENRES_FILTER_KEY] = this.genresFilter
                }
            }

            NavBarSection.FOLDERS -> {
                context.dataStore.edit { preferences: MutablePreferences ->
                    this.foldersFilter = !this.foldersFilter
                    preferences[FOLDERS_FILTER_KEY] = this.foldersFilter
                }
            }

            NavBarSection.PLAYLISTS -> {
                context.dataStore.edit { preferences: MutablePreferences ->
                    this.playlistsFilter = !this.playlistsFilter
                    preferences[PLAYLISTS_FILTER_KEY] = this.playlistsFilter
                }
            }
        }
    }

    suspend fun resetDefaultSearchFiltersSettings(context: Context) {
        context.dataStore.edit { preferences: MutablePreferences ->
            this.musicsFilter = DEFAULT_MUSICS_FILTER
            this.albumsFilter = DEFAULT_ALBUMS_FILTER
            this.artistsFilter = DEFAULT_ARTISTS_FILTER
            this.genresFilter = DEFAULT_GENRES_FILTER
            this.foldersFilter = DEFAULT_FOLDERS_FILTER
            this.playlistsFilter = DEFAULT_PLAYLISTS_FILTER
            preferences[MUSICS_FILTER_KEY] = this.musicsFilter
            preferences[ALBUMS_FILTER_KEY] = this.albumsFilter
            preferences[ARTISTS_FILTER_KEY] = this.artistsFilter
            preferences[GENRES_FILTER_KEY] = this.genresFilter
            preferences[FOLDERS_FILTER_KEY] = this.foldersFilter
            preferences[PLAYLISTS_FILTER_KEY] = this.playlistsFilter
        }
    }

    suspend fun resetAll(context: Context) {
        this.resetDefaultSearchFiltersSettings(context = context)
    }
}