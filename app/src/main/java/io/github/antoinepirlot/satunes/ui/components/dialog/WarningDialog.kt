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

package io.github.antoinepirlot.satunes.ui.components.dialog

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import io.github.antoinepirlot.jetpack_libs.components.texts.NormalText
import io.github.antoinepirlot.jetpack_libs.components.texts.Title
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.icons.SatunesIcons
import io.github.antoinepirlot.satunes.ui.components.images.Icon

/**
 * @author Antoine Pirlot on 19/08/2024
 */

@Composable
internal fun WarningDialog(
    modifier: Modifier = Modifier,
    text: String,
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
) {
    AlertDialog(
        modifier = modifier,
        icon = {
            Icon(icon = SatunesIcons.WARNING)
        },
        title = {
            Title(text = stringResource(id = R.string.warning), fontSize = 25.sp)
        },
        text = {
            val scrollState: ScrollState = rememberScrollState()
            NormalText(
                modifier = Modifier.verticalScroll(state = scrollState),
                text = text,
                maxLines = Int.MAX_VALUE
            )
        },
        confirmButton = confirmButton,
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                NormalText(text = stringResource(id = R.string.cancel))
            }
        },
        onDismissRequest = onDismissRequest,
    )
}

@Preview
@Composable
private fun AskToExportPlaylistsDialogPreview() {
    WarningDialog(onDismissRequest = {}, text = "Hello World!", confirmButton = {})
}