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

package io.github.antoinepirlot.satunes.ui.components.forms

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.satunes.data.viewmodels.MediaSelectionViewModel
import io.github.antoinepirlot.satunes.database.models.MediaImpl
import io.github.antoinepirlot.satunes.database.models.Music
import io.github.antoinepirlot.satunes.database.models.Playlist
import io.github.antoinepirlot.satunes.ui.components.cards.media.MediaCard

/**
 * @author Antoine Pirlot on 30/03/2024
 */

@Composable
internal fun MediaSelectionCheckbox(
    modifier: Modifier = Modifier,
    mediaSelectionViewModel: MediaSelectionViewModel = viewModel(),
    mediaImpl: MediaImpl
) {
    val checked: MutableState<Boolean> =
        rememberSaveable { mutableStateOf(mediaSelectionViewModel.isChecked(mediaImpl = mediaImpl)) }

    Box(
        modifier = modifier
            .clip(CircleShape)
            .clickable {
                onClick(
                    checked = checked,
                    mediaSelectionViewModel = mediaSelectionViewModel,
                    mediaImpl = mediaImpl
                )
            }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = checked.value,
                onCheckedChange = null
            )
            Spacer(modifier = Modifier.size(10.dp))
            MediaCard(
                media = mediaImpl,
                onClick = null,
                enableExtraOptions = false,
                openedPlaylist = null,
            )
        }
    }
}

private fun onClick(
    checked: MutableState<Boolean>,
    mediaSelectionViewModel: MediaSelectionViewModel,
    mediaImpl: MediaImpl
) {
    checked.value = !checked.value
    if (checked.value) {
        if (mediaImpl is Playlist) {
            mediaSelectionViewModel.addPlaylist(playlist = mediaImpl)
        } else if (mediaImpl is Music) {
            mediaSelectionViewModel.addMusic(music = mediaImpl)
        }
    } else {
        if (mediaImpl is Playlist) {
            mediaSelectionViewModel.removePlaylist(playlist = mediaImpl)
        } else if (mediaImpl is Music) {
            mediaSelectionViewModel.removeMusic(music = mediaImpl)
        }
    }
}

@Preview
@Composable
private fun MediaSelectionCheckboxPreview() {
    MediaSelectionCheckbox(mediaImpl = Playlist(id = 0, title = ""))
}