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

    var foldersChecked: Boolean = DEFAULT_FOLDERS_CHECKED
        private set
    var artistsChecked: Boolean = DEFAULT_ARTISTS_CHECKED
        private set
    var albumsChecked: Boolean = DEFAULT_ALBUMS_CHECKED
        private set
    var genresChecked: Boolean = DEFAULT_GENRE_CHECKED
        private set
    var playlistsChecked: Boolean = DEFAULT_PLAYLIST_CHECKED
        private set
    var playbackWhenClosedChecked: Boolean = DEFAULT_PLAYBACK_WHEN_CLOSED_CHECKED
        private set
    var pauseIfNoisyChecked: Boolean = DEFAULT_PAUSE_IF_NOISY
        private set
    var includeRingtonesChecked: Boolean = DEFAULT_INCLUDE_RINGTONES
        private set
    var barSpeed: Float = DEFAULT_BAR_SPEED_VALUE
        private set
    var repeatMode: Int = DEFAULT_REPEAT_MODE
        private set
    var shuffleMode: Boolean = DEFAULT_SHUFFLE_MODE_CHECKED
        private set
    var pauseIfAnotherPlayback: Boolean = DEFAULT_PAUSE_IF_ANOTHER_PLAYBACK_CHECKED
        private set
    var audioOffloadChecked: Boolean = DEFAULT_AUDIO_OFFLOAD_CHECKED
        private set

    val menuTitleCheckedMap: Map<MenuTitle, Boolean> = mapOf(
        Pair(MenuTitle.FOLDERS, foldersChecked),
        Pair(MenuTitle.ARTISTS, artistsChecked),
        Pair(MenuTitle.ALBUMS, albumsChecked),
        Pair(MenuTitle.GENRES, genresChecked),
        Pair(MenuTitle.PLAYLISTS, playlistsChecked)
    )

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

    private val logger = SatunesLogger.getLogger()

    suspend fun loadSettings(context: Context) {
        try {
            context.dataStore.data.map { preferences: Preferences ->
                foldersChecked =
                    preferences[FOLDERS_CHECKED_PREFERENCES_KEY] ?: DEFAULT_FOLDERS_CHECKED

                artistsChecked =
                    preferences[ARTISTS_CHECKED_PREFERENCES_KEY] ?: DEFAULT_ARTISTS_CHECKED

                albumsChecked =
                    preferences[ALBUMS_CHECKED_PREFERENCES_KEY] ?: DEFAULT_ALBUMS_CHECKED

                genresChecked =
                    preferences[GENRE_CHECKED_PREFERENCES_KEY] ?: DEFAULT_GENRE_CHECKED

                playlistsChecked =
                    preferences[PLAYLISTS_CHECKED_PREFERENCES_KEY] ?: DEFAULT_PLAYLIST_CHECKED

                playbackWhenClosedChecked =
                    preferences[PLAYBACK_WHEN_CLOSED_CHECKED_PREFERENCES_KEY]
                        ?: DEFAULT_PLAYBACK_WHEN_CLOSED_CHECKED

                pauseIfNoisyChecked =
                    preferences[PAUSE_IF_NOISY_PREFERENCES_KEY] ?: DEFAULT_PAUSE_IF_NOISY

                includeRingtonesChecked =
                    preferences[INCLUDE_RINGTONES_KEY] ?: DEFAULT_INCLUDE_RINGTONES

                barSpeed = preferences[BAR_SPEED_KEY] ?: DEFAULT_BAR_SPEED_VALUE

                repeatMode = preferences[REPEAT_MODE_KEY] ?: DEFAULT_REPEAT_MODE

                shuffleMode =
                    preferences[SHUFFLE_MODE_KEY] ?: DEFAULT_SHUFFLE_MODE_CHECKED

                pauseIfAnotherPlayback = preferences[PAUSE_IF_ANOTHER_PLAYBACK_KEY]
                    ?: DEFAULT_PAUSE_IF_ANOTHER_PLAYBACK_CHECKED

                audioOffloadChecked =
                    preferences[AUDIO_OFFLOAD_CHECKED_KEY] ?: DEFAULT_AUDIO_OFFLOAD_CHECKED
                loadWhatsNew(context = context, preferences = preferences)

                loadFilters(context = context)
            }.first()
        } catch (e: Throwable) {
            logger.severe(e.message)
            throw e
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

    suspend fun switchMenuTitle(context: Context, menuTitle: MenuTitle) {
        try {
            when (menuTitle) {
                MenuTitle.FOLDERS -> {
                    context.dataStore.edit { preferences: MutablePreferences ->
                        foldersChecked = !foldersChecked
                        preferences[FOLDERS_CHECKED_PREFERENCES_KEY] = foldersChecked
                    }
                }

                MenuTitle.ARTISTS -> {
                    context.dataStore.edit { preferences: MutablePreferences ->
                        artistsChecked = !artistsChecked
                        preferences[ARTISTS_CHECKED_PREFERENCES_KEY] = artistsChecked
                    }
                }

                MenuTitle.ALBUMS -> {
                    context.dataStore.edit { preferences: MutablePreferences ->
                        albumsChecked = !albumsChecked
                        preferences[ALBUMS_CHECKED_PREFERENCES_KEY] = albumsChecked
                    }
                }

                MenuTitle.GENRES -> {
                    context.dataStore.edit { preferences: MutablePreferences ->
                        genresChecked = !genresChecked
                        preferences[GENRE_CHECKED_PREFERENCES_KEY] = genresChecked
                    }
                }

                MenuTitle.PLAYLISTS -> {
                    context.dataStore.edit { preferences: MutablePreferences ->
                        playlistsChecked = !playlistsChecked
                        preferences[PLAYLISTS_CHECKED_PREFERENCES_KEY] = playlistsChecked
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

    suspend fun switchPlaybackWhenClosedChecked(context: Context) {
        try {
            context.dataStore.edit { preferences: MutablePreferences ->
                playbackWhenClosedChecked = !playbackWhenClosedChecked
                preferences[PLAYBACK_WHEN_CLOSED_CHECKED_PREFERENCES_KEY] =
                    playbackWhenClosedChecked
            }
        } catch (e: Throwable) {
            logger.severe(e.message)
            throw e
        }
    }

    suspend fun switchPauseIfNoisy(context: Context) {
        try {
            context.dataStore.edit { preferences: MutablePreferences ->
                pauseIfNoisyChecked = !pauseIfNoisyChecked
                preferences[PAUSE_IF_NOISY_PREFERENCES_KEY] = pauseIfNoisyChecked
            }
        } catch (e: Throwable) {
            logger.severe(e.message)
            throw e
        }
    }

    suspend fun switchIncludeRingtones(context: Context) {
        try {
            context.dataStore.edit { preferences: MutablePreferences ->
                includeRingtonesChecked = !includeRingtonesChecked
                preferences[INCLUDE_RINGTONES_KEY] = includeRingtonesChecked
            }
        } catch (e: Throwable) {
            logger.severe(e.message)
            throw e
        }
    }

    suspend fun updateBarSpeed(context: Context, newValue: Float) {
        try {
            context.dataStore.edit { preferences: MutablePreferences ->
                barSpeed = newValue
                preferences[BAR_SPEED_KEY] = barSpeed
            }
        } catch (e: Throwable) {
            logger.severe(e.message)
            throw e
        }
    }

    suspend fun updateRepeatMode(context: Context, newValue: Int) {
        try {
            context.dataStore.edit { preferences: MutablePreferences ->
                repeatMode = newValue
                preferences[REPEAT_MODE_KEY] = repeatMode
            }
        } catch (e: Throwable) {
            logger.severe(e.message)
            throw e
        }
    }

    suspend fun switchShuffleMode(context: Context) {
        try {
            context.dataStore.edit { preferences: MutablePreferences ->
                shuffleMode = !shuffleMode
                preferences[SHUFFLE_MODE_KEY] = shuffleMode
            }
        } catch (e: Throwable) {
            logger.severe(e.message)
            throw e
        }
    }

    suspend fun switchPauseIfAnotherPlayback(context: Context) {
        try {
            context.dataStore.edit { preferences: MutablePreferences ->
                pauseIfAnotherPlayback = !pauseIfAnotherPlayback
                preferences[PAUSE_IF_ANOTHER_PLAYBACK_KEY] = pauseIfAnotherPlayback
            }
        } catch (e: Throwable) {
            logger.severe(e.message)
            throw e
        }
    }

    suspend fun switchAudioOffload(context: Context) {
        try {
            context.dataStore.edit { preferences: MutablePreferences ->
                audioOffloadChecked = !audioOffloadChecked
                preferences[AUDIO_OFFLOAD_CHECKED_KEY] = audioOffloadChecked
            }
        } catch (e: Throwable) {
            logger.severe(e.message)
            throw e
        }
    }

    suspend fun seeWhatsNew(context: Context) {
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

    private suspend fun unSeeWhatsNew(context: Context) {
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

    suspend fun switchFilter(context: Context, filterSetting: MenuTitle) {
        try {
            when (filterSetting) {
                MenuTitle.MUSICS -> {
                    context.dataStore.edit { preferences: MutablePreferences ->
                        musicsFilter = !musicsFilter
                        preferences[MUSICS_FILTER_KEY] = musicsFilter
                    }
                }

                MenuTitle.ARTISTS -> {
                    context.dataStore.edit { preferences: MutablePreferences ->
                        artistsFilter = !artistsFilter
                        preferences[ARTISTS_FILTER_KEY] = artistsFilter
                    }
                }

                MenuTitle.ALBUMS -> {
                    context.dataStore.edit { preferences: MutablePreferences ->
                        albumsFilter = !albumsFilter
                        preferences[ALBUMS_FILTER_KEY] = albumsFilter
                    }
                }

                MenuTitle.GENRES -> {
                    context.dataStore.edit { preferences: MutablePreferences ->
                        genresFilter = !genresFilter
                        preferences[GENRES_FILTER_KEY] = genresFilter
                    }
                }

                MenuTitle.FOLDERS -> {
                    context.dataStore.edit { preferences: MutablePreferences ->
                        foldersFilter = !foldersFilter
                        preferences[FOLDERS_FILTER_KEY] = foldersFilter
                    }
                }

                MenuTitle.PLAYLISTS -> {
                    context.dataStore.edit { preferences: MutablePreferences ->
                        playlistsFilter = !playlistsFilter
                        preferences[PLAYLISTS_FILTER_KEY] = playlistsFilter
                    }
                }
            }
        } catch (e: Throwable) {
            logger.severe(e.message)
            throw e
        }
    }

    suspend fun loadFilters(context: Context) {
        try {
            context.dataStore.edit { preferences: MutablePreferences ->
                foldersFilter = preferences[FOLDERS_FILTER_KEY] ?: DEFAULT_FOLDERS_FILTER
                artistsFilter = preferences[ARTISTS_FILTER_KEY] ?: DEFAULT_ARTISTS_FILTER
                albumsFilter = preferences[ALBUMS_FILTER_KEY] ?: DEFAULT_ALBUMS_FILTER
                genresFilter = preferences[GENRES_FILTER_KEY] ?: DEFAULT_GENRES_FILTER
                playlistsFilter =
                    preferences[PLAYLISTS_FILTER_KEY] ?: DEFAULT_PLAYLISTS_FILTER
                musicsFilter = preferences[MUSICS_FILTER_KEY] ?: DEFAULT_MUSICS_FILTER
            }
        } catch (e: Throwable) {
            logger.severe(e.message)
            throw e
        }
    }
}