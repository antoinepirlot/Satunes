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

package io.github.antoinepirlot.satunes.data.states

import io.github.antoinepirlot.satunes.data.DEFAULT_DESTINATION
import io.github.antoinepirlot.satunes.database.models.BarSpeed
import io.github.antoinepirlot.satunes.database.models.NavBarSection
import io.github.antoinepirlot.satunes.database.services.settings.SettingsManager
import io.github.antoinepirlot.satunes.data.viewmodels.utils.isAudioAllowed

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
    val includeRingtonesChecked: Boolean = SettingsManager.includeRingtonesChecked,
    val playbackWhenClosedChecked: Boolean = SettingsManager.playbackWhenClosedChecked,
    val pauseIfNoisyChecked: Boolean = SettingsManager.pauseIfNoisyChecked,
    val pauseIfAnotherPlayback: Boolean = SettingsManager.pauseIfAnotherPlayback,
    val shuffleMode: Boolean = SettingsManager.shuffleMode,
    val repeatMode: Int = SettingsManager.repeatMode,
    val audioOffloadChecked: Boolean = SettingsManager.audioOffloadChecked,
    val barSpeed: BarSpeed = SettingsManager.barSpeed,
    val isMediaOptionsOpened: Boolean = false
)
