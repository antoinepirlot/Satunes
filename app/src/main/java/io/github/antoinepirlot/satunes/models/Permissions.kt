/*
 * This file is part of Satunes.
 *
 * Satunes is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * Satunes is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with Satunes.
 * If not, see <https://www.gnu.org/licenses/>.
 *
 * *** INFORMATION ABOUT THE AUTHOR *****
 * The author of this file is Antoine Pirlot, the owner of this project.
 * You find this original project on Codeberg.
 *
 * My Codeberg link is: https://codeberg.org/antoinepirlot
 * This current project's link is: https://codeberg.org/antoinepirlot/Satunes
 */

package io.github.antoinepirlot.satunes.models

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_AUDIO
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.SnackbarHostState
import io.github.antoinepirlot.satunes.MainActivity
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.data.viewmodels.SatunesViewModel
import io.github.antoinepirlot.satunes.icons.SatunesIcons
import io.github.antoinepirlot.satunes.utils.initSatunes
import kotlinx.coroutines.CoroutineScope

/**
 * @author Antoine Pirlot on 29/04/2024
 */
internal enum class Permissions(
    val stringId: Int,
    val value: String,
    val icon: SatunesIcons,
    val onGranted: (satunesViewModel: SatunesViewModel, scope: CoroutineScope, snackbarHostState: SnackbarHostState) -> Unit
) {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    READ_AUDIO_PERMISSION(
        R.string.read_audio_permission,
        value = READ_MEDIA_AUDIO,
        icon = SatunesIcons.MUSIC,
        onGranted = { satunesViewModel: SatunesViewModel, scope: CoroutineScope, snackbarHostState: SnackbarHostState ->
            onAudioGranted(
                satunesViewModel = satunesViewModel,
                scope = scope,
                snackbarHostState = snackbarHostState
            )
        }
    ),
    READ_EXTERNAL_STORAGE_PERMISSION(
        stringId = R.string.read_external_storage_permission,
        value = READ_EXTERNAL_STORAGE,
        icon = SatunesIcons.FOLDER,
        onGranted = { satunesViewModel: SatunesViewModel, scope: CoroutineScope, snackbarHostState: SnackbarHostState ->
            onAudioGranted(
                satunesViewModel = satunesViewModel,
                scope = scope,
                snackbarHostState = snackbarHostState
            )
        }
    )
}

/**
 * Reload playback
 */
private fun onAudioGranted(
    satunesViewModel: SatunesViewModel,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState
) {
    initSatunes(
        context = MainActivity.instance.applicationContext,
        satunesViewModel = satunesViewModel,
        scope = scope,
        snackbarHostState = snackbarHostState
    )
}