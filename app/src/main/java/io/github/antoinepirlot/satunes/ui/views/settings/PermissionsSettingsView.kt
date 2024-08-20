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

package io.github.antoinepirlot.satunes.ui.views.settings

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import io.github.antoinepirlot.jetpack_libs.components.texts.NormalText
import io.github.antoinepirlot.jetpack_libs.components.texts.Title
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.data.permissionsList
import io.github.antoinepirlot.satunes.data.viewmodels.SatunesViewModel
import io.github.antoinepirlot.satunes.icons.SatunesIcons
import io.github.antoinepirlot.satunes.models.Permissions

/**
 * @author Antoine Pirlot on 29/04/2024
 */

@OptIn(ExperimentalPermissionsApi::class)
@Composable
internal fun PermissionsSettingsView(
    modifier: Modifier = Modifier,
    satunesViewModel: SatunesViewModel = viewModel()
) {
    val spacerSize = 16.dp
    val context: Context = LocalContext.current
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {
        Title(text = stringResource(id = R.string.permissions))
        for (permission: Permissions in permissionsList) {
            val permissionState: PermissionState =
                rememberPermissionState(permission = permission.value)
            if (permissionState.status.isGranted && !satunesViewModel.isAudioAllowed) {
                satunesViewModel.updateIsAudioAllowed()
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                @SuppressLint("NewApi")
                val permissionIcon: SatunesIcons = when (permission) {
                    Permissions.READ_AUDIO_PERMISSION -> SatunesIcons.MUSIC
                    Permissions.READ_EXTERNAL_STORAGE_PERMISSION -> SatunesIcons.FOLDER
                }
                Icon(
                    imageVector = permissionIcon.imageVector,
                    contentDescription = permissionIcon.description
                )
                Spacer(modifier = Modifier.size(16.dp))
                NormalText(text = stringResource(id = permission.stringId))
                Spacer(modifier = Modifier.size(spacerSize))
                val icon: SatunesIcons =
                    if (permissionState.status.isGranted) SatunesIcons.PERMISSION_GRANTED
                    else SatunesIcons.PERMISSION_NOT_GRANTED

                Icon(
                    imageVector = icon.imageVector,
                    contentDescription = icon.description,
                    tint = if (permissionState.status.isGranted) Color.Green else Color.Red
                )
                if (!permissionState.status.isGranted) {
                    Spacer(modifier = Modifier.size(spacerSize))
                    Button(onClick = {
                        askPermission(
                            context = context,
                            permissionState = permissionState
                        )
                    }) {
                        NormalText(text = stringResource(id = R.string.ask_permission))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
private fun askPermission(context: Context, permissionState: PermissionState) {
    if (permissionState.status.shouldShowRationale) {
        val intent = Intent(ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", context.packageName, null)
        intent.data = uri
        context.startActivity(intent)
    } else {
        permissionState.launchPermissionRequest()
    }
}

@SuppressLint("UnrememberedMutableState")
@Preview
@Composable
private fun PermissionsSettingsViewPreview() {
    PermissionsSettingsView()
}