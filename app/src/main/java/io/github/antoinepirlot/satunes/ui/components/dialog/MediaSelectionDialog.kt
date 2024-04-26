/*
 * This file is part of Satunes.
 *
 * Satunes is free software: you can redistribute it and/or modify it under
 *  the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * Satunes is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Satunes.
 * If not, see <https://www.gnu.org/licenses/>.
 *
 * **** INFORMATIONS ABOUT THE AUTHOR *****
 * The author of this file is Antoine Pirlot, the owner of this project.
 * You find this original project on github.
 *
 * My github link is: https://github.com/antoinepirlot
 * This current project's link is: https://github.com/antoinepirlot/MP3-Player
 *
 * You can contact me via my email: pirlot.antoine@outlook.com
 * PS: I don't answer quickly.
 */

package io.github.antoinepirlot.satunes.ui.components.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.database.models.Media
import io.github.antoinepirlot.satunes.database.models.Music
import io.github.antoinepirlot.satunes.ui.components.forms.MediaSelectionForm
import io.github.antoinepirlot.satunes.ui.components.texts.NormalText

/**
 * @author Antoine Pirlot on 30/03/2024
 */

@Composable
fun MediaSelectionDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    mediaList: List<Media>,
    playlistTitle: String? = null,
    icon: @Composable () -> Unit,
) {
    AlertDialog(
        modifier = modifier,
        icon = icon,
        title = {
            if (mediaList.isEmpty()) {
                NormalText(text = stringResource(id = R.string.no_music))
            } else if (mediaList[0] is Music) {
                if (playlistTitle == null) {
                    throw IllegalStateException("PLaylist title is required when adding music to playist")
                }
                NormalText(text = stringResource(id = R.string.add_to) + playlistTitle)
            } else {
                NormalText(text = stringResource(id = R.string.add_to_playlist))
            }
        },
        text = {
            Column {
                MediaSelectionForm(mediaList = mediaList)
            }
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                if (mediaList.isNotEmpty()) {
                    NormalText(text = stringResource(id = R.string.add))
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                NormalText(text = stringResource(id = R.string.cancel))
            }
        }
    )
}

@Preview
@Composable
fun PlaylistSelectionDialogPreview() {
    MediaSelectionDialog(
        icon = {},
        onDismissRequest = {},
        onConfirm = {},
        mediaList = listOf()
    )
}