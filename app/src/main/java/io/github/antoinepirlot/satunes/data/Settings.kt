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

package io.github.antoinepirlot.satunes.data

import android.os.Build
import io.github.antoinepirlot.satunes.database.models.FoldersSelection
import io.github.antoinepirlot.satunes.models.Permissions
import io.github.antoinepirlot.satunes.models.SwitchSettings

/**
 * @author Antoine Pirlot on 26/07/2024
 */

internal val permissionsList: List<Permissions> =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        listOf(
            Permissions.READ_AUDIO_PERMISSION,
        )
    } else {
        listOf(
            Permissions.READ_EXTERNAL_STORAGE_PERMISSION,
        )
    }

internal val switchSettingsNeedRestarts: List<SwitchSettings> = listOf(
    SwitchSettings.AUDIO_OFFLOAD,
    SwitchSettings.PAUSE_IF_ANOTHER_PLAYBACK,
    SwitchSettings.PAUSE_IF_NOISY,
)

internal val switchSettingsNeedReloadLibrary: List<SwitchSettings> = listOf(
    SwitchSettings.COMPILATION_MUSIC,
    SwitchSettings.ARTIST_REPLACEMENT
)

internal val allFoldersSelections: List<FoldersSelection> = listOf(
    FoldersSelection.INCLUDE,
    FoldersSelection.EXCLUDE
)