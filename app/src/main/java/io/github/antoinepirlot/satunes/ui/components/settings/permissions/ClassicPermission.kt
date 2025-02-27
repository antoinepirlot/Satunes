package io.github.antoinepirlot.satunes.ui.components.settings.permissions

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import io.github.antoinepirlot.satunes.MainActivity
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.data.viewmodels.SatunesViewModel
import io.github.antoinepirlot.satunes.icons.SatunesIcons
import io.github.antoinepirlot.satunes.models.Permissions
import io.github.antoinepirlot.satunes.utils.openManageExternalPermissionSetting

@SuppressLint("NewApi")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
internal fun ClassicPermission(
    modifier: Modifier = Modifier,
    satunesViewModel: SatunesViewModel = viewModel(),
    permission: Permissions,
    permissionIcon: SatunesIcons
) {
    val spacerSize = 16.dp
    val permissionState: PermissionState =
        rememberPermissionState(permission = permission.value)
    if (permission == Permissions.READ_EXTERNAL_STORAGE_PERMISSION || permission == Permissions.READ_AUDIO_PERMISSION)
        if (permissionState.status.isGranted && !satunesViewModel.isAudioAllowed)
            satunesViewModel.updateIsAudioAllowed()
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
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
                if (permission == Permissions.MANAGE_EXTERNAL_STORAGE_PERMISSION)
                    openManageExternalPermissionSetting()
                else askPermission(permissionState = permissionState)
            }) {
                NormalText(text = stringResource(id = R.string.ask_permission))
            }
        }
    }
}

@Preview
@Composable
private fun ClassicPermissionPreview() {
    ClassicPermission(
        permission = Permissions.READ_EXTERNAL_STORAGE_PERMISSION,
        permissionIcon = SatunesIcons.FOLDER
    )
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