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
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import io.github.antoinepirlot.satunes.database.models.MenuTitle
import io.github.antoinepirlot.satunes.utils.logger.SatunesLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

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
    private const val DEFAULT_INCLUDE_RINGTONES = false
    private const val DEFAULT_BAR_SPEED_VALUE = 1f
    private const val DEFAULT_REPEAT_MODE: Int = 0
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
    private val INCLUDE_RINGTONES_KEY = booleanPreferencesKey("include_ringtones")
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

    /**
     * VARIABLES
     */
    private val Context.dataStore: DataStore<Preferences> by PREFERENCES_DATA_STORE

    val foldersChecked: MutableState<Boolean> = mutableStateOf(DEFAULT_FOLDERS_CHECKED)
    val artistsChecked: MutableState<Boolean> = mutableStateOf(DEFAULT_ARTISTS_CHECKED)
    val albumsChecked: MutableState<Boolean> = mutableStateOf(DEFAULT_ALBUMS_CHECKED)
    val genresChecked: MutableState<Boolean> = mutableStateOf(DEFAULT_GENRE_CHECKED)
    val playlistsChecked: MutableState<Boolean> = mutableStateOf(DEFAULT_PLAYLIST_CHECKED)
    val playbackWhenClosedChecked: MutableState<Boolean> =
        mutableStateOf(DEFAULT_PLAYBACK_WHEN_CLOSED_CHECKED)
    val pauseIfNoisyChecked: MutableState<Boolean> = mutableStateOf(DEFAULT_PAUSE_IF_NOISY)
    val includeRingtonesChecked: MutableState<Boolean> = mutableStateOf(DEFAULT_INCLUDE_RINGTONES)
    val barSpeed: MutableState<Float> = mutableFloatStateOf(DEFAULT_BAR_SPEED_VALUE)
    val repeatMode: MutableIntState = mutableIntStateOf(DEFAULT_REPEAT_MODE)
    val shuffleMode: MutableState<Boolean> = mutableStateOf(DEFAULT_SHUFFLE_MODE_CHECKED)
    val pauseIfAnotherPlayback: MutableState<Boolean> = mutableStateOf(
        DEFAULT_PAUSE_IF_ANOTHER_PLAYBACK_CHECKED
    )
    val audioOffloadChecked: MutableState<Boolean> = mutableStateOf(DEFAULT_AUDIO_OFFLOAD_CHECKED)

    val menuTitleCheckedMap: Map<MenuTitle, MutableState<Boolean>> = mapOf(
        Pair(MenuTitle.FOLDERS, foldersChecked),
        Pair(MenuTitle.ARTISTS, artistsChecked),
        Pair(MenuTitle.ALBUMS, albumsChecked),
        Pair(MenuTitle.GENRES, genresChecked),
        Pair(MenuTitle.PLAYLISTS, playlistsChecked)
    )

    var whatsNewSeen: Boolean = DEFAULT_WHATS_NEW_SEEN
        private set

    private var whatsNewVersionSeen: String = DEFAULT_WHATS_NEW_VERSION_SEEN

    val foldersFilter: MutableState<Boolean> = mutableStateOf(DEFAULT_FOLDERS_FILTER)
    val artistsFilter: MutableState<Boolean> = mutableStateOf(DEFAULT_ARTISTS_FILTER)
    val albumsFilter: MutableState<Boolean> = mutableStateOf(DEFAULT_ALBUMS_FILTER)
    val genresFilter: MutableState<Boolean> = mutableStateOf(DEFAULT_GENRES_FILTER)
    val playlistsFilter: MutableState<Boolean> = mutableStateOf(DEFAULT_PLAYLISTS_FILTER)
    val musicsFilter: MutableState<Boolean> = mutableStateOf(DEFAULT_MUSICS_FILTER)

    private val logger = SatunesLogger(name = this::class.java.name)

    fun loadSettings(context: Context) {
        runBlocking {
            try {
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

                    playbackWhenClosedChecked.value =
                        preferences[PLAYBACK_WHEN_CLOSED_CHECKED_PREFERENCES_KEY]
                            ?: DEFAULT_PLAYBACK_WHEN_CLOSED_CHECKED

                    pauseIfNoisyChecked.value =
                        preferences[PAUSE_IF_NOISY_PREFERENCES_KEY] ?: DEFAULT_PAUSE_IF_NOISY

                    includeRingtonesChecked.value =
                        preferences[INCLUDE_RINGTONES_KEY] ?: DEFAULT_INCLUDE_RINGTONES

                    barSpeed.value = preferences[BAR_SPEED_KEY] ?: DEFAULT_BAR_SPEED_VALUE

                    repeatMode.intValue = preferences[REPEAT_MODE_KEY] ?: DEFAULT_REPEAT_MODE

                    shuffleMode.value =
                        preferences[SHUFFLE_MODE_KEY] ?: DEFAULT_SHUFFLE_MODE_CHECKED

                    pauseIfAnotherPlayback.value = preferences[PAUSE_IF_ANOTHER_PLAYBACK_KEY]
                        ?: DEFAULT_PAUSE_IF_ANOTHER_PLAYBACK_CHECKED

                    audioOffloadChecked.value =
                        preferences[AUDIO_OFFLOAD_CHECKED_KEY] ?: DEFAULT_AUDIO_OFFLOAD_CHECKED
                    loadWhatsNew(context = context, preferences = preferences)

                    loadFilters(context = context)
                }.first()
            } catch (e: Throwable) {
                logger.severe(e.message)
                throw e
            }
        }
    }

    private fun loadWhatsNew(context: Context, preferences: Preferences) {
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

    fun switchMenuTitle(context: Context, menuTitle: MenuTitle) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                when (menuTitle) {
                    MenuTitle.FOLDERS -> {
                        context.dataStore.edit { preferences: MutablePreferences ->
                            foldersChecked.value = !foldersChecked.value
                            preferences[FOLDERS_CHECKED_PREFERENCES_KEY] = foldersChecked.value
                        }
                    }

                    MenuTitle.ARTISTS -> {
                        context.dataStore.edit { preferences: MutablePreferences ->
                            artistsChecked.value = !artistsChecked.value
                            preferences[ARTISTS_CHECKED_PREFERENCES_KEY] = artistsChecked.value
                        }
                    }

                    MenuTitle.ALBUMS -> {
                        context.dataStore.edit { preferences: MutablePreferences ->
                            albumsChecked.value = !albumsChecked.value
                            preferences[ALBUMS_CHECKED_PREFERENCES_KEY] = albumsChecked.value
                        }
                    }

                    MenuTitle.GENRES -> {
                        context.dataStore.edit { preferences: MutablePreferences ->
                            genresChecked.value = !genresChecked.value
                            preferences[GENRE_CHECKED_PREFERENCES_KEY] = genresChecked.value
                        }
                    }

                    MenuTitle.PLAYLISTS -> {
                        context.dataStore.edit { preferences: MutablePreferences ->
                            playlistsChecked.value = !playlistsChecked.value
                            preferences[PLAYLISTS_CHECKED_PREFERENCES_KEY] = playlistsChecked.value
                        }
                    }

                    MenuTitle.MUSICS -> { /*Do nothing*/
                    }
                }
            } catch (e: Throwable) {
                logger.severe(e.message)
                throw e
            }
        }
    }

    fun switchPlaybackWhenClosedChecked(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                context.dataStore.edit { preferences: MutablePreferences ->
                    playbackWhenClosedChecked.value = !playbackWhenClosedChecked.value
                    preferences[PLAYBACK_WHEN_CLOSED_CHECKED_PREFERENCES_KEY] =
                        playbackWhenClosedChecked.value
                }
            } catch (e: Throwable) {
                logger.severe(e.message)
                throw e
            }
        }
    }

    fun switchPauseIfNoisy(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                context.dataStore.edit { preferences: MutablePreferences ->
                    pauseIfNoisyChecked.value = !pauseIfNoisyChecked.value
                    preferences[PAUSE_IF_NOISY_PREFERENCES_KEY] = pauseIfNoisyChecked.value
                }
            } catch (e: Throwable) {
                logger.severe(e.message)
                throw e
            }
        }
    }

    fun switchIncludeRingtones(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                context.dataStore.edit { preferences: MutablePreferences ->
                    includeRingtonesChecked.value = !includeRingtonesChecked.value
                    preferences[INCLUDE_RINGTONES_KEY] = includeRingtonesChecked.value
                }
            } catch (e: Throwable) {
                logger.severe(e.message)
                throw e
            }
        }
    }

    fun updateBarSpeed(context: Context, newValue: Float) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                context.dataStore.edit { preferences: MutablePreferences ->
                    barSpeed.value = newValue
                    preferences[BAR_SPEED_KEY] = barSpeed.value
                }
            } catch (e: Throwable) {
                logger.severe(e.message)
                throw e
            }
        }
    }

    fun updateRepeatMode(context: Context, newValue: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                context.dataStore.edit { preferences: MutablePreferences ->
                    repeatMode.intValue = newValue
                    preferences[REPEAT_MODE_KEY] = repeatMode.intValue
                }
            } catch (e: Throwable) {
                logger.severe(e.message)
                throw e
            }
        }
    }

    fun switchShuffleMode(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                context.dataStore.edit { preferences: MutablePreferences ->
                    shuffleMode.value = !shuffleMode.value
                    preferences[SHUFFLE_MODE_KEY] = shuffleMode.value
                }
            } catch (e: Throwable) {
                logger.severe(e.message)
                throw e
            }
        }
    }

    fun switchPauseIfPlayback(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                context.dataStore.edit { preferences: MutablePreferences ->
                    pauseIfAnotherPlayback.value = !pauseIfAnotherPlayback.value
                    preferences[PAUSE_IF_ANOTHER_PLAYBACK_KEY] = pauseIfAnotherPlayback.value
                }
            } catch (e: Throwable) {
                logger.severe(e.message)
                throw e
            }
        }
    }

    fun switchAudioOffload(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                context.dataStore.edit { preferences: MutablePreferences ->
                    audioOffloadChecked.value = !audioOffloadChecked.value
                    preferences[AUDIO_OFFLOAD_CHECKED_KEY] = audioOffloadChecked.value
                }
            } catch (e: Throwable) {
                logger.severe(e.message)
                throw e
            }
        }
    }

    fun seeWhatsNew(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                context.dataStore.edit { preferences: MutablePreferences ->
                    whatsNewSeen = true
                    preferences[WHATS_NEW_SEEN_KEY] = whatsNewSeen
                    val packageManager = context.packageManager
                    val packageInfo = packageManager.getPackageInfo(context.packageName, 0)
                    val versionName = 'v' + packageInfo.versionName
                    preferences[WHATS_NEW_VERSION_SEEN_KEY] = versionName
                    whatsNewVersionSeen = versionName
                }
            } catch (e: Throwable) {
                logger.severe(e.message)
                throw e
            }
        }
    }

    private fun unSeeWhatsNew(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                context.dataStore.edit { preferences: MutablePreferences ->
                    whatsNewSeen = false
                    preferences[WHATS_NEW_SEEN_KEY] = whatsNewSeen
                }
            } catch (e: Throwable) {
                logger.severe(e.message)
                throw e
            }
        }
    }

    fun switchFilter(context: Context, filterSetting: MenuTitle) {
        runBlocking {
            try {
                when (filterSetting) {
                    MenuTitle.MUSICS -> {
                        context.dataStore.edit { preferences: MutablePreferences ->
                            musicsFilter.value = !musicsFilter.value
                            preferences[MUSICS_FILTER_KEY] = musicsFilter.value
                        }
                    }

                    MenuTitle.ARTISTS -> {
                        context.dataStore.edit { preferences: MutablePreferences ->
                            artistsFilter.value = !artistsFilter.value
                            preferences[ARTISTS_FILTER_KEY] = artistsFilter.value
                        }
                    }

                    MenuTitle.ALBUMS -> {
                        context.dataStore.edit { preferences: MutablePreferences ->
                            albumsFilter.value = !albumsFilter.value
                            preferences[ALBUMS_FILTER_KEY] = albumsFilter.value
                        }
                    }

                    MenuTitle.GENRES -> {
                        context.dataStore.edit { preferences: MutablePreferences ->
                            genresFilter.value = !genresFilter.value
                            preferences[GENRES_FILTER_KEY] = genresFilter.value
                        }
                    }

                    MenuTitle.FOLDERS -> {
                        context.dataStore.edit { preferences: MutablePreferences ->
                            foldersFilter.value = !foldersFilter.value
                            preferences[FOLDERS_FILTER_KEY] = foldersFilter.value
                        }
                    }

                    MenuTitle.PLAYLISTS -> {
                        context.dataStore.edit { preferences: MutablePreferences ->
                            playlistsFilter.value = !playlistsFilter.value
                            preferences[PLAYLISTS_FILTER_KEY] = playlistsFilter.value
                        }
                    }
                }
            } catch (e: Throwable) {
                logger.severe(e.message)
                throw e
            }
        }
    }

    suspend fun loadFilters(context: Context) {
        try {
            context.dataStore.edit { preferences: MutablePreferences ->
                foldersFilter.value = preferences[FOLDERS_FILTER_KEY] ?: DEFAULT_FOLDERS_FILTER
                artistsFilter.value = preferences[ARTISTS_FILTER_KEY] ?: DEFAULT_ARTISTS_FILTER
                albumsFilter.value = preferences[ALBUMS_FILTER_KEY] ?: DEFAULT_ALBUMS_FILTER
                genresFilter.value = preferences[GENRES_FILTER_KEY] ?: DEFAULT_GENRES_FILTER
                playlistsFilter.value =
                    preferences[PLAYLISTS_FILTER_KEY] ?: DEFAULT_PLAYLISTS_FILTER
                musicsFilter.value = preferences[MUSICS_FILTER_KEY] ?: DEFAULT_MUSICS_FILTER
            }
        } catch (e: Throwable) {
            logger.severe(e.message)
            throw e
        }
    }
}