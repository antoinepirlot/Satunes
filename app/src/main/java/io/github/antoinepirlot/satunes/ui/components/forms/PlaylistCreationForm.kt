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

package io.github.antoinepirlot.satunes.ui.components.forms

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.jetpack_libs.components.texts.NormalText
import io.github.antoinepirlot.jetpack_libs.models.JetpackLibsIcons
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.data.states.SatunesUiState
import io.github.antoinepirlot.satunes.data.viewmodels.PlaylistCreationFormViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.SatunesViewModel
import io.github.antoinepirlot.satunes.models.SatunesModes
import io.github.antoinepirlot.satunes.models.SwitchSettings
import io.github.antoinepirlot.satunes.ui.components.settings.SwitchSetting

/**
 * @author Antoine Pirlot on 30/03/2024
 */

@Composable
internal fun PlaylistCreationForm(
    modifier: Modifier = Modifier,
    satunesViewModel: SatunesViewModel = viewModel(),
    playlistCreationFormViewModel: PlaylistCreationFormViewModel = viewModel(),
    onConfirm: (playlistTitle: String, isStoringOnCloud: Boolean) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val satunesUiState: SatunesUiState by satunesViewModel.uiState.collectAsState()

    val playlistTitle: String = playlistCreationFormViewModel.title
    val isStoringOnCloud: Boolean = playlistCreationFormViewModel.isStoringOnCloud

    AlertDialog(
        icon = {
            val jetpackLibsIcons: JetpackLibsIcons = JetpackLibsIcons.PLAYLIST_ADD
            Icon(
                imageVector = jetpackLibsIcons.imageVector,
                contentDescription = "PlaylistDB Creation Button Icon"
            )
        },
        title = {
            NormalText(text = stringResource(id = R.string.create_playlist))
        },
        text = {
            Column(
                modifier = modifier
            ) {
                OutlinedTextField(
                    value = playlistTitle,
                    onValueChange = { playlistCreationFormViewModel.updateTitle(value = it) },
                    label = { NormalText(text = stringResource(id = R.string.playlist_form)) }
                )

                if (satunesUiState.mode == SatunesModes.ONLINE)
                    SwitchSetting(
                        setting = SwitchSettings.SYNCHRONIZE_WITH_CLOUD,
                        jetpackLibsIcons = if (isStoringOnCloud) JetpackLibsIcons.CLOUD_ON_ICON
                        else JetpackLibsIcons.CLOUD_OFF_ICON,
                        checked = isStoringOnCloud,
                        onCheckedChange = { playlistCreationFormViewModel.switchIsStoringOnCloud() }
                    )
            }
        },
        onDismissRequest = { onDismissRequest() },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(playlistTitle, isStoringOnCloud)
                }
            ) {
                NormalText(text = stringResource(id = R.string.create))
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismissRequest() }) {
                NormalText(text = stringResource(id = R.string.cancel))
            }
        }
    )

}

@Preview
@Composable
private fun PlaylistCreationFormPreview() {
    PlaylistCreationForm(onConfirm = { _, _ -> }, onDismissRequest = {})
}