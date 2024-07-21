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

package io.github.antoinepirlot.satunes.ui.states

import io.github.antoinepirlot.satunes.database.models.NavBarSection
import io.github.antoinepirlot.satunes.database.services.settings.SettingsManager
import io.github.antoinepirlot.satunes.models.DEFAULT_DESTINATION
import io.github.antoinepirlot.satunes.models.SwitchSettings
import io.github.antoinepirlot.satunes.ui.viewmodels.utils.isAudioAllowed

/**
 * @author Antoine Pirlot on 19/07/2024
 */
internal data class SatunesUiState(
    val whatsNewSeen: Boolean = SettingsManager.whatsNewSeen,
    val currentDestination: String = DEFAULT_DESTINATION,
    //Use this in UiSate and ViewModel as it is a particular value. It could change but most of the time it won't change
    val isAudioAllowed: Boolean = isAudioAllowed(),

    val foldersChecked: Boolean = SettingsManager.foldersChecked.value,
    val artistsChecked: Boolean = SettingsManager.artistsChecked.value,
    val albumsChecked: Boolean = SettingsManager.albumsChecked.value,
    val genresChecked: Boolean = SettingsManager.genresChecked.value,
    val playlistsChecked: Boolean = SettingsManager.playlistsChecked.value,
    val selectedNavBarSection: NavBarSection =
    // Selected the default menu title in this priority order
        if (foldersChecked) NavBarSection.FOLDERS
        else if (artistsChecked) NavBarSection.ARTISTS
        else if (albumsChecked) NavBarSection.ALBUMS
        else if (genresChecked) NavBarSection.GENRES
        else NavBarSection.MUSICS,
    val navBarSectionSettingsChecked: Map<SwitchSettings, Boolean> = mapOf(
        Pair(first = SwitchSettings.FOLDERS_CHECKED, second = foldersChecked),
        Pair(first = SwitchSettings.ARTISTS_CHECKED, second = artistsChecked),
        Pair(first = SwitchSettings.ALBUMS_CHECKED, second = albumsChecked),
        Pair(first = SwitchSettings.GENRES_CHECKED, second = genresChecked),
        Pair(first = SwitchSettings.PLAYLISTS_CHECKED, second = playlistsChecked),
    ),

    val includeRingtonesChecked: Boolean = SettingsManager.includeRingtonesChecked,
    val exclusionSettingsChecked: Map<SwitchSettings, Boolean> = mapOf(
        Pair(first = SwitchSettings.INCLUDE_RINGTONES, second = includeRingtonesChecked)
    ),

    val musicsFilter: Boolean = SettingsManager.musicsFilter,
    val albumsFilter: Boolean = SettingsManager.albumsFilter,
    val artistsFilter: Boolean = SettingsManager.artistsFilter,
    val genresFilter: Boolean = SettingsManager.genresFilter,
    val foldersFilter: Boolean = SettingsManager.foldersFilter,
    val playlistsFilter: Boolean = SettingsManager.playlistsFilter,
    val playbackWhenClosedChecked: Boolean = SettingsManager.playbackWhenClosedChecked,
    val pauseIfNoisyChecked: Boolean = SettingsManager.pauseIfNoisyChecked,
    val pauseIfAnotherPlayback: Boolean = SettingsManager.pauseIfAnotherPlayback,
    val shuffleMode: Boolean = SettingsManager.shuffleMode,
    val repeatMode: Int = SettingsManager.repeatMode,
    val audioOffloadChecked: Boolean = SettingsManager.audioOffloadChecked,
    val barSpeed: Float = SettingsManager.barSpeed,
) {
    init {

    }
}
