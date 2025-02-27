package io.github.antoinepirlot.satunes.data.viewmodels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import io.github.antoinepirlot.satunes.database.services.data.ErrorCatcher

class ErrorCatcherViewModel : ViewModel() {

    @RequiresApi(Build.VERSION_CODES.R)
    private val _needManageExternalStoragePermission: MutableState<Boolean> =
        ErrorCatcher.needManageExternalStoragePermission

    @delegate:RequiresApi(Build.VERSION_CODES.R)
    val needManageExternalStoragePermission: Boolean by _needManageExternalStoragePermission
}