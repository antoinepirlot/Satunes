package io.github.antoinepirlot.satunes.database.services.data

import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

object ErrorCatcher {

    @RequiresApi(Build.VERSION_CODES.R)
    val needManageExternalStoragePermission: MutableState<Boolean> =
        mutableStateOf(Environment.isExternalStorageManager())

    @RequiresApi(Build.VERSION_CODES.R)
    fun manageExternalStoragePermissionNeeded() {
        this.needManageExternalStoragePermission.value = true
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun manageExternalStoragePermissionGranted() {
        this.needManageExternalStoragePermission.value = false
    }
}