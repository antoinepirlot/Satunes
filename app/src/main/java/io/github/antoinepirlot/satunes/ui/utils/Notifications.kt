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
 * You find this original project on github.
 *
 * My github link is: https://github.com/antoinepirlot
 * This current project's link is: https://github.com/antoinepirlot/Satunes
 *
 * PS: I don't answer quickly.
 */

package io.github.antoinepirlot.satunes.ui.utils

import android.content.Context
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import io.github.antoinepirlot.satunes.MainActivity
import io.github.antoinepirlot.satunes.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * @author Antoine Pirlot on 25/07/2024
 */
internal fun showErrorSnackBar(
    scope: CoroutineScope,
    snackBarHostState: SnackbarHostState,
    action: () -> Unit,
) {
    val context: Context = MainActivity.instance.applicationContext
    showSnackBar(
        scope = scope,
        snackBarHostState = snackBarHostState,
        message = context.getString(R.string.error_occurred),
        actionLabel = context.getString(R.string.retry),
        action = action,
        duration = SnackbarDuration.Long
    )
}

internal fun showSnackBar(
    scope: CoroutineScope,
    snackBarHostState: SnackbarHostState,
    message: String,
    actionLabel: String? = null,
    withDismissAction: Boolean = true,
    duration: SnackbarDuration = if (actionLabel == null) SnackbarDuration.Short else SnackbarDuration.Indefinite,
    action: (() -> Unit)? = null,
) {
    if (actionLabel == null && action != null || actionLabel != null && action == null) {
        throw IllegalArgumentException("actionLabel or action are null. Both must be null or not null.")
    }
    scope.launch {
        val result: SnackbarResult = snackBarHostState.showSnackbar(
            message = message,
            actionLabel = actionLabel,
            withDismissAction = withDismissAction,
            duration = duration
        )

        if (result == SnackbarResult.ActionPerformed && action != null) {
            action()
        }
    }
}
