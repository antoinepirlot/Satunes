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

package io.github.antoinepirlot.satunes.ui.components.settings.permissions

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import io.github.antoinepirlot.satunes.MainActivity
import io.github.antoinepirlot.satunes.data.states.SatunesUiState
import io.github.antoinepirlot.satunes.data.viewmodels.SatunesViewModel
import io.github.antoinepirlot.satunes.models.Permissions

@SuppressLint("NewApi")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
internal fun ClassicPermission(
    modifier: Modifier = Modifier,
    satunesViewModel: SatunesViewModel = viewModel(),
    permission: Permissions
) {
    val satunesUiState: SatunesUiState by satunesViewModel.uiState.collectAsState()
    val permissionState: PermissionState =
        rememberPermissionState(
            permission = permission.value,
            onPermissionResult = { allowed: Boolean ->
                if (allowed) permission.onGranted(satunesViewModel)
            }
        )
    if (permission == Permissions.READ_EXTERNAL_STORAGE_PERMISSION || permission == Permissions.READ_AUDIO_PERMISSION)
        if (permissionState.status.isGranted && !satunesUiState.isAudioAllowed)
            satunesViewModel.updateIsAudioAllowed()
    Permission(
        modifier = modifier,
        isGranted = permissionState.status.isGranted,
        jetpackLibsIcons = permission.jetpackLibsIcons,
        title = stringResource(id = permission.stringId),
        onClick = { askPermission(permissionState = permissionState) },
    )
}

@Preview
@Composable
private fun ClassicPermissionPreview() {
    ClassicPermission(permission = Permissions.READ_EXTERNAL_STORAGE_PERMISSION)
}

@OptIn(ExperimentalPermissionsApi::class)
private fun askPermission(permissionState: PermissionState) {
    val context: Context = MainActivity.instance.applicationContext
    if (permissionState.status.shouldShowRationale) {
        val intent = Intent(ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", context.packageName, null)
        intent.data = uri
        context.startActivity(intent)
    } else {
        permissionState.launchPermissionRequest()
    }
}