package io.github.antoinepirlot.satunes.ui.components.settings.permissions

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import androidx.compose.runtime.Composable
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
    val permissionState: PermissionState =
        rememberPermissionState(permission = permission.value)
    if (permission == Permissions.READ_EXTERNAL_STORAGE_PERMISSION || permission == Permissions.READ_AUDIO_PERMISSION)
        if (permissionState.status.isGranted && !satunesViewModel.isAudioAllowed)
            satunesViewModel.updateIsAudioAllowed()
    Permission(
        modifier = modifier,
        isGranted = permissionState.status.isGranted,
        icon = permission.icon,
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