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

package io.github.antoinepirlot.satunes.ui.components.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import io.github.antoinepirlot.jetpack_libs.components.texts.NormalText
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.icons.SatunesIcons

/**
 * @author Antoine Pirlot on 14/10/2024
 */

@Composable
internal fun Dialog(
    modifier: Modifier = Modifier,
    icon: SatunesIcons,
    title: String,
    onDismissRequest: () -> Unit,
    onConfirmRequest: () -> Unit,
    confirmText: String? = stringResource(id = R.string.ok),
    dismissText: String? = stringResource(id = R.string.cancel),
    content: @Composable () -> Unit,
) {
    AlertDialog(
        modifier = modifier,
        icon = {
            Icon(
                imageVector = icon.imageVector,
                contentDescription = icon.description
            )
        },
        title = {
            NormalText(text = title)
        },
        text = content,
        onDismissRequest = onDismissRequest,
        confirmButton = {
            if (confirmText != null) {
                TextButton(onClick = onConfirmRequest) {
                    NormalText(text = confirmText)
                }
            }
        },
        dismissButton = if (dismissText != null) {
            {
                TextButton(onClick = onDismissRequest) {
                    NormalText(text = dismissText)
                }
            }
        } else null
    )
}