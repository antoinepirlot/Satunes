/*
 *  This file is part of MP3 Player.
 *
 *  MP3 Player is free software: you can redistribute it and/or modify it under
 *  the terms of the GNU General Public License as published by the Free Software Foundation,
 *  either version 3 of the License, or (at your option) any later version.
 *
 *  MP3 Player is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *   without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with MP3 Player.
 *  If not, see <https://www.gnu.org/licenses/>.
 *
 *  ***** INFORMATIONS ABOUT THE AUTHOR *****
 *  The author of this file is Antoine Pirlot, the owner of this project.
 *  You find this original project on github.
 *
 *  My github link is: https://github.com/antoinepirlot
 *  This current project's link is: https://github.com/antoinepirlot/MP3-Player
 *
 *  You can contact me via my email: pirlot.antoine@outlook.com
 *  PS: I don't answer quickly.
 */

package earth.mp3player.services

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import earth.mp3player.models.MenuTitle
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * @author Antoine Pirlot on 02-03-24
 */
object SettingsManager {
    private const val DEFAULT_FOLDERS_CHECKED = true
    private const val DEFAULT_ARTISTS_CHECKED = true
    private const val DEFAULT_ALBUMS_CHECKED = true

    private val PREFERENCES_DATA_STORE = preferencesDataStore("settings")
    private val FOLDERS_CHECKED_PREFERENCES_KEY = booleanPreferencesKey("folders_checked")
    private val ARTISTS_CHECKED_PREFERENCES_KEY = booleanPreferencesKey("artist_checked")
    private val ALBUMS_CHECKED_PREFERENCES_KEY = booleanPreferencesKey("albums_checked")

    private val Context.dataStore: DataStore<Preferences> by PREFERENCES_DATA_STORE

    val foldersChecked: MutableState<Boolean> = mutableStateOf(DEFAULT_FOLDERS_CHECKED)
    val artistsChecked: MutableState<Boolean> = mutableStateOf(DEFAULT_ARTISTS_CHECKED)
    val albumsChecked: MutableState<Boolean> = mutableStateOf(DEFAULT_ALBUMS_CHECKED)

    val menuTitleCheckedMap: Map<MenuTitle, MutableState<Boolean>> = mapOf(
        Pair(MenuTitle.FOLDERS, foldersChecked),
        Pair(MenuTitle.ARTISTS, artistsChecked),
        Pair(MenuTitle.ALBUMS, albumsChecked),
    )

    suspend fun loadSettings(context: Context) {
        //Find a way to do it in one flow (maybe it's not possible)
        foldersChecked.value = context.dataStore.data.map { preferences: Preferences ->
            preferences[FOLDERS_CHECKED_PREFERENCES_KEY]
                ?: DEFAULT_FOLDERS_CHECKED
        }.first()

        artistsChecked.value = context.dataStore.data.map { preferences: Preferences ->
            preferences[ARTISTS_CHECKED_PREFERENCES_KEY]
                ?: DEFAULT_ARTISTS_CHECKED
        }.first()

        albumsChecked.value = context.dataStore.data.map { preferences: Preferences ->
            preferences[ALBUMS_CHECKED_PREFERENCES_KEY]
                ?: DEFAULT_ALBUMS_CHECKED
        }.first()
    }

    suspend fun switchMenuTitle(context: Context, menuTitle: MenuTitle) {
        when (menuTitle) {
            MenuTitle.FOLDERS -> {
                context.dataStore.edit { preferences: MutablePreferences ->
                    preferences[FOLDERS_CHECKED_PREFERENCES_KEY] = !foldersChecked.value
                }
            }

            MenuTitle.ARTISTS -> {
                context.dataStore.edit { preferences: MutablePreferences ->
                    preferences[ARTISTS_CHECKED_PREFERENCES_KEY] = !artistsChecked.value
                }
            }

            MenuTitle.ALBUMS -> {
                context.dataStore.edit { preferences: MutablePreferences ->
                    preferences[ALBUMS_CHECKED_PREFERENCES_KEY] = !albumsChecked.value
                }
            }

            MenuTitle.MUSIC -> { /*Do nothing*/
            }
        }
        loadSettings(context = context)
    }
}