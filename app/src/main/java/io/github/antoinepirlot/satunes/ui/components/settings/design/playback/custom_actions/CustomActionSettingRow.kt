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

package io.github.antoinepirlot.satunes.ui.components.settings.design.playback.custom_actions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.jetpack_libs.components.JetpackLibsIcons
import io.github.antoinepirlot.jetpack_libs.components.texts.NormalText
import io.github.antoinepirlot.satunes.data.viewmodels.PlaybackViewModel
import io.github.antoinepirlot.satunes.database.models.custom_action.CustomActions
import io.github.antoinepirlot.satunes.ui.components.buttons.IconButton
import io.github.antoinepirlot.satunes.ui.components.images.Icon

/**
 * @author Antoine Pirlot 04/02/2025
 */
@Composable
fun CustomActionSettingRow(
    modifier: Modifier = Modifier,
    playbackViewModel: PlaybackViewModel = viewModel(),
    customAction: CustomActions
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 80.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(modifier = Modifier.fillMaxWidth(fraction = 0.5f)) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(jetpackLibsIcons = customAction.icon)
                Spacer(Modifier.size(size = 5.dp))
                if (customAction.stringId != null)
                    NormalText(text = stringResource(customAction.stringId!!))
            }
        }
        Column {
            if (playbackViewModel.customActionsOrder.first() != customAction)
                IconButton(
                    jetpackLibsIcons = JetpackLibsIcons.MOVE_UP,
                    onClick = { playbackViewModel.moveUp(customAction = customAction) })
            if (playbackViewModel.customActionsOrder.last() != customAction)
                IconButton(
                    jetpackLibsIcons = JetpackLibsIcons.MOVE_DOWN,
                    onClick = { playbackViewModel.moveDown(customAction = customAction) })
        }
    }
}

@Preview
@Composable
private fun CustomActionSettingRowPreview(modifier: Modifier = Modifier) {
    Column {
        CustomActionSettingRow(customAction = CustomActions.LIKE)
        CustomActionSettingRow(customAction = CustomActions.ADD_TO_PLAYLIST)
    }
}