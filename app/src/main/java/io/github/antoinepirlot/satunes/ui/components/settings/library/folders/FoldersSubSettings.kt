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

package io.github.antoinepirlot.satunes.ui.components.settings.library.folders

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.data.local.LocalMainScope
import io.github.antoinepirlot.satunes.data.local.LocalSnackBarHostState
import io.github.antoinepirlot.satunes.data.states.SatunesUiState
import io.github.antoinepirlot.satunes.data.viewmodels.SatunesViewModel
import io.github.antoinepirlot.satunes.ui.components.dialog.InformationDialog
import io.github.antoinepirlot.satunes.ui.components.settings.SubSettings
import kotlinx.coroutines.CoroutineScope
import io.github.antoinepirlot.satunes.database.R as RDb

/**
 * @author Antoine Pirlot on 31/08/2024
 */

@Composable
internal fun FoldersSubSettings(
    modifier: Modifier = Modifier,
    satunesViewModel: SatunesViewModel = viewModel()
) {
    val satunesUiState: SatunesUiState by satunesViewModel.uiState.collectAsState()

    val scope: CoroutineScope = LocalMainScope.current
    val snackbarHostState: SnackbarHostState = LocalSnackBarHostState.current

    var showDialog: Boolean by rememberSaveable { mutableStateOf(true) }

    SubSettings(
        modifier = modifier,
        title = stringResource(R.string.folders_settings)
    ) {
        if (showDialog && !satunesUiState.includeExcludeSeen)
            InformationDialog(
                title = "${stringResource(RDb.string.include)} / ${stringResource(RDb.string.exclude)}",
                text = stringResource(R.string.include_exclude_change_text),
                onDismissRequest = {
                    showDialog = false
                    satunesViewModel.seeIncludeExcludeInfo(
                        scope = scope,
                        snackbarHostState = snackbarHostState
                    )
                },
                onConfirm = {
                    showDialog = false
                    satunesViewModel.seeIncludeExcludeInfo(
                        scope = scope,
                        snackbarHostState = snackbarHostState,
                        permanently = true
                    )
                }
            )
        FoldersRowSelection()
        FoldersPathsSelection()
    }
}

@Preview
@Composable
private fun FoldersSubSettingsPreview() {
    FoldersSubSettings()
}