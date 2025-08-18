/*
 * This file is part of Satunes.
 *
 * Satunes is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * Satunes is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License along with Satunes.
 *
 * If not, see <https://www.gnu.org/licenses/>.
 *
 * **** INFORMATION ABOUT THE AUTHOR *****
 * The author of this file is Antoine Pirlot, the owner of this project.
 * You find this original project on Codeberg.
 *
 * My Codeberg link is: https://codeberg.org/antoinepirlot
 * This current project's link is: https://codeberg.org/antoinepirlot/Satunes
 */

package io.github.antoinepirlot.satunes.ui.components.dialog.playlist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.jetpack_libs.components.texts.NormalText
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.data.local.LocalMainScope
import io.github.antoinepirlot.satunes.data.local.LocalSnackBarHostState
import io.github.antoinepirlot.satunes.data.viewmodels.DataViewModel
import io.github.antoinepirlot.satunes.database.models.FileExtensions
import io.github.antoinepirlot.satunes.icons.SatunesIcons
import io.github.antoinepirlot.satunes.ui.components.buttons.ButtonWithIcon
import io.github.antoinepirlot.satunes.ui.components.forms.playlists.FileExtensionSelection
import io.github.antoinepirlot.satunes.ui.components.forms.playlists.MusicsRootPathSelection
import io.github.antoinepirlot.satunes.ui.components.images.Icon
import kotlinx.coroutines.CoroutineScope

/**
 * @author Antoine Pirlot 16/08/2025
 */

@Composable
fun ExportImportPlaylistsDialog(
    modifier: Modifier = Modifier,
    dataViewModel: DataViewModel = viewModel(),
    export: Boolean, //false means import
) {
    val scope: CoroutineScope = LocalMainScope.current
    val snackBarHostState: SnackbarHostState = LocalSnackBarHostState.current
    val icon: SatunesIcons = if (export) SatunesIcons.EXPORT else SatunesIcons.IMPORT
    val stringId: Int = if (export) R.string.export else R.string._import

    val onDismissRequest: () -> Unit = {
        if (export) dataViewModel.closeExportPlaylistDialog()
        else dataViewModel.closeImportPlaylistDialog()
    }

    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        icon = {
            Icon(icon = icon)
        },
        title = {
            NormalText(text = stringResource(id = stringId))
        },
        confirmButton = {
            ButtonWithIcon(
                icon = icon, text = stringResource(id = stringId),
                onClick = {
                    if (export) {
                        dataViewModel.exportPlaylists(
                            scope = scope,
                            snackBarHostState = snackBarHostState,
                        )
                        dataViewModel.closeExportPlaylistDialog()
                    } else {
                        dataViewModel.importPlaylists()
                        dataViewModel.closeImportPlaylistDialog()
                    }
                },
            )
        },
        dismissButton = {
            Button(
                modifier = Modifier.height(40.dp),
                onClick = onDismissRequest
            ) {
                NormalText(text = stringResource(R.string.cancel))
            }
        },
        text = {
            Column(
                modifier = modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                NormalText(text = stringResource(R.string.file_format_information_text))
                FileExtensionSelection()
                if (export && dataViewModel.fileExtension == FileExtensions.M3U) {
                    MusicsRootPathSelection()
                    //TODO uncomment next line when a way to create multiple files has been found
//                    ExportMultipleFiles()
                }
            }
        },
    )
}