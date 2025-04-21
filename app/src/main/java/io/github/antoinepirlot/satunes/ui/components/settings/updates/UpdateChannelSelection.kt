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

package io.github.antoinepirlot.satunes.ui.components.settings.updates

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.jetpack_libs.components.texts.NormalText
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.data.viewmodels.SatunesViewModel
import io.github.antoinepirlot.satunes.database.models.UpdateChannel
import io.github.antoinepirlot.satunes.ui.components.forms.SatunesDropDownMenu

/**
 * @author Antoine Pirlot 21/04/2025
 */
@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun UpdateChannelSelection(
    modifier: Modifier = Modifier,
    satunesViewModel: SatunesViewModel = viewModel()
) {
    SatunesDropDownMenu(
        modifier = modifier,
        title = stringResource(id = R.string.update_channel_dropdown_title),
        selectedItemText = stringResource(satunesViewModel.updateChannel.stringId)
    ) { expanded: Boolean, onDismissRequest: () -> Unit ->
        Menu(expanded = expanded, onDismissRequest = onDismissRequest)
    }
}

@RequiresApi(Build.VERSION_CODES.M)
@Composable
private fun Menu(
    modifier: Modifier = Modifier,
    satunesViewModel: SatunesViewModel = viewModel(),
    expanded: Boolean,
    onDismissRequest: () -> Unit
) {
    DropdownMenu(
        modifier = modifier,
        expanded = expanded,
        onDismissRequest = onDismissRequest
    ) {
        for (channel: UpdateChannel in UpdateChannel.entries) {
            DropdownMenuItem(
                text = { NormalText(text = stringResource(channel.stringId)) },
                onClick = {
                    satunesViewModel.selectUpdateChannel(channel)
                    onDismissRequest()
                }
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.M)
@Preview
@Composable
private fun UpdateChannelSelectionPreview() {
    UpdateChannelSelection()
}