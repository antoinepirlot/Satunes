/*
 * This file is part of Satunes.
 *
 *  Satunes is free software: you can redistribute it and/or modify it under
 *  the terms of the GNU General Public License as published by the Free Software Foundation,
 *  either version 3 of the License, or (at your option) any later version.
 *
 *  Satunes is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with Satunes.
 *  If not, see <https://www.gnu.org/licenses/>.
 *
 *  **** INFORMATIONS ABOUT THE AUTHOR *****
 *  The author of this file is Antoine Pirlot, the owner of this project.
 *  You find this original project on github.
 *
 *  My github link is: https://github.com/antoinepirlot
 *  This current project's link is: https://github.com/antoinepirlot/Satunes
 *
 *  You can contact me via my email: pirlot.antoine@outlook.com
 *  PS: I don't answer quickly.
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
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.media3.common.Player
import io.github.antoinepirlot.satunes.database.models.BarSpeed
import io.github.antoinepirlot.satunes.database.models.FoldersSelection
import io.github.antoinepirlot.satunes.database.models.NavBarSection
import io.github.antoinepirlot.satunes.utils.logger.SatunesLogger
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * @author Antoine Pirlot on 02-03-24
 */

object SettingsManager {

    /**
     * DEFAULT VALUES
     */
    private const val DEFAULT_FOLDERS_CHECKED = true
    private const val DEFAULT_ARTISTS_CHECKED = true
    private const val DEFAULT_ALBUMS_CHECKED = true
    private const val DEFAULT_GENRE_CHECKED = true
    private const val DEFAULT_PLAYLIST_CHECKED = true
    private const val DEFAULT_PLAYBACK_WHEN_CLOSED_CHECKED =
        false //App stop after removed app from multi-task if false
    private const val DEFAULT_PAUSE_IF_NOISY = true
    private val DEFAULT_BAR_SPEED_VALUE: BarSpeed = BarSpeed.NORMAL
    private const val DEFAULT_REPEAT_MODE: Int = Player.REPEAT_MODE_OFF
    private const val DEFAULT_SHUFFLE_MODE_CHECKED: Boolean = false
    private const val DEFAULT_PAUSE_IF_ANOTHER_PLAYBACK_CHECKED: Boolean = true
    private const val DEFAULT_AUDIO_OFFLOAD_CHECKED: Boolean = false
    private const val DEFAULT_WHATS_NEW_SEEN: Boolean = false
    private const val DEFAULT_WHATS_NEW_VERSION_SEEN: String = ""
    private const val DEFAULT_MUSICS_FILTER: Boolean = true
    private const val DEFAULT_ARTISTS_FILTER: Boolean = false
    private const val DEFAULT_ALBUMS_FILTER: Boolean = false
    private const val DEFAULT_GENRES_FILTER: Boolean = false
    private const val DEFAULT_FOLDERS_FILTER: Boolean = false
    private const val DEFAULT_PLAYLISTS_FILTER: Boolean = false
    private val DEFAULT_FOLDERS_SELECTION_SELECTED: FoldersSelection = FoldersSelection.INCLUDE
    private val DEFAULT_SELECTED_PATHS: Set<String> = setOf("/0/Music/%")

    /**
     * KEYS
     */
    private val PREFERENCES_DATA_STORE = preferencesDataStore("settings")
    private val FOLDERS_CHECKED_PREFERENCES_KEY = booleanPreferencesKey("folders_checked")
    private val ARTISTS_CHECKED_PREFERENCES_KEY = booleanPreferencesKey("artist_checked")
    private val ALBUMS_CHECKED_PREFERENCES_KEY = booleanPreferencesKey("albums_checked")
    private val GENRE_CHECKED_PREFERENCES_KEY = booleanPreferencesKey("genres_checked")
    private val PLAYLISTS_CHECKED_PREFERENCES_KEY = booleanPreferencesKey("playlists_checked")
    private val PLAYBACK_WHEN_CLOSED_CHECKED_PREFERENCES_KEY =
        booleanPreferencesKey("playback_when_closed_checked")
    private val PAUSE_IF_NOISY_PREFERENCES_KEY = booleanPreferencesKey("pause_if_noisy")
    private val BAR_SPEED_KEY = floatPreferencesKey("bar_speed")
    private val REPEAT_MODE_KEY = intPreferencesKey("repeat_mode")
    private val SHUFFLE_MODE_KEY = booleanPreferencesKey("shuffle_mode")
    private val PAUSE_IF_ANOTHER_PLAYBACK_KEY = booleanPreferencesKey("pause_if_another_playback")
    private val AUDIO_OFFLOAD_CHECKED_KEY = booleanPreferencesKey("audio_offload_checked")
    private val WHATS_NEW_SEEN_KEY = booleanPreferencesKey("whats_new_seen")
    private val WHATS_NEW_VERSION_SEEN_KEY = stringPreferencesKey("whats_new_version_seen")
    private val MUSICS_FILTER_KEY: Preferences.Key<Boolean> = booleanPreferencesKey("musics_filter")
    private val ARTISTS_FILTER_KEY: Preferences.Key<Boolean> =
        booleanPreferencesKey("artists_filter")
    private val ALBUMS_FILTER_KEY: Preferences.Key<Boolean> = booleanPreferencesKey("albums_filter")
    private val GENRES_FILTER_KEY: Preferences.Key<Boolean> = booleanPreferencesKey("genres_filter")
    private val FOLDERS_FILTER_KEY: Preferences.Key<Boolean> =
        booleanPreferencesKey("folders_filter")
    private val PLAYLISTS_FILTER_KEY: Preferences.Key<Boolean> =
        booleanPreferencesKey("playlists_filter")
    private val FOLDERS_SELECTION_SELECTED_KEY: Preferences.Key<Int> =
        intPreferencesKey("folders_selection")
    private val SELECTED_PATHS_KEY: Preferences.Key<Set<String>> =
        stringSetPreferencesKey("selected_paths_set")

    /**
     * VARIABLES
     */
    private val Context.dataStore: DataStore<Preferences> by PREFERENCES_DATA_STORE

    var foldersChecked: MutableState<Boolean> = mutableStateOf(DEFAULT_FOLDERS_CHECKED)
        private set
    var artistsChecked: MutableState<Boolean> = mutableStateOf(DEFAULT_ARTISTS_CHECKED)
        private set
    var albumsChecked: MutableState<Boolean> = mutableStateOf(DEFAULT_ALBUMS_CHECKED)
        private set
    var genresChecked: MutableState<Boolean> = mutableStateOf(DEFAULT_GENRE_CHECKED)
        private set
    var playlistsChecked: MutableState<Boolean> = mutableStateOf(DEFAULT_PLAYLIST_CHECKED)
        private set
    var playbackWhenClosedChecked: Boolean = DEFAULT_PLAYBACK_WHEN_CLOSED_CHECKED
        private set
    var pauseIfNoisyChecked: Boolean = DEFAULT_PAUSE_IF_NOISY
        private set
    var barSpeed: BarSpeed = DEFAULT_BAR_SPEED_VALUE
        private set
    var repeatMode: Int = DEFAULT_REPEAT_MODE
        private set
    var shuffleMode: Boolean = DEFAULT_SHUFFLE_MODE_CHECKED
        private set
    var pauseIfAnotherPlayback: Boolean = DEFAULT_PAUSE_IF_ANOTHER_PLAYBACK_CHECKED
        private set
    var audioOffloadChecked: Boolean = DEFAULT_AUDIO_OFFLOAD_CHECKED
        private set

    var whatsNewSeen: Boolean = DEFAULT_WHATS_NEW_SEEN
        private set

    private var whatsNewVersionSeen: String = DEFAULT_WHATS_NEW_VERSION_SEEN

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

    var foldersSelectionSelected: FoldersSelection = DEFAULT_FOLDERS_SELECTION_SELECTED
        private set

    var foldersPathsSelectedSet: MutableState<Set<String>> = mutableStateOf(DEFAULT_SELECTED_PATHS)
        private set

    private val _logger = SatunesLogger.getLogger()

    suspend fun loadSettings(context: Context) {
        context.dataStore.data.map { preferences: Preferences ->
            foldersChecked.value =
                preferences[FOLDERS_CHECKED_PREFERENCES_KEY] ?: DEFAULT_FOLDERS_CHECKED

            artistsChecked.value =
                preferences[ARTISTS_CHECKED_PREFERENCES_KEY] ?: DEFAULT_ARTISTS_CHECKED

            albumsChecked.value =
                preferences[ALBUMS_CHECKED_PREFERENCES_KEY] ?: DEFAULT_ALBUMS_CHECKED

            genresChecked.value =
                preferences[GENRE_CHECKED_PREFERENCES_KEY] ?: DEFAULT_GENRE_CHECKED

            playlistsChecked.value =
                preferences[PLAYLISTS_CHECKED_PREFERENCES_KEY] ?: DEFAULT_PLAYLIST_CHECKED

            playbackWhenClosedChecked =
                preferences[PLAYBACK_WHEN_CLOSED_CHECKED_PREFERENCES_KEY]
                    ?: DEFAULT_PLAYBACK_WHEN_CLOSED_CHECKED

            pauseIfNoisyChecked =
                preferences[PAUSE_IF_NOISY_PREFERENCES_KEY] ?: DEFAULT_PAUSE_IF_NOISY

            barSpeed = getBarSpeed(preferences[BAR_SPEED_KEY])

            repeatMode = preferences[REPEAT_MODE_KEY] ?: DEFAULT_REPEAT_MODE

            shuffleMode =
                preferences[SHUFFLE_MODE_KEY] ?: DEFAULT_SHUFFLE_MODE_CHECKED

            pauseIfAnotherPlayback = preferences[PAUSE_IF_ANOTHER_PLAYBACK_KEY]
                ?: DEFAULT_PAUSE_IF_ANOTHER_PLAYBACK_CHECKED

            audioOffloadChecked =
                preferences[AUDIO_OFFLOAD_CHECKED_KEY] ?: DEFAULT_AUDIO_OFFLOAD_CHECKED

            foldersSelectionSelected =
                getFoldersSelection(preferences[FOLDERS_SELECTION_SELECTED_KEY])

            foldersPathsSelectedSet.value =
                preferences[SELECTED_PATHS_KEY] ?: DEFAULT_SELECTED_PATHS

            loadWhatsNew(context = context, preferences = preferences)

            loadFilters(context = context)
        }.first()
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

    suspend fun loadFilters(context: Context) {
        context.dataStore.edit { preferences: MutablePreferences ->
            foldersFilter = preferences[FOLDERS_FILTER_KEY] ?: DEFAULT_FOLDERS_FILTER
            artistsFilter = preferences[ARTISTS_FILTER_KEY] ?: DEFAULT_ARTISTS_FILTER
            albumsFilter = preferences[ALBUMS_FILTER_KEY] ?: DEFAULT_ALBUMS_FILTER
            genresFilter = preferences[GENRES_FILTER_KEY] ?: DEFAULT_GENRES_FILTER
            playlistsFilter =
                preferences[PLAYLISTS_FILTER_KEY] ?: DEFAULT_PLAYLISTS_FILTER
            musicsFilter = preferences[MUSICS_FILTER_KEY] ?: DEFAULT_MUSICS_FILTER
        }
    }

    private suspend fun loadWhatsNew(context: Context, preferences: Preferences) {
        whatsNewSeen = preferences[WHATS_NEW_SEEN_KEY] ?: DEFAULT_WHATS_NEW_SEEN
        whatsNewVersionSeen =
            preferences[WHATS_NEW_VERSION_SEEN_KEY] ?: DEFAULT_WHATS_NEW_VERSION_SEEN
        if (whatsNewSeen) {
            val packageManager = context.packageManager
            val packageInfo = packageManager.getPackageInfo(context.packageName, 0)
            val versionName = 'v' + packageInfo.versionName
            if (whatsNewVersionSeen != versionName) {
                this.unSeeWhatsNew(context = context)
            }
        }
    }

    suspend fun switchNavBarSection(context: Context, navBarSection: NavBarSection) {
        when (navBarSection) {
            NavBarSection.FOLDERS -> {
                context.dataStore.edit { preferences: MutablePreferences ->
                    foldersChecked.value = !foldersChecked.value
                    preferences[FOLDERS_CHECKED_PREFERENCES_KEY] = foldersChecked.value
                }
            }

            NavBarSection.ARTISTS -> {
                context.dataStore.edit { preferences: MutablePreferences ->
                    artistsChecked.value = !artistsChecked.value
                    preferences[ARTISTS_CHECKED_PREFERENCES_KEY] = artistsChecked.value
                }
            }

            NavBarSection.ALBUMS -> {
                context.dataStore.edit { preferences: MutablePreferences ->
                    albumsChecked.value = !albumsChecked.value
                    preferences[ALBUMS_CHECKED_PREFERENCES_KEY] = albumsChecked.value
                }
            }

            NavBarSection.GENRES -> {
                context.dataStore.edit { preferences: MutablePreferences ->
                    genresChecked.value = !genresChecked.value
                    preferences[GENRE_CHECKED_PREFERENCES_KEY] = genresChecked.value
                }
            }

            NavBarSection.PLAYLISTS -> {
                context.dataStore.edit { preferences: MutablePreferences ->
                    playlistsChecked.value = !playlistsChecked.value
                    preferences[PLAYLISTS_CHECKED_PREFERENCES_KEY] = playlistsChecked.value
                }
            }

            NavBarSection.MUSICS -> { /*Do nothing*/
            }
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

    suspend fun seeWhatsNew(context: Context) {
        context.dataStore.edit { preferences: MutablePreferences ->
            whatsNewSeen = true
            preferences[WHATS_NEW_SEEN_KEY] = whatsNewSeen
            val packageManager = context.packageManager
            val packageInfo = packageManager.getPackageInfo(context.packageName, 0)
            val versionName = 'v' + packageInfo.versionName
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
        when (filterSetting) {
            NavBarSection.MUSICS -> {
                context.dataStore.edit { preferences: MutablePreferences ->
                    musicsFilter = !musicsFilter
                    preferences[MUSICS_FILTER_KEY] = musicsFilter
                }
            }

            NavBarSection.ARTISTS -> {
                context.dataStore.edit { preferences: MutablePreferences ->
                    artistsFilter = !artistsFilter
                    preferences[ARTISTS_FILTER_KEY] = artistsFilter
                }
            }

            NavBarSection.ALBUMS -> {
                context.dataStore.edit { preferences: MutablePreferences ->
                    albumsFilter = !albumsFilter
                    preferences[ALBUMS_FILTER_KEY] = albumsFilter
                }
            }

            NavBarSection.GENRES -> {
                context.dataStore.edit { preferences: MutablePreferences ->
                    genresFilter = !genresFilter
                    preferences[GENRES_FILTER_KEY] = genresFilter
                }
            }

            NavBarSection.FOLDERS -> {
                context.dataStore.edit { preferences: MutablePreferences ->
                    foldersFilter = !foldersFilter
                    preferences[FOLDERS_FILTER_KEY] = foldersFilter
                }
            }

            NavBarSection.PLAYLISTS -> {
                context.dataStore.edit { preferences: MutablePreferences ->
                    playlistsFilter = !playlistsFilter
                    preferences[PLAYLISTS_FILTER_KEY] = playlistsFilter
                }
            }
        }
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
        val formattedPath: String = getFormattedPath(path = uri.path!!)
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
}