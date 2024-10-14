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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.jetpack_libs.components.texts.NormalText
import io.github.antoinepirlot.satunes.data.viewmodels.MediaSelectionViewModel
import io.github.antoinepirlot.satunes.database.R
import io.github.antoinepirlot.satunes.database.daos.LIKES_PLAYLIST_TITLE
import io.github.antoinepirlot.satunes.database.models.MediaImpl
import io.github.antoinepirlot.satunes.database.models.Music
import io.github.antoinepirlot.satunes.database.models.Playlist

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
    val text: String = if (mediaImpl is Playlist) {
        if (mediaImpl.title == LIKES_PLAYLIST_TITLE) {
            stringResource(id = R.string.likes_playlist_title)
        } else {
            mediaImpl.title
        }
    } else {
        mediaImpl.title
    }

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
        Row(modifier = Modifier.fillMaxWidth()) {
            Checkbox(
                checked = checked.value,
                onCheckedChange = {
                    onClick(
                        checked = checked,
                        mediaSelectionViewModel = mediaSelectionViewModel,
                        mediaImpl = mediaImpl
                    )
                }
            )
            Spacer(modifier = modifier.size(10.dp))

            NormalText(
                modifier = Modifier.align(Alignment.CenterVertically),
                fontSize = 20.sp,
                text = text
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
    if (!checked.value) {
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
private fun PlaylistSelectionCheckboxPreview() {
    MediaSelectionCheckbox(mediaImpl = Playlist(id = 0, title = ""))
}