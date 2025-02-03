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

package io.github.antoinepirlot.satunes.database.services.settings

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import io.github.antoinepirlot.satunes.database.models.BarSpeed
import io.github.antoinepirlot.satunes.database.models.FoldersSelection
import io.github.antoinepirlot.satunes.database.models.NavBarSection
import io.github.antoinepirlot.satunes.database.services.data.DataLoader
import io.github.antoinepirlot.satunes.database.services.settings.navigation_bar.NavBarSettings
import io.github.antoinepirlot.satunes.database.services.settings.playback.PlaybackSettings
import io.github.antoinepirlot.satunes.database.services.settings.search.SearchSettings
import io.github.antoinepirlot.satunes.utils.logger.SatunesLogger
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * @author Antoine Pirlot on 02-03-24
 */

object SettingsManager {

    // DEFAULT VALUES

    private const val DEFAULT_WHATS_NEW_SEEN: Boolean = false
    private const val DEFAULT_WHATS_NEW_VERSION_SEEN: String = ""
    private val DEFAULT_FOLDERS_SELECTION_SELECTED: FoldersSelection = FoldersSelection.INCLUDE
    private val DEFAULT_SELECTED_PATHS: Set<String> = setOf("/0/Music/%")
    private const val DEFAULT_COMPILATION_MUSIC: Boolean = false
    private const val DEFAULT_ARTISTS_REPLACEMENT: Boolean = true
    private const val DEFAULT_SHOW_FIRST_LETTER = true

    // KEYS

    private val PREFERENCES_DATA_STORE = preferencesDataStore("settings")
    private val WHATS_NEW_SEEN_KEY = booleanPreferencesKey("whats_new_seen")
    private val WHATS_NEW_VERSION_SEEN_KEY = stringPreferencesKey("whats_new_version_seen")
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

    private val _logger = SatunesLogger.getLogger()
    internal val Context.dataStore: DataStore<Preferences> by PREFERENCES_DATA_STORE
    private var _isLoaded: Boolean = false

    // NavBarSettings
    val defaultNavBarSection: NavBarSection
        get() = NavBarSettings.defaultNavBarSection

    // Playback Settings
    val playbackWhenClosedChecked: Boolean
        get() = PlaybackSettings.playbackWhenClosedChecked
    val pauseIfNoisyChecked: Boolean
        get() = PlaybackSettings.pauseIfNoisyChecked
    val barSpeed: BarSpeed
        get() = PlaybackSettings.barSpeed
    val repeatMode: Int
        get() = PlaybackSettings.repeatMode
    val shuffleMode: Boolean
        get() = PlaybackSettings.shuffleMode
    val pauseIfAnotherPlayback: Boolean
        get() = PlaybackSettings.pauseIfAnotherPlayback
    val audioOffloadChecked: Boolean
        get() = PlaybackSettings.audioOffloadChecked
    val forwardMs: Long
        get() = PlaybackSettings.forwardMs
    val rewindMs: Long
        get() = PlaybackSettings.rewindMs

    var whatsNewSeen: Boolean = DEFAULT_WHATS_NEW_SEEN
        private set

    private var whatsNewVersionSeen: String = DEFAULT_WHATS_NEW_VERSION_SEEN

    // Search Settings
    val foldersFilter: Boolean
        get() = SearchSettings.foldersFilter
    val artistsFilter: Boolean
        get() = SearchSettings.artistsFilter
    val albumsFilter: Boolean
        get() = SearchSettings.albumsFilter
    val genresFilter: Boolean
        get() = SearchSettings.genresFilter
    val playlistsFilter: Boolean
        get() = SearchSettings.playlistsFilter
    val musicsFilter: Boolean
        get() = SearchSettings.musicsFilter

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

    suspend fun loadSettings(context: Context) {
        if (_isLoaded) {
            _logger?.info("Settings already loaded")
            return
        }
        context.dataStore.data.map { preferences: Preferences ->
            NavBarSettings.loadSettings(context = context)
            PlaybackSettings.loadSettings(context = context)
            loadFilters(context = context)

            foldersSelectionSelected =
                getFoldersSelection(preferences[FOLDERS_SELECTION_SELECTED_KEY])

            foldersPathsSelectedSet.value =
                preferences[SELECTED_PATHS_KEY] ?: DEFAULT_SELECTED_PATHS

            compilationMusic = preferences[COMPILATION_MUSIC_KEY] ?: DEFAULT_COMPILATION_MUSIC

            artistReplacement = preferences[ARTISTS_REPLACEMENT_KEY] ?: DEFAULT_ARTISTS_REPLACEMENT
            showFirstLetter = preferences[SHOW_FIRST_LETTER_KEY] ?: DEFAULT_SHOW_FIRST_LETTER

            DataLoader.loadFoldersPaths()

            loadWhatsNew(context = context, preferences = preferences)
        }.first()
        _isLoaded = true
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

    fun loadFilters(context: Context) {
        SearchSettings.loadSettings(context = context)
    }

    private suspend fun loadWhatsNew(context: Context, preferences: Preferences) {
        whatsNewSeen = preferences[WHATS_NEW_SEEN_KEY] ?: DEFAULT_WHATS_NEW_SEEN
        whatsNewVersionSeen =
            preferences[WHATS_NEW_VERSION_SEEN_KEY] ?: DEFAULT_WHATS_NEW_VERSION_SEEN
        if (whatsNewSeen) {
            val packageManager = context.packageManager
            val packageInfo = packageManager.getPackageInfo(context.packageName, 0)
            val versionName = 'v' + packageInfo.versionName!!
            if (whatsNewVersionSeen != versionName) {
                this.unSeeWhatsNew(context = context)
            }
        }
    }

    suspend fun switchNavBarSection(context: Context, navBarSection: NavBarSection) {
        NavBarSettings.switchNavBarSection(context = context, navBarSection = navBarSection)
    }

    suspend fun switchPlaybackWhenClosedChecked(context: Context) {
        PlaybackSettings.switchPlaybackWhenClosedChecked(context = context)
    }

    suspend fun switchPauseIfNoisy(context: Context) {
        PlaybackSettings.switchPauseIfNoisy(context = context)
    }

    suspend fun updateBarSpeed(context: Context, newSpeedBar: BarSpeed) {
        PlaybackSettings.updateBarSpeed(context = context, newSpeedBar = newSpeedBar)
    }

    suspend fun updateRepeatMode(context: Context, newValue: Int) {
        PlaybackSettings.updateRepeatMode(context = context, newValue = newValue)
    }

    suspend fun setShuffleModeOn(context: Context) {
        PlaybackSettings.setShuffleModeOn(context = context)
    }

    suspend fun setShuffleModeOff(context: Context) {
        PlaybackSettings.setShuffleModeOff(context = context)
    }

    suspend fun switchPauseIfAnotherPlayback(context: Context) {
        PlaybackSettings.switchPauseIfAnotherPlayback(context = context)
    }

    suspend fun switchAudioOffload(context: Context) {
        PlaybackSettings.switchAudioOffload(context = context)
    }

    suspend fun seeWhatsNew(context: Context) {
        context.dataStore.edit { preferences: MutablePreferences ->
            whatsNewSeen = true
            preferences[WHATS_NEW_SEEN_KEY] = whatsNewSeen
            val packageManager = context.packageManager
            val packageInfo = packageManager.getPackageInfo(context.packageName, 0)
            val versionName = 'v' + packageInfo.versionName!!
            preferences[WHATS_NEW_VERSION_SEEN_KEY] = versionName
            whatsNewVersionSeen = versionName
        }
    }

    suspend fun unSeeWhatsNew(context: Context) {
        context.dataStore.edit { preferences: MutablePreferences ->
            whatsNewSeen = false
            preferences[WHATS_NEW_SEEN_KEY] = whatsNewSeen
        }
    }

    suspend fun switchFilter(context: Context, filterSetting: NavBarSection) {
        SearchSettings.switchFilter(context = context, filterSetting = filterSetting)
    }

    suspend fun selectFoldersSelection(context: Context, foldersSelection: FoldersSelection) {
        context.dataStore.edit { preferences: MutablePreferences ->
            foldersSelectionSelected = foldersSelection
            preferences[FOLDERS_SELECTION_SELECTED_KEY] = foldersSelectionSelected.id
        }
    }

    /**
     * Add a path to the selected paths set and memorize it in storage.
     *
     * @param context the app context
     * @param uri the uri containing the selected path
     */
    suspend fun addPath(context: Context, uri: Uri) {
        this.addPath(context = context, path = uri.path!!)
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
            val newSet: MutableSet<String> = foldersPathsSelectedSet.value.toMutableSet()
            newSet.add(formattedPath)
            foldersPathsSelectedSet.value = newSet.toSet()
            preferences[SELECTED_PATHS_KEY] = foldersPathsSelectedSet.value
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

    suspend fun removePath(context: Context, path: String) {
        context.dataStore.edit { preferences: MutablePreferences ->
            val newSet: MutableSet<String> = foldersPathsSelectedSet.value.toMutableSet()
            newSet.remove(path)
            foldersPathsSelectedSet.value = newSet.toSet()
            preferences[SELECTED_PATHS_KEY] = foldersPathsSelectedSet.value
        }
    }

    suspend fun selectDefaultNavBarSection(context: Context, navBarSection: NavBarSection) {
        NavBarSettings.selectDefaultNavBarSection(context = context, navBarSection = navBarSection)
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

    suspend fun updateForwardMs(context: Context, seconds: Int) {
        PlaybackSettings.updateForwardMs(context = context, seconds = seconds)
    }

    suspend fun updateRewindMs(context: Context, seconds: Int) {
        PlaybackSettings.updateRewindMs(context = context, seconds = seconds)
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

    suspend fun resetBatterySettings(context: Context) {
        PlaybackSettings.resetBatterySettings(context = context)
    }

    suspend fun resetPlaybackBehaviorSettings(context: Context) {
        PlaybackSettings.resetPlaybackBehaviorSettings(context = context)
    }

    suspend fun resetPlaybackModesSettings(context: Context) {
        PlaybackSettings.resetPlaybackModesSettings(context = context)
    }

    suspend fun resetDefaultSearchFiltersSettings(context: Context) {
        SearchSettings.resetDefaultSearchFiltersSettings(context = context)
    }

    suspend fun resetNavigationBarSettings(context: Context) {
        NavBarSettings.resetNavigationBarSettings(context = context)
    }

    suspend fun resetAll(context: Context) {
        this.resetFoldersSettings(context = context)
        this.resetLoadingLogicSettings(context = context)
        this.resetBatterySettings(context = context)
        this.resetPlaybackBehaviorSettings(context = context)
        this.resetPlaybackModesSettings(context = context)
        this.resetDefaultSearchFiltersSettings(context = context)
        this.resetNavigationBarSettings(context = context)
    }
}
