/*
 * This file is part of Satunes.
 *
 * Satunes is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * Satunes is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License along with Satunes.
 *
 * If not, see <https://www.gnu.org/licenses/>.
 *
 * **** INFORMATION ABOUT THE AUTHOR *****
 * The author of this file is Antoine Pirlot, the owner of this project.
 * You find this original project on Codeberg.
 *
 * My Codeberg link is: https://codeberg.org/antoinepirlot
 * This current project's link is: https://codeberg.org/antoinepirlot/Satunes
 */

package io.github.antoinepirlot.satunes.database.services.settings

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.MutableLongState
import androidx.compose.runtime.MutableState
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import io.github.antoinepirlot.satunes.database.models.BarSpeed
import io.github.antoinepirlot.satunes.database.models.FoldersSelection
import io.github.antoinepirlot.satunes.database.models.NavBarSection
import io.github.antoinepirlot.satunes.database.models.Playlist
import io.github.antoinepirlot.satunes.database.models.UpdateChannel
import io.github.antoinepirlot.satunes.database.models.custom_action.CustomActions
import io.github.antoinepirlot.satunes.database.services.data.DataLoader
import io.github.antoinepirlot.satunes.database.services.settings.design.DesignSettings
import io.github.antoinepirlot.satunes.database.services.settings.library.LibrarySettings
import io.github.antoinepirlot.satunes.database.services.settings.playback.PlaybackSettings
import io.github.antoinepirlot.satunes.database.services.settings.search.SearchSettings
import io.github.antoinepirlot.satunes.utils.logger.SatunesLogger

/**
 * @author Antoine Pirlot on 02-03-24
 */

object SettingsManager {
    private val PREFERENCES_DATA_STORE = preferencesDataStore("settings")
    private val _logger = SatunesLogger.getLogger()
    internal val Context.dataStore: DataStore<Preferences> by PREFERENCES_DATA_STORE
    private var _isLoaded: Boolean = false

    // Satunes Settings
    val whatsNewSeen: Boolean
        get() = SatunesSettings.whatsNewSeen
    val includeExcludeSeen: Boolean
        get() = SatunesSettings.includeExcludeSeen
    val logsActivation: MutableState<Boolean> = SatunesSettings.logsActivation

    // NavBarSettings
    val defaultNavBarSection: MutableState<NavBarSection> = DesignSettings.defaultNavBarSection
    val defaultPlaylistId: MutableLongState = DesignSettings.defaultPlaylistId

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
    val customActionsOrder: Collection<CustomActions> = DesignSettings.customActionsOrder

    //Design
    val artworkAnimation: MutableState<Boolean> = DesignSettings.artworkAnimation
    val artworkCircleShape: MutableState<Boolean> = DesignSettings.artworkCircleShape

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

    // Library Settings
    val foldersPathsIncludingCollection: Collection<String>
        get() = LibrarySettings.foldersPathsIncludingCollection
    val foldersPathsExcludingCollection: Collection<String>
        get() = LibrarySettings.foldersPathsExcludingCollection
    val subsonicUrl: String
        get() = LibrarySettings.subsonicUrl

    //Update Settings
    val updateChannel: MutableState<UpdateChannel>
        get() = SatunesSettings.updateChannel

    /**
     * This setting is true if the compilation's music has to be added to compilation's artist's music list
     */
    val compilationMusic: Boolean
        get() = LibrarySettings.compilationMusic

    val artistReplacement: Boolean
        get() = LibrarySettings.artistReplacement

    val showFirstLetter: Boolean
        get() = DesignSettings.showFirstLetter

    suspend fun loadSettings(context: Context) {
        if (_isLoaded) {
            _logger?.info("Settings already loaded")
            return
        }
        SatunesSettings.loadSettings(context = context)
        SatunesLogger.enabled = this.logsActivation.value
        DesignSettings.loadSettings(context = context)
        PlaybackSettings.loadSettings(context = context)
        loadFilters(context = context)
        LibrarySettings.loadSettings(context = context)
        DataLoader.loadFoldersPaths()
        _isLoaded = true
    }

    suspend fun loadFilters(context: Context) {
        SearchSettings.loadSettings(context = context)
    }

    suspend fun switchNavBarSection(context: Context, navBarSection: NavBarSection) {
        DesignSettings.switchNavBarSection(context = context, navBarSection = navBarSection)
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

    suspend fun switchArtworkAnimation(context: Context) {
        DesignSettings.switchArtworkAnimation(context = context)
    }

    suspend fun switchArtworkCircleShape(context: Context) {
        DesignSettings.switchArtworkCircleShape(context = context)
    }

    suspend fun seeWhatsNew(context: Context) {
        SatunesSettings.seeWhatsNew(context = context)
    }

    suspend fun unSeeWhatsNew(context: Context) {
        SatunesSettings.unSeeWhatsNew(context = context)
    }

    suspend fun switchFilter(context: Context, filterSetting: NavBarSection) {
        SearchSettings.switchFilter(context = context, filterSetting = filterSetting)
    }

    /**
     * Add a path to the including list or excluding list base on param [folderSelection] and memorize it in storage.
     *
     * @param context the app context
     * @param uri the uri containing the selected path
     * @param folderSelection the option selected on screen.
     */
    suspend fun addPath(context: Context, uri: Uri, folderSelection: FoldersSelection) {
        this.addPath(context = context, path = uri.path!!, folderSelection = folderSelection)
    }

    /**
     * Add a path to the including list or excluding list base on param [folderSelection] and memorize it in storage.
     *
     * @param context the app context
     * @param path the selected path as string
     * @param folderSelection the option selected on screen.
     */
    suspend fun addPath(context: Context, path: String, folderSelection: FoldersSelection) {
        LibrarySettings.addPath(context = context, path = path, folderSelection = folderSelection)
    }

    suspend fun removePath(context: Context, path: String, folderSelection: FoldersSelection) {
        LibrarySettings.removePath(
            context = context,
            path = path,
            folderSelection = folderSelection
        )
    }

    suspend fun selectDefaultNavBarSection(context: Context, navBarSection: NavBarSection) {
        DesignSettings.selectDefaultNavBarSection(context = context, navBarSection = navBarSection)
    }

    suspend fun switchCompilationMusic(context: Context) {
        LibrarySettings.switchCompilationMusic(context = context)
    }

    suspend fun switchArtistReplacement(context: Context) {
        LibrarySettings.switchArtistReplacement(context = context)
    }

    suspend fun updateForwardMs(context: Context, seconds: Int) {
        PlaybackSettings.updateForwardMs(context = context, seconds = seconds)
    }

    suspend fun updateRewindMs(context: Context, seconds: Int) {
        PlaybackSettings.updateRewindMs(context = context, seconds = seconds)
    }

    suspend fun switchShowFirstLetter(context: Context) {
        DesignSettings.switchShowFirstLetter(context = context)
    }

    suspend fun resetFoldersSettings(context: Context) {
        LibrarySettings.resetFoldersSettings(context = context)
    }

    suspend fun resetLoadingLogicSettings(context: Context) {
        LibrarySettings.resetLoadingLogicSettings(context = context)
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
        DesignSettings.resetNavigationBarSettings(context = context)
    }

    suspend fun moveUp(context: Context, customAction: CustomActions) {
        DesignSettings.moveUp(context = context, customAction = customAction)
    }

    suspend fun moveDown(context: Context, customAction: CustomActions) {
        DesignSettings.moveDown(context = context, customAction = customAction)
    }

    suspend fun selectDefaultPlaylist(context: Context, playlist: Playlist?) {
        DesignSettings.selectDefaultPlaylist(context = context, playlist = playlist)
    }

    suspend fun resetAll(context: Context) {
        SatunesSettings.reset(context = context)
        PlaybackSettings.resetAll(context = context)
        SearchSettings.resetAll(context = context)
        LibrarySettings.resetAll(context = context)
        DesignSettings.resetAll(context = context)
    }

    /**
     * Check if the default playlist is correct.
     * If the value stored is > 0 but the matching playlist is null. Then reset the value.
     */
    suspend fun checkDefaultPlaylistSetting(context: Context) {
        DesignSettings.checkDefaultPlaylistSetting(context = context)
    }

    suspend fun resetListsSettings(context: Context) {
        DesignSettings.resetListsSettings(context = context)
    }

    suspend fun resetCustomActions(context: Context) {
        DesignSettings.resetCustomActions(context = context)
    }

    suspend fun switchLogsActivation(context: Context) {
        SatunesSettings.switchLogsActivation(context)
        SatunesLogger.getLogger()?.info(
            if (this.logsActivation.value) "Logs enabled." else "Logs Disabled."
        )
        SatunesLogger.enabled = this.logsActivation.value
    }

    suspend fun resetArtworkSettings(context: Context) {
        DesignSettings.resetArtworkSettings(context = context)
    }

    suspend fun seeIncludeExcludeInfo(context: Context) {
        SatunesSettings.seeIncludeExcludeInfo(context)
    }

    suspend fun unSeeIncludeExcludeInfo(context: Context) {
        SatunesSettings.unSeeIncludeExcludeInfo(context)
    }

    suspend fun selectUpdateChannel(context: Context, channel: UpdateChannel) {
        SatunesSettings.selectUpdateChannel(context = context, channel = channel)
    }

    suspend fun updateSubsonicUrl(context: Context, url: String) {
        LibrarySettings.updateSubsonicUrl(context = context, url = url)
    }
}
