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
import androidx.datastore.preferences.preferencesDataStore
import io.github.antoinepirlot.satunes.database.models.MenuTitle
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

/**
 * @author Antoine Pirlot on 02-03-24
 */

object SettingsManager {

    private const val DEFAULT_FOLDERS_CHECKED = true
    private const val DEFAULT_ARTISTS_CHECKED = true
    private const val DEFAULT_ALBUMS_CHECKED = true
    private const val DEFAULT_GENRE_CHECKED = true
    private const val DEFAULT_PLAYLIST_CHECKED = true
    private const val DEFAULT_PLAYBACK_WHEN_CLOSED_CHECKED =
        false //App stop after removed app from multi-task if false
    private const val DEFAULT_PAUSE_IF_NOISY = true
    private const val DEFAULT_EXCLUDE_RINGTONES = true
    private const val DEFAULT_BAR_SPEED_VALUE = 1f
    private const val DEFAULT_REPEAT_MODE: Int = 0
    private const val DEFAULT_SHUFFLE_MODE_CHECKED: Boolean = false
    private const val DEFAULT_PAUSE_IF_ANOTHER_PLAYBACK_CHECKED: Boolean = true
    private const val DEFAULT_AUDIO_OFFLOAD_CHECKED: Boolean = false
    private const val DEFAULT_WHATS_NEW_SEEN: Boolean = false

    private val PREFERENCES_DATA_STORE = preferencesDataStore("settings")
    private val FOLDERS_CHECKED_PREFERENCES_KEY = booleanPreferencesKey("folders_checked")
    private val ARTISTS_CHECKED_PREFERENCES_KEY = booleanPreferencesKey("artist_checked")
    private val ALBUMS_CHECKED_PREFERENCES_KEY = booleanPreferencesKey("albums_checked")
    private val GENRE_CHECKED_PREFERENCES_KEY = booleanPreferencesKey("genres_checked")
    private val PLAYLISTS_CHECKED_PREFERENCES_KEY = booleanPreferencesKey("playlists_checked")
    private val PLAYBACK_WHEN_CLOSED_CHECKED_PREFERENCES_KEY =
        booleanPreferencesKey("playback_when_closed_checked")
    private val PAUSE_IF_NOISY_PREFERENCES_KEY = booleanPreferencesKey("pause_if_noisy")
    private val EXCLUDE_RINGTONES_KEY = booleanPreferencesKey("exclude_ringtones")
    private val BAR_SPEED_KEY = floatPreferencesKey("bar_speed")
    private val REPEAT_MODE_KEY = intPreferencesKey("repeat_mode")
    private val SHUFFLE_MODE_KEY = booleanPreferencesKey("shuffle_mode")
    private val PAUSE_IF_ANOTHER_PLAYBACK_KEY = booleanPreferencesKey("pause_if_another_playback")
    private val AUDIO_OFFLOAD_CHECKED_KEY = booleanPreferencesKey("audio_offload_checked")
    private val WHATS_NEW_SEEN_KEY = booleanPreferencesKey("whats_new_seen")

    private val Context.dataStore: DataStore<Preferences> by PREFERENCES_DATA_STORE

    val foldersChecked: MutableState<Boolean> = mutableStateOf(DEFAULT_FOLDERS_CHECKED)
    val artistsChecked: MutableState<Boolean> = mutableStateOf(DEFAULT_ARTISTS_CHECKED)
    val albumsChecked: MutableState<Boolean> = mutableStateOf(DEFAULT_ALBUMS_CHECKED)
    val genresChecked: MutableState<Boolean> = mutableStateOf(DEFAULT_GENRE_CHECKED)
    val playlistsChecked: MutableState<Boolean> = mutableStateOf(DEFAULT_PLAYLIST_CHECKED)
    val playbackWhenClosedChecked: MutableState<Boolean> =
        mutableStateOf(DEFAULT_PLAYBACK_WHEN_CLOSED_CHECKED)
    val pauseIfNoisyChecked: MutableState<Boolean> = mutableStateOf(DEFAULT_PAUSE_IF_NOISY)
    val excludeRingtonesChecked: MutableState<Boolean> = mutableStateOf(DEFAULT_EXCLUDE_RINGTONES)
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

    val whatsNewSeen: MutableState<Boolean> = mutableStateOf(false)

    fun loadSettings(context: Context) {
        runBlocking {
            // Using first() at the end and for nothing, prevent wrong UI data switch synchronisation
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

                excludeRingtonesChecked.value =
                    preferences[EXCLUDE_RINGTONES_KEY] ?: DEFAULT_EXCLUDE_RINGTONES

                barSpeed.value = preferences[BAR_SPEED_KEY] ?: DEFAULT_BAR_SPEED_VALUE

                repeatMode.intValue = preferences[REPEAT_MODE_KEY] ?: DEFAULT_REPEAT_MODE

                shuffleMode.value = preferences[SHUFFLE_MODE_KEY] ?: DEFAULT_SHUFFLE_MODE_CHECKED

                pauseIfAnotherPlayback.value = preferences[PAUSE_IF_ANOTHER_PLAYBACK_KEY]
                    ?: DEFAULT_PAUSE_IF_ANOTHER_PLAYBACK_CHECKED

                audioOffloadChecked.value =
                    preferences[AUDIO_OFFLOAD_CHECKED_KEY] ?: DEFAULT_AUDIO_OFFLOAD_CHECKED

                whatsNewSeen.value = preferences[WHATS_NEW_SEEN_KEY] ?: DEFAULT_WHATS_NEW_SEEN

            }.first()
        }
    }

    fun switchMenuTitle(context: Context, menuTitle: MenuTitle) {
        runBlocking {
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
        }
    }

    fun switchPlaybackWhenClosedChecked(context: Context) {
        runBlocking {
            context.dataStore.edit { preferences: MutablePreferences ->
                playbackWhenClosedChecked.value = !playbackWhenClosedChecked.value
                preferences[PLAYBACK_WHEN_CLOSED_CHECKED_PREFERENCES_KEY] =
                    playbackWhenClosedChecked.value
            }
        }
    }

    fun switchPauseIfNoisy(context: Context) {
        runBlocking {
            context.dataStore.edit { preferences: MutablePreferences ->
                pauseIfNoisyChecked.value = !pauseIfNoisyChecked.value
                preferences[PAUSE_IF_NOISY_PREFERENCES_KEY] = pauseIfNoisyChecked.value
            }
        }
    }

    fun switchExcludeRingtones(context: Context) {
        runBlocking {
            context.dataStore.edit { preferences: MutablePreferences ->
                excludeRingtonesChecked.value = !excludeRingtonesChecked.value
                preferences[EXCLUDE_RINGTONES_KEY] = excludeRingtonesChecked.value
            }
        }
    }

    fun updateBarSpeed(context: Context, newValue: Float) {
        runBlocking {
            context.dataStore.edit { preferences: MutablePreferences ->
                barSpeed.value = newValue
                preferences[BAR_SPEED_KEY] = barSpeed.value
            }
        }
    }

    fun updateRepeatMode(context: Context, newValue: Int) {
        runBlocking {
            context.dataStore.edit { preferences: MutablePreferences ->
                repeatMode.intValue = newValue
                preferences[REPEAT_MODE_KEY] = repeatMode.intValue
            }
        }
    }

    fun switchShuffleMode(context: Context) {
        runBlocking {
            context.dataStore.edit { preferences: MutablePreferences ->
                shuffleMode.value = !shuffleMode.value
                preferences[SHUFFLE_MODE_KEY] = shuffleMode.value
            }
        }
    }

    fun switchPauseIfPlayback(context: Context) {
        runBlocking {
            context.dataStore.edit { preferences: MutablePreferences ->
                pauseIfAnotherPlayback.value = !pauseIfAnotherPlayback.value
                preferences[PAUSE_IF_ANOTHER_PLAYBACK_KEY] = pauseIfAnotherPlayback.value
            }
        }
    }

    fun switchAudioOffload(context: Context) {
        runBlocking {
            context.dataStore.edit { preferences: MutablePreferences ->
                audioOffloadChecked.value = !audioOffloadChecked.value
                preferences[AUDIO_OFFLOAD_CHECKED_KEY] = audioOffloadChecked.value
            }
        }
    }

    fun whatsNewSeen(context: Context, seen: Boolean) {
        runBlocking {
            context.dataStore.edit { preferences: MutablePreferences ->
                whatsNewSeen.value = seen
                preferences[WHATS_NEW_SEEN_KEY] = whatsNewSeen.value
            }
        }
    }
}