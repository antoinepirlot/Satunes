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

package io.github.antoinepirlot.satunes.ui.components.buttons.settings.library.playlists

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.jetpack_libs.components.texts.NormalText
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.data.local.LocalMainScope
import io.github.antoinepirlot.satunes.data.local.LocalSnackBarHostState
import io.github.antoinepirlot.satunes.data.viewmodels.DataViewModel
import io.github.antoinepirlot.satunes.icons.SatunesIcons
import io.github.antoinepirlot.satunes.ui.components.buttons.ButtonWithIcon
import io.github.antoinepirlot.satunes.ui.components.dialog.WarningDialog
import kotlinx.coroutines.CoroutineScope

/**
 * @author Antoine Pirlot on 12/09/2024
 */

@Composable
internal fun CleanPlaylistsButton(
    modifier: Modifier = Modifier,
    dataViewModel: DataViewModel = viewModel(),
) {
    var showDialog: Boolean by remember { mutableStateOf(false) }

    ButtonWithIcon(
        modifier = modifier.fillMaxWidth(),
        icon = SatunesIcons.CLEANING,
        onClick = { showDialog = true },
        text = stringResource(id = R.string.clean_playlist_button_text),
    )

    if (showDialog) {
        val scope: CoroutineScope = LocalMainScope.current
        val snackBarHostState: SnackbarHostState = LocalSnackBarHostState.current

        WarningDialog(
            text = stringResource(R.string.clean_playlists_dialog_content),
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        dataViewModel.cleanPlaylists(
                            scope = scope,
                            snackBarHostState = snackBarHostState
                        )
                        showDialog = false
                    }
                ) {
                    NormalText(text = stringResource(R.string.clean_playlist_button_text))
                }
                //Put export on the far right to prevent too fast user click and prevent cleaning in that case
                TextButton(
                    onClick = {
                        dataViewModel.exportPlaylists(
                            scope = scope,
                            snackBarHostState = snackBarHostState
                        )
                    }
                ) {
                    NormalText(text = stringResource(R.string.export))
                }
            },
        )
    }
}

@Preview
@Composable
private fun CleanPlaylistsSettingPreview() {
    CleanPlaylistsButton()
}