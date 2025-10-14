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

package io.github.antoinepirlot.satunes.ui.components.dialog.folder

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.jetpack_libs.components.texts.NormalText
import io.github.antoinepirlot.satunes.data.viewmodels.PlaybackViewModel
import io.github.antoinepirlot.satunes.database.models.media.Folder
import io.github.antoinepirlot.satunes.database.models.media.RootFolder
import io.github.antoinepirlot.satunes.icons.SatunesIcons
import io.github.antoinepirlot.satunes.ui.components.dialog.media.options.AddToPlaylistMediaOption
import io.github.antoinepirlot.satunes.ui.components.dialog.media.options.AddToQueueDialogOption
import io.github.antoinepirlot.satunes.ui.components.dialog.media.options.PlayNextMediaOption
import io.github.antoinepirlot.satunes.ui.components.dialog.media.options.RemoveFromQueueOption
import io.github.antoinepirlot.satunes.ui.components.dialog.media.options.ShareMediaOption
import io.github.antoinepirlot.satunes.ui.utils.getFirstFolderNameInChain

/**
 * @author Antoine Pirlot on 07/07/2024
 */

@Composable
internal fun FolderOptionsDialog(
    modifier: Modifier = Modifier,
    playbackViewModel: PlaybackViewModel = viewModel(),
    folder: Folder,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        confirmButton = { /* Nothing */ },
        icon = {
            val icon: SatunesIcons = SatunesIcons.FOLDER
            Icon(imageVector = icon.imageVector, contentDescription = icon.description)
        },
        title = {
            val title: String = if (folder is RootFolder) {
                getFirstFolderNameInChain(title = folder.title)
            } else {
                folder.title
            }
            NormalText(text = title)
        },
        text = {
            Column {

                /**
                 * Playlist
                 */

                AddToPlaylistMediaOption(mediaImpl = folder, onFinished = onDismissRequest)

                /**
                 * Playback
                 */
                if (playbackViewModel.isLoaded && folder.getAllMusic().size <= 500) {
                    PlayNextMediaOption(mediaImpl = folder, onDismissRequest = onDismissRequest)
                    AddToQueueDialogOption(mediaImpl = folder, onDismissRequest = onDismissRequest)
                    RemoveFromQueueOption(
                        mediaImpl = folder,
                        onDismissRequest = onDismissRequest
                    )
                }

                /**
                 * Share
                 */
                ShareMediaOption(media = folder)
            }
        }
    )
}

@Preview
@Composable
private fun FolderOptionsDialogPreview() {
    FolderOptionsDialog(folder = Folder(title = "Folder title"), onDismissRequest = {})
}