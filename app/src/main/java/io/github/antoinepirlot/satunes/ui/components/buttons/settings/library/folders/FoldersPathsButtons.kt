/*
 * This file is part of Satunes.
 *
 *  Satunes is free software: you can redistribute it and/or modify it under
 *  the terms of the GNU General Public License as published by the Free Software Foundation,
 *  either version 3 of the License, or (at your option) any later version.
 *
 *  Satunes is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with Satunes.
 *  If not, see <https://www.gnu.org/licenses/>.
 *
 *  **** INFORMATIONS ABOUT THE AUTHOR *****
 *  The author of this file is Antoine Pirlot, the owner of this project.
 *  You find this original project on github.
 *
 *  My github link is: https://github.com/antoinepirlot
 *  This current project's link is: https://github.com/antoinepirlot/Satunes
 *
 *  You can contact me via my email: pirlot.antoine@outlook.com
 *  PS: I don't answer quickly.
 */

package io.github.antoinepirlot.satunes.ui.components.buttons.settings.library.folders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.jetpack_libs.components.texts.NormalText
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.data.local.LocalMainScope
import io.github.antoinepirlot.satunes.data.local.LocalSnackBarHostState
import io.github.antoinepirlot.satunes.data.states.SatunesUiState
import io.github.antoinepirlot.satunes.data.viewmodels.DataViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.PlaybackViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.SatunesViewModel
import io.github.antoinepirlot.satunes.icons.SatunesIcons
import io.github.antoinepirlot.satunes.ui.components.buttons.ButtonWithIcon
import io.github.antoinepirlot.satunes.ui.components.dialog.WarningDialog
import kotlinx.coroutines.CoroutineScope

/**
 * @author Antoine Pirlot on 12/09/2024
 */

@Composable
internal fun FoldersPathButtons(
    modifier: Modifier = Modifier,
    satunesViewModel: SatunesViewModel = viewModel(),
    playbackViewModel: PlaybackViewModel = viewModel(),
    dataViewModel: DataViewModel = viewModel(),
) {
    val satunesUiState: SatunesUiState by satunesViewModel.uiState.collectAsState()
    val scope: CoroutineScope = LocalMainScope.current
    val snackBarHostState: SnackbarHostState = LocalSnackBarHostState.current

    val spacerSize: Dp = 5.dp

    var showDialog: Boolean by rememberSaveable { mutableStateOf(false) }

    //Show text "Add path to exclude/include".
    Column(
        modifier = modifier.width(250.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        ButtonWithIcon(
            modifier = Modifier.fillMaxWidth(),
            icon = SatunesIcons.ADD,
            onClick = { satunesViewModel.addPath() },
            text = stringResource(
                id = R.string.add_path_button,
                stringResource(
                    id = satunesUiState.foldersSelectionSelected.stringId
                ).lowercase()
            )
        )

        Spacer(modifier = Modifier.size(spacerSize))

        ButtonWithIcon(
            modifier = Modifier.fillMaxWidth(),
            icon = SatunesIcons.REFRESH,
            onClick = { showDialog = true },
            enabled = !satunesViewModel.isLoadingData,
            isLoading = satunesViewModel.isLoadingData,
            text = stringResource(id = R.string.refresh_data_button)
        )
        if (showDialog) {
            WarningDialog(
                text = stringResource(id = R.string.export_all_dialog_content),
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    Row {
                        TextButton(onClick = {
                            showDialog = false
                            satunesViewModel.reloadAllData(playbackViewModel = playbackViewModel)
                        }) {
                            NormalText(text = stringResource(id = R.string.reload_library_no_export_button))
                        }

                        TextButton(onClick = {
                            dataViewModel.exportPlaylists(
                                scope = scope,
                                snackBarHostState = snackBarHostState
                            )
                            showDialog = false
                            satunesViewModel.reloadAllData(playbackViewModel = playbackViewModel)
                        }) {
                            NormalText(text = stringResource(id = R.string.export))
                        }
                    }
                }
            )
        }
    }
}

@Preview
@Composable
private fun FoldersPathButtonsPreview() {
    FoldersPathButtons()
}