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

package io.github.antoinepirlot.satunes.database.services.settings.library

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import io.github.antoinepirlot.satunes.database.models.FoldersSelection
import io.github.antoinepirlot.satunes.database.services.settings.SettingsManager.dataStore
import kotlinx.coroutines.flow.map

/**
 * @author Antoine Pirlot 03/02/2025
 */
internal object LibrarySettings {

    // DEFAULT VALUES

    private val DEFAULT_FOLDERS_SELECTION_SELECTED: FoldersSelection = FoldersSelection.INCLUDE
    private val DEFAULT_SELECTED_PATHS: Set<String> = setOf("/0/Music/%")
    private const val DEFAULT_COMPILATION_MUSIC: Boolean = false
    private const val DEFAULT_ARTISTS_REPLACEMENT: Boolean = true
    private const val DEFAULT_SHOW_FIRST_LETTER = true

    // KEYS

    private val FOLDERS_SELECTION_SELECTED_KEY: Preferences.Key<Int> =
        intPreferencesKey("folders_selection")
    private val SELECTED_PATHS_KEY: Preferences.Key<Set<String>> =
        stringSetPreferencesKey("selected_paths_set")
    private val COMPILATION_MUSIC_KEY: Preferences.Key<Boolean> =
        booleanPreferencesKey("compilation_music")
    private val ARTISTS_REPLACEMENT_KEY: Preferences.Key<Boolean> =
        booleanPreferencesKey("artist_replacement")
    private val SHOW_FIRST_LETTER_KEY: Preferences.Key<Boolean> =
        booleanPreferencesKey("show_first_letter")

    // VARIABLES

    var foldersSelectionSelected: FoldersSelection = DEFAULT_FOLDERS_SELECTION_SELECTED
        private set
    var foldersPathsSelectedSet: MutableState<Set<String>> = mutableStateOf(DEFAULT_SELECTED_PATHS)
        private set

    /**
     * This setting is true if the compilation's music has to be added to compilation's artist's music list
     */
    var compilationMusic: Boolean = DEFAULT_COMPILATION_MUSIC
        private set
    var artistReplacement: Boolean = DEFAULT_ARTISTS_REPLACEMENT
        private set
    var showFirstLetter: Boolean = DEFAULT_SHOW_FIRST_LETTER
        private set

    fun loadSettings(context: Context) {
        context.dataStore.data.map { preferences: Preferences ->
            foldersSelectionSelected =
                getFoldersSelection(preferences[FOLDERS_SELECTION_SELECTED_KEY])

            foldersPathsSelectedSet.value =
                preferences[SELECTED_PATHS_KEY] ?: DEFAULT_SELECTED_PATHS

            compilationMusic = preferences[COMPILATION_MUSIC_KEY] ?: DEFAULT_COMPILATION_MUSIC

            artistReplacement = preferences[ARTISTS_REPLACEMENT_KEY] ?: DEFAULT_ARTISTS_REPLACEMENT
            showFirstLetter = preferences[SHOW_FIRST_LETTER_KEY] ?: DEFAULT_SHOW_FIRST_LETTER
        }
    }

    private fun getFoldersSelection(id: Int?): FoldersSelection {
        if (id == null) {
            return DEFAULT_FOLDERS_SELECTION_SELECTED
        }
        // Warning, be sure the id is correct
        return when (id) {
            1 -> FoldersSelection.INCLUDE
            2 -> FoldersSelection.EXCLUDE

            else -> DEFAULT_FOLDERS_SELECTION_SELECTED
        }
    }

    suspend fun selectFoldersSelection(context: Context, foldersSelection: FoldersSelection) {
        context.dataStore.edit { preferences: MutablePreferences ->
            this.foldersSelectionSelected = foldersSelection
            preferences[FOLDERS_SELECTION_SELECTED_KEY] = this.foldersSelectionSelected.id
        }
    }

    /**
     * Add a path to the selected paths set and memorize it in storage.
     *
     * @param context the app context
     * @param path the selected path as string
     */
    suspend fun addPath(context: Context, path: String) {
        val formattedPath: String = getFormattedPath(path = path)
        context.dataStore.edit { preferences: MutablePreferences ->
            val newSet: MutableSet<String> = this.foldersPathsSelectedSet.value.toMutableSet()
            newSet.add(formattedPath)
            this.foldersPathsSelectedSet.value = newSet.toSet()
            preferences[SELECTED_PATHS_KEY] = this.foldersPathsSelectedSet.value
        }
    }

    suspend fun removePath(context: Context, path: String) {
        context.dataStore.edit { preferences: MutablePreferences ->
            val newSet: MutableSet<String> = this.foldersPathsSelectedSet.value.toMutableSet()
            newSet.remove(path)
            this.foldersPathsSelectedSet.value = newSet.toSet()
            preferences[SELECTED_PATHS_KEY] = this.foldersPathsSelectedSet.value
        }
    }

    private fun getFormattedPath(path: String): String {
        val formattedPath: String = Uri.decode(path)
        val splitList: List<String> = formattedPath.split(":")
        if (splitList.size == 1) return path
        var storage: String = splitList[0].split("/").last()
        if (storage == "primary") {
            storage = "0"
        }
        return '/' + storage + '/' + splitList[1] + "/%"
    }

    suspend fun switchCompilationMusic(context: Context) {
        context.dataStore.edit { preferences: MutablePreferences ->
            this.compilationMusic = !this.compilationMusic
            preferences[COMPILATION_MUSIC_KEY] = this.compilationMusic
        }
    }

    suspend fun switchArtistReplacement(context: Context) {
        context.dataStore.edit { preferences: MutablePreferences ->
            this.artistReplacement = !this.artistReplacement
            preferences[ARTISTS_REPLACEMENT_KEY] = this.artistReplacement
        }
    }

    suspend fun switchShowFirstLetter(context: Context) {
        context.dataStore.edit { preferences: MutablePreferences ->
            this.showFirstLetter = !this.showFirstLetter
            preferences[SHOW_FIRST_LETTER_KEY] = this.showFirstLetter
        }
    }

    suspend fun resetFoldersSettings(context: Context) {
        context.dataStore.edit { preferences: MutablePreferences ->
            this.foldersPathsSelectedSet.value = DEFAULT_SELECTED_PATHS
            preferences[SELECTED_PATHS_KEY] = this.foldersPathsSelectedSet.value
        }
    }

    suspend fun resetLoadingLogicSettings(context: Context) {
        context.dataStore.edit { preferences: MutablePreferences ->
            this.compilationMusic = DEFAULT_COMPILATION_MUSIC
            this.artistReplacement = DEFAULT_ARTISTS_REPLACEMENT
            this.showFirstLetter = DEFAULT_SHOW_FIRST_LETTER
            preferences[COMPILATION_MUSIC_KEY] = this.compilationMusic
            preferences[ARTISTS_REPLACEMENT_KEY] = this.artistReplacement
            preferences[SHOW_FIRST_LETTER_KEY] = this.showFirstLetter
        }
    }
}