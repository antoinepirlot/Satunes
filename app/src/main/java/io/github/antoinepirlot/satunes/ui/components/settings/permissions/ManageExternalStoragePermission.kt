package io.github.antoinepirlot.satunes.ui.components.settings.permissions

import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.github.antoinepirlot.satunes.icons.SatunesIcons
import io.github.antoinepirlot.satunes.models.Permissions
import io.github.antoinepirlot.satunes.utils.openManageExternalPermissionSetting

@RequiresApi(Build.VERSION_CODES.R)
@Composable
internal fun ManageExternalStoragePermission(
    modifier: Modifier = Modifier,
    permission: Permissions,
    permissionIcon: SatunesIcons
) {
    val isGranted: Boolean by rememberSaveable { mutableStateOf(Environment.isExternalStorageManager()) }
    Permission(
        modifier = modifier,
        isGranted = isGranted,
        icon = permissionIcon,
        title = stringResource(permission.stringId),
        onClick = { openManageExternalPermissionSetting() },
    )
}

@RequiresApi(Build.VERSION_CODES.R)
@Preview
@Composable
fun ManageExternalStoragePermissionPreview(modifier: Modifier = Modifier) {
    ManageExternalStoragePermission(
        permission = Permissions.MANAGE_EXTERNAL_STORAGE_PERMISSION,
        permissionIcon = SatunesIcons.FOLDER
    )
}