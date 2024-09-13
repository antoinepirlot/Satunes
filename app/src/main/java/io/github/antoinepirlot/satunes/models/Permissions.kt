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

package io.github.antoinepirlot.satunes.models

import android.Manifest.permission.ACCESS_MEDIA_LOCATION
import android.Manifest.permission.MANAGE_MEDIA
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_AUDIO
import android.os.Build
import androidx.annotation.RequiresApi
import io.github.antoinepirlot.satunes.R

/**
 * @author Antoine Pirlot on 29/04/2024
 */
internal enum class Permissions(val stringId: Int, val value: String) {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    READ_AUDIO_PERMISSION(R.string.read_audio_permission, value = READ_MEDIA_AUDIO),
    READ_EXTERNAL_STORAGE_PERMISSION(
        stringId = R.string.read_external_storage_permission,
        value = READ_EXTERNAL_STORAGE
    ),

    @RequiresApi(Build.VERSION_CODES.S)
    MANAGE_MEDIA_PERMISSION(stringId = R.string.manage_media_permission, value = MANAGE_MEDIA),

    @RequiresApi(Build.VERSION_CODES.Q)
    ACCESS_MEDIA_LOCATION_PERMISSION(
        stringId = R.string.access_media_location_permission,
        value = ACCESS_MEDIA_LOCATION
    )
}