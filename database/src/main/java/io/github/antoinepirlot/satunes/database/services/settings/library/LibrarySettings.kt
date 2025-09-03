/*
 * This file is part of Satunes.
 * Satunes is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *  Satunes is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License along with Satunes.
 *  If not, see <https://www.gnu.org/licenses/>.
 *
 * ** INFORMATION ABOUT THE AUTHOR *****
 * The author of this file is Antoine Pirlot, the owner of this project.
 * You find this original project on Codeberg.
 *
 * My Codeberg link is: https://codeberg.org/antoinepirlot
 * This current project's link is: https://codeberg.org/antoinepirlot/Satunes
 */

package io.github.antoinepirlot.satunes.database.services.settings.library

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import io.github.antoinepirlot.satunes.database.models.FoldersSelection
import io.github.antoinepirlot.satunes.database.services.settings.SettingsManager.dataStore
import io.github.antoinepirlot.satunes.database.services.settings.library.LibrarySettings.SELECTED_PATHS_KEY
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * @author Antoine Pirlot 03/02/2025
 */
internal object LibrarySettings {

    // DEFAULT VALUES

    private val DEFAULT_FOLDERS_SELECTION_SELECTED: FoldersSelection = FoldersSelection.INCLUDE
    private const val DEFAULT_INCLUDE_PATH: String = "/0/Music/%"
    private const val DEFAULT_COMPILATION_MUSIC: Boolean = false
    private const val DEFAULT_ARTISTS_REPLACEMENT: Boolean = true
    private const val DEFAULT_SUBSONIC_URL: String = ""

    // KEYS

    private val FOLDERS_SELECTION_SELECTED_KEY: Preferences.Key<Int> =
        intPreferencesKey("folders_selection")

    @Deprecated("Replaced by 'INCLUDING_PATHS_KEY' and 'EXCLUDING_PATHS_KEY'")
    private val SELECTED_PATHS_KEY: Preferences.Key<Set<String>> =
        stringSetPreferencesKey("selected_paths_set")

    private val INCLUDING_PATHS_KEY: Preferences.Key<Set<String>> =
        stringSetPreferencesKey("including_paths_set")
    private val EXCLUDING_PATHS_KEY: Preferences.Key<Set<String>> =
        stringSetPreferencesKey("excluding_paths_set")

    private val COMPILATION_MUSIC_KEY: Preferences.Key<Boolean> =
        booleanPreferencesKey("compilation_music")
    private val ARTISTS_REPLACEMENT_KEY: Preferences.Key<Boolean> =
        booleanPreferencesKey("artist_replacement")
    private val SUBSONIC_URL_KEY: Preferences.Key<String> = stringPreferencesKey("subsonic_url")

    // VARIABLES

    @Deprecated("No more used as 'foldersPathsIncludingCollection' and 'foldersPathsExcludingCollection' do it.")
    var foldersSelectionSelected: FoldersSelection = DEFAULT_FOLDERS_SELECTION_SELECTED
        private set

    val foldersPathsIncludingCollection: Collection<String> =
        mutableStateListOf(DEFAULT_INCLUDE_PATH)
    val foldersPathsExcludingCollection: Collection<String> =
        mutableStateListOf() // By default there's no exclusion

    /**
     * This setting is true if the compilation's music has to be added to compilation's artist's music list
     */
    var compilationMusic: Boolean = DEFAULT_COMPILATION_MUSIC
        private set
    var artistReplacement: Boolean = DEFAULT_ARTISTS_REPLACEMENT
        private set

    var subsonicUrl: String = DEFAULT_SUBSONIC_URL
        private set

    suspend fun loadSettings(context: Context) {
        context.dataStore.data.map { preferences: Preferences ->
            if (preferences[this.SELECTED_PATHS_KEY] != null)
                this.transferPaths(context = context, preferences = preferences)
            this.loadIncludingPaths(preferences = preferences)
            this.loadExcludingPaths(preferences = preferences)

            this.compilationMusic = preferences[COMPILATION_MUSIC_KEY] ?: DEFAULT_COMPILATION_MUSIC

            this.artistReplacement =
                preferences[ARTISTS_REPLACEMENT_KEY] ?: DEFAULT_ARTISTS_REPLACEMENT
            this.subsonicUrl = preferences[SUBSONIC_URL_KEY] ?: DEFAULT_SUBSONIC_URL
        }.first() //Without .first() settings are not loaded correctly
    }

    private fun loadIncludingPaths(preferences: Preferences) {
        var paths: Collection<String> =
            preferences[INCLUDING_PATHS_KEY] ?: setOf(DEFAULT_INCLUDE_PATH)
        (this.foldersPathsIncludingCollection as MutableCollection<String>).clear()
        (this.foldersPathsIncludingCollection).addAll(paths)
    }

    private fun loadExcludingPaths(preferences: Preferences) {
        var paths: Collection<String> =
            preferences[EXCLUDING_PATHS_KEY] ?: setOf()
        (this.foldersPathsExcludingCollection as MutableCollection<String>).clear()
        (this.foldersPathsExcludingCollection).addAll(paths)
    }

    /**
     * Used only to transfer old data from [SELECTED_PATHS_KEY] to the new ones.
     * It will be run only once in app's life if the user used the app before the modification.
     */
    private suspend fun transferPaths(context: Context, preferences: Preferences) {
        foldersSelectionSelected =
            getFoldersSelection(preferences[FOLDERS_SELECTION_SELECTED_KEY])
        var tempList: Set<String> = setOf()
        context.dataStore.edit { preferences: MutablePreferences ->
            tempList = preferences[SELECTED_PATHS_KEY] ?: setOf()
            preferences.remove(this.SELECTED_PATHS_KEY)
        }
        if (tempList.isEmpty()) return
        if (this.foldersSelectionSelected == FoldersSelection.INCLUDE) {
            (this.foldersPathsIncludingCollection as MutableCollection<String>).clear()
            for (path: String in tempList) this.addPath(
                context = context,
                path,
                folderSelection = this.foldersSelectionSelected
            )
        } else if (this.foldersSelectionSelected == FoldersSelection.EXCLUDE) {
            (this.foldersPathsExcludingCollection as MutableCollection<String>).clear()
            for (path: String in tempList) this.addPath(
                context = context,
                path,
                folderSelection = this.foldersSelectionSelected
            )
        } else throw InternalError("Unexpected situation. The foldersSelectionSelected was not include or exclude.")

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

    /**
     * Add a path to include or exclude and memorize it in storage.
     *
     * @param context the app context.
     * @param path the selected path as string.
     * @param folderSelection the option selected on screen.
     */
    suspend fun addPath(context: Context, path: String, folderSelection: FoldersSelection) {
        val formattedPath: String = getFormattedPath(path = path)
        context.dataStore.edit { preferences: MutablePreferences ->
            if (folderSelection == FoldersSelection.INCLUDE) {
                (this.foldersPathsIncludingCollection as MutableCollection<String>).add(
                    formattedPath
                )
                preferences[INCLUDING_PATHS_KEY] = this.foldersPathsIncludingCollection.toSet()
            } else if (folderSelection == FoldersSelection.EXCLUDE) {
                (this.foldersPathsExcludingCollection as MutableCollection<String>).add(
                    formattedPath
                )
                preferences[EXCLUDING_PATHS_KEY] = this.foldersPathsExcludingCollection.toSet()
            } else throw IllegalArgumentException("folderSelection must be Include or Exclude.")
        }
    }

    /**
     * Remove the specified path (assume it's already formatted)
     *
     * @param context the [Context] fo the app
     * @param path as [String] that is formatted
     * @param folderSelection the option selected on screen.
     */
    suspend fun removePath(context: Context, path: String, folderSelection: FoldersSelection) {
        context.dataStore.edit { preferences: MutablePreferences ->
            if (folderSelection == FoldersSelection.INCLUDE) {
                (this.foldersPathsIncludingCollection as MutableCollection<String>).remove(path)
                preferences[INCLUDING_PATHS_KEY] = this.foldersPathsIncludingCollection.toSet()
            } else if (folderSelection == FoldersSelection.EXCLUDE) {
                (this.foldersPathsExcludingCollection as MutableCollection<String>).remove(path)
                preferences[EXCLUDING_PATHS_KEY] = this.foldersPathsExcludingCollection.toSet()
            } else throw IllegalArgumentException("folderSelection must be Include or Exclude")
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

    suspend fun updateSubsonicUrl(context: Context, url: String) {
        context.dataStore.edit { preferences: MutablePreferences ->
            preferences[SUBSONIC_URL_KEY] = url
            this.subsonicUrl = url
        }
    }

    suspend fun resetFoldersSettings(context: Context) {
        context.dataStore.edit { preferences: MutablePreferences ->
            (this.foldersPathsIncludingCollection as MutableCollection<String>).clear()
            (this.foldersPathsIncludingCollection)
                .add(DEFAULT_INCLUDE_PATH)
            preferences[INCLUDING_PATHS_KEY] = this.foldersPathsIncludingCollection.toSet()

            (this.foldersPathsExcludingCollection as MutableCollection<String>).clear()
            preferences[EXCLUDING_PATHS_KEY] = this.foldersPathsExcludingCollection.toSet()
        }
    }

    suspend fun resetLoadingLogicSettings(context: Context) {
        context.dataStore.edit { preferences: MutablePreferences ->
            this.compilationMusic = DEFAULT_COMPILATION_MUSIC
            this.artistReplacement = DEFAULT_ARTISTS_REPLACEMENT
            preferences[COMPILATION_MUSIC_KEY] = DEFAULT_COMPILATION_MUSIC
            preferences[ARTISTS_REPLACEMENT_KEY] = this.artistReplacement
        }
    }

    suspend fun resetSubsonic(context: Context) {
        this.updateSubsonicUrl(context = context, url = DEFAULT_SUBSONIC_URL)
    }

    suspend fun resetAll(context: Context) {
        this.resetFoldersSettings(context = context)
        this.resetLoadingLogicSettings(context = context)
        this.resetSubsonic(context = context)
    }
}